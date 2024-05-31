package com.bytezone.xmit;

class Header {
  final byte[] buffer = new byte[12];

  public int getSize() {

    return (int) Utility.getValue(buffer, 9, 3);
  }

  boolean isEmpty() {

    for (byte b : buffer) if (b != 0) return false;
    return true;
  }

  long getTtr() {

    return Utility.getValue(buffer, 4, 5);
  }

  boolean ttrMatches(byte[] ttr) {

    return Utility.matches(ttr, buffer, 4);
  }

  @Override
  public String toString() {

    return Utility.getHexValues(buffer);
  }
}
