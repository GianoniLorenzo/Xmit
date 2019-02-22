package com.bytezone.xmit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// ---------------------------------------------------------------------------------//
public class PdsMember extends DataFile implements Iterable<DataBlock>
//---------------------------------------------------------------------------------//
{
  private final List<DataBlock> dataBlocks;                    // PDS & PDS/E
  private final List<DataBlock> extraDataBlocks;               // PDSE only
  private final Map<Integer, Count> blockCounts = new TreeMap<> ();

  // ---------------------------------------------------------------------------------//
  PdsMember (Dataset dataset, Disposition disposition)
  // ---------------------------------------------------------------------------------//
  {
    super (dataset, disposition);

    dataBlocks = new ArrayList<> ();
    extraDataBlocks = new ArrayList<> ();
  }

  // ---------------------------------------------------------------------------------//
  void addDataBlock (DataBlock dataBlock)
  // ---------------------------------------------------------------------------------//
  {
    byte type = dataBlock.getType ();
    if (type == 0x00 || type == (byte) 0x80)      // basic PDS data
    {
      dataBlocks.add (dataBlock);
      dataLength += dataBlock.getSize ();
      incrementCount (dataBlock.getSize ());
    }
    else                                          // additional PDS/E blocks
      extraDataBlocks.add (dataBlock);
  }

  // ---------------------------------------------------------------------------------//
  private void incrementCount (int size)
  // ---------------------------------------------------------------------------------//
  {
    if (size == 0)          // ignore eof
      return;

    Count count = blockCounts.get (size);
    if (count == null)
    {
      count = new Count (size);
      blockCounts.put (size, count);
    }

    count.increment ();
  }

  // ---------------------------------------------------------------------------------//
  //  void listCounts ()
  //  // ---------------------------------------------------------------------------------//
  //  {
  //    for (Count count : blockCounts.values ())
  //      System.out.println (count);
  //  }

  // ---------------------------------------------------------------------------------//
  int getCommonBlockLength ()
  // ---------------------------------------------------------------------------------//
  {
    if (blockCounts.size () != 1)
      return 0;
    Count count = blockCounts.entrySet ().iterator ().next ().getValue ();
    return count.blockSize;
  }

  // ---------------------------------------------------------------------------------//
  List<DataBlock> getExtraDataBlocks ()
  // ---------------------------------------------------------------------------------//
  {
    return extraDataBlocks;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public byte[] getDataBuffer ()
  // ---------------------------------------------------------------------------------//
  {
    byte[] buffer = new byte[dataLength];
    int ptr = 0;

    for (DataBlock dataBlock : dataBlocks)
      ptr = dataBlock.packBuffer (buffer, ptr);

    assert ptr == dataLength;
    return buffer;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public byte[] getDataBuffer (int limit)
  // ---------------------------------------------------------------------------------//
  {
    if (dataLength <= limit)
      return getDataBuffer ();

    int length = 0;
    List<DataBlock> tmpBlocks = new ArrayList<> ();
    for (DataBlock dataBlock : dataBlocks)
    {
      tmpBlocks.add (dataBlock);
      length += dataBlock.getSize ();
      if (length >= limit)
        break;
    }

    byte[] buffer = new byte[length];
    int ptr = 0;

    for (DataBlock dataBlock : tmpBlocks)
      ptr = dataBlock.packBuffer (buffer, ptr);

    assert ptr == length;
    return buffer;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public boolean isXmit ()
  // ---------------------------------------------------------------------------------//
  {
    return dataBlocks.get (0).isXmit ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  boolean isRdw ()
  // ---------------------------------------------------------------------------------//
  {
    for (DataBlock dataBlock : dataBlocks)
      if (dataBlock.getTwoBytes () != dataBlock.getSize ())
        return false;

    return true;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  void rdwLines ()         // see SOURCE.XMI
  // ---------------------------------------------------------------------------------//
  {
    for (DataBlock dataBlock : dataBlocks)
    {
      if (dataBlock.getSize () == 0)
        continue;

      byte[] buffer = dataBlock.getBuffer ();

      int ptr = 4;

      // check first record for binary zeroes
      int len = Utility.getTwoBytes (buffer, ptr);
      boolean isBinary = Utility.isBinary (buffer, ptr + 4, len - 4);

      if (isBinary)
        while (ptr < buffer.length)
        {
          len = Utility.getTwoBytes (buffer, ptr);

          lines.add (Utility.getHexDump (buffer, ptr + 4, len - 4));
          lines.add ("");

          ptr += len;
        }
      else
        while (ptr < buffer.length)
        {
          len = Utility.getTwoBytes (buffer, ptr);
          lines
              .add (Utility.translateUnicode (buffer, ptr + 4, len - 4).stripTrailing ());

          ptr += len;
        }
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  byte[] getEightBytes ()
  // ---------------------------------------------------------------------------------//
  {
    if (isVB ())
    {
      byte[] buffer = dataBlocks.get (0).getSixteenBytes ();
      byte[] eightBytes = new byte[8];
      System.arraycopy (buffer, 8, eightBytes, 0, 8);
      return eightBytes;
    }
    return dataBlocks.get (0).getEightBytes ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    StringBuilder text = new StringBuilder ();

    int count = 0;
    int total = 0;
    int pointers = 0;

    text.append (
        "\n    #    Offset    Header                    Data         Data      Ptrs\n");
    text.append (
        "  ----  --------  -------------------------- --------  ---------  ------\n");
    for (DataBlock dataBlock : dataBlocks)
    {
      total += dataBlock.getSize ();
      pointers += dataBlock.totalBlockPointers ();
      text.append (String.format ("   %3d  %s%n", count++, dataBlock));
    }
    text.append (String.format ("%44.44s %s%n", "", "--------  ---------  ------"));

    int b1 = (total & 0xFF0000) >>> 16;
    int b2 = (total & 0x00FF00) >>> 8;
    int b3 = (total & 0x0000FF);
    text.append (String.format ("%44.44s %02X %02X %02X %,10d  %,6d%n", "", b1, b2, b3,
        total, pointers));

    for (DataBlock dataBlock : extraDataBlocks)
      text.append (String.format ("   %3d  %s%n", count++, dataBlock));

    Utility.removeTrailingNewlines (text);

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Iterator<DataBlock> iterator ()
  // ---------------------------------------------------------------------------------//
  {
    return dataBlocks.iterator ();
  }

  // ---------------------------------------------------------------------------------//
  class Count
  // ---------------------------------------------------------------------------------//
  {
    int blockSize;
    int count;

    // ---------------------------------------------------------------------------------//
    Count (int size)
    // ---------------------------------------------------------------------------------//
    {
      blockSize = size;
    }

    // ---------------------------------------------------------------------------------//
    void increment ()
    // ---------------------------------------------------------------------------------//
    {
      count++;
    }

    // ---------------------------------------------------------------------------------//
    @Override
    public String toString ()
    // ---------------------------------------------------------------------------------//
    {
      return String.format ("%,7d  %,5d", blockSize, count);
    }
  }
}
