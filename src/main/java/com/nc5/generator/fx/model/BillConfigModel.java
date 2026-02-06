package com.nc5.generator.fx.model;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.config.FieldConfig;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * BillConfig的JavaFX属性包装类
 * 用于在UI中绑定和编辑单据配置
 */
public class BillConfigModel {
    
    private final StringProperty billCode = new SimpleStringProperty("");
    private final StringProperty billName = new SimpleStringProperty("");
    private final StringProperty module = new SimpleStringProperty("");
    private final StringProperty packageName = new SimpleStringProperty("");
    private final StringProperty bodyCode = new SimpleStringProperty("");
    private final StringProperty headCode = new SimpleStringProperty("");
    private final StringProperty headFieldsPath = new SimpleStringProperty("");
    private final StringProperty bodyFieldsPath = new SimpleStringProperty("");
    private final StringProperty billType = new SimpleStringProperty("single");
    private final StringProperty description = new SimpleStringProperty("");

    // 全局配置模型
    private final GlobalConfigModel globalConfigModel = new GlobalConfigModel();

    private final ObservableList<FieldConfigModel> headFields = FXCollections.observableArrayList();
    private final ObservableList<FieldConfigModel> bodyFields = FXCollections.observableArrayList();
    private final ObservableList<String> bodyCodeList = FXCollections.observableArrayList();
    private final ObservableList<EnumConfigModel> enumConfigs = FXCollections.observableArrayList();
    
    public BillConfigModel() {
    }
    
    public BillConfigModel(BillConfig config) {
        fromBillConfig(config);
    }
    
    /**
     * 从BillConfig加载数据
     */
    public void fromBillConfig(BillConfig config) {
        billCode.set(config.getBillCode() != null ? config.getBillCode() : "");
        billName.set(config.getBillName() != null ? config.getBillName() : "");
        module.set(config.getModule() != null ? config.getModule() : "");
        packageName.set(config.getPackageName() != null ? config.getPackageName() : "");
        bodyCode.set(config.getBodyCode() != null ? config.getBodyCode() : "");
        // 表头编码默认为 单据编码 + HVO
        headCode.set(config.getBillCode() != null && !config.getBillCode().isEmpty() ? config.getBillCode() + "HVO" : "");
        headFieldsPath.set(config.getHeadFieldsPath() != null ? config.getHeadFieldsPath() : "");
        bodyFieldsPath.set(config.getBodyFieldsPath() != null ? config.getBodyFieldsPath() : "");
        billType.set(config.getBillType() != null ? config.getBillType() : "single");
        description.set(config.getDescription() != null ? config.getDescription() : "");

        // 加载全局配置
        if (config.getGlobalConfig() != null) {
            globalConfigModel.fromGlobalConfig(config.getGlobalConfig());
        }

        // 加载表头字段
        headFields.clear();
        if (config.getHeadFields() != null) {
            for (FieldConfig field : config.getHeadFields()) {
                headFields.add(new FieldConfigModel(field));
            }
        }
        
        // 加载表体字段
        bodyFields.clear();
        if (config.getBodyFields() != null) {
            for (FieldConfig field : config.getBodyFields()) {
                bodyFields.add(new FieldConfigModel(field));
            }
        }
        
        // 加载表体编码列表
        bodyCodeList.clear();
        if (config.getBodyCodeList() != null) {
            bodyCodeList.addAll(config.getBodyCodeList());
        }

        // 加载枚举配置
        enumConfigs.clear();
        if (config.getEnums() != null) {
            for (com.nc5.generator.config.EnumConfig enumConfig : config.getEnums()) {
                EnumConfigModel enumConfigModel = new EnumConfigModel(
                    enumConfig.getName(),
                    enumConfig.getDisplayName(),
                    enumConfig.getClassName()
                );
                // 加载枚举项
                for (com.nc5.generator.config.EnumConfig.EnumItem item : enumConfig.getItems()) {
                    EnumItemModel itemModel = new EnumItemModel(item.getDisplay(), item.getValue());
                    enumConfigModel.getItems().add(itemModel);
                }
                enumConfigs.add(enumConfigModel);
            }
        }
    }
    
    /**
     * 转换为BillConfig
     */
    public BillConfig toBillConfig() {
        BillConfig config = new BillConfig();
        config.setBillCode(billCode.get());
        config.setBillName(billName.get());
        config.setModule(module.get());
        config.setPackageName(packageName.get());
        config.setBodyCode(bodyCode.get());
        // headCode 不保存到 BillConfig，它是根据 billCode 动态生成的
        config.setHeadFieldsPath(headFieldsPath.get());
        config.setBodyFieldsPath(bodyFieldsPath.get());
        config.setBillType(billType.get());
        config.setDescription(description.get());

        // 设置全局配置
        config.setGlobalConfig(globalConfigModel.toGlobalConfig());

        // 转换表头字段
        for (FieldConfigModel model : headFields) {
            config.getHeadFields().add(model.toFieldConfig());
        }
        
        // 转换表体字段
        for (FieldConfigModel model : bodyFields) {
            config.getBodyFields().add(model.toFieldConfig());
        }
        
        // 转换表体编码列表
        config.getBodyCodeList().addAll(bodyCodeList);

        // 转换枚举配置
        // 确保 GlobalConfig 存在
        if (config.getGlobalConfig() == null) {
            config.setGlobalConfig(new com.nc5.generator.config.GlobalConfig());
        }
        for (EnumConfigModel enumConfigModel : enumConfigs) {
            com.nc5.generator.config.EnumConfig enumConfig = new com.nc5.generator.config.EnumConfig();
            enumConfig.setName(enumConfigModel.getName());
            enumConfig.setDisplayName(enumConfigModel.getDisplayName());
            enumConfig.setClassName(enumConfigModel.getClassName());

            // 转换枚举项
            for (EnumItemModel itemModel : enumConfigModel.getItems()) {
                com.nc5.generator.config.EnumConfig.EnumItem item = new com.nc5.generator.config.EnumConfig.EnumItem();
                item.setDisplay(itemModel.getDisplay());
                item.setValue(itemModel.getValue());
                enumConfig.getItems().add(item);
            }
            config.getEnums().add(enumConfig);
        }

        return config;
    }
    
