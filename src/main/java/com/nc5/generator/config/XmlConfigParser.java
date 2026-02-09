package com.nc5.generator.config;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * XML配置文件解析器
 */
public class XmlConfigParser {

    private static final Logger logger = LoggerFactory.getLogger(XmlConfigParser.class);

    /**
     * 解析全局配置文件
     */
    public GlobalConfig parseGlobal(String configPath) throws Exception {
        File configFile = new File(configPath);
        SAXReader reader = new SAXReader();
        Document document = reader.read(configFile);
        Element root = document.getRootElement();

        GlobalConfig globalConfig = new GlobalConfig();

        // 解析全局配置项
        parseGlobalInfo(root, globalConfig);

        logger.info("全局配置解析完成");

        return globalConfig;
    }

    /**
     * 解析全局配置项（从子节点读取）
     */
    private void parseGlobalInfo(Element root, GlobalConfig globalConfig) {
        // 解析项目配置
        Element projectConfigElement = root.element("projectConfig");
        if (projectConfigElement != null) {
            globalConfig.setSourcePath(projectConfigElement.elementText("sourcePath"));
            globalConfig.setOutputDir(projectConfigElement.elementText("outputDir"));
            globalConfig.setAuthor(projectConfigElement.elementText("author"));
        }

        // 解析生成选项
        Element generateOptionsElement = root.element("generateOptions");
        if (generateOptionsElement != null) {
            String generateClient = generateOptionsElement.elementText("generateClient");
            if (generateClient != null) {
                globalConfig.setGenerateClient(Boolean.parseBoolean(generateClient));
            }

            String generateBusiness = generateOptionsElement.elementText("generateBusiness");
            if (generateBusiness != null) {
                globalConfig.setGenerateBusiness(Boolean.parseBoolean(generateBusiness));
            }

            String generateMetadata = generateOptionsElement.elementText("generateMetadata");
            if (generateMetadata != null) {
                globalConfig.setGenerateMetadata(Boolean.parseBoolean(generateMetadata));
            }

            String syncAfterGenerate = generateOptionsElement.elementText("syncAfterGenerate");
            if (syncAfterGenerate != null) {
                globalConfig.setSyncAfterGenerate(Boolean.parseBoolean(syncAfterGenerate));
            }
        }

        // 解析元数据生成开关
        Element metadataSwitchesElement = root.element("metadataSwitches");
        if (metadataSwitchesElement != null) {
            String enablePubBillInterface = metadataSwitchesElement.elementText("enablePubBillInterface");
            if (enablePubBillInterface != null) {
                globalConfig.setEnablePubBillInterface(Boolean.parseBoolean(enablePubBillInterface));
            }

            String enableUser = metadataSwitchesElement.elementText("enableUser");
            if (enableUser != null) {
                globalConfig.setEnableUser(Boolean.parseBoolean(enableUser));
            }

            String enableBillStatus = metadataSwitchesElement.elementText("enableBillStatus");
            if (enableBillStatus != null) {
                globalConfig.setEnableBillStatus(Boolean.parseBoolean(enableBillStatus));
            }
        }

        // 解析元数据ID
        Element metadataIdsElement = root.element("metadataIds");
        if (metadataIdsElement != null) {
            globalConfig.setComponentId(metadataIdsElement.elementText("componentId"));
            globalConfig.setMainEntityId(metadataIdsElement.elementText("mainEntityId"));
            globalConfig.setPkFieldId(metadataIdsElement.elementText("pkFieldId"));
            globalConfig.setBillNoFieldId(metadataIdsElement.elementText("billNoFieldId"));
            globalConfig.setCorpFieldId(metadataIdsElement.elementText("corpFieldId"));
            globalConfig.setBusiTypeFieldId(metadataIdsElement.elementText("busiTypeFieldId"));
            globalConfig.setOperatorIdFieldId(metadataIdsElement.elementText("operatorIdFieldId"));
            globalConfig.setApproverFieldId(metadataIdsElement.elementText("approverFieldId"));
            globalConfig.setBillStatusFieldId(metadataIdsElement.elementText("billStatusFieldId"));
            globalConfig.setApproveNoteFieldId(metadataIdsElement.elementText("approveNoteFieldId"));
            globalConfig.setApproveDateFieldId(metadataIdsElement.elementText("approveDateFieldId"));
            globalConfig.setBillDateFieldId(metadataIdsElement.elementText("billDateFieldId"));
            globalConfig.setBillTypeFieldId(metadataIdsElement.elementText("billTypeFieldId"));
        }

        // 解析参照ID
        Element referenceIdsElement = root.element("referenceIds");
        if (referenceIdsElement != null) {
            globalConfig.setRefPubBillId(referenceIdsElement.elementText("refPubBillId"));
            globalConfig.setRefUserId(referenceIdsElement.elementText("refUserId"));
            globalConfig.setRefBillStatusId(referenceIdsElement.elementText("refBillStatusId"));
        }

        // 解析连接ID
        Element connectionIdsElement = root.element("connectionIds");
        if (connectionIdsElement != null) {
            globalConfig.setConnectionPubBillId(connectionIdsElement.elementText("connectionPubBillId"));
            globalConfig.setConnectionUserId(connectionIdsElement.elementText("connectionUserId"));
            globalConfig.setConnectionBillStatusId(connectionIdsElement.elementText("connectionBillStatusId"));
        }

        // 解析枚举配置
        parseEnums(root, globalConfig);
    }

