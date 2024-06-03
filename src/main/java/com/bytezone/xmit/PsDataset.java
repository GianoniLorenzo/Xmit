package com.bytezone.xmit;

import com.bytezone.xmit.api.XmitPartitionedDataset;
import com.bytezone.xmit.api.XmitPhysicalSequentialDataset;

public class PsDataset extends Dataset implements XmitPhysicalSequentialDataset {

  private FlatFile flatFile;

  PsDataset(AbstractXmitReader reader, Disposition disposition, String name) {

    super(reader, disposition, name);
  }

  public FlatFile getFlatFile() {

    return flatFile;
  }

  @Override
  void allocateSegments() {

    flatFile = new FlatFile(this, getDisposition());
    flatFile.setName(getName());

    for (Segment segment : segments) flatFile.addSegment(segment);
  }

  @Override
  public String getName() {
    return super.getName();
  }
}
