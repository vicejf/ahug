package com.nc5.generator.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 字段配置模型
 */
public class FieldConfig {

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段中文名
     */
    private String label;

    /**
     * 字段类型（String, Integer, Double, UFDate等）
     */
    private String type;

    /**
     * 数据库类型
     */
    private String dbType;

    /**
     * 字段长度
     */
    private Integer length;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 是否主键
     */
    private boolean primaryKey;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * UI组件类型（Text, Combo, DateTime等）
     */
    private String uiType;

    /**
     * 是否可编辑
     */
    private boolean editable = true;

    /**
     * 是否可见
     */
    private boolean visible = true;

    /**
     * 子字段（用于复杂类型）
     */
    private List<FieldConfig> children = new ArrayList<>();

    // ==================== 元数据生成相关字段 ====================

    /**
     * 字段ID（36位UUID）
     */
    private String id;

    /**
     * 数据类型ID（元数据中的dataType）
     */
    private String dataType;

    /**
     * 类型显示名称
     */
    private String typeDisplayName;

    /**
     * 参照模型名称
     */
    private String refModelName;

    /**
     * 精度（用于数值类型）
     */
    private Integer precision;

    /**
     * 小数位数（用于数值类型）
     */
    private Integer scale;

    /**
     * 参照表名
     */
    private String refTable;

    /**
     * 枚举编码
     */
    private String enumCode;

    /**
     * 字段描述
     */
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getUiType() {
        return uiType;
    }

    public void setUiType(String uiType) {
        this.uiType = uiType;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<FieldConfig> getChildren() {
        return children;
    }

    public void setChildren(List<FieldConfig> children) {
        this.children = children;
    }

    /**
     * 获取Java类型
     */
    public String getJavaType() {
        if (type == null) {
            return "String";
        }
        switch (type.toLowerCase()) {
            case "int":
            case "integer":
                return "Integer";
            case "double":
                return "Double";
            case "date":
            case "datetime":
                return "UFDate";
            case "decimal":
                return "UFDouble";
            case "boolean":
                return "UFBoolean";
            default:
                return "String";
        }
    }

    // ==================== 元数据生成相关getter/setter ====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getTypeDisplayName() {
        return typeDisplayName;
    }

    public void setTypeDisplayName(String typeDisplayName) {
        this.typeDisplayName = typeDisplayName;
    }

    public String getRefModelName() {
        return refModelName;
    }

    public void setRefModelName(String refModelName) {
        this.refModelName = refModelName;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getRefTable() {
        return refTable;
    }

    public void setRefTable(String refTable) {
        this.refTable = refTable;
    }

    public String getEnumCode() {
        return enumCode;
    }

    public void setEnumCode(String enumCode) {
        this.enumCode = enumCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 为布尔类型字段添加 getXxx 方法以兼容 JSON 序列化
    public Boolean getRequired() {
        return required;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    public Boolean getEditable() {
        return editable;
    }

    public Boolean getVisible() {
        return visible;
    }
}
