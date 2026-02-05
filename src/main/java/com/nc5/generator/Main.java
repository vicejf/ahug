package com.nc5.generator;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.config.XmlConfigParser;
import com.nc5.generator.generator.CodeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * NC5代码生成器 - 命令行入口
 *
 * 使用方法：
 *   java -jar nc5-code-generator.jar <配置文件路径> [输出目录]
 *
 * 示例：
 *   java -jar nc5-code-generator.jar config/bill-config.xml output
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        CommandLineArguments cliArgs = parseArguments(args);

        if (cliArgs == null) {
            System.exit(1);
        }

        try {
            logger.info("========================================");
            logger.info("NC5代码生成器 - 命令行模式");
            logger.info("========================================");
            logger.info("配置文件: {}", cliArgs.configFile);
            logger.info("输出目录: {}", cliArgs.outputDir);

            // 解析配置文件
            BillConfig billConfig = parseConfig(cliArgs.configFile);
            logger.info("已加载配置: {} - {}", billConfig.getBillCode(), billConfig.getBillName());

            // 执行代码生成
            executeGeneration(cliArgs.outputDir, billConfig);

            logger.info("========================================");
            logger.info("代码生成成功！");
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("代码生成失败", e);
            System.exit(1);
        }
    }

    /**
     * 解析命令行参数
     */
    private static CommandLineArguments parseArguments(String[] args) {
        if (args.length == 0) {
            logger.error("错误: 未指定配置文件路径");
            logger.info("\n用法: java -jar nc5-code-generator.jar <配置文件路径> [输出目录]");
            logger.info("\n参数说明:");
            logger.info("  配置文件路径   必需，XML格式的配置文件");
            logger.info("  输出目录       可选，默认为'output'目录");
            logger.info("\n示例:");
            logger.info("  java -jar nc5-code-generator.jar config/bill-config.xml");
            logger.info("  java -jar nc5-code-generator.jar config/bill-config.xml output");
            return null;
        }

        String configPath = args[0];
        String outputDir = args.length > 1 ? args[1] : "output";

        File configFile = new File(configPath);
        if (!configFile.exists()) {
            logger.error("错误: 配置文件不存在: {}", configPath);
            return null;
        }

        if (!configFile.isFile()) {
            logger.error("错误: 指定的路径不是文件: {}", configPath);
            return null;
        }

        return new CommandLineArguments(configFile, outputDir);
    }

    /**
     * 解析配置文件
     */
    private static BillConfig parseConfig(File configFile) {
        try {
            XmlConfigParser parser = new XmlConfigParser();
            return parser.parse(configFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("解析配置文件失败: " + configFile.getName(), e);
        }
    }

    /**
     * 执行代码生成
     */
    private static void executeGeneration(String outputDir, BillConfig billConfig) {
        CodeGenerator codeGenerator = new CodeGenerator(outputDir);
        try {
            codeGenerator.generate(billConfig);
        } catch (Exception e) {
            throw new RuntimeException("代码生成执行失败", e);
        }
    }

    /**
     * 命令行参数封装类
     */
    private static class CommandLineArguments {
        final File configFile;
        final String outputDir;

        CommandLineArguments(File configFile, String outputDir) {
            this.configFile = configFile;
            this.outputDir = outputDir;
        }
    }
}
