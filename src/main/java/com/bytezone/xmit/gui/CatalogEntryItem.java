package com.bytezone.xmit.gui;

import com.bytezone.xmit.BasicModule;
import com.bytezone.xmit.CatalogEntry;
import com.bytezone.xmit.LoadModule;
import com.bytezone.xmit.Utility.FileType;
import java.time.LocalDate;
import javafx.beans.property.*;

public class CatalogEntryItem // must be public
 {
  private final CatalogEntry catalogEntry;

  private StringProperty memberName;
  private StringProperty userName;
  private StringProperty aliasName;

  // basic module
  private StringProperty version;
  private StringProperty time;
  private ObjectProperty<FileType> type;
  private IntegerProperty size;
  private IntegerProperty bytes;
  private IntegerProperty init;
  private ObjectProperty<LocalDate> dateCreated;
  private ObjectProperty<LocalDate> dateModified;

  // load module
  private IntegerProperty epa;
  private IntegerProperty storage;
  private IntegerProperty aMode;
  private IntegerProperty rMode;
  private IntegerProperty ssi;
  private StringProperty apf;
  private StringProperty attr;

  CatalogEntryItem(CatalogEntry catalogEntry) {

    this.catalogEntry = catalogEntry;

    setMemberName(catalogEntry.getMemberName());
    setAliasName(catalogEntry.getAliasName());
    setBytes(catalogEntry.getDataLength());
    setType(catalogEntry.getFileType());

    switch (catalogEntry.getModuleType()) {
      case BASIC:
        BasicModule module = (BasicModule) catalogEntry;
        setUserName(module.getUserName());
        setSize(module.getSize());
        setInit(module.getInit());
        setDateCreated(module.getDateCreated());
        setDateModified(module.getDateModified());
        setTime(module.getTime());
        setVersion(module.getVersion());
        break;

      case LOAD:
        LoadModule loadModule = (LoadModule) catalogEntry;
        setEpa(loadModule.getEpa());
        setStorage(loadModule.getStorage());
        setAMode(loadModule.getAMode());
        setRMode(loadModule.getRMode());
        setSsi((int) loadModule.getSsi());
        setApf(loadModule.isApf() ? "apf" : "");
        setAttr(
            String.format(
                "%2s %2s %2s %2s", //
                loadModule.isReentrant() ? "RN" : "", //
                loadModule.isReusable() ? "RU" : "", //
                loadModule.isOverlay() ? "OV" : "", //
                loadModule.isTest() ? "TS" : ""));
        break;
    }
  }

  CatalogEntry getCatalogEntry() {

    return catalogEntry;
  }

  public final String getMemberName() {

    return memberNameProperty().get();
  }

  private void setMemberName(String value) {

    memberNameProperty().set(value);
  }

  private StringProperty memberNameProperty() {

    if (memberName == null) memberName = new SimpleStringProperty();
    return memberName;
  }

  public final String getUserName() {

    return userNameProperty().get();
  }

  private void setUserName(String value) {

    userNameProperty().set(value);
  }

  private StringProperty userNameProperty() {

    if (userName == null) userName = new SimpleStringProperty();
    return userName;
  }

  public final String getAliasName() {

    return aliasNameProperty().get();
  }

  private void setAliasName(String value) {

    aliasNameProperty().set(value);
  }

  private StringProperty aliasNameProperty() {

    if (aliasName == null) aliasName = new SimpleStringProperty();
    return aliasName;
  }

  public final int getSize() {

    return sizeProperty().get();
  }

  private void setSize(int value) {

    sizeProperty().set(value);
  }

  private IntegerProperty sizeProperty() {

    if (size == null) size = new SimpleIntegerProperty();
    return size;
  }

  public final int getBytes() {

    return bytesProperty().get();
  }

  private void setBytes(int value) {

    bytesProperty().set(value);
  }

  private IntegerProperty bytesProperty() {

    if (bytes == null) bytes = new SimpleIntegerProperty();
    return bytes;
  }

  public final int getInit() {

    return initProperty().get();
  }

  private void setInit(int value) {

    initProperty().set(value);
  }

  private IntegerProperty initProperty() {

    if (init == null) init = new SimpleIntegerProperty();
    return init;
  }

  public LocalDate getDateCreated() {

    return dateCreatedProperty().get();
  }

  private void setDateCreated(LocalDate value) {

    dateCreatedProperty().set(value);
  }

  private ObjectProperty<LocalDate> dateCreatedProperty() {

    if (dateCreated == null) dateCreated = new SimpleObjectProperty<>();
    return dateCreated;
  }

  public LocalDate getDateModified() {

    return dateModifiedProperty().get();
  }

  private void setDateModified(LocalDate value) {

    dateModifiedProperty().set(value);
  }

  private ObjectProperty<LocalDate> dateModifiedProperty() {

    if (dateModified == null) dateModified = new SimpleObjectProperty<>();
    return dateModified;
  }

  public final String getTime() {

    return timeProperty().get();
  }

  private void setTime(String value) {

    timeProperty().set(value);
  }

  private StringProperty timeProperty() {

    if (time == null) time = new SimpleStringProperty();
    return time;
  }

  public final FileType getType() {

    return typeProperty().get();
  }

  private void setType(FileType value) {

    typeProperty().set(value);
  }

  private ObjectProperty<FileType> typeProperty() {

    if (type == null) type = new SimpleObjectProperty<>();
    return type;
  }

  public final String getVersion() {

    return versionProperty().get();
  }

  private void setVersion(String value) {

    versionProperty().set(value);
  }

  private StringProperty versionProperty() {

    if (version == null) version = new SimpleStringProperty();
    return version;
  }

  public final int getStorage() {

    return storageProperty().get();
  }

  private void setStorage(int value) {

    storageProperty().set(value);
  }

  private IntegerProperty storageProperty() {

    if (storage == null) storage = new SimpleIntegerProperty();
    return storage;
  }

  public final int getEpa() {

    return epaProperty().get();
  }

  private void setEpa(int value) {

    epaProperty().set(value);
  }

  private IntegerProperty epaProperty() {

    if (epa == null) epa = new SimpleIntegerProperty();
    return epa;
  }

  public final int getAMode() {

    return aModeProperty().get();
  }

  private void setAMode(int value) {

    aModeProperty().set(value);
  }

  private IntegerProperty aModeProperty() {

    if (aMode == null) aMode = new SimpleIntegerProperty();
    return aMode;
  }

  public final int getRMode() {

    return rModeProperty().get();
  }

  private void setRMode(int value) {

    rModeProperty().set(value);
  }

  private IntegerProperty rModeProperty() {

    if (rMode == null) rMode = new SimpleIntegerProperty();
    return rMode;
  }

  public final int getSsi() {

    return ssiProperty().get();
  }

  private void setSsi(int value) {

    ssiProperty().set(value);
  }

  private IntegerProperty ssiProperty() {

    if (ssi == null) ssi = new SimpleIntegerProperty();
    return ssi;
  }

  public final String getApf() {

    return apfProperty().get();
  }

  private void setApf(String value) {

    apfProperty().set(value);
  }

  private StringProperty apfProperty() {

    if (apf == null) apf = new SimpleStringProperty();
    return apf;
  }

  public final String getAttr() {

    return attrProperty().get();
  }

  private void setAttr(String value) {

    attrProperty().set(value);
  }

  private StringProperty attrProperty() {

    if (attr == null) attr = new SimpleStringProperty();
    return attr;
  }

  @Override
  public String toString() {

    return catalogEntry.toString();
  }
}