    /**
     * 解析枚举配置（全局配置）
     */
    private void parseEnums(Element root, GlobalConfig globalConfig) {
        Element enumsElement = root.element("enums");
        if (enumsElement != null) {
            List<Element> enumElements = enumsElement.elements("enum");
            for (Element enumElement : enumElements) {
                EnumConfig enumConfig = parseEnum(enumElement);
                globalConfig.getEnums().add(enumConfig);
            }
        }
    }

    /**
     * 解析XML配置文件
     */
    public BillConfig parse(String configPath) throws Exception {
        File configFile = new File(configPath);
        SAXReader reader = new SAXReader();
        Document document = reader.read(configFile);
        Element root = document.getRootElement();

        BillConfig billConfig = new BillConfig();

        // 解析基本信息
        parseBasicInfo(root, billConfig);

        // 解析全局配置（如果存在）
        parseGlobalInfoFromBill(root, billConfig);

        // 解析表头字段
        parseHeadFields(root, billConfig);

        // 解析表体字段
        parseBodyFields(root, billConfig);

        logger.info("配置解析完成: {}", billConfig.getBillCode());

        return billConfig;
    }

    /**
     * 从 BillConfig XML 中解析全局配置信息
     * 注意：projectConfig（sourcePath、outputDir、author）已从单据配置中移除，改从全局 INI 配置读取
     */
    private void parseGlobalInfoFromBill(Element root, BillConfig billConfig) {
        GlobalConfig globalConfig = new GlobalConfig();

        // 从 globalConfig 子节点读取
        Element globalConfigElement = root.element("globalConfig");
        if (globalConfigElement != null) {
            // 解析生成选项
            Element generateOptionsElement = globalConfigElement.element("generateOptions");
            if (generateOptionsElement != null) {
                String generateClient = generateOptionsElement.elementText("generateClient");
                if (generateClient != null) {
                    globalConfig.setGenerateClient(Boolean.parseBoolean(generateClient));
                }

                String generateBusiness = generateOptionsElement.elementText("generateBusiness");
                if (generateBusiness != null) {
                    globalConfig.setGenerateBusiness(Boolean.parseBoolean(generateBusiness));
                }

                String generateMetadata = generateOptionsElement.elementText("generateMetadata");
                if (generateMetadata != null) {
                    globalConfig.setGenerateMetadata(Boolean.parseBoolean(generateMetadata));
                }

                String syncAfterGenerate = generateOptionsElement.elementText("syncAfterGenerate");
                if (syncAfterGenerate != null) {
                    globalConfig.setSyncAfterGenerate(Boolean.parseBoolean(syncAfterGenerate));
                }
            }

            // 解析元数据生成开关
            Element metadataSwitchesElement = globalConfigElement.element("metadataSwitches");
            if (metadataSwitchesElement != null) {
                String enablePubBillInterface = metadataSwitchesElement.elementText("enablePubBillInterface");
                if (enablePubBillInterface != null) {
                    globalConfig.setEnablePubBillInterface(Boolean.parseBoolean(enablePubBillInterface));
                }

                String enableUser = metadataSwitchesElement.elementText("enableUser");
                if (enableUser != null) {
                    globalConfig.setEnableUser(Boolean.parseBoolean(enableUser));
                }

                String enableBillStatus = metadataSwitchesElement.elementText("enableBillStatus");
                if (enableBillStatus != null) {
                    globalConfig.setEnableBillStatus(Boolean.parseBoolean(enableBillStatus));
                }
            }

            // 解析元数据ID
            Element metadataIdsElement = globalConfigElement.element("metadataIds");
            if (metadataIdsElement != null) {
                globalConfig.setComponentId(metadataIdsElement.elementText("componentId"));
                globalConfig.setMainEntityId(metadataIdsElement.elementText("mainEntityId"));
                globalConfig.setPkFieldId(metadataIdsElement.elementText("pkFieldId"));
                globalConfig.setBillNoFieldId(metadataIdsElement.elementText("billNoFieldId"));
                globalConfig.setCorpFieldId(metadataIdsElement.elementText("corpFieldId"));
                globalConfig.setBusiTypeFieldId(metadataIdsElement.elementText("busiTypeFieldId"));
                globalConfig.setOperatorIdFieldId(metadataIdsElement.elementText("operatorIdFieldId"));
                globalConfig.setApproverFieldId(metadataIdsElement.elementText("approverFieldId"));
                globalConfig.setBillStatusFieldId(metadataIdsElement.elementText("billStatusFieldId"));
                globalConfig.setApproveNoteFieldId(metadataIdsElement.elementText("approveNoteFieldId"));
                globalConfig.setApproveDateFieldId(metadataIdsElement.elementText("approveDateFieldId"));
                globalConfig.setBillDateFieldId(metadataIdsElement.elementText("billDateFieldId"));
                globalConfig.setBillTypeFieldId(metadataIdsElement.elementText("billTypeFieldId"));
            }

            // 解析参照ID
            Element referenceIdsElement = globalConfigElement.element("referenceIds");
            if (referenceIdsElement != null) {
                globalConfig.setRefPubBillId(referenceIdsElement.elementText("refPubBillId"));
                globalConfig.setRefUserId(referenceIdsElement.elementText("refUserId"));
                globalConfig.setRefBillStatusId(referenceIdsElement.elementText("refBillStatusId"));
            }

            // 解析连接ID
            Element connectionIdsElement = globalConfigElement.element("connectionIds");
            if (connectionIdsElement != null) {
                globalConfig.setConnectionPubBillId(connectionIdsElement.elementText("connectionPubBillId"));
                globalConfig.setConnectionUserId(connectionIdsElement.elementText("connectionUserId"));
                globalConfig.setConnectionBillStatusId(connectionIdsElement.elementText("connectionBillStatusId"));
            }

            // 解析枚举配置
            Element enumsElement = globalConfigElement.element("enums");
            if (enumsElement != null) {
                List<Element> enumElements = enumsElement.elements("enum");
                for (Element enumElement : enumElements) {
                    EnumConfig enumConfig = parseEnum(enumElement);
                    globalConfig.getEnums().add(enumConfig);
                }
            }
        }

        // 将全局配置设置到 BillConfig
        billConfig.setGlobalConfig(globalConfig);
    }

