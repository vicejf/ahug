package com.nc5.generator.fx.controller;

import com.nc5.generator.fx.component.AutoSaveTableCell;
import com.nc5.generator.fx.model.BillConfigModel;
import com.nc5.generator.fx.model.EnumConfigModel;
import com.nc5.generator.fx.model.FieldConfigModel;
import com.nc5.generator.fx.util.NotificationUtil;
import com.nc5.generator.service.FieldService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * 字段表格控制器基类
 * 封装表头和表体字段表格的公共逻辑
 */
public abstract class BaseFieldsController {

    // 通用表格组件
    @FXML protected TableView<FieldConfigModel> fieldsTable;
    @FXML protected TableColumn<FieldConfigModel, String> nameColumn;
    @FXML protected TableColumn<FieldConfigModel, String> labelColumn;
    @FXML protected TableColumn<FieldConfigModel, String> typeColumn;
    @FXML protected TableColumn<FieldConfigModel, String> dbTypeColumn;
    @FXML protected TableColumn<FieldConfigModel, Integer> lengthColumn;
    @FXML protected TableColumn<FieldConfigModel, Boolean> requiredColumn;
    @FXML protected TableColumn<FieldConfigModel, Boolean> editableColumn;
    @FXML protected Label fieldCountLabel;

    // 可选组件（子类FXML中可存在也可不存在）
    @FXML protected javafx.scene.layout.HBox bodyCodeSelector;
    @FXML protected ComboBox<String> bodyCodeCombo;
    @FXML protected Label currentBodyCodeLabel;

    protected BillConfigModel billConfigModel;

    // 字段服务
    protected final FieldService fieldService = new FieldService();

    // 下拉框选项
    protected static final String[] FIELD_TYPES = {"String", "Integer", "Double", "Date", "Boolean", "BigDecimal"};

    // 动态更新的类型选项列表（用于枚举配置监听）
    protected javafx.collections.ObservableList<String> dynamicFieldTypes =
            javafx.collections.FXCollections.observableArrayList(FIELD_TYPES);

    @FXML
    public void initialize() {
        setupTableColumns();
        setupTableEditing();
        setupKeyboardShortcuts();
    }

    /**
     * 设置数据模型
     */
    public void setBillConfigModel(BillConfigModel model) {
        this.billConfigModel = model;
        fieldsTable.setItems(getFieldsList());

        // 监听字段数量变化
        getFieldsList().addListener((javafx.collections.ListChangeListener.Change<? extends FieldConfigModel> c) -> {
            updateFieldCount();
        });
        updateFieldCount();

        // 设置枚举配置监听（如果启用）
        if (isEnumConfigListenerEnabled()) {
            setupEnumConfigListener();
        }

        // 设置编码选择器（如果存在且启用）
        if (isBodyCodeSelectorEnabled() && bodyCodeSelector != null) {
            setupBodyCodeSelector();
        }

        // 子类可扩展的初始化逻辑
        onModelSet(model);
    }

    /**
     * 获取字段列表（由子类实现）
     */
    protected abstract ObservableList<FieldConfigModel> getFieldsList();

    /**
     * 获取字段类型选项数组
     */
    protected String[] getFieldTypeOptions() {
        return isEnumConfigListenerEnabled() ? dynamicFieldTypes.toArray(new String[0]) : FIELD_TYPES;
    }

    /**
     * 是否启用枚举配置监听（子类可重写）
     */
    protected boolean isEnumConfigListenerEnabled() {
        return false;
    }

    /**
     * 是否启用编码选择器（子类可重写）
     */
    protected boolean isBodyCodeSelectorEnabled() {
        return false;
    }

    /**
     * 是否启用复制表头功能（子类可重写）
     */
    protected boolean isCopyFromHeadEnabled() {
        return false;
    }

    /**
     * 是否启用导入模板功能（子类可重写）
     */
    protected boolean isImportTemplateEnabled() {
        return false;
    }

    /**
     * 获取导入模板的资源路径（子类可重写）
     */
    protected String getTemplateResourcePath() {
        return "templates/field-template.json";
    }

