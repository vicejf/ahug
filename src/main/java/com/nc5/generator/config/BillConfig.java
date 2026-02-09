package com.nc5.generator.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 单据配置模型
 */
public class BillConfig {

    /**
     * 表头编码（如AUJZH, AU05）
     * 如果未设置，则默认为 billCode + "H"（如果billCode不以H结尾）
     */
    private String headCode;
    
    /**
     * 单据编码（如AU84、AUJZ）
     */
    private String billCode;

    /**
     * 单据名称（如"生产入库单"、"物料开封登记"）
     */
    private String billName;

    /**
     * 模块名（如ahu、au等）
     */
    private String module;

    /**
     * 包名（如nc.ui.ahu）
     */
    private String packageName;

    /**
     * 表体编码（如AU84BVO, AUJZBVO）
     */
    private String bodyCode;

    /**
     * 表头字段JSON文件路径
     */
    private String headFieldsPath;

    /**
     * 表体字段JSON文件路径
     */
    private String bodyFieldsPath;

    /**
     * 表头字段列表
     */
    private List<FieldConfig> headFields = new ArrayList<>();

    /**
     * 表体字段列表
     */
    private List<FieldConfig> bodyFields = new ArrayList<>();

    /**
     * 表体编码列表（多表体时使用，如["SPECBVO", "BOMBVO"]）
     */
    private List<String> bodyCodeList = new ArrayList<>();

    /**
     * 单据类型：single（单表头）、multi（多表体）或archive（档案类型）
     */
    private String billType = "single";

    /**
     * 描述
     */
    private String description;

    /**
     * 全局配置
     */
    private GlobalConfig globalConfig;

    public String getHeadCode() {
        // 如果未设置headCode，则根据billCode自动推断
        if (headCode == null || headCode.isEmpty()) {
            if (billCode != null && !billCode.isEmpty()) {
                // 如果billCode已经以H结尾，则直接使用
                if (billCode.endsWith("H")) {
                    return billCode + "VO";
                } else {
                    // 否则添加H
                    return billCode + "HVO";
                }
            }
        }
        return headCode;
    }