    /**
     * 解析基本信息
     */
    private void parseBasicInfo(Element root, BillConfig billConfig) {
        // 优先从 basicInfo 子节点读取（新格式）
        Element basicInfoElement = root.element("basicInfo");
        if (basicInfoElement != null) {
            billConfig.setBillCode(basicInfoElement.elementText("billCode"));
            billConfig.setBillName(basicInfoElement.elementText("billName"));
            billConfig.setModule(basicInfoElement.elementText("module"));
            billConfig.setPackageName(basicInfoElement.elementText("packageName"));
            billConfig.setBodyCode(basicInfoElement.elementText("bodyCode"));
            billConfig.setHeadCode(basicInfoElement.elementText("headCode"));  // 新增
            billConfig.setDescription(basicInfoElement.elementText("description"));

            String billType = basicInfoElement.elementText("billType");
            if (billType != null && !billType.isEmpty()) {
                billConfig.setBillType(billType);
            }
        } else {
            // 兼容旧格式，直接从根节点读取
            billConfig.setBillCode(root.elementText("billCode"));
            billConfig.setBillName(root.elementText("billName"));
            billConfig.setModule(root.elementText("module"));
            billConfig.setPackageName(root.elementText("packageName"));
            billConfig.setBodyCode(root.elementText("bodyCode"));
            billConfig.setHeadCode(root.elementText("headCode"));  // 新增
            billConfig.setDescription(root.elementText("description"));

            String billType = root.elementText("billType");
            if (billType != null && !billType.isEmpty()) {
                billConfig.setBillType(billType);
            }
        }

        // 解析表体编码列表
        Element bodyCodesElement = root.element("bodyCodeList");
        if (bodyCodesElement != null) {
            List<Element> codeElements = bodyCodesElement.elements("bodyCode");
            for (Element codeElement : codeElements) {
                String bodyCode = codeElement.getTextTrim();
                if (bodyCode != null && !bodyCode.isEmpty()) {
                    billConfig.getBodyCodeList().add(bodyCode);
                }
            }
        }

        // 解析字段配置路径
        Element fieldPathsElement = root.element("fieldPaths");
        if (fieldPathsElement != null) {
            billConfig.setHeadFieldsPath(fieldPathsElement.elementText("headFieldsPath"));
            billConfig.setBodyFieldsPath(fieldPathsElement.elementText("bodyFieldsPath"));
        } else {
            // 兼容旧格式
            billConfig.setHeadFieldsPath(root.elementText("headFieldsPath"));
            billConfig.setBodyFieldsPath(root.elementText("bodyFieldsPath"));
        }
    }

