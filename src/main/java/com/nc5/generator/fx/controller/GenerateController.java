package com.nc5.generator.fx.controller;

import com.nc5.generator.fx.model.BillConfigModel;
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

        // 生成选项复选框双向绑定
        generateClientCheck.selectedProperty().bindBidirectional(billConfigModel.generateClientProperty());
        generateBusinessCheck.selectedProperty().bindBidirectional(billConfigModel.generateBusinessProperty());
        generateMetadataCheck.selectedProperty().bindBidirectional(billConfigModel.generateMetadataProperty());

        // 输出配置字段绑定
        outputDirField.textProperty().bindBidirectional(billConfigModel.outputDirProperty());
        projectSrcField.textProperty().bindBidirectional(billConfigModel.sourcePathProperty());
        syncAfterGenerate.selectedProperty().bindBidirectional(billConfigModel.syncAfterGenerateProperty());
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
        }
    }

    @FXML
    public void handleClearOutputDir() {
        if (outputDirField != null) {
            outputDirField.clear();
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
