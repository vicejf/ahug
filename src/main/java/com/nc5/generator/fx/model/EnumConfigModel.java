package com.nc5.generator.fx.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 枚举配置模型
 */
public class EnumConfigModel {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty displayName = new SimpleStringProperty();
    private StringProperty className = new SimpleStringProperty();
    private ObservableList<EnumItemModel> items = FXCollections.observableArrayList();

    public EnumConfigModel() {
    }

    public EnumConfigModel(String name, String displayName, String className) {
        this.name.set(name);
        this.displayName.set(displayName);
        this.className.set(className);
    }

    // Getters
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public StringProperty displayNameProperty() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }

    public String getClassName() {
        return className.get();
    }

    public StringProperty classNameProperty() {
        return className;
    }

    public void setClassName(String className) {
        this.className.set(className);
    }

    public ObservableList<EnumItemModel> getItems() {
        return items;
    }

    public void setItems(ObservableList<EnumItemModel> items) {
        this.items = items;
    }
}
