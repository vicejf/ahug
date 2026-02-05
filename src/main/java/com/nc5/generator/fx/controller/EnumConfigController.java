package com.nc5.generator.fx.controller;

import com.nc5.generator.fx.model.BillConfigModel;
import com.nc5.generator.fx.model.EnumConfigModel;
import com.nc5.generator.fx.model.EnumItemModel;
import com.nc5.generator.fx.util.NotificationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * 枚举配置标签页控制器
 */
public class EnumConfigController {

    @FXML private TableView<EnumConfigModel> enumTable;
    @FXML private TableColumn<EnumConfigModel, String> enumNameCol;
    @FXML private TableColumn<EnumConfigModel, String> enumDisplayNameCol;
    @FXML private TableColumn<EnumConfigModel, String> enumClassNameCol;

    @FXML private TableView<EnumItemModel> enumItemTable;
    @FXML private TableColumn<EnumItemModel, String> itemDisplayCol;
    @FXML private TableColumn<EnumItemModel, String> itemValueCol;

    @FXML private TextField enumNameField;
    @FXML private TextField enumDisplayNameField;
    @FXML private TextField enumClassNameField;

    @FXML private TextField itemDisplayField;
    @FXML private TextField itemValueField;

    @FXML private Button addEnumBtn;
    @FXML private Button updateEnumBtn;
    @FXML private Button deleteEnumBtn;
    @FXML private Button clearEnumBtn;

    @FXML private Button addItemBtn;
    @FXML private Button updateItemBtn;
    @FXML private Button deleteItemBtn;
    @FXML private Button clearItemBtn;

    @FXML private Button addItemToEnumBtn;

    private BillConfigModel billConfigModel;