    /**
     * 清空所有数据
     */
    public void clear() {
        billCode.set("");
        billName.set("");
        module.set("");
        packageName.set("");
        bodyCode.set("");
        headCode.set("");
        billType.set("single");
        description.set("");
        headFields.clear();
        bodyFields.clear();
        bodyCodeList.clear();
        enumConfigs.clear();
    }
    
    /**
     * 判断是否单表头类型
     */
    public boolean isSingleBill() {
        return "single".equals(billType.get());
    }
    
    /**
     * 判断是否多表体类型
     */
    public boolean isMultiBill() {
        return "multi".equals(billType.get());
    }

    /**
     * 判断是否档案类型
     */
    public boolean isArchiveBill() {
        return "archive".equals(billType.get());
    }
    
    // Property getters
    public StringProperty billCodeProperty() { return billCode; }
    public StringProperty billNameProperty() { return billName; }
    public StringProperty moduleProperty() { return module; }
    public StringProperty packageNameProperty() { return packageName; }
    public StringProperty bodyCodeProperty() { return bodyCode; }
    public StringProperty headCodeProperty() { return headCode; }
    public StringProperty headFieldsPathProperty() { return headFieldsPath; }
    public StringProperty bodyFieldsPathProperty() { return bodyFieldsPath; }
    public StringProperty billTypeProperty() { return billType; }
    public StringProperty descriptionProperty() { return description; }

    // 全局配置属性的代理方法
    public BooleanProperty generateClientProperty() { return globalConfigModel.generateClientProperty(); }
    public BooleanProperty generateBusinessProperty() { return globalConfigModel.generateBusinessProperty(); }
    public BooleanProperty generateMetadataProperty() { return globalConfigModel.generateMetadataProperty(); }
    public StringProperty authorProperty() { return globalConfigModel.authorProperty(); }
    public StringProperty sourcePathProperty() { return globalConfigModel.sourcePathProperty(); }
    public StringProperty outputDirProperty() { return globalConfigModel.outputDirProperty(); }
    public BooleanProperty syncAfterGenerateProperty() { return globalConfigModel.syncAfterGenerateProperty(); }

    // ObservableList getters
    public ObservableList<FieldConfigModel> getHeadFields() { return headFields; }
    public ObservableList<FieldConfigModel> getBodyFields() { return bodyFields; }
    public ObservableList<String> getBodyCodeList() { return bodyCodeList; }
    public ObservableList<EnumConfigModel> getEnumConfigs() { return enumConfigs; }
    
    // Getters
    public String getBillCode() { return billCode.get(); }
    public String getBillName() { return billName.get(); }
    public String getModule() { return module.get(); }
    public String getPackageName() { return packageName.get(); }
    public String getBodyCode() { return bodyCode.get(); }
    public String getHeadCode() { return headCode.get(); }
    public String getHeadFieldsPath() { return headFieldsPath.get(); }
    public String getBodyFieldsPath() { return bodyFieldsPath.get(); }
    public String getBillType() { return billType.get(); }
    public String getDescription() { return description.get(); }

    // 全局配置属性的代理 getter 方法
    public boolean isGenerateClient() { return globalConfigModel.isGenerateClient(); }
    public boolean isGenerateBusiness() { return globalConfigModel.isGenerateBusiness(); }
    public boolean isGenerateMetadata() { return globalConfigModel.isGenerateMetadata(); }
    public String getAuthor() { return globalConfigModel.getAuthor(); }
    public String getSourcePath() { return globalConfigModel.getSourcePath(); }
    public String getOutputDir() { return globalConfigModel.getOutputDir(); }
    public boolean isSyncAfterGenerate() { return globalConfigModel.isSyncAfterGenerate(); }

    // 全局配置模型的 getter
    public GlobalConfigModel getGlobalConfigModel() {
        return globalConfigModel;
    }

    // Setters
    public void setBillCode(String value) { billCode.set(value); }
    public void setBillName(String value) { billName.set(value); }
    public void setModule(String value) { module.set(value); }
    public void setPackageName(String value) { packageName.set(value); }
    public void setBodyCode(String value) { bodyCode.set(value); }
    public void setHeadCode(String value) { headCode.set(value); }
    public void setHeadFieldsPath(String value) { headFieldsPath.set(value); }
    public void setBodyFieldsPath(String value) { bodyFieldsPath.set(value); }
    public void setBillType(String value) { billType.set(value); }
    public void setDescription(String value) { description.set(value); }

    // 全局配置属性的代理 setter 方法
    public void setGenerateClient(boolean value) { globalConfigModel.setGenerateClient(value); }
    public void setGenerateBusiness(boolean value) { globalConfigModel.setGenerateBusiness(value); }
    public void setGenerateMetadata(boolean value) { globalConfigModel.setGenerateMetadata(value); }
    public void setAuthor(String value) { globalConfigModel.setAuthor(value); }
    public void setSourcePath(String value) { globalConfigModel.setSourcePath(value); }
    public void setOutputDir(String value) { globalConfigModel.setOutputDir(value); }
    public void setSyncAfterGenerate(boolean value) { globalConfigModel.setSyncAfterGenerate(value); }
}
