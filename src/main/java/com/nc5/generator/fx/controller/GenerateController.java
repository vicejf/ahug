package com.nc5.generator.fx.controller;

import com.nc5.generator.fx.model.BillConfigModel;
import com.nc5.generator.fx.CodeGeneratorApp;
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

    @FXML
    public void initialize() {
        // 初始化时不需要特殊处理
    }

    /**
     * 设置数据模型并绑定
     */
    public void setBillConfigModel(BillConfigModel model) {
        this.billConfigModel = model;
        bindFields();
    }

    /**
     * 绑定字段到模型
     */
    private void bindFields() {
        if (billConfigModel == null) {
            return;
        }

        System.out.println("[GenerateController] 开始绑定字段");

        // 先设置字段的初始值，确保双向绑定前字段有正确的值
        String outputDirValue = billConfigModel.getOutputDir();
        String sourcePathValue = billConfigModel.getSourcePath();

        outputDirField.setText(outputDirValue);
        projectSrcField.setText(sourcePathValue);

        // 生成选项复选框双向绑定
        generateClientCheck.selectedProperty().bindBidirectional(billConfigModel.generateClientProperty());
        generateBusinessCheck.selectedProperty().bindBidirectional(billConfigModel.generateBusinessProperty());
        generateMetadataCheck.selectedProperty().bindBidirectional(billConfigModel.generateMetadataProperty());

        // 输出配置字段双向绑定
        outputDirField.textProperty().bindBidirectional(billConfigModel.outputDirProperty());
        projectSrcField.textProperty().bindBidirectional(billConfigModel.sourcePathProperty());
        syncAfterGenerate.selectedProperty().bindBidirectional(billConfigModel.syncAfterGenerateProperty());

        // 不再添加监听器，避免在初始化时触发保存
        // 全局配置将在应用退出时统一保存
    }

    /**
     * 手动保存全局配置
     * 可以在需要时调用，例如：在浏览目录后立即保存
     */
    public void saveGlobalConfig() {
        try {
            if (CodeGeneratorApp.getGlobalConfigManager() != null) {
                CodeGeneratorApp.getGlobalConfigManager().setGlobalConfig(
                    billConfigModel.getGlobalConfigModel().toGlobalConfig()
                );
                CodeGeneratorApp.getGlobalConfigManager().saveGlobalConfig();
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
