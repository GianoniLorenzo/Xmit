package com.bytezone.xmit;

import java.util.ArrayList;
import java.util.List;

// ---------------------------------------------------------------------------------//
public class Filter
// ---------------------------------------------------------------------------------//
{
  private final PdsDataset pdsDataset;
  private final String key;
  private final List<CatalogEntry> filteredTrue = new ArrayList<> ();
  private final List<CatalogEntry> filteredFalse = new ArrayList<> ();

  public enum FilterMode
  {
    POSITIVE, NEGATIVE, NONE
  }

  // ---------------------------------------------------------------------------------//
  public Filter (PdsDataset pdsDataset, String key)
  // ---------------------------------------------------------------------------------//
  {
    this.pdsDataset = pdsDataset;
    this.key = key;

    if (key.isEmpty ())
      filteredTrue.addAll (pdsDataset.getCatalogEntries ());
    else
      for (CatalogEntry catalogEntry : pdsDataset.getCatalogEntries ())
        if (catalogEntry.contains (key))
          filteredTrue.add (catalogEntry);
        else
          filteredFalse.add (catalogEntry);
  }

  // ---------------------------------------------------------------------------------//
  public List<CatalogEntry> getFiltered (FilterMode filterMode)
  // ---------------------------------------------------------------------------------//
  {
    return filteredTrue;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  public String toString ()
  // ---------------------------------------------------------------------------------//
  {
    return String.format ("Filter: %-10s %,d / %,d", key, filteredTrue.size (),
        filteredFalse.size ());
  }
}