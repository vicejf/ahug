package com.nc5.generator.generator;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.config.EnumConfig;
import com.nc5.generator.config.FieldConfig;
import com.nc5.generator.fx.util.UUIDUtil;
import com.nc5.generator.template.TemplateSelector;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 元数据生成器
 * 用于生成.bmf元数据文件
 */
public class MetadataGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MetadataGenerator.class);

    private String outputDir;

    public MetadataGenerator(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * 生成元数据文件
     */
    public void generate(BillConfig billConfig) throws Exception {
        // 获取元数据模板
        String metadataTemplatePath = TemplateSelector.getMetadataTemplate(billConfig);
        if (metadataTemplatePath == null) {
            logger.info("跳过元数据生成：当前单据类型不支持元数据生成");
            return;
        }

        logger.info("开始生成元数据文件...");

        // 初始化元数据配置
        initMetadataConfig(billConfig);

        // 获取模板
        Template template = VelocityUtil.getTemplate(metadataTemplatePath);

        // 创建上下文
        VelocityContext context = new VelocityContext();
        context.put("bill", billConfig);

        // 输出文件
        File outputFile = new File(outputDir, "metadata/" + billConfig.getBillCode().toLowerCase() + ".bmf");
        outputFile.getParentFile().mkdirs();

        // 生成文件
        try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                new java.io.FileOutputStream(outputFile), java.nio.charset.Charset.forName("GBK"))) {
            template.merge(context, writer);
        } catch (IOException e) {
            throw new RuntimeException("生成元数据文件失败: " + outputFile.getAbsolutePath(), e);
        }

        logger.info("元数据文件生成成功: {}", outputFile.getAbsolutePath());
    }

    /**
     * 初始化元数据配置（生成UUID、设置元数据字段等）
     */
    private void initMetadataConfig(BillConfig billConfig) {
        // 确保全局配置存在
        com.nc5.generator.config.GlobalConfig globalConfig = billConfig.getGlobalConfig();
        if (globalConfig == null) {
            globalConfig = new com.nc5.generator.config.GlobalConfig();
            billConfig.setGlobalConfig(globalConfig);
        }

        // 生成组件ID
        if (globalConfig.getComponentId() == null) {
            globalConfig.setComponentId(UUIDUtil.generateUUID());
        }

        // 生成主实体ID
        if (globalConfig.getMainEntityId() == null) {
            globalConfig.setMainEntityId(UUIDUtil.generateUUID());
        }

        // 为每个枚举生成ID
        for (EnumConfig enumConfig : globalConfig.getEnums()) {
            if (enumConfig.getId() == null) {
                enumConfig.setId(UUIDUtil.generateUUID());
            }

            // 为每个枚举项生成ID
            for (EnumConfig.EnumItem item : enumConfig.getItems()) {
                if (item.getId() == null) {
                    item.setId(UUIDUtil.generateUUID());
                }
            }
        }

        // 为每个字段生成ID，并设置元数据字段
        for (FieldConfig field : billConfig.getHeadFields()) {
            if (field.getId() == null) {
                field.setId(UUIDUtil.generateUUID());
            }

            // 设置数据类型ID - 检查是否是枚举类型
            if (field.getDataType() == null) {
                field.setDataType(getDataTypeId(field.getType(), field.getRefModelName(), globalConfig.getEnums()));
            }

            // 设置类型显示名称
            if (field.getTypeDisplayName() == null) {
                field.setTypeDisplayName(getTypeDisplayName(field.getType()));
            }

            // 设置参照模型名称（如果字段是参照类型）
            if ("Combo".equals(field.getUiType()) && field.getRefModelName() == null) {
                field.setRefModelName(inferRefModelName(field.getLabel()));
            }
        }

        // 生成各个参照连接的ID
        if (globalConfig.isEnablePubBillInterface()) {
            if (globalConfig.getRefPubBillId() == null) {
                globalConfig.setRefPubBillId(UUIDUtil.generateUUID());
            }
            if (globalConfig.getConnectionPubBillId() == null) {
                globalConfig.setConnectionPubBillId(UUIDUtil.generateUUID());
            }
        }

        if (globalConfig.isEnableUser()) {
            if (globalConfig.getRefUserId() == null) {
                globalConfig.setRefUserId(UUIDUtil.generateUUID());
            }
            if (globalConfig.getConnectionUserId() == null) {
                globalConfig.setConnectionUserId(UUIDUtil.generateUUID());
            }
        }

        if (globalConfig.isEnableBillStatus()) {
            if (globalConfig.getRefBillStatusId() == null) {
                globalConfig.setRefBillStatusId(UUIDUtil.generateUUID());
            }
            if (globalConfig.getConnectionBillStatusId() == null) {
                globalConfig.setConnectionBillStatusId(UUIDUtil.generateUUID());
            }
        }

        // 设置关键字段ID
        for (FieldConfig field : billConfig.getHeadFields()) {
            if (field.isPrimaryKey() && globalConfig.getPkFieldId() == null) {
                globalConfig.setPkFieldId(field.getId());
            }

            if ("billno".equals(field.getName()) && globalConfig.getBillNoFieldId() == null) {
                globalConfig.setBillNoFieldId(field.getId());
            }

            if ("corp".equals(field.getName()) && globalConfig.getCorpFieldId() == null) {
                globalConfig.setCorpFieldId(field.getId());
            }

            if ("vbusitype".equals(field.getName()) && globalConfig.getBusiTypeFieldId() == null) {
                globalConfig.setBusiTypeFieldId(field.getId());
            }

            if ("operatorid".equals(field.getName()) && globalConfig.getOperatorIdFieldId() == null) {
                globalConfig.setOperatorIdFieldId(field.getId());
            }

            if ("reviewer".equals(field.getName()) && globalConfig.getApproverFieldId() == null) {
                globalConfig.setApproverFieldId(field.getId());
            }

            if ("vstatus".equals(field.getName()) && globalConfig.getBillStatusFieldId() == null) {
                globalConfig.setBillStatusFieldId(field.getId());
            }

            if ("vbillstatus".equals(field.getName()) && globalConfig.getBillStatusFieldId() == null) {
                globalConfig.setBillStatusFieldId(field.getId());
            }

            if ("reviewnote".equals(field.getName()) && globalConfig.getApproveNoteFieldId() == null) {
                globalConfig.setApproveNoteFieldId(field.getId());
            }

            if ("reviewdate".equals(field.getName()) && globalConfig.getApproveDateFieldId() == null) {
                globalConfig.setApproveDateFieldId(field.getId());
            }

            if ("billdate".equals(field.getName()) && globalConfig.getBillDateFieldId() == null) {
                globalConfig.setBillDateFieldId(field.getId());
            }

            if ("createdate".equals(field.getName()) && globalConfig.getBillDateFieldId() == null) {
                globalConfig.setBillDateFieldId(field.getId());
            }

            if ("vbilltype".equals(field.getName()) && globalConfig.getBillTypeFieldId() == null) {
                globalConfig.setBillTypeFieldId(field.getId());
            }
        }
    }

    /**
     * 根据Java类型和参照模型获取数据类型ID
     */
    private String getDataTypeId(String javaType, String refModelName, List<EnumConfig> enums) {
        if (javaType == null) {
            return "BS000010000100001001"; // String
        }

        // 处理枚举类型
        if (javaType.toLowerCase().startsWith("enum:")) {
            String enumName = javaType.substring(5); // 去掉 "enum:" 前缀
            // 查找对应的枚举配置
            for (EnumConfig enumConfig : enums) {
                if (enumName.equals(enumConfig.getName())) {
                    return enumConfig.getId(); // 返回动态生成的枚举ID
                }
            }
        }

        switch (javaType.toLowerCase()) {
            case "string":
                return "BS000010000100001001";
            case "integer":
            case "int":
                return "BS000010000100001002";
            case "double":
            case "decimal":
                return "BS000010000100001003";
            case "ufdate":
                return "BS000010000100001033";
            case "ufboolean":
                return "BS000010000100001032";
            case "ufdouble":
                return "BS000010000100001004";
            case "ufid":
                return "BS000010000100001051";
            case "enum":
                return "BS000010000100001001"; // 如果没有枚举配置，默认使用String
            default:
                return "BS000010000100001001";
        }
    }

    /**
     * 根据Java类型获取类型显示名称
     */
    private String getTypeDisplayName(String javaType) {
        if (javaType == null) {
            return "String";
        }

        switch (javaType.toLowerCase()) {
            case "string":
                return "String";
            case "integer":
            case "int":
                return "Integer";
            case "double":
                return "Double";
            case "ufdate":
                return "UFDate";
            case "ufboolean":
                return "UFBoolean";
            case "ufdouble":
                return "UFDouble";
            case "ufid":
                return "UFID";
            case "enum":
                return "状态";
            default:
                return "String";
        }
    }

    /**
     * 根据字段标签推断参照模型名称
     */
    private String inferRefModelName(String label) {
        if (label == null) {
            return null;
        }

        // 根据字段中文名推断参照模型
        if (label.contains("操作员") || label.contains("制单人") || label.contains("审批人")) {
            return "操作员";
        } else if (label.contains("公司") || label.contains("组织")) {
            return "公司目录(集团)";
        } else if (label.contains("业务类型")) {
            return "业务类型";
        } else if (label.contains("单据类型")) {
            return "影响因素单据类型";
        } else if (label.contains("部门")) {
            return "部门";
        } else if (label.contains("物料") || label.contains("产品")) {
            return "物料基本(集团)";
        }

        return null;
    }
}
