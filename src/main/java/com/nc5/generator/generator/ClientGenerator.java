package com.nc5.generator.generator;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.template.TemplateContext;
import com.nc5.generator.template.TemplateEngine;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * 客户端代码生成器
 */
public class ClientGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ClientGenerator.class);

    private TemplateEngine templateEngine;

    private String outputDir;

    public ClientGenerator(String outputDir) {
        this.templateEngine = new TemplateEngine();
        this.outputDir = outputDir;
    }

    /**
     * 生成所有客户端代码
     */
    public void generate(BillConfig billConfig) throws IOException {
        logger.info("开始生成客户端代码");

        // 生成Controller
        generateController(billConfig);

        // 生成IPrivateBtn
        generateIPrivateBtn(billConfig);

        // 生成ClientUI
        generateClientUI(billConfig);

        // 生成BusinessAction
        generateBusinessAction(billConfig);

        // 生成Delegator
        generateDelegator(billConfig);

        // 生成EventHandler
        generateEventHandler(billConfig);

        // 生成RefModel
        generateRefModel(billConfig);

        logger.info("客户端代码生成完成");
    }

    /**
     * 生成Controller
     */
    private void generateController(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        // 根据单据类型选择模板
        String templatePath;
        if (billConfig.isSingleBill()) {
            templatePath = "templates/client/Controller_Single.vm";
            logger.info("使用单表头模板生成Controller");
        } else if (billConfig.isMultiBill()) {
            templatePath = "templates/client/Controller_Multi.vm";
            logger.info("使用多表体模板生成Controller");
        } else {
            templatePath = "templates/client/Controller_Single.vm";
            logger.info("使用默认单表头模板生成Controller");
        }

        String content = templateEngine.render(templatePath, context);

        // 输出路径: src/client/nc/ui/{module}/{billCode}/{billCode}Controller.java
        String outputPath = String.format("%s/src/client/nc/ui/%s/%s/%sController.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower(),
                billConfig.getClassName());

        writeToFile(outputPath, content);
        logger.info("生成Controller: {}", outputPath);
    }

    /**
     * 生成IPrivateBtn
     */
    private void generateIPrivateBtn(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        String content = templateEngine.render("templates/client/IPrivateBtn.vm", context);

        // 输出路径: src/client/nc/ui/{module}/{billCode}/{billCode}IPrivateBtn.java
        String outputPath = String.format("%s/src/client/nc/ui/%s/%s/%sIPrivateBtn.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower(),
                billConfig.getClassName());

        writeToFile(outputPath, content);
        logger.info("生成IPrivateBtn: {}", outputPath);
    }


    /**
     * 生成ClientUI
     */
    private void generateClientUI(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        // 根据单据类型选择模板
        String templatePath;
        if (billConfig.isSingleBill()) {
            templatePath = "templates/client/ClientUI_Single.vm";
            logger.info("使用单表头模板生成ClientUI");
        } else if (billConfig.isMultiBill()) {
            templatePath = "templates/client/ClientUI_Multi.vm";
            logger.info("使用多表体模板生成ClientUI");
        } else {
            templatePath = "templates/client/ClientUI_Single.vm";
            logger.info("使用默认单表头模板生成ClientUI");
        }

        String content = templateEngine.render(templatePath, context);

        // 输出路径: src/client/nc/ui/{module}/{billCode}/{billCode}ClientUI.java
        String outputPath = String.format("%s/src/client/nc/ui/%s/%s/%sClientUI.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower(),
                billConfig.getClassName());

        writeToFile(outputPath, content);
        logger.info("生成ClientUI: {}", outputPath);
    }

    /**
     * 生成BusinessAction
     */
    private void generateBusinessAction(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        String content = templateEngine.render("templates/client/BusinessAction.vm", context);

        // 输出路径: src/client/nc/ui/{module}/{billCode}/{billCode}BusinessAction.java
        String outputPath = String.format("%s/src/client/nc/ui/%s/%s/%sBusinessAction.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower(),
                billConfig.getClassName());

        writeToFile(outputPath, content);
        logger.info("生成BusinessAction: {}", outputPath);
    }

    /**
     * 生成Delegator
     */
    private void generateDelegator(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        String content = templateEngine.render("templates/client/Delegator.vm", context);

        // 输出路径: src/client/nc/ui/{module}/{billCode}/{billCode}Delegator.java
        String outputPath = String.format("%s/src/client/nc/ui/%s/%s/%sDelegator.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower(),
                billConfig.getClassName());

        writeToFile(outputPath, content);
        logger.info("生成Delegator: {}", outputPath);
    }

    /**
     * 生成EventHandler
     */
    private void generateEventHandler(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        String content = templateEngine.render("templates/client/EventHandler.vm", context);

        // 输出路径: src/client/nc/ui/{module}/{billCode}/{billCode}EventHandler.java
        String outputPath = String.format("%s/src/client/nc/ui/%s/%s/%sEventHandler.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower(),
                billConfig.getClassName());

        writeToFile(outputPath, content);
        logger.info("生成EventHandler: {}", outputPath);
    }

    /**
     * 生成RefModel
     */
    private void generateRefModel(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        String content = templateEngine.render("templates/client/RefModel.vm", context);

        // 输出路径: src/client/nc/ui/{module}/{billCode}/{billCode}RefModel.java
        String outputPath = String.format("%s/src/client/nc/ui/%s/%s/%sRefModel.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower(),
                billConfig.getClassName());

        writeToFile(outputPath, content);
        logger.info("生成RefModel: {}", outputPath);
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
