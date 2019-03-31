package com.bytezone.xmit.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javafx.geometry.Side;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;

// ---------------------------------------------------------------------------------//
abstract class HeaderTabPane extends HeaderPane
//---------------------------------------------------------------------------------//
{
  private static final int TAB_WIDTH = 100;
  final TabPane tabPane = new TabPane ();
  final List<XmitTab> tabs = new ArrayList<> ();

  // ---------------------------------------------------------------------------------//
  public HeaderTabPane ()
  // ---------------------------------------------------------------------------------//
  {
    tabPane.setSide (Side.BOTTOM);
    tabPane.setTabClosingPolicy (TabClosingPolicy.UNAVAILABLE);
    tabPane.setTabMinWidth (TAB_WIDTH);

    tabPane.getSelectionModel ().selectedItemProperty ()
        .addListener ( (ov, oldTab, newTab) -> updateCurrentTab ());

    setCenter (tabPane);
  }

  // ---------------------------------------------------------------------------------//
  XmitTab createStringTab (String title, KeyCode keyCode,
      Supplier<List<String>> tabUpdater)
  // ---------------------------------------------------------------------------------//
  {
    XmitTab xmitTab = new XmitTab (title, keyCode, tabUpdater);
    tabs.add (xmitTab);

    return xmitTab;
  }

  // ---------------------------------------------------------------------------------//
  void updateCurrentTab ()
  // ---------------------------------------------------------------------------------//
  {
    Tab selectedTab = tabPane.getSelectionModel ().getSelectedItem ();
    if (selectedTab != null)
      ((XmitTab) selectedTab.getUserData ()).update ();
  }

  // ---------------------------------------------------------------------------------//
  void clearText ()
  // ---------------------------------------------------------------------------------//
  {
    for (XmitTab tab : tabs)
      tab.textFlow.getChildren ().clear ();
  }

  // ---------------------------------------------------------------------------------//
  public void keyPressed (KeyCode keyCode)
  // ---------------------------------------------------------------------------------//
  {
    for (XmitTab tab : tabs)
      if (tab.keyCode == keyCode)
      {
        tabPane.getSelectionModel ().select (tab.tab);
        break;
      }
  }

  // ---------------------------------------------------------------------------------//
  public void setFont (Font font)
  // ---------------------------------------------------------------------------------//
  {
    for (XmitTab tab : tabs)
      tab.setFont (font);
  }
}
