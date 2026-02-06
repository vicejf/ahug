package com.nc5.generator.fx.controller;

import com.nc5.generator.config.ConfigManager;
import com.nc5.generator.config.GlobalConfig;
import com.nc5.generator.config.GlobalConfigManager;
import com.nc5.generator.fx.CodeGeneratorApp;
import com.nc5.generator.fx.model.BillConfigModel;
import com.nc5.generator.fx.util.NotificationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码预览标签页控制器
 * 只负责预览生成的代码文件
 */
public class CodePreviewController {

    @FXML private TreeView<String> fileTree;
    @FXML private Label currentFileLabel;
    @FXML private TextArea codePreview;
    @FXML private TextArea logArea;

    private BillConfigModel billConfigModel;
    private MainController mainController;
    private File outputDir;
    private Map<TreeItem<String>, String> treeItemPathMap = new HashMap<>();

    @FXML
    public void initialize() {
        // 从全局配置文件中读取输出目录
        loadOutputDirFromGlobalConfig();
        
        setupFileTree();

        // 如果输出目录存在且有文件,自动刷新文件树
        if (outputDir != null && outputDir.exists() && outputDir.isDirectory()) {
            refreshFileTree();
        }
    }

    public void setBillConfigModel(BillConfigModel model) {
        this.billConfigModel = model;
    }

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    /**
     * 从全局配置文件中加载输出目录
     */
    private void loadOutputDirFromGlobalConfig() {
        GlobalConfigManager globalConfigManager = CodeGeneratorApp.getGlobalConfigManager();
        if (globalConfigManager == null) {
            // 如果全局配置管理器不存在,使用默认输出目录
            outputDir = new File("output");
            return;
        }

        // 直接从全局配置文件中读取最新配置
        GlobalConfig globalConfig = globalConfigManager.loadOrCreateDefault();
        if (globalConfig != null && globalConfig.getOutputDir() != null && !globalConfig.getOutputDir().isEmpty()) {
            outputDir = new File(globalConfig.getOutputDir());
        } else {
            // 如果全局配置中没有输出目录,使用默认值
            outputDir = new File("output");
        }
    }

    /**
     * 从配置文件加载后更新UI(保留此方法供兼容)
     */
    public void updateUIFromModel() {
        // 重新从全局配置文件加载输出目录
        loadOutputDirFromGlobalConfig();
        // 预览标签页无需特殊处理
        refreshFileTree();
    }

