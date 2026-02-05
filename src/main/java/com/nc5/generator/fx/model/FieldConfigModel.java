package com.nc5.generator.fx.model;

import com.nc5.generator.config.FieldConfig;
import javafx.beans.property.*;

/**
 * FieldConfig的JavaFX属性包装类
 * 用于在UI中绑定和编辑字段配置
 */
public class FieldConfigModel {
    
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty label = new SimpleStringProperty("");
    private final StringProperty type = new SimpleStringProperty("String");
    private final StringProperty dbType = new SimpleStringProperty("");
    private final IntegerProperty length = new SimpleIntegerProperty(0);
    private final BooleanProperty required = new SimpleBooleanProperty(false);
    private final BooleanProperty primaryKey = new SimpleBooleanProperty(false);
    private final StringProperty defaultValue = new SimpleStringProperty("");
    private final StringProperty uiType = new SimpleStringProperty("Text");
    private final BooleanProperty editable = new SimpleBooleanProperty(true);
    private final BooleanProperty visible = new SimpleBooleanProperty(true);
    private final StringProperty enumComponentName = new SimpleStringProperty("");

    // 临时属性，用于编辑时保存旧值
    private String editingOldName = "";
    
    public FieldConfigModel() {
    }

    public FieldConfigModel(FieldConfig config) {
        fromFieldConfig(config);
    }
    
    /**
     * 从FieldConfig加载数据
     */
    public void fromFieldConfig(FieldConfig config) {
        name.set(config.getName() != null ? config.getName() : "");
        label.set(config.getLabel() != null ? config.getLabel() : "");
        type.set(config.getType() != null ? config.getType() : "String");
        dbType.set(config.getDbType() != null ? config.getDbType() : "");
        length.set(config.getLength() != null ? config.getLength() : 0);
        required.set(config.isRequired());
        primaryKey.set(config.isPrimaryKey());
        defaultValue.set(config.getDefaultValue() != null ? config.getDefaultValue() : "");
        uiType.set(config.getUiType() != null ? config.getUiType() : "Text");
        editable.set(config.isEditable());
        visible.set(config.isVisible());
        enumComponentName.set(config.getRefModelName() != null ? config.getRefModelName() : "");
    }
    
    /**
     * 转换为FieldConfig
     */
    public FieldConfig toFieldConfig() {
        FieldConfig config = new FieldConfig();
        config.setName(name.get());
        config.setLabel(label.get());
        config.setType(type.get());
        config.setDbType(dbType.get());
        config.setLength(length.get());
        config.setRequired(required.get());
        config.setPrimaryKey(primaryKey.get());
        config.setDefaultValue(defaultValue.get());
        config.setUiType(uiType.get());
        config.setEditable(editable.get());
        config.setVisible(visible.get());
        config.setRefModelName(enumComponentName.get());
        return config;
    }
    
    // Property getters
    public StringProperty nameProperty() { return name; }
    public StringProperty labelProperty() { return label; }
    public StringProperty typeProperty() { return type; }
    public StringProperty dbTypeProperty() { return dbType; }
    public IntegerProperty lengthProperty() { return length; }
    public BooleanProperty requiredProperty() { return required; }
    public BooleanProperty primaryKeyProperty() { return primaryKey; }
    public StringProperty defaultValueProperty() { return defaultValue; }
    public StringProperty uiTypeProperty() { return uiType; }
    public BooleanProperty editableProperty() { return editable; }
    public BooleanProperty visibleProperty() { return visible; }
    public StringProperty enumComponentNameProperty() { return enumComponentName; }
    
    // Getters
    public String getName() { return name.get(); }
    public String getLabel() { return label.get(); }
    public String getType() { return type.get(); }
    public String getDbType() { return dbType.get(); }
    public int getLength() { return length.get(); }
    public boolean isRequired() { return required.get(); }
    public boolean isPrimaryKey() { return primaryKey.get(); }
    public String getDefaultValue() { return defaultValue.get(); }
    public String getUiType() { return uiType.get(); }
    public boolean isEditable() { return editable.get(); }
    public boolean isVisible() { return visible.get(); }
    public String getEnumComponentName() { return enumComponentName.get(); }
    
    // Setters
    public void setName(String value) { name.set(value); }
    public void setLabel(String value) { label.set(value); }
    public void setType(String value) { type.set(value); }
    public void setDbType(String value) { dbType.set(value); }
    public void setLength(int value) { length.set(value); }
    public void setRequired(boolean value) { required.set(value); }
    public void setPrimaryKey(boolean value) { primaryKey.set(value); }
    public void setDefaultValue(String value) { defaultValue.set(value); }
    public void setUiType(String value) { uiType.set(value); }
    public void setEditable(boolean value) { editable.set(value); }
    public void setVisible(boolean value) { visible.set(value); }
    public void setEnumComponentName(String value) { enumComponentName.set(value); }

    // 编辑时的临时属性方法
    public String getEditingOldName() { return editingOldName; }
    public void setEditingOldName(String value) { editingOldName = value; }
    
    @Override
    public String toString() {
        return name.get() + " (" + label.get() + ")";
    }
}
