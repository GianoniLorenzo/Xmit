package com.bytezone.xmit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// -----------------------------------------------------------------------------------//
public abstract class Reader implements Iterable<Dataset>
// -----------------------------------------------------------------------------------//
{
  final String fileName;
  boolean incomplete;
  final List<Dataset> datasets = new ArrayList<> ();
  final ReaderType readerType;

  enum ReaderType
  {
    XMIT, TAPE
  }

  // ---------------------------------------------------------------------------------//
  public Reader (String fileName, ReaderType readerType)
  // ---------------------------------------------------------------------------------//
  {
    this.fileName = fileName;
    this.readerType = readerType;
  }

  // ---------------------------------------------------------------------------------//
  public boolean isIncomplete ()
  // ---------------------------------------------------------------------------------//
  {
    return incomplete;
  }

  // ---------------------------------------------------------------------------------//
  public boolean isTape ()
  // ---------------------------------------------------------------------------------//
  {
    return readerType == ReaderType.TAPE;
  }

  // ---------------------------------------------------------------------------------//
  public boolean isXmit ()
  // ---------------------------------------------------------------------------------//
  {
    return readerType == ReaderType.XMIT;
  }

  // ---------------------------------------------------------------------------------//
  public String getFileName ()
  // ---------------------------------------------------------------------------------//
  {
    return fileName;
  }

  // ---------------------------------------------------------------------------------//
  public int size ()
  // ---------------------------------------------------------------------------------//
  {
    return datasets.size ();
  }

  // ---------------------------------------------------------------------------------//
  public Dataset getDataset (int index)
  // ---------------------------------------------------------------------------------//
  {
    return datasets.get (index);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public Iterator<Dataset> iterator ()
  // ---------------------------------------------------------------------------------//
  {
    return datasets.iterator ();
  }

  // ---------------------------------------------------------------------------------//
  static byte[] readFile (File file)
  // ---------------------------------------------------------------------------------//
  {
    try
    {
      return Files.readAllBytes (file.toPath ());
    }
    catch (IOException e)
    {
      e.printStackTrace ();
      return null;
    }
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("Reader: %s", fileName);
  }
}
