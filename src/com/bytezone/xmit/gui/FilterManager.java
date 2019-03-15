package com.bytezone.xmit.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

// ---------------------------------------------------------------------------------//
public class FilterManager implements SaveState
//---------------------------------------------------------------------------------//
{
  private final Preferences prefs = Preferences.userNodeForPackage (this.getClass ());
  private static final String PREFS_FILTER = "Filter";

  private final List<FilterListener> listeners = new ArrayList<> ();
  private String filter;
  private String savedFilter;
  private Stage stage;
  private final TextField textField = new TextField ();

  //---------------------------------------------------------------------------------//
  void showWindow ()
  //---------------------------------------------------------------------------------//
  {
    if (stage == null)
    {
      stage = new Stage ();
      stage.setTitle ("Filter Manager");

      BorderPane borderPane = new BorderPane ();
      Label text = new Label ("Filter text");
      textField.setPrefWidth (300);

      Button btnApply = getButton ("Apply");
      Button btnCancel = getButton ("Cancel");
      Button btnAccept = getButton ("Accept");
      Button btnRemove = getButton ("Remove");

      HBox textBox = new HBox (10);
      textBox.setPrefHeight (70);
      textBox.setPadding (new Insets (6, 10, 6, 20));
      textBox.setAlignment (Pos.CENTER_LEFT);
      textBox.getChildren ().addAll (text, textField);

      HBox controlBox = new HBox (10);
      controlBox.setPrefHeight (20);
      controlBox.setPadding (new Insets (6, 10, 6, 10));
      controlBox.setAlignment (Pos.CENTER_LEFT);
      Region filler = new Region ();
      HBox.setHgrow (filler, Priority.ALWAYS);
      controlBox.getChildren ().addAll (filler, btnCancel, btnApply, btnAccept,
          btnRemove);

      borderPane.setBottom (controlBox);
      borderPane.setCenter (textBox);

      btnApply.setOnAction (e -> apply ());
      btnCancel.setOnAction (e -> cancel ());
      btnAccept.setOnAction (e -> accept ());
      btnRemove.setOnAction (e -> remove ());

      btnAccept.setDefaultButton (true);
      btnCancel.setCancelButton (true);

      stage.setScene (new Scene (borderPane, 500, 100));
    }

    savedFilter = filter;
    textField.setText (filter);
    textField.requestFocus ();
    textField.selectAll ();
    stage.show ();
  }

  // ---------------------------------------------------------------------------------//
  private void apply ()
  // ---------------------------------------------------------------------------------//
  {
    if (!filter.equals (textField.getText ()))
    {
      filter = textField.getText ();
      notifyListeners ();
    }
  }

  // ---------------------------------------------------------------------------------//
  private void cancel ()
  // ---------------------------------------------------------------------------------//
  {
    if (!filter.equals (savedFilter))
    {
      filter = savedFilter;
      notifyListeners ();
    }
    stage.hide ();
  }

  // ---------------------------------------------------------------------------------//
  private void accept ()
  // ---------------------------------------------------------------------------------//
  {
    apply ();
    stage.hide ();
  }

  // ---------------------------------------------------------------------------------//
  private void remove ()
  // ---------------------------------------------------------------------------------//
  {
    savedFilter = "";
    cancel ();
  }

  // ---------------------------------------------------------------------------------//
  private Button getButton (String text)
  // ---------------------------------------------------------------------------------//
  {
    Button button = new Button (text);
    button.setMinWidth (100);
    return button;
  }

  //---------------------------------------------------------------------------------//
  @Override
  public void save ()
  //---------------------------------------------------------------------------------//
  {
    prefs.put (PREFS_FILTER, filter);
  }

  //---------------------------------------------------------------------------------//
  @Override
  public void restore ()
  //---------------------------------------------------------------------------------//
  {
    filter = prefs.get (PREFS_FILTER, "");
    notifyListeners ();
  }

  // ---------------------------------------------------------------------------------//
  private void notifyListeners ()
  // ---------------------------------------------------------------------------------//
  {
    for (FilterListener listener : listeners)
      listener.setFilter (filter);
  }

  // ---------------------------------------------------------------------------------//
  public void addFilterListener (FilterListener listener)
  // ---------------------------------------------------------------------------------//
  {
    if (!listeners.contains (listener))
      listeners.add (listener);
  }
}