    @FXML
    public void initialize() {
        // 初始化枚举表格列
        enumNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        enumDisplayNameCol.setCellValueFactory(new PropertyValueFactory<>("displayName"));
        enumClassNameCol.setCellValueFactory(new PropertyValueFactory<>("className"));

        // 初始化枚举项表格列
        itemDisplayCol.setCellValueFactory(new PropertyValueFactory<>("display"));
        itemValueCol.setCellValueFactory(new PropertyValueFactory<>("value"));

        // 监听枚举表格选择变化
        enumTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadEnumToFields(newVal);
                enumItemTable.setItems(newVal.getItems());
            } else {
                clearEnumFields();
                enumItemTable.getItems().clear();
            }
            updateButtonStates();
        });

        // 监听枚举项表格选择变化
        enumItemTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadItemToFields(newVal);
            } else {
                clearItemFields();
            }
            updateItemButtonStates();
        });

        updateButtonStates();
        updateItemButtonStates();
    }

    /**
     * 设置数据模型
     */
    public void setBillConfigModel(BillConfigModel model) {
        this.billConfigModel = model;
        enumTable.setItems(model.getEnumConfigs());
    }

    /**
     * 添加枚举
     */
    @FXML
    private void handleAddEnum() {
        EnumConfigModel enumConfig = new EnumConfigModel();
        enumConfig.setName(enumNameField.getText());
        enumConfig.setDisplayName(enumDisplayNameField.getText());
        enumConfig.setClassName(enumClassNameField.getText());

        if (validateEnum(enumConfig)) {
            billConfigModel.getEnumConfigs().add(enumConfig);
            clearEnumFields();
            updateButtonStates();
        }
    }

    /**
     * 更新枚举
     */
    @FXML
    private void handleUpdateEnum() {
        EnumConfigModel selected = enumTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        selected.setName(enumNameField.getText());
        selected.setDisplayName(enumDisplayNameField.getText());
        selected.setClassName(enumClassNameField.getText());

        if (validateEnum(selected)) {
            enumTable.refresh();
        }
    }

    /**
     * 删除枚举
     */
    @FXML
    private void handleDeleteEnum() {
        EnumConfigModel selected = enumTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText(null);
        alert.setContentText("确定要删除枚举配置 \"" + selected.getDisplayName() + "\" 吗？");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                billConfigModel.getEnumConfigs().remove(selected);
                clearEnumFields();
                enumItemTable.getItems().clear();
                updateButtonStates();
            }
        });
    }

    /**
     * 清空枚举字段
     */
    @FXML
    private void handleClearEnum() {
        clearEnumFields();
        enumTable.getSelectionModel().clearSelection();
    }

    /**
     * 添加枚举项
     */
    @FXML
    private void handleAddItem() {
        if (enumTable.getSelectionModel().getSelectedItem() == null) {
            NotificationUtil.showWarning("请先选择一个枚举配置");
            return;
        }

        EnumItemModel item = new EnumItemModel();
        item.setDisplay(itemDisplayField.getText());
        item.setValue(itemValueField.getText());

        if (validateItem(item)) {
            enumTable.getSelectionModel().getSelectedItem().getItems().add(item);
            clearItemFields();
            updateItemButtonStates();
        }
    }

    /**
     * 更新枚举项
     */
    @FXML
    private void handleUpdateItem() {
        EnumItemModel selected = enumItemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        selected.setDisplay(itemDisplayField.getText());
        selected.setValue(itemValueField.getText());

        if (validateItem(selected)) {
            enumItemTable.refresh();
        }
    }

    /**
     * 删除枚举项
     */
    @FXML
    private void handleDeleteItem() {
        EnumItemModel selected = enumItemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText(null);
        alert.setContentText("确定要删除枚举项 \"" + selected.getDisplay() + "\" 吗？");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                EnumConfigModel enumConfig = enumTable.getSelectionModel().getSelectedItem();
                if (enumConfig != null) {
                    enumConfig.getItems().remove(selected);
                    clearItemFields();
                    updateItemButtonStates();
                }
            }
        });
    }

    /**
     * 清空枚举项字段
     */
    @FXML
    private void handleClearItem() {
        clearItemFields();
        enumItemTable.getSelectionModel().clearSelection();
    }

    /**
     * 将枚举加载到字段
     */
    private void loadEnumToFields(EnumConfigModel enumConfig) {
        enumNameField.setText(enumConfig.getName());
        enumDisplayNameField.setText(enumConfig.getDisplayName());
        enumClassNameField.setText(enumConfig.getClassName());
    }

    /**
     * 清空枚举字段
     */
    private void clearEnumFields() {
        enumNameField.clear();
        enumDisplayNameField.clear();
        enumClassNameField.clear();
    }

    /**
     * 将枚举项加载到字段
     */
    private void loadItemToFields(EnumItemModel item) {
        itemDisplayField.setText(item.getDisplay());
        itemValueField.setText(item.getValue());
    }

    /**
     * 清空枚举项字段
     */
    private void clearItemFields() {
        itemDisplayField.clear();
        itemValueField.clear();
    }

    /**
     * 验证枚举
     */
    private boolean validateEnum(EnumConfigModel enumConfig) {
        StringBuilder errors = new StringBuilder();

        if (isNullOrEmpty(enumConfig.getName())) {
            errors.append("- 枚举名称不能为空\n");
        }
        if (isNullOrEmpty(enumConfig.getDisplayName())) {
            errors.append("- 枚举显示名称不能为空\n");
        }
        if (isNullOrEmpty(enumConfig.getClassName())) {
            errors.append("- 枚举类名不能为空\n");
        }

        if (errors.length() > 0) {
            NotificationUtil.showWarning("验证失败：\n" + errors.toString());
            return false;
        }

        return true;
    }

    /**
     * 验证枚举项
     */
    private boolean validateItem(EnumItemModel item) {
        StringBuilder errors = new StringBuilder();

        if (isNullOrEmpty(item.getDisplay())) {
            errors.append("- 枚举项显示文本不能为空\n");
        }
        if (isNullOrEmpty(item.getValue())) {
            errors.append("- 枚举项值不能为空\n");
        }

        if (errors.length() > 0) {
            NotificationUtil.showWarning("验证失败：\n" + errors.toString());
            return false;
        }

        return true;
    }

    /**
     * 更新枚举按钮状态
     */
    private void updateButtonStates() {
        boolean hasSelection = enumTable.getSelectionModel().getSelectedItem() != null;
        updateEnumBtn.setDisable(!hasSelection);
        deleteEnumBtn.setDisable(!hasSelection);

        addEnumBtn.setDisable(isNullOrEmpty(enumNameField.getText())
                || isNullOrEmpty(enumDisplayNameField.getText())
                || isNullOrEmpty(enumClassNameField.getText()));

        addItemBtn.setDisable(!hasSelection);
    }

    /**
     * 更新枚举项按钮状态
     */
    private void updateItemButtonStates() {
        boolean hasSelection = enumItemTable.getSelectionModel().getSelectedItem() != null;
        updateItemBtn.setDisable(!hasSelection);
        deleteItemBtn.setDisable(!hasSelection);

        addItemBtn.setDisable(isNullOrEmpty(itemDisplayField.getText())
                || isNullOrEmpty(itemValueField.getText())
                || enumTable.getSelectionModel().getSelectedItem() == null);
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
