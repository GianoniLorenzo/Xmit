package com.bytezone.xmit.textunit;

public class TextUnitNumber extends TextUnit {

  long number;

  public TextUnitNumber(byte[] buffer, int ptr) {

    super(buffer, ptr);

    Data data = dataList.get(0);
    number = 0;
    for (int i = 0; i < data.length; i++) {
      number *= 256;
      number += (data.data[i] & 0xFF);
    }
  }

  @Override
  public long getNumber() {

    return number;
  }

  @Override
  public String toString() {

    return String.format("%04X  %-8s  %,d", keys[keyId], mnemonics[keyId], number);
  }
}
