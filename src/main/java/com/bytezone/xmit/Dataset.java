package com.bytezone.xmit;

import com.bytezone.xmit.textunit.Dsorg.Org;
import java.util.ArrayList;
import java.util.List;

public abstract class Dataset {
  final List<Segment> segments = new ArrayList<>();
  private final Reader reader;
  private final String name;
  int rawBufferLength;
  private Disposition disposition;

  Dataset(Reader reader, Disposition disposition, String name) {
    this.reader = reader;
    this.disposition = disposition;
    this.name = name;
  }

  public Reader getReader() {
    return reader;
  }

  public String getName() {
    return name;
  }

  public Disposition getDisposition() {

    return disposition;
  }

  public void setDisposition(Disposition disposition) {

    this.disposition = disposition;
  }

  public boolean isPhysicalSequential() {

    return disposition.dsorg == Org.PS;
  }

  public boolean isPartitionedDataset() {

    return disposition.dsorg == Org.PDS;
  }

  int getRawBufferLength() {

    return rawBufferLength;
  }

  abstract void allocateSegments();

  void addSegment(Segment segment) {

    segments.add(segment);
    rawBufferLength += segment.getRawBufferLength();
  }

  public String listSegments() {

    StringBuilder text = new StringBuilder();

    text.append(
        String.format(
            "File contains %,d bytes in %,d Segments%n%n", rawBufferLength, segments.size()));

    int count = 0;
    int total = 0;
    for (Segment segment : segments) {
      total += segment.getRawBufferLength();
      text.append(
          String.format(
              "%,5d  %,7d  %,7d  %3d%n",
              count++, segment.getRawBufferLength(), total, segment.size()));

      if (count > 500) {
        text.append("Incomplete list\n");
        break;
      }
    }

    Utility.removeTrailingNewlines(text);

    return text.toString();
  }

  @Override
  public String toString() {

    return String.format("%-20s %s", name, disposition);
  }
}
