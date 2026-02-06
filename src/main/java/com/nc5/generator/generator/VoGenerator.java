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
 * VO代码生成器
 */
public class VoGenerator {

    private static final Logger logger = LoggerFactory.getLogger(VoGenerator.class);

    private TemplateEngine templateEngine;

    private String outputDir;

    public VoGenerator(String outputDir) {
        this.templateEngine = new TemplateEngine();
        this.outputDir = outputDir;
    }

    /**
     * 生成所有VO代码
     */
    public void generate(BillConfig billConfig) throws IOException {
        logger.info("开始生成VO代码");

        // 生成HVO
        generateHVO(billConfig);

        // 生成BVO（仅当多表体类型时）
        if (billConfig.isMultiBill()) {
            if (billConfig.getBodyCodeList().size() > 0) {
                // 多表体：生成多个BVO
                generateMultipleBVO(billConfig);
            } else {
                // 单表体：生成一个BVO
                generateSingleBVO(billConfig);
            }
        }

        // 生成AggVO
        generateAggVO(billConfig);

        logger.info("VO代码生成完成");
    }

    /**
     * 生成HVO（表头VO）
     */
    private void generateHVO(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        // 使用模板选择器获取模板路径
        String templatePath = TemplateSelector.getHVOTemplate(billConfig);
        String content = templateEngine.render(templatePath, context);

        // 输出路径: src/public/nc/vo/{module}/{billCode}/{billCode}HVO.java
        String outputPath = String.format("%s/src/public/nc/vo/%s/%s/%sHVO.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower(),
                billConfig.getClassName());

        writeToFile(outputPath, content);
        logger.info("生成HVO: {}", outputPath);
    }

    /**
     * 生成BVO（单表体VO）
     */
    private void generateSingleBVO(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        // 使用模板选择器获取模板路径
        String templatePath = TemplateSelector.getBVOTemplate(billConfig);
        String content = templateEngine.render(templatePath, context);

        // 输出路径: src/public/nc/vo/{module}/{billCode}/{bodyCode}VO.java
        String outputPath = String.format("%s/src/public/nc/vo/%s/%s/%sVO.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower(),
                billConfig.getBodyCode());

        writeToFile(outputPath, content);
        logger.info("生成BVO: {}", outputPath);
    }

    /**
     * 生成多个BVO（多表体VO）
     */
    private void generateMultipleBVO(BillConfig billConfig) throws IOException {
        for (String bodyCode : billConfig.getBodyCodeList()) {
            TemplateContext context = new TemplateContext();
            context.setBillConfig(billConfig);
            context.put("currentBodyCode", bodyCode);

            // 使用模板选择器获取模板路径
            String templatePath = TemplateSelector.getBVOTemplate(billConfig);
            String content = templateEngine.render(templatePath, context);

            // 输出路径: src/public/nc/vo/{module}/{billCode}/{bodyCode}VO.java
            String outputPath = String.format("%s/src/public/nc/vo/%s/%s/%sVO.java",
                    outputDir,
                    billConfig.getModule(),
                    billConfig.getClassNameLower(),
                    bodyCode);

            writeToFile(outputPath, content);
            logger.info("生成BVO: {}", outputPath);
        }
    }

    /**
     * 生成AggVO（聚合VO）
     */
    private void generateAggVO(BillConfig billConfig) throws IOException {
        TemplateContext context = new TemplateContext();
        context.setBillConfig(billConfig);

        // 使用模板选择器获取模板路径
        String templatePath = TemplateSelector.getAggVOTemplate(billConfig);
        String content = templateEngine.render(templatePath, context);

        // 输出路径: src/public/nc/vo/{module}/{billCode}/Agg{billCode}VO.java
        String outputPath = String.format("%s/src/public/nc/vo/%s/%s/Agg%sVO.java",
                outputDir,
                billConfig.getModule(),
                billConfig.getClassNameLower(),
                billConfig.getClassName());

        writeToFile(outputPath, content);
        logger.info("生成AggVO: {}", outputPath);
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
