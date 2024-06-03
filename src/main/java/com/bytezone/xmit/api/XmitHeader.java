package com.bytezone.xmit.api;

import com.bytezone.xmit.textunit.ControlRecord;
import com.bytezone.xmit.textunit.TextUnit;

import java.util.List;

public interface XmitHeader {
    String getName();
    List<TextUnit> getTextUnits();
    ControlRecord.ControlRecordType getControlRecordType();
    int getFileNumber();
}