    public void setHeadCode(String headCode) {
        this.headCode = headCode;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getBodyCode() {
        return bodyCode;
    }

    public void setBodyCode(String bodyCode) {
        this.bodyCode = bodyCode;
    }

    public String getHeadFieldsPath() {
        return headFieldsPath;
    }

    public void setHeadFieldsPath(String headFieldsPath) {
        this.headFieldsPath = headFieldsPath;
    }

    public String getBodyFieldsPath() {
        return bodyFieldsPath;
    }

    public void setBodyFieldsPath(String bodyFieldsPath) {
        this.bodyFieldsPath = bodyFieldsPath;
    }

    public List<FieldConfig> getHeadFields() {
        return headFields;
    }

    public void setHeadFields(List<FieldConfig> headFields) {
        this.headFields = headFields;
    }

    public List<FieldConfig> getBodyFields() {
        return bodyFields;
    }

    public void setBodyFields(List<FieldConfig> bodyFields) {
        this.bodyFields = bodyFields;
    }

    public List<String> getBodyCodeList() {
        return bodyCodeList;
    }

    public void setBodyCodeList(List<String> bodyCodeList) {
        this.bodyCodeList = bodyCodeList;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public boolean isSingleBill() {
        return "single".equals(billType);
    }

    /**
     * 判断是否多表体类型
     */
    public boolean isMultiBill() {
        return "multi".equals(billType);
    }

    /**
     * 判断是否档案类型
     */
    public boolean isArchiveBill() {
        return "archive".equals(billType);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取类名（如AU84）
     */
    public String getClassName() {
        return billCode;
    }

    /**
     * 获取小写类名（如au84）
     */
    public String getClassNameLower() {
        return billCode.toLowerCase();
    }

    /**
     * 获取混合类名（如Au84）
     */
    public String getClassNameCamel() {
        if (billCode == null || billCode.length() < 2) {
            return billCode;
        }
        return billCode.substring(0, 1) + billCode.substring(1).toLowerCase();
    }

    /**
     * 获取包名后缀（如ahu.au84）
     */
    public String getPackageSuffix() {
        return module + "." + getClassNameLower();
    }

    /**
     * 获取HVO类名
     */
    public String getClassNameHVO() {
        return getClassName() + "HVO";
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    // 便捷方法：从 GlobalConfig 获取生成开关

    public boolean isGenerateClient() {
        return globalConfig != null && globalConfig.isGenerateClient();
    }

    public boolean isGenerateBusiness() {
        return globalConfig != null && globalConfig.isGenerateBusiness();
    }

    public boolean isGenerateMetadata() {
        return globalConfig != null && globalConfig.isGenerateMetadata();
    }

    public String getAuthor() {
        return globalConfig != null ? globalConfig.getAuthor() : null;
    }

    // 便捷方法：从 GlobalConfig 获取元数据配置

    public String getComponentId() {
        return globalConfig != null ? globalConfig.getComponentId() : null;
    }

    public String getMainEntityId() {
        return globalConfig != null ? globalConfig.getMainEntityId() : null;
    }

    public List<EnumConfig> getEnums() {
        return globalConfig != null ? globalConfig.getEnums() : new ArrayList<>();
    }

    public boolean isEnablePubBillInterface() {
        return globalConfig != null && globalConfig.isEnablePubBillInterface();
    }

    public boolean isEnableUser() {
        return globalConfig != null && globalConfig.isEnableUser();
    }

    public boolean isEnableBillStatus() {
        return globalConfig != null && globalConfig.isEnableBillStatus();
    }

    public String getPkFieldId() {
        return globalConfig != null ? globalConfig.getPkFieldId() : null;
    }

    public String getBillNoFieldId() {
        return globalConfig != null ? globalConfig.getBillNoFieldId() : null;
    }

    public String getCorpFieldId() {
        return globalConfig != null ? globalConfig.getCorpFieldId() : null;
    }

    public String getBusiTypeFieldId() {
        return globalConfig != null ? globalConfig.getBusiTypeFieldId() : null;
    }

    public String getOperatorIdFieldId() {
        return globalConfig != null ? globalConfig.getOperatorIdFieldId() : null;
    }

    public String getApproverFieldId() {
        return globalConfig != null ? globalConfig.getApproverFieldId() : null;
    }

    public String getBillStatusFieldId() {
        return globalConfig != null ? globalConfig.getBillStatusFieldId() : null;
    }

    public String getApproveNoteFieldId() {
        return globalConfig != null ? globalConfig.getApproveNoteFieldId() : null;
    }

    public String getApproveDateFieldId() {
        return globalConfig != null ? globalConfig.getApproveDateFieldId() : null;
    }

    public String getBillDateFieldId() {
        return globalConfig != null ? globalConfig.getBillDateFieldId() : null;
    }

    public String getBillTypeFieldId() {
        return globalConfig != null ? globalConfig.getBillTypeFieldId() : null;
    }

    public String getRefPubBillId() {
        return globalConfig != null ? globalConfig.getRefPubBillId() : null;
    }

    public String getRefUserId() {
        return globalConfig != null ? globalConfig.getRefUserId() : null;
    }

    public String getRefBillStatusId() {
        return globalConfig != null ? globalConfig.getRefBillStatusId() : null;
    }

    public String getConnectionPubBillId() {
        return globalConfig != null ? globalConfig.getConnectionPubBillId() : null;
    }

    public String getConnectionUserId() {
        return globalConfig != null ? globalConfig.getConnectionUserId() : null;
    }

    public String getConnectionBillStatusId() {
        return globalConfig != null ? globalConfig.getConnectionBillStatusId() : null;
    }

    public String getSourcePath() {
        return globalConfig != null ? globalConfig.getSourcePath() : null;
    }

    public String getOutputDir() {
        return globalConfig != null ? globalConfig.getOutputDir() : null;
    }

    public boolean isSyncAfterGenerate() {
        return globalConfig != null && globalConfig.isSyncAfterGenerate();
    }
}
