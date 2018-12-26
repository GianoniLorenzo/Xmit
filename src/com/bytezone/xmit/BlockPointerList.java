package com.bytezone.xmit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BlockPointerList implements Iterable<BlockPointer>
{
  private final int id;
  private CatalogEntry catalogEntry;
  private final byte[] buffer;          // all block pointers refer to this
  private int bufferLength;

  private int dataLength;               // only applies to data blocks
  private boolean isBinary;
  private byte sortKey;

  private final List<BlockPointer> blockPointers = new ArrayList<> ();
  private final List<BlockPointer> newList = new ArrayList<> ();

  private boolean isLastBlock = false;
  private final List<byte[]> headers = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  // constructor
  // ---------------------------------------------------------------------------------//

  public BlockPointerList (byte[] buffer, int id)
  {
    this.buffer = buffer;
    this.id = id;
  }

  // ---------------------------------------------------------------------------------//
  // addSegment
  // ---------------------------------------------------------------------------------//

  public void addSegment (boolean firstSegment, boolean lastSegment,
      BlockPointer blockPointer)
  {
    if (blockPointer.offset + blockPointer.length > buffer.length)
    {
      System.out.println ("invalid block pointer");
      return;
    }
    blockPointers.add (blockPointer);
    bufferLength += blockPointer.length;                // used for non-data blocks
  }

  // ---------------------------------------------------------------------------------//
  // build
  // ---------------------------------------------------------------------------------//

  void build ()                       // used only for data blocks
  {
    setBinaryFlag (blockPointers.get (0));
    sortKey = buffer[blockPointers.get (0).offset + 8];

    int recLen = 0;
    int headerPtr = 0;
    byte[] header = null;

    for (BlockPointer blockPointer : blockPointers)
    {
      int ptr = blockPointer.offset;
      int avail = blockPointer.length;

      while (avail > 0)
      {
        if (recLen == 0)                // at a data header
        {
          if (headerPtr == 0)
          {
            header = new byte[12];
            headers.add (header);
          }

          if (avail < 12 - headerPtr)
          {
            System.arraycopy (buffer, ptr, header, headerPtr, avail);
            ptr += avail;
            headerPtr += avail;
            avail = 0;
            break;
          }

          int needed = 12 - headerPtr;
          System.arraycopy (buffer, ptr, header, headerPtr, needed);
          ptr += needed;
          avail -= needed;
          headerPtr = 0;

          recLen = Utility.getTwoBytes (header, 10);

          if (recLen == 0)
          {
            isLastBlock = true;
            break;
          }
        }

        int len = Math.min (recLen, avail);
        BlockPointer bp = new BlockPointer (buffer, ptr, len);
        newList.add (bp);
        ptr += len;
        avail -= len;
        recLen -= len;
      }
    }

    for (BlockPointer blockPointer : newList)
      dataLength += blockPointer.length;
  }

  // ---------------------------------------------------------------------------------//
  // getOffset
  // ---------------------------------------------------------------------------------//

  int getOffset ()
  {
    return (int) Utility.getValue (headers.get (0), 6, 3);
  }

  // ---------------------------------------------------------------------------------//
  // isPDSE
  // ---------------------------------------------------------------------------------//

  boolean isPDSE ()
  {
    return headers.get (0)[0] == (byte) 0x88;
  }

  // ---------------------------------------------------------------------------------//
  // getList
  // ---------------------------------------------------------------------------------//

  public String getList ()
  {
    StringBuilder text = new StringBuilder ();
    text.append (String.format ("BlockPointerList: %d  (%d / %d blocks)%n", id,
        blockPointers.size (), newList.size ()));
    for (byte[] header2 : headers)
      text.append (String.format ("   %s%n", Utility.getHexValues (header2)));
    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  // setBinaryFlag
  // ---------------------------------------------------------------------------------//

  private void setBinaryFlag (BlockPointer blockPointer)
  {
    for (int i = 0; i < 10; i++)
    {
      int ptr = blockPointer.offset + 12 + i;
      if (ptr >= buffer.length)
        break;
      int b = buffer[ptr] & 0xFF;
      if (b < 0x40 || b == 0xFF)
      {
        isBinary = true;
        break;
      }
    }
  }

  // ---------------------------------------------------------------------------------//
  // sortKeyMatches
  // ---------------------------------------------------------------------------------//

  boolean sortKeyMatches (byte b)
  {
    return b == sortKey;
  }

  // ---------------------------------------------------------------------------------//
  // setCatalogEntry
  // ---------------------------------------------------------------------------------//

  void setCatalogEntry (CatalogEntry catalogEntry)
  {
    this.catalogEntry = catalogEntry;
  }

  // ---------------------------------------------------------------------------------//
  // getDataLength
  // ---------------------------------------------------------------------------------//

  public int getDataLength ()
  {
    return dataLength;
  }

  // ---------------------------------------------------------------------------------//
  // isLastBlock
  // ---------------------------------------------------------------------------------//

  boolean isLastBlock ()
  {
    return isLastBlock;
  }

  // ---------------------------------------------------------------------------------//
  // listHeaders
  // ---------------------------------------------------------------------------------//

  String listHeaders ()
  {
    StringBuilder text = new StringBuilder ();
    if (catalogEntry != null)
      text.append (String.format ("Member         : %s  %06X%n",
          catalogEntry.getMemberName (), catalogEntry.getOffset ()));
    text.append (String.format ("Block pointers : %d%n", size ()));
    text.append (String.format ("Buffer length  : %06X  %<d%n", bufferLength));
    text.append (String.format ("Data length    : %06X  %<d%n", dataLength));

    if (false)
    {
      text.append ("\nOld list:\n");
      for (BlockPointer blockPointer : blockPointers)
      {
        text.append (String.format ("%s%n", blockPointer));
        text.append (
            Utility.getHexDump (buffer, blockPointer.offset, blockPointer.length));
        text.append ("\n");
      }
    }

    if (false)
    {
      text.append ("\nNew list:\n");
      for (BlockPointer blockPointer : newList)
      {
        text.append (String.format ("%s%n", blockPointer));
        text.append (
            Utility.getHexDump (buffer, blockPointer.offset, blockPointer.length));
        text.append ("\n");
      }
    }

    text.append ("\nHeaders:\n");
    for (byte[] header : headers)
    {
      text.append (Utility.getHexValues (header));
      text.append ("\n");
    }

    text.deleteCharAt (text.length () - 1);

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  // size
  // ---------------------------------------------------------------------------------//

  public int size ()
  {
    return blockPointers.size ();
  }

  // ---------------------------------------------------------------------------------//
  // isBinary
  // ---------------------------------------------------------------------------------//

  boolean isBinary ()
  {
    return isBinary;
  }

  // ---------------------------------------------------------------------------------//
  // getBuffer
  // ---------------------------------------------------------------------------------//

  byte[] getBuffer ()
  {
    byte[] fullBlock = new byte[bufferLength];
    int ptr = 0;
    for (BlockPointer blockPointer : blockPointers)
    {
      System.arraycopy (buffer, blockPointer.offset, fullBlock, ptr, blockPointer.length);
      ptr += blockPointer.length;
    }
    assert ptr == bufferLength;
    return fullBlock;
  }

  // ---------------------------------------------------------------------------------//
  // getDataBuffer
  // ---------------------------------------------------------------------------------//

  byte[] getDataBuffer ()
  {
    byte[] fullBlock = new byte[dataLength];
    int ptr = 0;
    for (BlockPointer blockPointer : newList)
    {
      System.arraycopy (buffer, blockPointer.offset, fullBlock, ptr, blockPointer.length);
      ptr += blockPointer.length;
    }
    assert ptr == dataLength;
    return fullBlock;
  }

  // ---------------------------------------------------------------------------------//
  // getDataBuffer
  // ---------------------------------------------------------------------------------//

  int getDataBuffer (byte[] xmitBuffer, int ptr)
  {
    assert buffer.length >= ptr + dataLength;
    //    byte[] fullBlock = new byte[dataLength];
    //    int ptr = 0;
    for (BlockPointer blockPointer : newList)
    {
      System.arraycopy (buffer, blockPointer.offset, xmitBuffer, ptr,
          blockPointer.length);
      ptr += blockPointer.length;
    }
    //    assert ptr == dataLength;
    //    return fullBlock;
    return ptr;
  }

  // ---------------------------------------------------------------------------------//
  // isXmit
  // ---------------------------------------------------------------------------------//

  private static byte[] INMR01 = { (byte) 0xE0, (byte) 0xC9, (byte) 0xD5, (byte) 0xD4,
                                   (byte) 0xD9, (byte) 0xF0, (byte) 0xF1 };

  boolean isXmit ()
  {
    BlockPointer blockPointer = blockPointers.get (0);
    return Utility.matches (INMR01, buffer, blockPointer.offset + 13);
  }

  // ---------------------------------------------------------------------------------//
  // dump
  // ---------------------------------------------------------------------------------//

  void dump ()
  {
    for (BlockPointer blockPointer : blockPointers)
      System.out.println (blockPointer.toHex ());
  }

  // ---------------------------------------------------------------------------------//
  // toString
  // ---------------------------------------------------------------------------------//

  @Override
  public String toString ()
  {
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("Data length  : %04X  %<,8d%n", dataLength));

    int count = 0;
    for (BlockPointer blockPointer : blockPointers)
    {
      text.append (
          String.format ("%nBlockPointer %d of %d%n", ++count, blockPointers.size ()));
      if (false)
      {
        text.append (blockPointer);
        text.append ("\n");
      }
      else
      {
        text.append (
            Utility.getHexDump (buffer, blockPointer.offset, blockPointer.length));
        text.append ("\n");
      }
    }

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  // Iterator
  // ---------------------------------------------------------------------------------//

  @Override
  public Iterator<BlockPointer> iterator ()
  {
    return blockPointers.iterator ();
  }
}
