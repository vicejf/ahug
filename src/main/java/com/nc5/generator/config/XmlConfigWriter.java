package com.nc5.generator.config;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * XML配置文件写入器
 */
public class XmlConfigWriter {

    /**
     * 将GlobalConfig写入XML文件
     */
    public void writeGlobal(GlobalConfig config, String filePath) throws Exception {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("globalConfig");

        // 写入全局配置项
        writeGlobalInfo(root, config);

        // 写入文件
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        format.setIndentSize(4);
        format.setSuppressDeclaration(false);

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
            xmlWriter.close();
        }
    }

    /**
     * 写入全局配置项（使用子节点结构）
     */
    private void writeGlobalInfo(Element root, GlobalConfig config) {
        // 项目配置
        Element projectConfigElement = root.addElement("projectConfig");
        addComment(projectConfigElement, "项目配置");
        addElement(projectConfigElement, "sourcePath", config.getSourcePath());
        addElement(projectConfigElement, "outputDir", config.getOutputDir());
        addElement(projectConfigElement, "author", config.getAuthor());

        // 生成选项
        Element generateOptionsElement = root.addElement("generateOptions");
        addComment(generateOptionsElement, "代码生成选项");
        addElement(generateOptionsElement, "generateClient", String.valueOf(config.isGenerateClient()));
        addElement(generateOptionsElement, "generateBusiness", String.valueOf(config.isGenerateBusiness()));
        addElement(generateOptionsElement, "generateMetadata", String.valueOf(config.isGenerateMetadata()));
        addElement(generateOptionsElement, "syncAfterGenerate", String.valueOf(config.isSyncAfterGenerate()));

        // 元数据生成开关
        Element metadataSwitchesElement = root.addElement("metadataSwitches");
        addComment(metadataSwitchesElement, "元数据生成开关");
        addElement(metadataSwitchesElement, "enablePubBillInterface", String.valueOf(config.isEnablePubBillInterface()));
        addElement(metadataSwitchesElement, "enableUser", String.valueOf(config.isEnableUser()));
        addElement(metadataSwitchesElement, "enableBillStatus", String.valueOf(config.isEnableBillStatus()));

        // 元数据ID
        Element metadataIdsElement = root.addElement("metadataIds");
        addComment(metadataIdsElement, "元数据字段ID");
        addElement(metadataIdsElement, "componentId", config.getComponentId());
        addElement(metadataIdsElement, "mainEntityId", config.getMainEntityId());
        addElement(metadataIdsElement, "pkFieldId", config.getPkFieldId());
        addElement(metadataIdsElement, "billNoFieldId", config.getBillNoFieldId());
        addElement(metadataIdsElement, "corpFieldId", config.getCorpFieldId());
        addElement(metadataIdsElement, "busiTypeFieldId", config.getBusiTypeFieldId());
        addElement(metadataIdsElement, "operatorIdFieldId", config.getOperatorIdFieldId());
        addElement(metadataIdsElement, "approverFieldId", config.getApproverFieldId());
        addElement(metadataIdsElement, "billStatusFieldId", config.getBillStatusFieldId());
        addElement(metadataIdsElement, "approveNoteFieldId", config.getApproveNoteFieldId());
        addElement(metadataIdsElement, "approveDateFieldId", config.getApproveDateFieldId());
        addElement(metadataIdsElement, "billDateFieldId", config.getBillDateFieldId());
        addElement(metadataIdsElement, "billTypeFieldId", config.getBillTypeFieldId());

        // 参照ID
        Element referenceIdsElement = root.addElement("referenceIds");
        addComment(referenceIdsElement, "参照模型ID");
        addElement(referenceIdsElement, "refPubBillId", config.getRefPubBillId());
        addElement(referenceIdsElement, "refUserId", config.getRefUserId());
        addElement(referenceIdsElement, "refBillStatusId", config.getRefBillStatusId());

        // 连接ID
        Element connectionIdsElement = root.addElement("connectionIds");
        addComment(connectionIdsElement, "连接ID");
        addElement(connectionIdsElement, "connectionPubBillId", config.getConnectionPubBillId());
        addElement(connectionIdsElement, "connectionUserId", config.getConnectionUserId());
        addElement(connectionIdsElement, "connectionBillStatusId", config.getConnectionBillStatusId());

        // 写入枚举配置
        writeGlobalEnums(root, config);
    }

    /**
     * 写入枚举配置（全局配置）
     */
    private void writeGlobalEnums(Element root, GlobalConfig config) {
        if (config.getEnums() == null || config.getEnums().isEmpty()) {
            return;
        }

        addComment(root, "枚举配置");
        Element enumsElement = root.addElement("enums");

        for (EnumConfig enumConfig : config.getEnums()) {
            Element enumElement = enumsElement.addElement("enum");
            addElement(enumElement, "name", enumConfig.getName());
            addElement(enumElement, "displayName", enumConfig.getDisplayName());
            addElement(enumElement, "className", enumConfig.getClassName());

            // 写入枚举项
            if (enumConfig.getItems() != null && !enumConfig.getItems().isEmpty()) {
                Element itemsElement = enumElement.addElement("items");
                for (EnumConfig.EnumItem item : enumConfig.getItems()) {
                    Element itemElement = itemsElement.addElement("item");
                    addElement(itemElement, "display", item.getDisplay());
                    addElement(itemElement, "value", item.getValue());
                }
            }
        }
    }

    /**
     * 将BillConfig写入XML文件
     */
    public void write(BillConfig config, String filePath) throws Exception {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("billConfig");

        // 写入基本信息
        writeBasicInfo(root, config);

        // 写入全局配置（如果存在）
        if (config.getGlobalConfig() != null) {
            writeGlobalInfoToBill(root, config.getGlobalConfig());
        }

        // 注意：字段配置已单独保存到 JSON 文件，不再写入 XML

        // 写入文件
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        format.setIndentSize(4);
        format.setSuppressDeclaration(false);

        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            XMLWriter xmlWriter = new XMLWriter(writer, format);
            xmlWriter.write(document);
            xmlWriter.close();
        }
    }

    /**
     * 将全局配置信息写入 BillConfig XML（使用分组结构）
     * 注意：projectConfig（sourcePath、outputDir、author）已移至全局 INI 配置，不包含在单据配置中
     */
    private void writeGlobalInfoToBill(Element root, GlobalConfig config) {
        // 使用 globalConfig 子节点组织全局配置
        Element globalConfigElement = root.addElement("globalConfig");

        // 生成选项
        Element generateOptionsElement = globalConfigElement.addElement("generateOptions");
        addComment(generateOptionsElement, "代码生成选项");
        addElement(generateOptionsElement, "generateClient", String.valueOf(config.isGenerateClient()));
        addElement(generateOptionsElement, "generateBusiness", String.valueOf(config.isGenerateBusiness()));
        addElement(generateOptionsElement, "generateMetadata", String.valueOf(config.isGenerateMetadata()));
        addElement(generateOptionsElement, "syncAfterGenerate", String.valueOf(config.isSyncAfterGenerate()));

        // 元数据生成开关
        Element metadataSwitchesElement = globalConfigElement.addElement("metadataSwitches");
        addComment(metadataSwitchesElement, "元数据生成开关");
        addElement(metadataSwitchesElement, "enablePubBillInterface", String.valueOf(config.isEnablePubBillInterface()));
        addElement(metadataSwitchesElement, "enableUser", String.valueOf(config.isEnableUser()));
        addElement(metadataSwitchesElement, "enableBillStatus", String.valueOf(config.isEnableBillStatus()));

        // 元数据ID
        Element metadataIdsElement = globalConfigElement.addElement("metadataIds");
        addComment(metadataIdsElement, "元数据字段ID");
        addElement(metadataIdsElement, "componentId", config.getComponentId());
        addElement(metadataIdsElement, "mainEntityId", config.getMainEntityId());
        addElement(metadataIdsElement, "pkFieldId", config.getPkFieldId());
        addElement(metadataIdsElement, "billNoFieldId", config.getBillNoFieldId());
        addElement(metadataIdsElement, "corpFieldId", config.getCorpFieldId());
        addElement(metadataIdsElement, "busiTypeFieldId", config.getBusiTypeFieldId());
        addElement(metadataIdsElement, "operatorIdFieldId", config.getOperatorIdFieldId());
        addElement(metadataIdsElement, "approverFieldId", config.getApproverFieldId());
        addElement(metadataIdsElement, "billStatusFieldId", config.getBillStatusFieldId());
        addElement(metadataIdsElement, "approveNoteFieldId", config.getApproveNoteFieldId());
        addElement(metadataIdsElement, "approveDateFieldId", config.getApproveDateFieldId());
        addElement(metadataIdsElement, "billDateFieldId", config.getBillDateFieldId());
        addElement(metadataIdsElement, "billTypeFieldId", config.getBillTypeFieldId());

        // 参照ID
        Element referenceIdsElement = globalConfigElement.addElement("referenceIds");
        addComment(referenceIdsElement, "参照模型ID");
        addElement(referenceIdsElement, "refPubBillId", config.getRefPubBillId());
        addElement(referenceIdsElement, "refUserId", config.getRefUserId());
        addElement(referenceIdsElement, "refBillStatusId", config.getRefBillStatusId());

        // 连接ID
        Element connectionIdsElement = globalConfigElement.addElement("connectionIds");
        addComment(connectionIdsElement, "连接ID");
        addElement(connectionIdsElement, "connectionPubBillId", config.getConnectionPubBillId());
        addElement(connectionIdsElement, "connectionUserId", config.getConnectionUserId());
        addElement(connectionIdsElement, "connectionBillStatusId", config.getConnectionBillStatusId());

        // 写入枚举配置
        writeGlobalEnumsToBill(globalConfigElement, config);
    }

    /**
     * 写入枚举配置（用于 BillConfig XML）
     */
    private void writeGlobalEnumsToBill(Element globalConfigElement, GlobalConfig config) {
        if (config.getEnums() == null || config.getEnums().isEmpty()) {
            return;
        }

        Element enumsElement = globalConfigElement.addElement("enums");
        addComment(enumsElement, "枚举配置");

        for (EnumConfig enumConfig : config.getEnums()) {
            Element enumElement = enumsElement.addElement("enum");
            addElement(enumElement, "name", enumConfig.getName());
            addElement(enumElement, "displayName", enumConfig.getDisplayName());
            addElement(enumElement, "className", enumConfig.getClassName());

            // 写入枚举项
            if (enumConfig.getItems() != null && !enumConfig.getItems().isEmpty()) {
                Element itemsElement = enumElement.addElement("items");
                for (EnumConfig.EnumItem item : enumConfig.getItems()) {
                    Element itemElement = itemsElement.addElement("item");
                    addElement(itemElement, "display", item.getDisplay());
                    addElement(itemElement, "value", item.getValue());
                }
            }
        }
    }

    /**
     * 写入基本信息
     */
    private void writeBasicInfo(Element root, BillConfig config) {
        // 使用 basicInfo 子节点组织基本信息
        Element basicInfoElement = root.addElement("basicInfo");
        addComment(basicInfoElement, "单据基本信息");
        addElement(basicInfoElement, "billCode", config.getBillCode());
        addElement(basicInfoElement, "billName", config.getBillName());
        addElement(basicInfoElement, "module", config.getModule());
        addElement(basicInfoElement, "packageName", config.getPackageName());
        addElement(basicInfoElement, "bodyCode", config.getBodyCode());
        addElement(basicInfoElement, "headCode", config.getHeadCode());  // 新增
        addElement(basicInfoElement, "billType", config.getBillType());
        addElement(basicInfoElement, "description", config.getDescription());

        // 写入表体编码列表
        if (config.getBodyCodeList() != null && !config.getBodyCodeList().isEmpty()) {
            Element bodyCodesElement = basicInfoElement.addElement("bodyCodeList");
            for (String bodyCode : config.getBodyCodeList()) {
                addElement(bodyCodesElement, "bodyCode", bodyCode);
            }
        }

        // 使用 fieldPaths 子节点组织字段配置路径
        Element fieldPathsElement = root.addElement("fieldPaths");
        addComment(fieldPathsElement, "字段配置文件路径");
        addElement(fieldPathsElement, "headFieldsPath", config.getHeadFieldsPath());
        addElement(fieldPathsElement, "bodyFieldsPath", config.getBodyFieldsPath());
    }

    /**
     * 添加注释
     */
    private void addComment(Element parent, String comment) {
        parent.addComment(" " + comment + " ");
    }

    /**
     * 添加元素（自动处理null值）
     */
    private void addElement(Element parent, String name, String value) {
        Element element = parent.addElement(name);
        element.setText(value != null ? value : "");
    }
}
