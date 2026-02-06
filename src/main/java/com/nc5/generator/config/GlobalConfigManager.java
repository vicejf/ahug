package com.nc5.generator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * 全局配置管理器
 * 负责全局配置的加载、创建和管理（使用INI格式）
 */
public class GlobalConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(GlobalConfigManager.class);

    private static final String GLOBAL_CONFIG_FILE = "global_cfg.ini";
    private static final String DEFAULT_AUTHOR = "Flynn Chen";

    private GlobalConfig globalConfig;
    private File globalConfigFile;

    public GlobalConfigManager() {
        String appDataDir = System.getenv("APPDATA");
        File configDir = new File(appDataDir, "ahug/config");
        this.globalConfigFile = new File(configDir, GLOBAL_CONFIG_FILE);
    }

    /**
     * 初始化全局配置
     * 如果配置文件不存在则创建默认配置
     */
    public GlobalConfig initialize() {
        if (globalConfigFile.exists()) {
            loadGlobalConfig();
            logger.info("已加载全局配置: {}", globalConfigFile.getAbsolutePath());
        } else {
            createDefaultGlobalConfig();
            logger.info("已创建默认全局配置: {}", globalConfigFile.getAbsolutePath());
        }
        return globalConfig;
    }

    /**
     * 加载全局配置文件（INI格式）
     * 注意：只加载真正的全局配置（project.*, generate.*），
     * 元数据配置（metadata.*, reference.*, connection.*）属于单据级别，存储在单据XML中
     */
    private void loadGlobalConfig() {
        try {
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(globalConfigFile)) {
                props.load(fis);
            }

            // 修复 Properties.load() 导致的反斜杠被当作转义字符处理的问题
            // D:\\Documents\\workspace\\test-output 会变成 D:Documentsworkspace est-output
            // 需要将每个 \ 恢复为 \\
            props = fixBackslashInLoad(props);

            globalConfig = new GlobalConfig();

            // 获取 APPDATA 目录作为默认输出目录
            String appDataDir = System.getenv("APPDATA");
            String defaultOutputDir = new File(new File(appDataDir, "ahug"), "output").getAbsolutePath();

            // 项目配置
            globalConfig.setSourcePath(props.getProperty("project.sourcePath", ""));
            globalConfig.setOutputDir(props.getProperty("project.outputDir", defaultOutputDir));
            globalConfig.setAuthor(props.getProperty("project.author", DEFAULT_AUTHOR));

            // 生成选项
            globalConfig.setSyncAfterGenerate(
                Boolean.parseBoolean(props.getProperty("generate.syncAfterGenerate", "true")));
            globalConfig.setGenerateClient(
                Boolean.parseBoolean(props.getProperty("generate.generateClient", "true")));
            globalConfig.setGenerateBusiness(
                Boolean.parseBoolean(props.getProperty("generate.generateBusiness", "true")));
            globalConfig.setGenerateMetadata(
                Boolean.parseBoolean(props.getProperty("generate.generateMetadata", "false")));

            // 注意：元数据配置（metadata.*, reference.*, connection.*）属于单据级别配置，
            // 从单据XML文件中读取，不存储在全局INI配置中

            logger.debug("全局配置加载成功");
        } catch (Exception e) {
            logger.error("加载全局配置失败，将创建默认配置", e);
            createDefaultGlobalConfig();
        }
    }

    /**
     * 手动读取配置文件，避免 Properties.load() 的转义问题
     * Properties.load() 会将反斜杠作为转义字符，导致 Windows 路径中的 \ 被错误处理
     */
    private Properties fixBackslashInLoad(Properties props) {
        Properties fixed = new Properties();

        // 直接从文件读取并手动解析，避免 Properties.load() 的转义问题
        try (BufferedReader reader = new BufferedReader(new FileReader(globalConfigFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                // 跳过注释和空行
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                // 解析键值对
                int separatorIndex = line.indexOf('=');
                if (separatorIndex > 0) {
                    String key = line.substring(0, separatorIndex).trim();
                    String value = line.substring(separatorIndex + 1).trim();
                    fixed.setProperty(key, value);
                }
            }
        } catch (IOException e) {
            logger.error("读取配置文件失败", e);
            return props;
        }

        return fixed;
    }

    /**
     * 创建默认全局配置
     */
    private void createDefaultGlobalConfig() {
        globalConfig = new GlobalConfig();

        // 获取 APPDATA 目录作为默认输出目录
        String appDataDir = System.getenv("APPDATA");
        String defaultOutputDir = new File(new File(appDataDir, "ahug"), "output").getAbsolutePath();

        // 设置默认值
        globalConfig.setSourcePath("");
        globalConfig.setOutputDir(defaultOutputDir);
        globalConfig.setAuthor(DEFAULT_AUTHOR);
        globalConfig.setSyncAfterGenerate(true);
        globalConfig.setGenerateClient(true);
        globalConfig.setGenerateBusiness(true);
        globalConfig.setGenerateMetadata(false);

        // 元数据生成开关默认值
        globalConfig.setEnablePubBillInterface(true);
        globalConfig.setEnableUser(true);
        globalConfig.setEnableBillStatus(true);

        // 保存到文件
        saveGlobalConfig();
    }

    /**
     * 保存全局配置到文件（INI格式）
     * 注意：只保存真正的全局配置（project.*, generate.*），
     * 元数据配置（metadata.*, reference.*, connection.*）属于单据级别，存储在单据XML中
     */
    public void saveGlobalConfig() {
        if (globalConfig == null) {
            logger.warn("全局配置为空，无法保存");
            return;
        }

        try {
            // 确保目录存在
            File parentDir = globalConfigFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 直接写入文件，避免 Properties.store() 的转义问题
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(globalConfigFile))) {
                // 写入注释
                writer.write("#NC5 Code Generator - Global Configuration\n");
                writer.write("#" + new java.util.Date() + "\n\n");

                // 写入生成选项
                writer.write("generate.generateBusiness=" + globalConfig.isGenerateBusiness() + "\n");
                writer.write("generate.generateClient=" + globalConfig.isGenerateClient() + "\n");
                writer.write("generate.generateMetadata=" + globalConfig.isGenerateMetadata() + "\n");
                writer.write("generate.syncAfterGenerate=" + globalConfig.isSyncAfterGenerate() + "\n");

                // 写入项目配置
                writer.write("project.author=" + getStringValue(globalConfig.getAuthor()) + "\n");
                writer.write("project.outputDir=" + getStringValue(globalConfig.getOutputDir()) + "\n");
                writer.write("project.sourcePath=" + getStringValue(globalConfig.getSourcePath()) + "\n");
            }

            logger.info("全局配置已保存: {}", globalConfigFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("保存全局配置失败", e);
        }
    }

    private String getStringValue(String value) {
        return value != null ? value : "";
    }

    /**
     * 获取全局配置
     */
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    /**
     * 设置全局配置
     */
    public void setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    /**
     * 获取全局配置文件
     */
    public File getGlobalConfigFile() {
        return globalConfigFile;
    }

    /**
     * 检查全局配置文件是否存在
     */
    public boolean exists() {
        return globalConfigFile != null && globalConfigFile.exists();
    }
}
