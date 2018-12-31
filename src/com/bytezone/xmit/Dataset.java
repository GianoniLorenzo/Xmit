package com.bytezone.xmit;

import java.util.ArrayList;
import java.util.List;

import com.bytezone.xmit.textunit.Dsorg.Org;

public abstract class Dataset
{
  final List<BlockPointerList> blockPointerLists = new ArrayList<> ();
  int lrecl;
  Org org;
  Reader reader;

  // ---------------------------------------------------------------------------------//
  // constructor
  // ---------------------------------------------------------------------------------//

  Dataset (Reader reader, Org org, int lrecl)
  {
    this.lrecl = lrecl;
    this.org = org;
    this.reader = reader;
  }

  abstract void process ();

  // ---------------------------------------------------------------------------------//
  // add
  // ---------------------------------------------------------------------------------//

  void add (BlockPointerList blockPointerList)
  {
    blockPointerLists.add (blockPointerList);
  }

  // ---------------------------------------------------------------------------------//
  // getOrg
  // ---------------------------------------------------------------------------------//

  public Org getOrg ()
  {
    return org;
  }

  // ---------------------------------------------------------------------------------//
  // toString
  // ---------------------------------------------------------------------------------//

  @Override
  public String toString ()
  {
    return String.format ("%-20s %-3s %,6d", reader.getFileName (), org, lrecl);
  }
}
