package com.bytezone.xmit;

// ---------------------------------------------------------------------------------//
public class PsDataset extends Dataset
// ---------------------------------------------------------------------------------//
{
  private FlatFile flatFile;

  // ---------------------------------------------------------------------------------//
  PsDataset (XmitReader reader, Disposition disposition)
  // ---------------------------------------------------------------------------------//
  {
    super (reader, disposition);
  }

  // ---------------------------------------------------------------------------------//
  public FlatFile getFlatFile ()
  // ---------------------------------------------------------------------------------//
  {
    return flatFile;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  void allocateSegments ()
  // ---------------------------------------------------------------------------------//
  {
    flatFile = new FlatFile (this, disposition);
    flatFile.setName (reader.getDatasetName ());

    for (Segment segment : segments)
      flatFile.addSegment (segment);
  }
}