    /**
     * 设置枚举配置监听
     */
    protected void setupEnumConfigListener() {
        billConfigModel.getEnumConfigs().addListener((javafx.collections.ListChangeListener.Change<? extends EnumConfigModel> c) -> {
            updateFieldTypeOptions();
        });
        updateFieldTypeOptions();
    }

    /**
     * 更新字段类型选项，添加已配置的枚举组件
     */
    protected void updateFieldTypeOptions() {
        dynamicFieldTypes.clear();
        dynamicFieldTypes.addAll(FIELD_TYPES);

        for (EnumConfigModel enumConfig : billConfigModel.getEnumConfigs()) {
            dynamicFieldTypes.add("enum:" + enumConfig.getName());
        }

        if (typeColumn != null) {
            typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(dynamicFieldTypes));
        }
    }

    /**
     * 设置编码选择器
     */
    protected void setupBodyCodeSelector() {
        billConfigModel.billTypeProperty().addListener((obs, oldVal, newVal) -> {
            updateBodyCodeSelectorVisibility();
        });
        updateBodyCodeSelectorVisibility();
    }

    /**
     * 更新编码选择器可见性
     */
    protected void updateBodyCodeSelectorVisibility() {
        if (bodyCodeSelector != null) {
            boolean isMulti = billConfigModel.isMultiBill();
            bodyCodeSelector.setVisible(isMulti);
            bodyCodeSelector.setManaged(isMulti);
        }
    }

    /**
     * 配置表格列（子类可扩展）
     */
    protected void setupTableColumns() {
        String[] typeOptions = getFieldTypeOptions();

        // 字段名
        nameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        nameColumn.setCellFactory(col -> createAutoSaveStringCell("name"));
        setupNameColumnEditCommit();

        // 中文名
        labelColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("label"));
        labelColumn.setCellFactory(col -> createAutoSaveStringCell("label"));
        labelColumn.setOnEditCommit(event -> event.getRowValue().setLabel(event.getNewValue()));

        // 字段类型
        typeColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("type"));
        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(typeOptions));

        // 数据库类型
        dbTypeColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("dbType"));
        dbTypeColumn.setCellFactory(col -> createAutoSaveStringCell("dbType"));
        dbTypeColumn.setOnEditCommit(event -> event.getRowValue().setDbType(event.getNewValue()));

        // 长度
        lengthColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("length"));
        lengthColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        lengthColumn.setOnEditCommit(event -> {
            Integer newValue = event.getNewValue();
            if (newValue != null && newValue >= 0) {
                event.getRowValue().setLength(newValue);
            }
        });

        // 必填
        requiredColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("required"));
        requiredColumn.setCellFactory(CheckBoxTableCell.forTableColumn(requiredColumn));
        requiredColumn.setStyle("-fx-alignment: CENTER;");

        // 可编辑
        editableColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("editable"));
        editableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(editableColumn));

        // 子类扩展列配置
        setupAdditionalColumns();
    }

    /**
     * 创建 AutoSaveTableCell 字符串单元格
     */
    private AutoSaveTableCell<FieldConfigModel, String> createAutoSaveStringCell(String fieldName) {
        return new AutoSaveTableCell<>(
            new javafx.util.StringConverter<>() {
                @Override
                public String toString(String s) {
                    return s == null ? "" : s;
                }

                @Override
                public String fromString(String s) {
                    return s;
                }
            },
            (rowItem, column, newValue) -> CompletableFuture.runAsync(() -> {
                System.out.println("[AUTO-SAVE] 字段 '" + rowItem.getName() + "' 的 '" + fieldName +
                                 "' 更新为 '" + newValue + "'");
            })
        );
    }

    /**
     * 设置字段名列的编辑提交处理
     */
    protected void setupNameColumnEditCommit() {
        nameColumn.setOnEditCommit(event -> {
            String newName = event.getNewValue();
            FieldConfigModel editedField = event.getRowValue();
            String oldName = editedField.getName();

            if (newName == null || (newName = newName.trim()).isEmpty()) {
                if (oldName != null) {
                    editedField.setName(oldName);
                }
                return;
            }

            final String finalNewName = newName;
            boolean isDuplicate = getFieldsList().stream()
                    .anyMatch(f -> f != editedField && finalNewName.equals(f.getName()));

            if (isDuplicate) {
                NotificationUtil.showWarning("字段名 '" + finalNewName + "' 已存在，请使用不同的名称");
                editedField.setName(oldName);
                fieldsTable.refresh();
                return;
            }

            editedField.setName(finalNewName);
        });
    }

    /**
     * 配置额外表格列（由子类实现）
     */
    protected abstract void setupAdditionalColumns();

    /**
     * 设置表格编辑行为
     */
    protected void setupTableEditing() {
        fieldsTable.setEditable(true);
        fieldsTable.getSelectionModel().setCellSelectionEnabled(true);
        fieldsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        fieldsTable.setRowFactory(tv -> {
            TableRow<FieldConfigModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    TablePosition<FieldConfigModel, ?> focused = fieldsTable.getFocusModel().getFocusedCell();
                    if (focused != null && focused.getTableColumn() != null) {
                        fieldsTable.edit(row.getIndex(), focused.getTableColumn());
                    }
                }
            });
            return row;
        });
    }

    /**
     * 设置键盘快捷键
     */
    protected void setupKeyboardShortcuts() {
        setupTableTabBehavior();

        fieldsTable.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.F2 ||
                event.getCode() == javafx.scene.input.KeyCode.ENTER) {

                int selectedIndex = fieldsTable.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && fieldsTable.getEditingCell() == null) {
                    TablePosition<FieldConfigModel, ?> focusedCell = fieldsTable.getFocusModel().getFocusedCell();
                    if (focusedCell != null) {
                        fieldsTable.edit(selectedIndex, focusedCell.getTableColumn());
                    } else {
                        fieldsTable.edit(selectedIndex, nameColumn);
                    }
                    event.consume();
                }
            }
        });
    }

    /**
     * 设置表格 Tab 键行为
     */
    protected void setupTableTabBehavior() {
        fieldsTable.getFocusModel().focusedCellProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.getTableColumn() == null) {
                return;
            }

            TablePosition<FieldConfigModel, ?> editingCell = fieldsTable.getEditingCell();
            if (editingCell != null && !editingCell.equals(newVal)) {
                fieldsTable.edit(-1, null);
            }

            javafx.application.Platform.runLater(() -> {
                if (fieldsTable.getEditingCell() == null && fieldsTable.isFocused()) {
                    fieldsTable.edit(newVal.getRow(), newVal.getTableColumn());
                }
            });
        });
    }

    @FXML
    protected void handleSelectAll() {
        if (getFieldsList().isEmpty()) {
            NotificationUtil.showWarning("字段列表为空，无需清空");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("确认清空");
        confirm.setHeaderText("清空所有字段");
        confirm.setContentText("这将删除所有 " + getFieldsList().size() + " 个字段，是否继续？");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                getFieldsList().clear();
                NotificationUtil.showInfo("已清空所有字段");
            }
        });
    }

    /**
     * 更新字段数量显示
     */
    protected void updateFieldCount() {
        int count = fieldsTable.getItems().size();
        fieldCountLabel.setText("共 " + count + " 个字段");
    }

    // ==================== 按钮事件处理 ====================

    @FXML
    protected void handleAddField() {
        FieldConfigModel field = createNewField();

        boolean isDuplicate = getFieldsList().stream()
                .anyMatch(f -> f.getName().equals(field.getName()));

        if (isDuplicate) {
            NotificationUtil.showWarning("字段名 '" + field.getName() + "' 已存在，请使用不同的名称");
            return;
        }

        getFieldsList().add(field);

        int newIndex = getFieldsList().size() - 1;
        fieldsTable.refresh();
        fieldsTable.getSelectionModel().select(newIndex);
        fieldsTable.scrollTo(newIndex);
        fieldsTable.requestFocus();
    }

    /**
     * 创建新字段（子类可重写默认值）
     */
    protected FieldConfigModel createNewField() {
        FieldConfigModel field = new FieldConfigModel();
        field.setName("newField");
        field.setLabel("新字段");
        field.setType("String");
        field.setDbType("VARCHAR2(50)");
        field.setLength(50);
        field.setUiType("Text");
        return field;
    }

    @FXML
    protected void handleDeleteField() {
        var selectedItems = new java.util.ArrayList<>(fieldsTable.getSelectionModel().getSelectedItems());
        if (!selectedItems.isEmpty()) {
            getFieldsList().removeAll(selectedItems);
        } else {
            NotificationUtil.showWarning("请先选择要删除的字段");
        }
    }

    @FXML
    protected void handleMoveUp() {
        int selectedIndex = fieldsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0) {
            FieldConfigModel item = getFieldsList().remove(selectedIndex);
            getFieldsList().add(selectedIndex - 1, item);
            fieldsTable.getSelectionModel().select(selectedIndex - 1);
        }
    }

    @FXML
    protected void handleMoveDown() {
        int selectedIndex = fieldsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < getFieldsList().size() - 1) {
            FieldConfigModel item = getFieldsList().remove(selectedIndex);
            getFieldsList().add(selectedIndex + 1, item);
            fieldsTable.getSelectionModel().select(selectedIndex + 1);
        }
    }

    @FXML
    protected void handleEditField() {
        int selectedIndex = fieldsTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            fieldsTable.edit(selectedIndex, nameColumn);
        } else {
            NotificationUtil.showWarning("请先选择要编辑的字段");
        }
    }

    @FXML
    protected void handleSaveField() {
        if (fieldsTable.getEditingCell() != null) {
            fieldsTable.edit(-1, null);
        }

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("保存字段配置");
        fileChooser.getExtensionFilters().add(
            new javafx.stage.FileChooser.ExtensionFilter("JSON 文件", "*.json")
        );

        String billCode = billConfigModel != null ? billConfigModel.getBillCode() : null;
        String fieldCode = getFieldCode();
        String defaultFileName = fieldService.getDefaultFileName(billCode, fieldCode);
        fileChooser.setInitialFileName(defaultFileName);

        File initialDir = fieldService.getLastSaveDirectory();
        if (initialDir != null) {
            fileChooser.setInitialDirectory(initialDir);
        }

        File file = fileChooser.showSaveDialog(fieldsTable.getScene().getWindow());
        if (file == null) return;

        if (fieldService.saveFieldsToFile(getFieldsList(), file, billConfigModel)) {
            NotificationUtil.showInfo("字段已成功保存到: " + file.getName());
        } else {
            NotificationUtil.showWarning("保存失败");
        }
    }

    /**
     * 获取字段编码（由子类重写）
     */
    protected String getFieldCode() {
        return "fields";
    }

    // ==================== 复制表头功能 ====================

    @FXML
    protected void handleCopyFromHead() {
        if (!isCopyFromHeadEnabled()) return;

        if (billConfigModel.getHeadFields().isEmpty()) {
            NotificationUtil.showWarning("表头字段为空，无法复制");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("确认复制");
        confirm.setHeaderText("将表头字段复制到当前列表");
        confirm.setContentText("这将复制所有表头字段到当前字段列表，是否继续？");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                for (FieldConfigModel headField : billConfigModel.getHeadFields()) {
                    getFieldsList().add(fieldService.copyFieldFrom(headField));
                }
                NotificationUtil.showInfo("已复制 " + billConfigModel.getHeadFields().size() + " 个字段");
            }
        });
    }

    // ==================== 编码选择器功能 ====================

    @FXML
    protected void handleAddBodyCode() {
        if (!isBodyCodeSelectorEnabled() || bodyCodeCombo == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("添加编码");
        dialog.setHeaderText("输入新的编码");
        dialog.setContentText("编码:");

        dialog.showAndWait().ifPresent(code -> {
            if (!code.isEmpty() && !billConfigModel.getBodyCodeList().contains(code)) {
                billConfigModel.getBodyCodeList().add(code);
                bodyCodeCombo.getItems().add(code);
                bodyCodeCombo.setValue(code);
            }
        });
    }

    @FXML
    protected void handleRemoveBodyCode() {
        if (!isBodyCodeSelectorEnabled() || bodyCodeCombo == null) return;

        String selected = bodyCodeCombo.getValue();
        if (selected != null) {
            billConfigModel.getBodyCodeList().remove(selected);
            bodyCodeCombo.getItems().remove(selected);
            if (!bodyCodeCombo.getItems().isEmpty()) {
                bodyCodeCombo.setValue(bodyCodeCombo.getItems().get(0));
            }
        }
    }

    // ==================== 导入模板功能 ====================

    @FXML
    protected void handleImportBasicFields() {
        if (!isImportTemplateEnabled()) return;

        FieldService.TemplateResult result = fieldService.importBasicFields(
            getFieldsList(), billConfigModel, getTemplateResourcePath());
        
        if (result.success) {
            NotificationUtil.showInfo(result.getMessage());
        } else {
            NotificationUtil.showWarning(result.errorMessage);
        }
    }

    @FXML
    protected void handleImportTemplate() {
        if (!isImportTemplateEnabled()) return;

        // 创建导入对话框
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("导入字段配置");
        dialog.setHeaderText("支持JSON配置或SQL建表语句");

        ButtonType importButtonType = new ButtonType("导入", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(importButtonType, ButtonType.CANCEL);

        TextArea inputTextArea = new TextArea();
        inputTextArea.setPromptText("选择文件后，内容将显示在这里...\n\n支持格式：\n1. JSON配置\n2. SQL建表语句 (CREATE TABLE ...)");
        inputTextArea.setEditable(true);
        inputTextArea.setPrefRowCount(15);
        inputTextArea.setPrefColumnCount(50);

        Button browseButton = new Button("选择文件...");
        TextField filePathField = new TextField();
        filePathField.setEditable(false);
        filePathField.setPromptText("请选择JSON或SQL文件");

        javafx.scene.layout.HBox fileSelectionBox = new javafx.scene.layout.HBox(10, filePathField, browseButton);
        fileSelectionBox.setMaxWidth(Double.MAX_VALUE);

        Label tipLabel = new Label("提示：支持JSON配置和SQL建表语句，可直接编辑内容或从文件导入");
        tipLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10,
            new Label("字段配置 (JSON/SQL):"),
            inputTextArea,
            new Label("选择配置文件:"),
            fileSelectionBox,
            tipLabel
        );
        content.setPadding(new javafx.geometry.Insets(10));

        dialog.getDialogPane().setContent(content);

        javafx.scene.Node importButton = dialog.getDialogPane().lookupButton(importButtonType);
        importButton.setDisable(true);

        inputTextArea.textProperty().addListener((obs, oldVal, newVal) -> {
            importButton.setDisable(newVal == null || newVal.trim().isEmpty());
        });

        browseButton.setOnAction(event -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("选择配置文件");
            fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("配置文件", "*.json", "*.sql"),
                new javafx.stage.FileChooser.ExtensionFilter("JSON 文件", "*.json"),
                new javafx.stage.FileChooser.ExtensionFilter("SQL 文件", "*.sql")
            );

            File initialDir = fieldService.getLastSaveDirectory();
            if (initialDir != null && initialDir.exists()) {
                fileChooser.setInitialDirectory(initialDir);
            }

            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                filePathField.setText(file.getAbsolutePath());
                try {
                    String fileContent = new String(java.nio.file.Files.readAllBytes(file.toPath()),
                            StandardCharsets.UTF_8);
                    inputTextArea.setText(fileContent);
                    NotificationUtil.showInfo("已加载文件: " + file.getName());
                } catch (Exception e) {
                    NotificationUtil.showWarning("读取文件失败: " + e.getMessage());
                }
            }
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response == importButtonType) {
                String inputContent = inputTextArea.getText();
                if (inputContent != null && !inputContent.trim().isEmpty()) {
                    FieldService.ImportResult result = fieldService.importFromContent(inputContent, getFieldsList());
                    if (result.success) {
                        NotificationUtil.showInfo(result.getMessage());
                    } else {
                        NotificationUtil.showWarning(result.errorMessage);
                    }
                }
            }
        });
    }

    /**
     * 子类重写此方法，在模型设置后执行额外初始化
     */
    protected void onModelSet(BillConfigModel model) {
        // 子类可重写
    }

    /**
     * 提交表格的编辑
     */
    public void commitTableEdit() {
        if (fieldsTable != null && fieldsTable.getEditingCell() != null) {
            fieldsTable.edit(-1, null);
        }
    }
}
