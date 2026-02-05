package com.nc5.generator.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局配置模型
 * 存放跨单据共用的配置项
 */
public class GlobalConfig {

    /**
     * 项目源码路径（用于代码同步）
     */
    private String sourcePath;

    /**
     * 输出目录路径
     */
    private String outputDir;

    /**
     * 代码生成后是否自动同步到项目源码
     */
    private boolean syncAfterGenerate = true;

    /**
     * 是否生成客户端代码
     */
    private boolean generateClient = true;

    /**
     * 是否生成业务逻辑代码
     */
    private boolean generateBusiness = true;

    /**
     * 是否生成元数据配置
     */
    private boolean generateMetadata = false;

    /**
     * 作者
     */
    private String author;

    // ==================== 元数据生成相关字段 ====================

    /**
     * 组件ID（36位UUID）
     */
    private String componentId;

    /**
     * 主实体ID（36位UUID）
     */
    private String mainEntityId;

    /**
     * 枚举列表
     */
    private List<EnumConfig> enums = new ArrayList<>();

    /**
     * 是否启用公共单据接口
     */
    private boolean enablePubBillInterface = true;

    /**
     * 是否启用用户参照
     */
    private boolean enableUser = true;

    /**
     * 是否启用单据状态
     */
    private boolean enableBillStatus = true;

    /**
     * 主键字段ID
     */
    private String pkFieldId;

    /**
     * 单据号字段ID
     */
    private String billNoFieldId;

    /**
     * 组织字段ID
     */
    private String corpFieldId;

    /**
     * 业务类型字段ID
     */
    private String busiTypeFieldId;

    /**
     * 操作员ID字段ID
     */
    private String operatorIdFieldId;

    /**
     * 审批人字段ID
     */
    private String approverFieldId;

    /**
     * 单据状态字段ID
     */
    private String billStatusFieldId;

    /**
     * 审批批语字段ID
     */
    private String approveNoteFieldId;

    /**
     * 审批日期字段ID
     */
    private String approveDateFieldId;

    /**
     * 单据日期字段ID
     */
    private String billDateFieldId;

    /**
     * 单据类型字段ID
     */
    private String billTypeFieldId;

    /**
     * 公共单据参照ID
     */
    private String refPubBillId;

    /**
     * 用户参照ID
     */
    private String refUserId;

    /**
     * 单据状态参照ID
     */
    private String refBillStatusId;

    /**
     * 公共单据连接ID
     */
    private String connectionPubBillId;

    /**
     * 用户连接ID
     */
    private String connectionUserId;

    /**
     * 单据状态连接ID
     */
    private String connectionBillStatusId;

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public boolean isSyncAfterGenerate() {
        return syncAfterGenerate;
    }

    public void setSyncAfterGenerate(boolean syncAfterGenerate) {
        this.syncAfterGenerate = syncAfterGenerate;
    }

    public boolean isGenerateClient() {
        return generateClient;
    }

    public void setGenerateClient(boolean generateClient) {
        this.generateClient = generateClient;
    }

    public boolean isGenerateBusiness() {
        return generateBusiness;
    }

    public void setGenerateBusiness(boolean generateBusiness) {
        this.generateBusiness = generateBusiness;
    }

    public boolean isGenerateMetadata() {
        return generateMetadata;
    }

    public void setGenerateMetadata(boolean generateMetadata) {
        this.generateMetadata = generateMetadata;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getMainEntityId() {
        return mainEntityId;
    }

    public void setMainEntityId(String mainEntityId) {
        this.mainEntityId = mainEntityId;
    }

    public List<EnumConfig> getEnums() {
        return enums;
    }

    public void setEnums(List<EnumConfig> enums) {
        this.enums = enums;
    }

    public boolean isEnablePubBillInterface() {
        return enablePubBillInterface;
    }

    public void setEnablePubBillInterface(boolean enablePubBillInterface) {
        this.enablePubBillInterface = enablePubBillInterface;
    }

    public boolean isEnableUser() {
        return enableUser;
    }

    public void setEnableUser(boolean enableUser) {
        this.enableUser = enableUser;
    }

    public boolean isEnableBillStatus() {
        return enableBillStatus;
    }

    public void setEnableBillStatus(boolean enableBillStatus) {
        this.enableBillStatus = enableBillStatus;
    }

    public String getPkFieldId() {
        return pkFieldId;
    }

    public void setPkFieldId(String pkFieldId) {
        this.pkFieldId = pkFieldId;
    }

    public String getBillNoFieldId() {
        return billNoFieldId;
    }

    public void setBillNoFieldId(String billNoFieldId) {
        this.billNoFieldId = billNoFieldId;
    }

    public String getCorpFieldId() {
        return corpFieldId;
    }

    public void setCorpFieldId(String corpFieldId) {
        this.corpFieldId = corpFieldId;
    }

    public String getBusiTypeFieldId() {
        return busiTypeFieldId;
    }

    public void setBusiTypeFieldId(String busiTypeFieldId) {
        this.busiTypeFieldId = busiTypeFieldId;
    }

    public String getOperatorIdFieldId() {
        return operatorIdFieldId;
    }

    public void setOperatorIdFieldId(String operatorIdFieldId) {
        this.operatorIdFieldId = operatorIdFieldId;
    }

    public String getApproverFieldId() {
        return approverFieldId;
    }

    public void setApproverFieldId(String approverFieldId) {
        this.approverFieldId = approverFieldId;
    }

    public String getBillStatusFieldId() {
        return billStatusFieldId;
    }

    public void setBillStatusFieldId(String billStatusFieldId) {
        this.billStatusFieldId = billStatusFieldId;
    }

    public String getApproveNoteFieldId() {
        return approveNoteFieldId;
    }

    public void setApproveNoteFieldId(String approveNoteFieldId) {
        this.approveNoteFieldId = approveNoteFieldId;
    }

    public String getApproveDateFieldId() {
        return approveDateFieldId;
    }

    public void setApproveDateFieldId(String approveDateFieldId) {
        this.approveDateFieldId = approveDateFieldId;
    }

    public String getBillDateFieldId() {
        return billDateFieldId;
    }

    public void setBillDateFieldId(String billDateFieldId) {
        this.billDateFieldId = billDateFieldId;
    }

    public String getBillTypeFieldId() {
        return billTypeFieldId;
    }

    public void setBillTypeFieldId(String billTypeFieldId) {
        this.billTypeFieldId = billTypeFieldId;
    }

    public String getRefPubBillId() {
        return refPubBillId;
    }

    public void setRefPubBillId(String refPubBillId) {
        this.refPubBillId = refPubBillId;
    }

    public String getRefUserId() {
        return refUserId;
    }

    public void setRefUserId(String refUserId) {
        this.refUserId = refUserId;
    }

    public String getRefBillStatusId() {
        return refBillStatusId;
    }

    public void setRefBillStatusId(String refBillStatusId) {
        this.refBillStatusId = refBillStatusId;
    }

    public String getConnectionPubBillId() {
        return connectionPubBillId;
    }

    public void setConnectionPubBillId(String connectionPubBillId) {
        this.connectionPubBillId = connectionPubBillId;
    }

    public String getConnectionUserId() {
        return connectionUserId;
    }

    public void setConnectionUserId(String connectionUserId) {
        this.connectionUserId = connectionUserId;
    }

    public String getConnectionBillStatusId() {
        return connectionBillStatusId;
    }

    public void setConnectionBillStatusId(String connectionBillStatusId) {
        this.connectionBillStatusId = connectionBillStatusId;
    }
}
