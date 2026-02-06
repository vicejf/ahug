package com.nc5.generator.fx.controller;

import com.nc5.generator.fx.model.BillConfigModel;
import com.nc5.generator.fx.CodeGeneratorApp;
import com.nc5.generator.config.ConfigManager;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * 生成选项标签页控制器
 */
public class GenerateController {

    @FXML private CheckBox generateClientCheck;
    @FXML private CheckBox generateBusinessCheck;
    @FXML private CheckBox generateMetadataCheck;

    // 输出配置相关
    @FXML private TextField outputDirField;
    @FXML private TextField projectSrcField;
    @FXML private CheckBox syncAfterGenerate;

    private BillConfigModel billConfigModel;
    
    // 配置管理器
    private final ConfigManager configManager = ConfigManager.getInstance();

    @FXML
    public void initialize() {
        // 绑定UI控件到配置管理器（单向绑定，避免循环更新）
        outputDirField.textProperty().bindBidirectional(configManager.outputDirProperty());
        projectSrcField.textProperty().bindBidirectional(configManager.sourcePathProperty());
        syncAfterGenerate.selectedProperty().bindBidirectional(configManager.syncAfterGenerateProperty());
        generateClientCheck.selectedProperty().bindBidirectional(configManager.generateClientProperty());
        generateBusinessCheck.selectedProperty().bindBidirectional(configManager.generateBusinessProperty());
        generateMetadataCheck.selectedProperty().bindBidirectional(configManager.generateMetadataProperty());
        
        // 添加配置变更监听器：任意配置变化后自动保存全局配置
        configManager.addChangeListener(this::saveGlobalConfig);
    }

    /**
     * 设置数据模型并绑定
     */
    public void setBillConfigModel(BillConfigModel model) {
        this.billConfigModel = model;

        // 将 ConfigManager 的属性同步到 billConfigModel.globalConfigModel
        if (model != null) {
            // 使用 GlobalConfigModel 承载全局配置，避免在 BillConfigModel 中展开具体字段
            model.getGlobalConfigModel().setGenerateClient(configManager.isGenerateClient());
            model.getGlobalConfigModel().setGenerateBusiness(configManager.isGenerateBusiness());
            model.getGlobalConfigModel().setGenerateMetadata(configManager.isGenerateMetadata());
            model.getGlobalConfigModel().setAuthor(configManager.getAuthor());
            model.getGlobalConfigModel().setSourcePath(configManager.getSourcePath());
            model.getGlobalConfigModel().setOutputDir(configManager.getOutputDir());
            model.getGlobalConfigModel().setSyncAfterGenerate(configManager.isSyncAfterGenerate());

            // 监听 ConfigManager 的变化并同步到 GlobalConfigModel
            configManager.outputDirProperty().addListener((obs, oldVal, newVal) -> {
                model.getGlobalConfigModel().setOutputDir(newVal);
            });
            configManager.sourcePathProperty().addListener((obs, oldVal, newVal) -> {
                model.getGlobalConfigModel().setSourcePath(newVal);
            });
            configManager.syncAfterGenerateProperty().addListener((obs, oldVal, newVal) -> {
                model.getGlobalConfigModel().setSyncAfterGenerate(newVal);
            });
            configManager.generateClientProperty().addListener((obs, oldVal, newVal) -> {
                model.getGlobalConfigModel().setGenerateClient(newVal);
            });
            configManager.generateBusinessProperty().addListener((obs, oldVal, newVal) -> {
                model.getGlobalConfigModel().setGenerateBusiness(newVal);
            });
            configManager.generateMetadataProperty().addListener((obs, oldVal, newVal) -> {
                model.getGlobalConfigModel().setGenerateMetadata(newVal);
            });
            configManager.authorProperty().addListener((obs, oldVal, newVal) -> {
                model.getGlobalConfigModel().setAuthor(newVal);
            });
        }

        // 使用新的配置管理器，配置变更会通过监听器自动处理
        // 不再依赖“保存”按钮，改为自动保存，无需额外初始化
    }

    /**
     * 手动保存全局配置
     * 也作为配置变更时的自动保存入口
     */
    public void saveGlobalConfig() {
        try {
            // 1) 保存到 ConfigManager (Preferences)，用于轻量本地记忆
            configManager.saveToPreferences();

            // 2) 同步到 GlobalConfigManager (INI 文件) 作为真正的全局配置
            com.nc5.generator.config.GlobalConfigManager globalConfigManager = CodeGeneratorApp.getGlobalConfigManager();
            if (globalConfigManager != null) {
                // 以 GlobalConfigManager 当前持有的 GlobalConfig 为基准，只更新真正的全局项
                com.nc5.generator.config.GlobalConfig globalConfig = globalConfigManager.getGlobalConfig();
                if (globalConfig == null) {
                    globalConfig = new com.nc5.generator.config.GlobalConfig();
                }

                // 从 ConfigManager 写入最新值
                globalConfig.setOutputDir(configManager.getOutputDir());
                globalConfig.setSourcePath(configManager.getSourcePath());
                globalConfig.setAuthor(configManager.getAuthor());
                globalConfig.setSyncAfterGenerate(configManager.isSyncAfterGenerate());
                globalConfig.setGenerateClient(configManager.isGenerateClient());
                globalConfig.setGenerateBusiness(configManager.isGenerateBusiness());
                globalConfig.setGenerateMetadata(configManager.isGenerateMetadata());

                globalConfigManager.setGlobalConfig(globalConfig);
                globalConfigManager.saveGlobalConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== 输出配置相关方法 ==========

    @FXML
    public void handleBrowseOutputDir() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择输出目录");

        // 设置初始目录
        String currentPath = outputDirField.getText();
        if (currentPath != null && !currentPath.isEmpty()) {
            File currentDir = new File(currentPath);
            if (currentDir.exists() && currentDir.isDirectory()) {
                chooser.setInitialDirectory(currentDir);
            }
        }

        File selectedDir = chooser.showDialog(outputDirField.getScene().getWindow());
        if (selectedDir != null) {
            outputDirField.setText(selectedDir.getAbsolutePath());
            saveGlobalConfig(); // 选择后立即保存
        }
    }

    @FXML
    public void handleClearOutputDir() {
        if (outputDirField != null) {
            outputDirField.clear();
            saveGlobalConfig(); // 清除后立即保存
        }
    }

    @FXML
    public void handleBrowseProjectSrc() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("选择项目源码目录");

        // 设置初始目录
        String currentPath = projectSrcField.getText();
        if (currentPath != null && !currentPath.isEmpty()) {
            File currentDir = new File(currentPath);
            if (currentDir.exists() && currentDir.isDirectory()) {
                chooser.setInitialDirectory(currentDir);
            }
        }

        File selectedDir = chooser.showDialog(projectSrcField.getScene().getWindow());
        if (selectedDir != null) {
            projectSrcField.setText(selectedDir.getAbsolutePath());
            saveGlobalConfig(); // 选择后立即保存
        }
    }

    /**
     * 获取输出目录字段
     */
    public TextField getOutputDirField() {
        return outputDirField;
    }

    /**
     * 获取项目源码字段
     */
    public TextField getProjectSrcField() {
        return projectSrcField;
    }

    /**
     * 获取自动同步复选框
     */
    public CheckBox getSyncAfterGenerate() {
        return syncAfterGenerate;
    }
}
