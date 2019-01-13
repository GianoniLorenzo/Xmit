package com.bytezone.xmit;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.xmit.textunit.ControlRecord;
import com.bytezone.xmit.textunit.Dsorg;
import com.bytezone.xmit.textunit.Dsorg.Org;
import com.bytezone.xmit.textunit.Recfm;
import com.bytezone.xmit.textunit.TextUnit;
import com.bytezone.xmit.textunit.TextUnitNumber;

public abstract class Dataset
{
  final Reader reader;
  final ControlRecord inmr02;

  final int lrecl;
  final Org dsorg;
  final int recfm;

  final List<Segment> segments = new ArrayList<> ();
  int rawBufferLength;

  // ---------------------------------------------------------------------------------//
  // constructor
  // ---------------------------------------------------------------------------------//

  Dataset (Reader reader, ControlRecord inmr02)
  {
    this.reader = reader;
    this.inmr02 = inmr02;

    lrecl = (int) ((TextUnitNumber) inmr02.getTextUnit (TextUnit.INMLRECL)).getNumber ();
    dsorg = ((Dsorg) inmr02.getTextUnit (TextUnit.INMDSORG)).type;
    recfm = (int) ((Recfm) inmr02.getTextUnit (TextUnit.INMRECFM)).getNumber ();
  }

  // ---------------------------------------------------------------------------------//
  // process
  // ---------------------------------------------------------------------------------//

  abstract void process ();

  // ---------------------------------------------------------------------------------//
  // addSegment
  // ---------------------------------------------------------------------------------//

  void addSegment (Segment segment)
  {
    segments.add (segment);
    rawBufferLength += segment.getRawBufferLength ();
  }

  // ---------------------------------------------------------------------------------//
  // getOrg
  // ---------------------------------------------------------------------------------//

  public Org getOrg ()
  {
    return dsorg;
  }

  // ---------------------------------------------------------------------------------//
  // getControlRecord
  // ---------------------------------------------------------------------------------//

  public ControlRecord getControlRecord ()
  {
    return inmr02;
  }

  // ---------------------------------------------------------------------------------//
  // toString
  // ---------------------------------------------------------------------------------//

  public String listSegments ()
  {
    StringBuilder text = new StringBuilder ();

    text.append (String.format ("File contains %,d bytes in %,d Segments%n%n",
        rawBufferLength, segments.size ()));

    int count = 0;
    int total = 0;
    for (Segment segment : segments)
    {
      total += segment.getRawBufferLength ();
      text.append (String.format ("%,5d  %,7d  %,7d  %3d%n", count++,
          segment.getRawBufferLength (), total, segment.size ()));

      if (count > 500)
        break;
    }

    //    text.append (String.format ("%44.44s %s%n", "", "------ ---------"));
    //    text.append (String.format ("%44.44s %06X %<,9d%n%n", "", total));

    Utility.removeTrailingNewlines (text);

    return text.toString ();
  }

  // ---------------------------------------------------------------------------------//
  // toString
  // ---------------------------------------------------------------------------------//

  @Override
  public String toString ()
  {
    return String.format ("%-20s %-3s %,6d  %04X", reader.getFileName (), dsorg, lrecl,
        recfm);
  }
}
