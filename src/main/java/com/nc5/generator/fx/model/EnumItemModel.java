package com.nc5.generator.fx.model;

import javafx.beans.property.*;

/**
 * 枚举项模型
 */
public class EnumItemModel {

    private StringProperty display = new SimpleStringProperty();
    private StringProperty value = new SimpleStringProperty();

    public EnumItemModel() {
    }

    public EnumItemModel(String display, String value) {
        this.display.set(display);
        this.value.set(value);
    }

    // Getters
    public String getDisplay() {
        return display.get();
    }

    public StringProperty displayProperty() {
        return display;
    }

    public void setDisplay(String display) {
        this.display.set(display);
    }

    public String getValue() {
        return value.get();
    }

    public StringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }
}