    /**
     * 解析表头字段
     */
    private void parseHeadFields(Element root, BillConfig billConfig) {
        Element headFieldsElement = root.element("headFields");
        if (headFieldsElement != null) {
            List<Element> fieldElements = headFieldsElement.elements("field");
            for (Element fieldElement : fieldElements) {
                FieldConfig fieldConfig = parseField(fieldElement);
                billConfig.getHeadFields().add(fieldConfig);
            }
        }
    }

    /**
     * 解析表体字段
     */
    private void parseBodyFields(Element root, BillConfig billConfig) {
        Element bodyFieldsElement = root.element("bodyFields");
        if (bodyFieldsElement != null) {
            List<Element> fieldElements = bodyFieldsElement.elements("field");
            for (Element fieldElement : fieldElements) {
                FieldConfig fieldConfig = parseField(fieldElement);
                billConfig.getBodyFields().add(fieldConfig);
            }
        }
    }

    /**
     * 解析字段配置
     */
    private FieldConfig parseField(Element fieldElement) {
        FieldConfig fieldConfig = new FieldConfig();

        fieldConfig.setName(fieldElement.elementText("name"));
        fieldConfig.setLabel(fieldElement.elementText("label"));
        fieldConfig.setType(fieldElement.elementText("type"));
        fieldConfig.setDbType(fieldElement.elementText("dbType"));
        fieldConfig.setUiType(fieldElement.elementText("uiType"));
        fieldConfig.setDefaultValue(fieldElement.elementText("defaultValue"));

        String length = fieldElement.elementText("length");
        if (length != null && !length.isEmpty()) {
            fieldConfig.setLength(Integer.parseInt(length));
        }

        String required = fieldElement.elementText("required");
        if (required != null) {
            fieldConfig.setRequired(Boolean.parseBoolean(required));
        }

        String primaryKey = fieldElement.elementText("primaryKey");
        if (primaryKey != null) {
            fieldConfig.setPrimaryKey(Boolean.parseBoolean(primaryKey));
        }

        String editable = fieldElement.elementText("editable");
        if (editable != null) {
            fieldConfig.setEditable(Boolean.parseBoolean(editable));
        }

        String visible = fieldElement.elementText("visible");
        if (visible != null) {
            fieldConfig.setVisible(Boolean.parseBoolean(visible));
        }

        String refModelName = fieldElement.elementText("refModelName");
        if (refModelName != null && !refModelName.isEmpty()) {
            fieldConfig.setRefModelName(refModelName);
        }

        return fieldConfig;
    }

    /**
     * 解析单个枚举配置
     */
    private EnumConfig parseEnum(Element enumElement) {
        EnumConfig enumConfig = new EnumConfig();
        enumConfig.setName(enumElement.elementText("name"));
        enumConfig.setDisplayName(enumElement.elementText("displayName"));
        enumConfig.setClassName(enumElement.elementText("className"));

        // 解析枚举项
        Element itemsElement = enumElement.element("items");
        if (itemsElement != null) {
            List<Element> itemElements = itemsElement.elements("item");
            for (Element itemElement : itemElements) {
                EnumConfig.EnumItem item = new EnumConfig.EnumItem();
                item.setDisplay(itemElement.elementText("display"));
                item.setValue(itemElement.elementText("value"));
                enumConfig.getItems().add(item);
            }
        }

        return enumConfig;
    }
}
