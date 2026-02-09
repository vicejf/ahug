package com.nc5.generator.template;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.config.BillType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模板选择器
 * 根据单据类型选择对应的模板路径
 */
public class TemplateSelector {

    private static final Logger logger = LoggerFactory.getLogger(TemplateSelector.class);

    private static final String TEMPLATES_BASE = "templates/";

    /**
     * 获取单据类型
     */
    public static BillType getBillType(BillConfig billConfig) {
        if (billConfig.isArchiveBill()) {
            return BillType.ARCHIVE;
        } else if (billConfig.isMultiBill()) {
            return BillType.MULTI;
        } else {
            return BillType.SINGLE;
        }
    }

    /**
     * 获取Controller模板路径
     */
    public static String getControllerTemplate(BillConfig billConfig) {
        BillType billType = getBillType(billConfig);
        String templatePath = TEMPLATES_BASE + "client/by-type/" + billType.getCode() + "/Controller_" + 
                              capitalize(billType.getCode()) + ".vm";
        logger.info("选择Controller模板: {} ({})", templatePath, billType.getDescription());
        return templatePath;
    }

    /**
     * 获取ClientUI模板路径
     */
    public static String getClientUITemplate(BillConfig billConfig) {
        BillType billType = getBillType(billConfig);
        String templatePath = TEMPLATES_BASE + "client/by-type/" + billType.getCode() + "/ClientUI_" + 
                              capitalize(billType.getCode()) + ".vm";
        logger.info("选择ClientUI模板: {} ({})", templatePath, billType.getDescription());
        return templatePath;
    }

    /**
     * 获取QueryAction模板路径
     */
    public static String getQueryActionTemplate(BillConfig billConfig) {
        BillType billType = getBillType(billConfig);
        String templatePath = TEMPLATES_BASE + "bs/by-type/" + billType.getCode() + "/QueryAction_" + 
                              capitalize(billType.getCode()) + ".vm";
        logger.info("选择QueryAction模板: {} ({})", templatePath, billType.getDescription());
        return templatePath;
    }

    /**
     * 获取HVO模板路径
     */
    public static String getHVOTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "vo/common/HVO.vm";
        logger.info("选择HVO模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取BVO模板路径
     */
    public static String getBVOTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "vo/common/BVO.vm";
        logger.info("选择BVO模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取AggVO模板路径
     */
    public static String getAggVOTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "vo/common/AggVO.vm";
        logger.info("选择AggVO模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取Impl模板路径
     */
    public static String getImplTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "impl/by-type/ServerImpl.vm";
        logger.info("选择Impl模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取Itf模板路径
     */
    public static String getItfTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "itf/by-type/IServer.vm";
        logger.info("选择Itf模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取Rule模板路径
     */
    public static String getRuleTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "rule/by-type/Rule.vm";
        logger.info("选择Rule模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取BusinessAction模板路径
     */
    public static String getBusinessActionTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "client/common/BusinessAction.vm";
        logger.info("选择BusinessAction模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取Delegator模板路径
     */
    public static String getDelegatorTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "client/common/Delegator.vm";
        logger.info("选择Delegator模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取EventHandler模板路径
     */
    public static String getEventHandlerTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "client/common/EventHandler.vm";
        logger.info("选择EventHandler模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取IPrivateBtn模板路径
     */
    public static String getIPrivateBtnTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "client/common/IPrivateBtn.vm";
        logger.info("选择IPrivateBtn模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取RefModel模板路径
     */
    public static String getRefModelTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "client/common/RefModel.vm";
        logger.info("选择RefModel模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取DeleteAction模板路径
     */
    public static String getDeleteActionTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "bs/common/DeleteAction.vm";
        logger.info("选择DeleteAction模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取InsertAction模板路径
     */
    public static String getInsertActionTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "bs/common/InsertAction.vm";
        logger.info("选择InsertAction模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取SaveAction模板路径
     */
    public static String getSaveActionTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "bs/common/SaveAction.vm";
        logger.info("选择SaveAction模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取UpdateAction模板路径
     */
    public static String getUpdateActionTemplate(BillConfig billConfig) {
        String templatePath = TEMPLATES_BASE + "bs/common/UpdateAction.vm";
        logger.info("选择UpdateAction模板: {}", templatePath);
        return templatePath;
    }

    /**
     * 获取PubAction模板路径
     */
    public static String getPubActionTemplate(BillConfig billConfig, String actionType) {
        String templatePath = TEMPLATES_BASE + "bs/common/PubAction_" + actionType + ".vm";
        logger.info("选择PubAction模板: {} ({})", templatePath, actionType);
        return templatePath;
    }

    /**
     * 获取元数据模板路径
     */
    public static String getMetadataTemplate(BillConfig billConfig) {
        BillType billType = getBillType(billConfig);
        String templatePath;
        
        switch (billType) {
            case SINGLE:
            case ARCHIVE:
                templatePath = TEMPLATES_BASE + "METADATA/common/SingleHeadBMF.vm";
                break;
            case MULTI:
                // 临时支持多表体元数据生成，使用单表头模板
                logger.info("多表体单据 {} 使用单表头元数据模板", billConfig.getBillCode());
                templatePath = TEMPLATES_BASE + "METADATA/common/SingleHeadBMF.vm";
                break;
            default:
                logger.warn("未知的单据类型: {}, 使用单表头元数据模板", billType);
                templatePath = TEMPLATES_BASE + "METADATA/common/SingleHeadBMF.vm";
        }
        
        logger.info("选择元数据模板: {} ({})", templatePath, billType.getDescription());
        return templatePath;
    }

    /**
     * 首字母大写
     */
    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
