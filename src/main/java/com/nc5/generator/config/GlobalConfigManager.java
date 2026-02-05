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

    private static final String GLOBAL_CONFIG_FILE = "config/global_cfg.ini";
    private static final String DEFAULT_AUTHOR = "Flynn Chen";

    private GlobalConfig globalConfig;
    private File globalConfigFile;

    public GlobalConfigManager() {
        this.globalConfigFile = new File(System.getProperty("user.dir"), GLOBAL_CONFIG_FILE);
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

            globalConfig = new GlobalConfig();

            // 项目配置
            globalConfig.setSourcePath(props.getProperty("project.sourcePath", ""));
            globalConfig.setOutputDir(props.getProperty("project.outputDir",
                new File(System.getProperty("user.dir"), "output").getAbsolutePath()));
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
     * 创建默认全局配置
     */
    private void createDefaultGlobalConfig() {
        globalConfig = new GlobalConfig();

        // 设置默认值
        globalConfig.setSourcePath("");
        globalConfig.setOutputDir(new File(System.getProperty("user.dir"), "output").getAbsolutePath());
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

            Properties props = new Properties();

            // 项目配置
            props.setProperty("project.sourcePath", getStringValue(globalConfig.getSourcePath()));
            props.setProperty("project.outputDir", getStringValue(globalConfig.getOutputDir()));
            props.setProperty("project.author", getStringValue(globalConfig.getAuthor()));

            // 生成选项
            props.setProperty("generate.syncAfterGenerate", String.valueOf(globalConfig.isSyncAfterGenerate()));
            props.setProperty("generate.generateClient", String.valueOf(globalConfig.isGenerateClient()));
            props.setProperty("generate.generateBusiness", String.valueOf(globalConfig.isGenerateBusiness()));
            props.setProperty("generate.generateMetadata", String.valueOf(globalConfig.isGenerateMetadata()));

            // 注意：元数据配置（metadata.*, reference.*, connection.*）属于单据级别配置，
            // 不存储在全局INI配置中，而是存储在各自的单据XML文件中

            // 写入文件（带注释）
            try (FileOutputStream fos = new FileOutputStream(globalConfigFile)) {
                props.store(fos, "NC5 Code Generator - Global Configuration");
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
