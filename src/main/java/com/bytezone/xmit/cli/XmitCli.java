package com.bytezone.xmit.cli;

import com.bytezone.xmit.XmitReader;
import java.io.File;
import java.io.IOException;

public class XmitCli {

  public static void main(String[] args) throws IOException {
    File f = new File("C:\\Users\\loren\\Desktop\\XMIT\\RES.SAVE.D230208.ADCD.Z23B.PARMLIB.XMIT");
    XmitReader reader = new XmitReader(f);
    System.out.println(reader);
  }
}
