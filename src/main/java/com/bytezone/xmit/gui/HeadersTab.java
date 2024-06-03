package com.bytezone.xmit.gui;

import com.bytezone.xmit.BasicModule;
import com.bytezone.xmit.CatalogEntry;
import com.bytezone.xmit.LoadModule;
import com.bytezone.xmit.PdsDataset;
import com.bytezone.xmit.AbstractXmitReader;
import com.bytezone.xmit.gui.XmitTree.TreeNodeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.input.KeyCode;

class HeadersTab extends XmitTextTab implements TreeNodeListener {

  private TreeNodeData nodeData;

  public HeadersTab(String title, KeyCode keyCode) {

    super(title, keyCode);
  }

  @Override
  List<String> getLines() {

    List<String> lines = new ArrayList<>();

    if (nodeData == null || !nodeData.isDataset()) return lines;

    if (nodeData.isXmit()) {
      for (var controlRecord : ((AbstractXmitReader) nodeData.getReader()).getControlRecords())
        lines.add(controlRecord.toString());
    } else if (nodeData.isTape()) {
      var pdsDataset = (PdsDataset) nodeData.getDataset();

      lines.add("HDR1");
      lines.add("-----------------------------------------------------------");
      String header1 = pdsDataset.getAwsTapeHeaders().header1();
      Collections.addAll(lines, header1.split("\n"));
      lines.add("");

      lines.add("HDR2");
      lines.add("-----------------------------------------------------------");
      String header2 = pdsDataset.getAwsTapeHeaders().header2();
      Collections.addAll(lines, header2.split("\n"));
      lines.add("");
    }

    if (nodeData.isPartitionedDataset()) {
      var pdsDataset = (PdsDataset) nodeData.getDataset();
      lines.add("COPYR1");
      lines.addAll(pdsDataset.getCopyR1().toLines());
      lines.add("");
      lines.add("COPYR2");
      lines.addAll(pdsDataset.getCopyR2().toLines());
      lines.add("");

      lines.add(String.format("%s Catalog Blocks:", pdsDataset.getName()));

      if (pdsDataset.isBasicModule()) lines.add(BasicModule.getDebugHeader());
      else lines.add(LoadModule.getDebugHeader());

      for (CatalogEntry catalogEntry : pdsDataset) lines.add(catalogEntry.getDebugLine());
    }

    return lines;
  }

  @Override
  public void treeNodeSelected(TreeNodeData nodeData) {

    this.nodeData = nodeData;
    refresh();
  }
}