    /**
     * 设置文件树
     */
    private void setupFileTree() {
        TreeItem<String> root = new TreeItem<>("生成的文件");
        root.setExpanded(true);
        fileTree.setRoot(root);
        fileTree.setShowRoot(false);

        // 设置右键菜单
        setupContextMenu();

        // 监听选择变化
        fileTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isLeaf()) {
                String filePath = treeItemPathMap.get(newVal);
                if (filePath != null) {
                    loadFilePreview(filePath);
                }
            }
        });
    }

    /**
     * 设置右键菜单
     */
    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("删除文件");
        deleteItem.setOnAction(e -> handleDeleteFile());
        MenuItem refreshItem = new MenuItem("刷新列表");
        refreshItem.setOnAction(e -> handleRefresh());
        contextMenu.getItems().addAll(refreshItem, new SeparatorMenuItem(), deleteItem);
        fileTree.setContextMenu(contextMenu);
    }

    /**
     * 加载文件预览
     */
    private void loadFilePreview(String relativePath) {
        if (outputDir == null) {
            codePreview.setText("输出目录未配置");
            return;
        }
        
        // 根据相对路径构建完整路径
        Path filePath = outputDir.toPath().resolve(relativePath);

        if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
            try {
                String content = Files.readString(filePath, java.nio.charset.Charset.forName("GBK"));
                codePreview.setText(content);
                currentFileLabel.setText(relativePath);
            } catch (IOException e) {
                codePreview.setText("无法加载文件: " + e.getMessage());
            }
        } else {
            codePreview.setText("文件不存在: " + relativePath);
        }
    }

    @FXML
    private void handleRefresh() {
        refreshFileTree();
        log("文件列表已刷新");
    }

    @FXML
    private void handleCopyCode() {
        String code = codePreview.getText();
        if (code != null && !code.isEmpty()) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(code);
            clipboard.setContent(content);

            if (mainController != null) {
                mainController.updateStatus("代码已复制到剪贴板");
            }
            log("代码已复制到剪贴板");
        }
    }

    @FXML
    private void handleOpenDir() {
        if (outputDir == null) {
            NotificationUtil.showError("输出目录未配置");
            return;
        }
        
        // 获取当前选中的文件
        TreeItem<String> selectedItem = fileTree.getSelectionModel().getSelectedItem();

        if (selectedItem == null || !selectedItem.isLeaf()) {
            NotificationUtil.showError("请先选择一个文件");
            return;
        }

        // 获取选中文件的相对路径
        String relativePath = treeItemPathMap.get(selectedItem);
        if (relativePath == null) {
            NotificationUtil.showError("无法获取文件路径");
            return;
        }

        // 构建文件的完整路径
        Path filePath = outputDir.toPath().resolve(relativePath);
        Path parentDir = filePath.getParent();

        if (parentDir != null && Files.exists(parentDir)) {
            try {
                Desktop.getDesktop().open(parentDir.toFile());
            } catch (IOException e) {
                NotificationUtil.showError("无法打开目录: " + e.getMessage());
            }
        } else {
            NotificationUtil.showError("文件目录不存在");
        }
    }

    @FXML
    private void handleExpandAll() {
        TreeItem<String> root = fileTree.getRoot();
        if (root != null) {
            expandAll(root);
        }
    }

    @FXML
    private void handleCollapseAll() {
        TreeItem<String> root = fileTree.getRoot();
        if (root != null) {
            // 收起所有子节点，但保持根节点展开
            for (TreeItem<String> child : root.getChildren()) {
                collapseAll(child);
            }
        }
    }

    /**
     * 递归展开所有节点
     */
    private void expandAll(TreeItem<String> item) {
        item.setExpanded(true);
        for (TreeItem<String> child : item.getChildren()) {
            expandAll(child);
        }
    }

    /**
     * 递归收起所有节点
     */
    private void collapseAll(TreeItem<String> item) {
        item.setExpanded(false);
        for (TreeItem<String> child : item.getChildren()) {
            collapseAll(child);
        }
    }

    /**
     * 处理删除文件
     */
    private void handleDeleteFile() {
        if (outputDir == null) {
            NotificationUtil.showError("输出目录未配置");
            return;
        }
        
        TreeItem<String> selectedItem = fileTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            NotificationUtil.showError("请先选择一个文件");
            return;
        }

        String relativePath = treeItemPathMap.get(selectedItem);
        if (relativePath == null) {
            NotificationUtil.showError("只能删除文件，不能删除目录");
            return;
        }

        Path filePath = outputDir.toPath().resolve(relativePath);
        if (!Files.exists(filePath)) {
            NotificationUtil.showError("文件不存在: " + relativePath);
            return;
        }

        // 确认删除
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认删除");
        confirmAlert.setHeaderText("确定要删除文件吗？");
        confirmAlert.setContentText("文件: " + relativePath);
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Files.delete(filePath);
                    log("已删除文件: " + relativePath);
                    refreshFileTree();
                    handleExpandAll();
                    codePreview.clear();
                    currentFileLabel.setText("未选择文件");
                    if (mainController != null) {
                        mainController.updateStatus("文件已删除");
                    }
                } catch (IOException e) {
                    NotificationUtil.showError("删除文件失败: " + e.getMessage());
                }
            }
        });
    }

    /**
     * 刷新文件树
     */
    public void refreshFileTree() {
        // 重新从全局配置文件加载输出目录,确保使用最新配置
        loadOutputDirFromGlobalConfig();
        
        // 清空路径映射
        treeItemPathMap.clear();

        TreeItem<String> root = new TreeItem<>("生成的文件");
        root.setExpanded(true);

        if (outputDir != null && outputDir.exists() && outputDir.isDirectory()) {
            addFilesToTree(root, outputDir, "");
        }

        fileTree.setRoot(root);
        fileTree.setShowRoot(false);
    }

    /**
     * 递归添加文件到树
     */
    private void addFilesToTree(TreeItem<String> parent, File dir, String relativePath) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            TreeItem<String> item = new TreeItem<>(file.getName());

            if (file.isDirectory()) {
                // 计算子目录的相对路径
                String childRelativePath = relativePath.isEmpty() ? file.getName() : relativePath + "/" + file.getName();
                addFilesToTree(item, file, childRelativePath);
                if (!item.getChildren().isEmpty()) {
                    parent.getChildren().add(item);
                }
            } else if (file.getName().endsWith(".java")) {
                // 计算文件的完整相对路径
                String fileRelativePath = relativePath.isEmpty() ? file.getName() : relativePath + "/" + file.getName();
                // 只为文件节点存储相对路径（因为只有文件需要预览）
                treeItemPathMap.put(item, fileRelativePath);
                parent.getChildren().add(item);
            }
        }
    }

    private void log(String message) {
        logArea.appendText(message + "\n");
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }
}
