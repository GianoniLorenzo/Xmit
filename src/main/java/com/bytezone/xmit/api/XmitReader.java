package com.bytezone.xmit.api;

import com.bytezone.xmit.AbstractXmitReader;
import com.bytezone.xmit.Utility;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface XmitReader {

    static XmitReader fromFile(File file) {
        Utility.setCodePage("CP037");
        return new AbstractXmitReader(file);
    }

    boolean isIncomplete();
    String getFileName();
    List<XmitHeader> getHeaders();
    List<XmitPartitionedDataset> getPartitionedDatasets();
    List<XmitPhysicalSequentialDataset> getPhysicalSequentialDatasets();
}
