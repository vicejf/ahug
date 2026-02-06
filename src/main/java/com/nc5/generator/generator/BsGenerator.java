package com.nc5.generator.generator;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.template.TemplateContext;
import com.nc5.generator.template.TemplateEngine;
import com.nc5.generator.template.TemplateSelector;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * 业务逻辑代码生成器
 */
public class BsGenerator {

    private static final Logger logger = LoggerFactory.getLogger(BsGenerator.class);

    private TemplateEngine templateEngine;

    private String outputDir;

    public BsGenerator(String outputDir) {
        this.templateEngine = new TemplateEngine();
        this.outputDir = outputDir;
    }

    /**
     * 生成所有业务逻辑代码
     */
    public void generate(BillConfig billConfig) throws IOException {
        logger.info("开始生成业务逻辑代码");

        // 生成InsertAction
        generateInsertAction(billConfig);

        // 生成UpdateAction
        generateUpdateAction(billConfig);

        // 生成DeleteAction
        generateDeleteAction(billConfig);

        // 生成SaveAction
        generateSaveAction(billConfig);

        // 生成QueryAction
        generateQueryAction(billConfig);

        // 生成发布动作类
        generatePubActions(billConfig);

        logger.info("业务逻辑代码生成完成");
    }

    /**
     * 生成InsertAction
     */
    private void generateInsertAction(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        // 使用模板选择器获取模板路径
        String templatePath = TemplateSelector.getInsertActionTemplate(billConfig);
        String content = templateEngine.render(templatePath, context);

        // 输出路径: src/private/nc/bs/{module}/{billCode}/InsertAction.java
        String outputPath = String.format("%s/src/private/nc/bs/%s/%s/InsertAction.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower());

        writeToFile(outputPath, content);
        logger.info("生成InsertAction: {}", outputPath);
    }

    /**
     * 生成UpdateAction
     */
    private void generateUpdateAction(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        // 使用模板选择器获取模板路径
        String templatePath = TemplateSelector.getUpdateActionTemplate(billConfig);
        String content = templateEngine.render(templatePath, context);

        // 输出路径: src/private/nc/bs/{module}/{billCode}/UpdateAction.java
        String outputPath = String.format("%s/src/private/nc/bs/%s/%s/UpdateAction.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower());

        writeToFile(outputPath, content);
        logger.info("生成UpdateAction: {}", outputPath);
    }

    /**
     * 生成DeleteAction
     */
    private void generateDeleteAction(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        // 使用模板选择器获取模板路径
        String templatePath = TemplateSelector.getDeleteActionTemplate(billConfig);
        String content = templateEngine.render(templatePath, context);

        // 输出路径: src/private/nc/bs/{module}/{billCode}/DeleteAction.java
        String outputPath = String.format("%s/src/private/nc/bs/%s/%s/DeleteAction.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower());

        writeToFile(outputPath, content);
        logger.info("生成DeleteAction: {}", outputPath);
    }

    /**
     * 生成SaveAction
     */
    private void generateSaveAction(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        // 使用模板选择器获取模板路径
        String templatePath = TemplateSelector.getSaveActionTemplate(billConfig);
        String content = templateEngine.render(templatePath, context);

        // 输出路径: src/public/nc/action/{module}/{billCode}/N_{billCode}_SAVE.java
        String outputPath = String.format("%s/src/public/nc/action/%s/%s/N_%s_SAVE.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower(),
                billConfig.getClassName());

        writeToFile(outputPath, content);
        logger.info("生成SaveAction: {}", outputPath);
    }

    /**
     * 生成QueryAction
     */
    private void generateQueryAction(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        // 使用模板选择器获取模板路径
        String templatePath = TemplateSelector.getQueryActionTemplate(billConfig);
        String content = templateEngine.render(templatePath, context);

        // 输出路径: src/private/nc/bs/{module}/{billCode}/QueryAction.java
        String outputPath = String.format("%s/src/private/nc/bs/%s/%s/QueryAction.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower());

        writeToFile(outputPath, content);
        logger.info("生成QueryAction: {}", outputPath);
    }

    /**
     * 生成发布动作类
     */
    private void generatePubActions(BillConfig billConfig) throws IOException {
        logger.info("开始生成发布动作类");
        
        // 定义发布动作配置：动作名称、模板文件名
        String[][] pubActions = {
            {"DELETE", "PubAction_DELETE.vm"},
            {"EDIT", "PubAction_EDIT.vm"},
            {"FREEZE", "PubAction_Simple.vm"},
            {"UNFREEZE", "PubAction_Simple.vm"},
            {"WRITE", "PubAction_WRITE.vm"},
            {"WRITEBATCH", "PubAction_WRITEBATCH.vm"},
            {"SAVE", "PubAction_SAVE.vm"},
            {"APPROVE", "PubAction_Approve.vm"}
        };
        
        for (String[] action : pubActions) {
            String actionName = action[0];
            String templateFile = action[1];
            
            generatePubAction(billConfig, actionName, templateFile);
        }
        
        logger.info("发布动作类生成完成");
    }
    
    /**
     * 生成单个发布动作类
     */
    private void generatePubAction(BillConfig billConfig, String actionName, String templateFile) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);
        
        // 使用模板选择器获取模板路径
        String templatePath = TemplateSelector.getPubActionTemplate(billConfig, templateFile);
        
        String content = templateEngine.render(templatePath, context);
        
        // 输出路径: src/private/nc/bs/pub/action/N_{BILLCODE}_{ACTIONNAME}.java
        String outputPath = String.format("%s/src/private/nc/bs/pub/action/N_%s_%s.java",
                outputDir,
                billConfig.getClassName(),
                actionName);
        
        writeToFile(outputPath, content);
        logger.info("生成发布动作类: N_{}{}", billConfig.getClassName(), actionName);
    }

    /**
     * 写入文件
     */
    private void writeToFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        FileUtils.writeStringToFile(file, content, "GBK");
    }
}
