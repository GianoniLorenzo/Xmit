package com.bytezone.xmit.gui;

import java.io.File;
import java.util.Arrays;
import java.util.prefs.Preferences;

import com.bytezone.appbase.AppBase;
import com.bytezone.appbase.StageManager;
import com.bytezone.appbase.StatusBar;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

// -----------------------------------------------------------------------------------//
public class XmitApp extends AppBase
// -----------------------------------------------------------------------------------//
{
  private static final String PREFS_ROOT_FOLDER = "RootFolder";

  private String rootFolderName;

  private XmitTree xmitTree;
  private TreePane treePane;
  private final XmitStatusBar xmitStatusBar = new XmitStatusBar ();
  private XmitStageManager xmitStageManager;

  private final SplitPane splitPane = new SplitPane ();
  private final OutputTabPane outputTabPane = new OutputTabPane ("Output");
  private final TableTabPane tableTabPane = new TableTabPane ("Table");

  private final FilterManager filterManager = new FilterManager ();

  private final FileMenu fileMenu = new FileMenu ();
  private final ViewMenu viewMenu = new ViewMenu ();

  // ---------------------------------------------------------------------------------//
  @Override
  public void start (Stage primaryStage) throws Exception
  // ---------------------------------------------------------------------------------//
  {
    super.start (primaryStage);

    primaryStage.getScene ().setOnKeyPressed (e -> keyPressed (e));

    // this must happen after show()
    xmitStageManager.setSplitPane (splitPane);
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected Parent createContent ()
  // ---------------------------------------------------------------------------------//
  {
    primaryStage.setTitle ("XmitApp");

    // get root folder
    validateRootFolderOrExit ();

    xmitTree = new XmitTree (new XmitTreeItem (new NodeData (new File (rootFolderName))));
    treePane = new TreePane (xmitTree);

    TableHeaderBar tableHeaderBar = new TableHeaderBar ();
    OutputHeaderBar outputHeaderBar = new OutputHeaderBar ();

    splitPane.getItems ().addAll (treePane, createBorderPane (tableHeaderBar, tableTabPane),
        createBorderPane (outputHeaderBar, outputTabPane));

    XmitTable xmitTable = tableTabPane.membersTab.xmitTable;

    // menu listeners
    viewMenu.setExclusiveFilterAction (e -> filterManager.toggleFilterExclusion ());
    viewMenu.setFilterAction (e -> filterManager.showWindow ());
    viewMenu.setFontAction (e -> fontManager.showWindow ());
    fileMenu.setRootAction (e -> changeRootFolder ());

    // codepage listeners
    viewMenu.addCodePageListener (outputTabPane.outputTab);
    viewMenu.addCodePageListener (outputTabPane.hexTab);
    viewMenu.addCodePageListener (xmitStatusBar);

    // lines listeners
    viewMenu.addShowLinesListener (xmitStatusBar);
    viewMenu.addShowLinesListener (outputHeaderBar);
    viewMenu.addShowLinesListener (outputTabPane.outputTab);

    // font change listeners
    fontManager.addFontChangeListener (xmitTree);
    fontManager.addFontChangeListener (outputTabPane);
    fontManager.addFontChangeListener (tableTabPane);
    fontManager.addFontChangeListener (xmitStatusBar);

    // filter change listeners (filter parameters)
    filterManager.addFilterListener (xmitStatusBar);
    filterManager.addFilterListener (tableHeaderBar);
    filterManager.addFilterListener (outputTabPane.outputTab);
    filterManager.addFilterListener (xmitTable);

    // filter action listeners (filter results)
    xmitTable.addFilterListener (tableHeaderBar);

    // treeview listeners
    xmitTree.addListener (fileMenu);
    xmitTree.addListener (outputTabPane.hexTab);
    xmitTree.addListener (outputTabPane.blocksTab);
    xmitTree.addListener (outputTabPane.outputTab);
    xmitTree.addListener (outputHeaderBar);
    xmitTree.addListener (tableHeaderBar);
    xmitTree.addListener (tableTabPane.headersTab);
    xmitTree.addListener (tableTabPane.commentsTab);
    xmitTree.addListener (xmitTable);

    // table listeners
    xmitTable.addListener (outputTabPane.hexTab);
    xmitTable.addListener (outputTabPane.blocksTab);
    xmitTable.addListener (outputTabPane.outputTab);
    xmitTable.addListener (outputHeaderBar);
    xmitTable.addListener (fileMenu);

    // add menus
    ObservableList<Menu> menus = menuBar.getMenus ();
    menus.addAll (fileMenu.getMenu (), viewMenu.getMenu ());

    fileMenu.setOutputWriter (outputTabPane.outputTab);

    // exit action
    primaryStage.setOnCloseRequest (e -> exit ());

    // ensure viewMenu (codepage) is set before xmitTree
    saveStateList.addAll (Arrays.asList (filterManager, outputTabPane, fileMenu, viewMenu, xmitTree,
        tableTabPane, fontManager));

    return splitPane;
  }

  // ---------------------------------------------------------------------------------//
  private BorderPane createBorderPane (HeaderBar headerBar, TabPane tabPane)
  // ---------------------------------------------------------------------------------//
  {
    BorderPane borderPane = new BorderPane ();
    borderPane.setTop (headerBar);
    borderPane.setCenter (tabPane);
    return borderPane;
  }

  // ---------------------------------------------------------------------------------//
  private void keyPressed (KeyEvent keyEvent)
  // ---------------------------------------------------------------------------------//
  {
    switch (keyEvent.getCode ())
    {
      case B:       // blocks
      case X:       // hex
      case O:       // output
        outputTabPane.keyPressed (keyEvent);
        keyEvent.consume ();
        break;

      case H:       // headers
      case M:       // members
      case C:       // comments
        tableTabPane.keyPressed (keyEvent);
        keyEvent.consume ();
        break;

      case COMMA:
      case PERIOD:
        fontManager.keyPressed (keyEvent);
        keyEvent.consume ();
        break;

      case F:
        filterManager.keyPressed (keyEvent);
        keyEvent.consume ();
        break;

      default:
        break;
    }
  }

  // ---------------------------------------------------------------------------------//
  void changeRootFolder ()
  // ---------------------------------------------------------------------------------//
  {
    if (setRootFolder ())
      treePane.setRootFolder (new XmitTreeItem (new NodeData (new File (rootFolderName))));
  }

  // ---------------------------------------------------------------------------------//
  private void validateRootFolderOrExit ()
  // ---------------------------------------------------------------------------------//
  {
    rootFolderName = prefs.get (PREFS_ROOT_FOLDER, "");
    if (rootFolderName.isEmpty ())
    {
      AppBase.showAlert (AlertType.INFORMATION, "XMIT folder",
          "The XMIT file folder has not yet been defined. Please choose the "
              + "TOP LEVEL FOLDER where you store your XMIT files. This folder "
              + "may contain subfolders. It can also be changed at any time.");
    }
    else
    {
      File file = new File (rootFolderName);
      if (!file.exists ())
        rootFolderName = "";
    }

    if (rootFolderName.isEmpty () && !setRootFolder ())
    {
      Platform.exit ();
      System.exit (0);
    }
  }

  // ---------------------------------------------------------------------------------//
  private boolean setRootFolder ()
  // ---------------------------------------------------------------------------------//
  {
    DirectoryChooser directoryChooser = new DirectoryChooser ();
    directoryChooser.setTitle ("Set XMIT file folder");

    String previousRootFolderName = prefs.get (PREFS_ROOT_FOLDER, "");
    if (previousRootFolderName.isEmpty ())
      directoryChooser.setInitialDirectory (new File (System.getProperty ("user.home")));
    else
      directoryChooser.setInitialDirectory (new File (previousRootFolderName));

    File file = directoryChooser.showDialog (null);
    if (file != null && file.isDirectory ())
    {
      rootFolderName = file.getAbsolutePath ();
      prefs.put (PREFS_ROOT_FOLDER, rootFolderName);
      return true;
    }

    return false;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected Preferences getPreferences ()
  // ---------------------------------------------------------------------------------//
  {
    return Preferences.userNodeForPackage (this.getClass ());
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected StageManager getStageManager (Stage stage)
  // ---------------------------------------------------------------------------------//
  {
    xmitStageManager = new XmitStageManager (stage);
    return xmitStageManager;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected StatusBar getStatusBar ()
  // ---------------------------------------------------------------------------------//
  {
    return xmitStatusBar;
  }

  // ---------------------------------------------------------------------------------//
  @Override
  protected XmitFontManager getFontManager ()
  // ---------------------------------------------------------------------------------//
  {
    return new XmitFontManager ();
  }

  // ---------------------------------------------------------------------------------//
  public static void main (String[] args)
  // ---------------------------------------------------------------------------------//
  {
    Application.launch (args);
  }
}
