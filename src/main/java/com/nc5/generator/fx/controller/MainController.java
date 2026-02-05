package com.nc5.generator.fx.controller;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.config.GlobalConfig;
import com.nc5.generator.fx.model.BillConfigModel;
import com.nc5.generator.fx.util.NotificationUtil;
import com.nc5.generator.service.CodeGenerateService;
import com.nc5.generator.service.ConfigFileService;
import com.nc5.generator.service.BillConfigValidator;
import com.nc5.generator.fx.CodeGeneratorApp;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主窗口控制器
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private static final String DEFAULT_OUTPUT_DIR = "output";

    @FXML private BorderPane rootPane;
    @FXML private TabPane mainTabPane;
    @FXML private Label statusLabel;
    @FXML private Label configPathLabel;
    @FXML private Menu recentFilesMenu;

    // 子控制器
    @FXML private BasicInfoController basicInfoTabController;
    @FXML private HeadFieldsController headFieldsTabController;
    @FXML private EnumConfigController enumConfigTabController;
    @FXML private BodyFieldsController bodyFieldsTabController;
    @FXML private GenerateController generateTabController;
    @FXML private CodePreviewController codePreviewTabController;

    // 服务和模型
    private BillConfigModel billConfigModel = new BillConfigModel();
    private ConfigFileService configFileService;
    private BillConfigValidator validator;
    private CodeGenerateService codeGenerateService;

    private boolean isModified = false;

    @FXML
    public void initialize() {
        // 初始化通知工具
        if (rootPane != null) {
            NotificationUtil.initialize(rootPane);
        }

        // 初始化服务
        configFileService = new ConfigFileService(billConfigModel);
        validator = new BillConfigValidator(billConfigModel);
        codeGenerateService = new CodeGenerateService(new File(DEFAULT_OUTPUT_DIR));

        // 加载全局配置到模型（必须在子控制器绑定之前）
        loadGlobalConfigToModel();

        // 初始化子控制器
        initSubControllers();

        // 设置监听器
        setupChangeListeners();
        setupTabDisableListener();
        setupConfigPathLabelClick();

        updateStatus("就绪");
        autoLoadLastConfig();
    }

    private void initSubControllers() {
        if (basicInfoTabController != null) {
            basicInfoTabController.setBillConfigModel(billConfigModel);
        }
        if (headFieldsTabController != null) {
            headFieldsTabController.setBillConfigModel(billConfigModel);
        }
        if (bodyFieldsTabController != null) {
            bodyFieldsTabController.setBillConfigModel(billConfigModel);
        }
        if (enumConfigTabController != null) {
            enumConfigTabController.setBillConfigModel(billConfigModel);
        }
        if (generateTabController != null) {
            generateTabController.setBillConfigModel(billConfigModel);
        }
        if (codePreviewTabController != null) {
            codePreviewTabController.setBillConfigModel(billConfigModel);
            codePreviewTabController.setMainController(this);
            codePreviewTabController.setOutputDir(new File(DEFAULT_OUTPUT_DIR));
        }
    }

    private void setupChangeListeners() {
        billConfigModel.billCodeProperty().addListener((obs, old, val) -> setModified(true));
        billConfigModel.billNameProperty().addListener((obs, old, val) -> setModified(true));
        billConfigModel.moduleProperty().addListener((obs, old, val) -> setModified(true));
        billConfigModel.packageNameProperty().addListener((obs, old, val) -> setModified(true));
        billConfigModel.bodyCodeProperty().addListener((obs, old, val) -> setModified(true));
        billConfigModel.billTypeProperty().addListener((obs, old, val) -> setModified(true));
        billConfigModel.generateClientProperty().addListener((obs, old, val) -> setModified(true));
        billConfigModel.generateBusinessProperty().addListener((obs, old, val) -> setModified(true));
        billConfigModel.generateMetadataProperty().addListener((obs, old, val) -> setModified(true));
        billConfigModel.authorProperty().addListener((obs, old, val) -> setModified(true));
        billConfigModel.descriptionProperty().addListener((obs, old, val) -> setModified(true));
        billConfigModel.sourcePathProperty().addListener((obs, old, val) -> setModified(true));

        billConfigModel.getHeadFields().addListener((javafx.collections.ListChangeListener.Change<?> c) -> setModified(true));
        billConfigModel.getBodyFields().addListener((javafx.collections.ListChangeListener.Change<?> c) -> setModified(true));
        billConfigModel.getEnumConfigs().addListener((javafx.collections.ListChangeListener.Change<?> c) -> setModified(true));
    }

    private void setupTabDisableListener() {
        Runnable updateTabsState = () -> {
            boolean isValid = validator.validateBasicInfo();
            String billType = billConfigModel.getBillType();
            boolean isSingleBill = "single".equals(billType);

            for (int i = 1; i < mainTabPane.getTabs().size(); i++) {
                if (i == 1) {
                    mainTabPane.getTabs().get(i).setDisable(!isValid);
                } else if (i == 2) {
                    mainTabPane.getTabs().get(i).setDisable(!isValid || isSingleBill);
                } else {
                    mainTabPane.getTabs().get(i).setDisable(!isValid);
                }
            }
        };

        billConfigModel.billCodeProperty().addListener((obs, old, val) -> Platform.runLater(updateTabsState));
        billConfigModel.billNameProperty().addListener((obs, old, val) -> Platform.runLater(updateTabsState));
        billConfigModel.moduleProperty().addListener((obs, old, val) -> Platform.runLater(updateTabsState));
        billConfigModel.packageNameProperty().addListener((obs, old, val) -> Platform.runLater(updateTabsState));
        billConfigModel.billTypeProperty().addListener((obs, old, val) -> Platform.runLater(updateTabsState));
        billConfigModel.bodyCodeProperty().addListener((obs, old, val) -> Platform.runLater(updateTabsState));

        Platform.runLater(updateTabsState);
    }

    private void setupConfigPathLabelClick() {
        if (configPathLabel != null) {
            configPathLabel.setStyle("-fx-text-fill: blue; -fx-cursor: hand;");
            configPathLabel.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    openConfigDirectory();
                }
            });
        }
    }

    private void openConfigDirectory() {
        File configDir = configFileService.getCurrentConfigFile() != null 
            ? configFileService.getCurrentConfigFile().getParentFile()
            : getBillConfigDirectory();

        if (configDir != null && configDir.exists()) {
            try {
                Desktop.getDesktop().open(configDir);
                updateStatus("已打开目录: " + configDir.getAbsolutePath());
            } catch (Exception e) {
                logger.error("打开配置目录失败", e);
                showErrorDialog("打开目录失败", "无法打开配置目录: " + e.getMessage());
            }
        } else {
            showErrorDialog("目录不存在", "配置目录不存在: " + configDir);
        }
    }

    private void setModified(boolean modified) {
        this.isModified = modified;
        // 只有在场景已初始化后才更新标题
        if (mainTabPane.getScene() != null) {
            updateTitle();
        }
    }

    private void updateTitle() {
        if (mainTabPane.getScene() == null) {
            return;
        }
        Stage stage = (Stage) mainTabPane.getScene().getWindow();
        String title = "NC5代码生成器";
        if (configFileService.getCurrentConfigFile() != null) {
            title += " - " + configFileService.getCurrentConfigFile().getName();
        }
        if (isModified) {
            title += " *";
        }
        stage.setTitle(title);
    }

    public void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }

    private void autoLoadLastConfig() {
        String lastFile = configFileService.getMostRecentFile();
        logger.info("autoLoadLastConfig - 最近文件: {}", lastFile);
        if (lastFile != null) {
            // 跳过全局配置文件
            if (lastFile.endsWith("global_cfg.ini")) {
                logger.info("跳过全局配置文件的自动加载");
                return;
            }
            Platform.runLater(() -> {
                File file = new File(lastFile);
                logger.info("autoLoadLastConfig - 文件存在: {}, 场景非空: {}", file.exists(), mainTabPane.getScene() != null);
                if (file.exists() && mainTabPane.getScene() != null) {
                    loadConfigFile(file);
                    updateStatus("已自动加载最近配置: " + file.getName());
                }
            });
        }
    }
    
    /**
     * 加载全局配置到模型
     */
    private void loadGlobalConfigToModel() {
        GlobalConfig globalConfig = CodeGeneratorApp.getGlobalConfig();
        if (globalConfig != null) {
            // 将全局配置的值设置到GlobalConfigModel中
            logger.info("加载全局配置到模型 - Author: {}, OutputDir: {}",
                globalConfig.getAuthor(), globalConfig.getOutputDir());

            billConfigModel.getGlobalConfigModel().fromGlobalConfig(globalConfig);

            logger.debug("已加载全局配置到模型");
        } else {
            logger.warn("全局配置为空，无法加载到模型");
        }
    }

    // ==================== 菜单和工具栏事件处理 ====================

    @FXML
    private void handleNewConfig() {
        if (isModified && !confirmSave()) return;

        // 新建配置时只清空单据配置，不清空全局配置
        billConfigModel.clear();

        configFileService.setCurrentConfigFile(null);
        isModified = false;
        updateConfigPathLabel();
        updateTitle();
        updateStatus("已新建配置");
    }

    @FXML
    private void handleOpenConfig() {
        if (isModified && !confirmSave()) return;

        File file = configFileService.showOpenDialog(mainTabPane.getScene().getWindow(), getBillConfigDirectory());
        if (file != null) {
            // 检查是否是全局配置文件
            if (file.getName().equals("global_cfg.ini")) {
                showErrorDialog("打开失败", "global_cfg.ini 是全局配置文件，不能作为单据配置打开");
                return;
            }
            loadConfigFile(file);
        }
    }

    private void loadConfigFile(File file) {
        boolean success = configFileService.loadConfigFile(file, new ConfigFileService.ConfigLoadCallback() {
            @Override
            public void onSuccess(File loadedFile) {
                Platform.runLater(() -> {
                    if (codePreviewTabController != null) {
                        codePreviewTabController.updateUIFromModel();
                    }
                    isModified = false;
                    updateConfigPathLabel();
                    updateTitle();
                    updateStatus("已加载: " + loadedFile.getName());
                    configFileService.addToRecentFiles(loadedFile.getAbsolutePath());
                    updateRecentFilesMenu();
                });
            }

            @Override
            public void onError(Exception e) {
                Platform.runLater(() -> showErrorDialog("加载失败", "无法加载配置文件: " + e.getMessage()));
            }
        });

        if (!success) {
            showErrorDialog("加载失败", "无法加载配置文件");
        }
    }

    @FXML
    private void handleSaveConfig() {
        commitAllTableEdits();

        if (configFileService.getCurrentConfigFile() == null) {
            handleSaveAsConfig();
        } else {
            saveConfigFile(configFileService.getCurrentConfigFile());
        }
    }

    @FXML
    private void handleSaveAsConfig() {
        String billCode = billConfigModel.getBillCode();
        String defaultFileName = (billCode != null && !billCode.isEmpty()) 
            ? billCode.toLowerCase() + "-config.xml" 
            : "config.xml";

        File file = configFileService.showSaveDialog(
            mainTabPane.getScene().getWindow(), 
            getBillConfigDirectory(), 
            defaultFileName
        );

        if (file != null) {
            saveConfigFile(file);
        }
    }

    private void saveConfigFile(File file) {
        boolean success = configFileService.saveConfigFile(file, new ConfigFileService.ConfigSaveCallback() {
            @Override
            public void onSuccess(File xmlFile, String headFieldsFileName, String bodyFieldsFileName) {
                Platform.runLater(() -> {
                    updateStatus("已保存: " + xmlFile.getName() + " + " + headFieldsFileName + " + " + bodyFieldsFileName);
                    isModified = false;
                    updateConfigPathLabel();
                    updateTitle();
                    configFileService.addToRecentFiles(xmlFile.getAbsolutePath());
                    updateRecentFilesMenu();
                });
            }

            @Override
            public void onError(Exception e) {
                Platform.runLater(() -> showErrorDialog("保存失败", "无法保存配置文件: " + e.getMessage()));
            }
        });

        if (!success) {
            showErrorDialog("保存失败", "无法保存配置文件");
        }
    }

    @FXML
    private void handleGenerate() {
        if (billConfigModel == null) {
            NotificationUtil.showError("数据模型未初始化");
            return;
        }

        BillConfigValidator.ValidationResult validation = validator.validateBasicInfoForGenerate();
        if (!validation.isValid()) {
            NotificationUtil.showWarning("验证失败：\n" + validation.getErrorMessage());
            mainTabPane.getSelectionModel().select(0);
            return;
        }

        BillConfig config = billConfigModel.toBillConfig();

        codeGenerateService.generateWithProgress(config, mainTabPane.getScene().getWindow(), () -> {
            if (codePreviewTabController != null) {
                codePreviewTabController.refreshFileTree();
            }

            if (generateTabController != null && generateTabController.getSyncAfterGenerate() != null
                    && generateTabController.getSyncAfterGenerate().isSelected()) {
                handleSyncCode();
            }
        });
    }

    @FXML
    private void handleSyncCode() {
        try {
            String projectSrcPath = generateTabController != null && generateTabController.getProjectSrcField() != null
                ? generateTabController.getProjectSrcField().getText()
                : null;

            if (projectSrcPath == null || projectSrcPath.isEmpty()) {
                NotificationUtil.showError("请先选择项目源码目录");
                return;
            }

            File projectSrcDir = new File(projectSrcPath);
            if (!projectSrcDir.exists()) {
                NotificationUtil.showError("项目源码目录不存在");
                return;
            }

            codeGenerateService.setProjectSrcDir(projectSrcDir);
            codeGenerateService.syncCode(null);

        } catch (Exception e) {
            NotificationUtil.showError("同步失败: " + e.getMessage());
        }
    }

    @FXML
    private void handleExit() {
        if (isModified) {
            Optional<ButtonType> result = showConfirmDialog("确认退出", "当前配置未保存，是否保存？");
            if (result.isPresent()) {
                if (result.get().getButtonData() == ButtonBar.ButtonData.YES) {
                    handleSaveConfig();
                } else if (result.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
                    return;
                }
            }
        }
        Platform.exit();
    }

    @FXML
    private void handleAbout() {
        String aboutText = "版本: 1.0.0\n基于JavaFX和Velocity模板引擎\n用于NC5单据代码自动生成\nAuthor: Flynn Chen\n邮箱: vicejf@live.com";
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("关于");
        alert.setHeaderText("NC5代码生成器");
        alert.setContentText(aboutText);
        alert.showAndWait();
    }

    // ==================== 浏览目录按钮处理 ====================

    @FXML
    private void handleBrowseOutputDir() {
        if (generateTabController != null) {
            generateTabController.handleBrowseOutputDir();
        }
    }

    @FXML
    private void handleBrowseProjectSrc() {
        if (generateTabController != null) {
            generateTabController.handleBrowseProjectSrc();
        }
    }

    @FXML
    private void handleClearOutputDir() {
        if (generateTabController != null) {
            generateTabController.handleClearOutputDir();
        }
    }

    // ==================== 最近文件管理 ====================

    private void updateRecentFilesMenu() {
        if (recentFilesMenu == null) return;

        recentFilesMenu.getItems().clear();

        if (configFileService.getRecentFiles().isEmpty()) {
            MenuItem emptyItem = new MenuItem("无最近文件");
            emptyItem.setDisable(true);
            recentFilesMenu.getItems().add(emptyItem);
            recentFilesMenu.setDisable(true);
        } else {
            recentFilesMenu.setDisable(false);
            for (int i = 0; i < configFileService.getRecentFiles().size(); i++) {
                String filePath = configFileService.getRecentFiles().get(i);
                File file = new File(filePath);
                String displayName = String.format("%d. %s", i + 1, file.getName());

                MenuItem item = new MenuItem(displayName);
                item.setOnAction(event -> {
                    if (isModified && !confirmSave()) return;
                    loadConfigFile(file);
                });

                recentFilesMenu.getItems().add(item);
            }

            recentFilesMenu.getItems().add(new SeparatorMenuItem());
            MenuItem clearItem = new MenuItem("清除最近文件列表");
            clearItem.setOnAction(event -> {
                configFileService.clearRecentFiles();
                updateRecentFilesMenu();
            });
            recentFilesMenu.getItems().add(clearItem);
        }
    }

    // ==================== 辅助方法 ====================

    private File getBillConfigDirectory() {
        String billCode = billConfigModel.getBillCode();
        if (billCode == null || billCode.isEmpty()) {
            File configDir = new File(System.getProperty("user.dir"), "config");
            if (!configDir.exists()) configDir.mkdirs();
            return configDir;
        }

        File billConfigDir = new File(System.getProperty("user.dir"), "config/" + billCode.toLowerCase());
        if (!billConfigDir.exists()) billConfigDir.mkdirs();
        return billConfigDir;
    }

    private void updateConfigPathLabel() {
        if (configPathLabel != null) {
            if (configFileService.getCurrentConfigFile() != null) {
                configPathLabel.setText(configFileService.getCurrentConfigFile().getAbsolutePath());
            } else {
                configPathLabel.setText("未加载配置文件");
            }
        }
    }

    private void commitAllTableEdits() {
        if (headFieldsTabController != null) headFieldsTabController.commitTableEdit();
        if (bodyFieldsTabController != null) bodyFieldsTabController.commitTableEdit();
    }

    private boolean confirmSave() {
        Optional<ButtonType> result = showConfirmDialog("确认", "当前配置未保存，是否保存？");
        if (result.isPresent()) {
            if (result.get().getButtonData() == ButtonBar.ButtonData.YES) {
                handleSaveConfig();
                return true;
            } else if (result.get().getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
                return false;
            }
        }
        return true;
    }

    private Optional<ButtonType> showConfirmDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType yesButton = new ButtonType("是", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("否", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);

        return alert.showAndWait();
    }

    private void showErrorDialog(String title, String message) {
        NotificationUtil.showError(title + ": " + message);
    }

    // Getters
    public BillConfigModel getBillConfigModel() {
        return billConfigModel;
    }

    public File getCurrentConfigFile() {
        return configFileService.getCurrentConfigFile();
    }

    public boolean isModified() {
        return isModified;
    }
}
