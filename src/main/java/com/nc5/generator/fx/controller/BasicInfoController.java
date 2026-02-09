package com.nc5.generator.fx.controller;

import com.nc5.generator.config.BillType;
import com.nc5.generator.config.ConfigManager;
import com.nc5.generator.fx.model.BillConfigModel;
import com.nc5.generator.fx.util.NotificationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.util.function.UnaryOperator;

/**
 * 基本信息标签页控制器
 */
public class BasicInfoController {

    @FXML private TextField billCodeField;
    @FXML private TextField billNameField;
    @FXML private TextField bodyCodeField;
    @FXML private TextField headCodeField;
    @FXML private ComboBox<String> billTypeCombo;
    @FXML private TextField authorField;
    @FXML private TextField packageNameField;

    private BillConfigModel billConfigModel;

    // 全局配置管理器（用于作者等全局信息）
    private final ConfigManager configManager = ConfigManager.getInstance();

    @FXML
    public void initialize() {
        // 初始化单据类型下拉框 - 使用全局映射表，便于维护
        billTypeCombo.getItems().addAll(BillType.getAllEnabledCodes());
        billTypeCombo.setValue(BillType.getDefault().getCode());

        // 作者字段不可编辑
        authorField.setEditable(false);

        // 单据编码字段：只能输入字母和数字，自动大写，限制为四个字符
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }
            // 验证只包含字母和数字，且长度不超过4
            if (newText.matches("[A-Za-z0-9]*") && newText.length() <= 4) {
                return change;
            }
            return null;
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        billCodeField.setTextFormatter(formatter);

        // 自动转换为大写
        billCodeField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter() != null && !event.getCharacter().isEmpty()) {
                event.consume();
                billCodeField.setText(billCodeField.getText() + event.getCharacter().toUpperCase());
            }
        });
    }
    
    /**
     * 设置数据模型并绑定
     */
    public void setBillConfigModel(BillConfigModel model) {
        System.out.println("[BasicInfoController] setBillConfigModel 被调用");
        this.billConfigModel = model;
        bindFields();
        System.out.println("[BasicInfoController] setBillConfigModel 完成");
    }

    /**
     * 绑定字段到模型
     */
    private void bindFields() {
        if (billConfigModel == null) {
            return;
        }

        // 文本字段双向绑定
        billCodeField.textProperty().bindBidirectional(billConfigModel.billCodeProperty());
        billNameField.textProperty().bindBidirectional(billConfigModel.billNameProperty());
        bodyCodeField.textProperty().bindBidirectional(billConfigModel.bodyCodeProperty());
        headCodeField.textProperty().bindBidirectional(billConfigModel.headCodeProperty());
        packageNameField.textProperty().bindBidirectional(billConfigModel.packageNameProperty());
        // 作者字段绑定到全局配置（而非单据配置）
        authorField.textProperty().bind(configManager.authorProperty());
        authorField.setEditable(false);

        // 下拉框双向绑定
        billTypeCombo.valueProperty().bindBidirectional(billConfigModel.billTypeProperty());

        // 监听单据类型变化，控制表体编码字段的可用状态
        billTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            BillType currentType = BillType.fromCode(newVal);
            if (currentType == BillType.SINGLE || currentType == BillType.ARCHIVE) {
                bodyCodeField.setDisable(true);
                bodyCodeField.clear();
                clearFieldError(bodyCodeField);
            } else {
                bodyCodeField.setDisable(false);
                // 切换到多表体类型时，设置默认表体编码
                setDefaultBodyCode();
            }
        });

        // 初始化表体编码字段状态
        BillType initialType = BillType.fromCode(billTypeCombo.getValue());
        if (initialType == BillType.SINGLE || initialType == BillType.ARCHIVE) {
            bodyCodeField.setDisable(true);
        } else {
            setDefaultBodyCode();
        }

        // 自动设置表头编码（始终设置为 单据编码 + HVO）
        billCodeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                headCodeField.setText(newVal + "HVO");
                // 只在多表体类型时自动推断表体编码
                if (BillType.fromCode(billTypeCombo.getValue()) == BillType.MULTI) {
                    setDefaultBodyCode();
                }
            } else {
                headCodeField.clear();
            }
        });

        // 实时验证必填项，添加错误样式
        setupRealtimeValidation();
    }

    /**
     * 设置实时验证
     */
    private void setupRealtimeValidation() {
        // 单据编码
        billCodeField.textProperty().addListener((obs, old, newVal) -> {
            if (isNullOrEmpty(newVal)) {
                applyFieldError(billCodeField);
            } else {
                clearFieldError(billCodeField);
            }
        });

        // 单据名称
        billNameField.textProperty().addListener((obs, old, newVal) -> {
            if (isNullOrEmpty(newVal)) {
                applyFieldError(billNameField);
            } else {
                clearFieldError(billNameField);
            }
        });

        // 表体编码（仅对多表体类型验证）
        bodyCodeField.textProperty().addListener((obs, old, newVal) -> {
            if (BillType.fromCode(billTypeCombo.getValue()) == BillType.MULTI) {
                if (isNullOrEmpty(newVal)) {
                    applyFieldError(bodyCodeField);
                } else {
                    clearFieldError(bodyCodeField);
                }
            }
        });
    }

    /**
     * 应用错误样式到字段
     */
    private void applyFieldError(TextField field) {
        field.getStyleClass().add("error");
    }

    /**
     * 清除字段错误样式
     */
    private void clearFieldError(TextField field) {
        field.getStyleClass().remove("error");
    }
    
    /**
     * 验证基本信息是否完整
     */
    public boolean validate() {
        StringBuilder errors = new StringBuilder();

        // 先清除所有错误样式
        clearFieldError(billCodeField);
        clearFieldError(billNameField);
        clearFieldError(bodyCodeField);

        // 验证并应用错误样式
        if (isNullOrEmpty(billCodeField.getText())) {
            errors.append("- 单据编码不能为空\n");
            applyFieldError(billCodeField);
        }
        if (isNullOrEmpty(billNameField.getText())) {
            errors.append("- 单据名称不能为空\n");
            applyFieldError(billNameField);
        }

        // 仅对多表体类型验证表体编码
        if (BillType.fromCode(billTypeCombo.getValue()) == BillType.MULTI) {
            if (isNullOrEmpty(bodyCodeField.getText())) {
                errors.append("- 表体编码不能为空（多表体类型）\n");
                applyFieldError(bodyCodeField);
            }
        }

        if (errors.length() > 0) {
            NotificationUtil.showWarning("验证失败：\n" + errors.toString());
            return false;
        }

        return true;
    }
    
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 设置默认表体编码
     */
    private void setDefaultBodyCode() {
        if (BillType.fromCode(billTypeCombo.getValue()) == BillType.MULTI) {
            String billCode = billCodeField.getText();
            if (billCode != null && !billCode.isEmpty()) {
                String defaultBodyCode = billCode + "BVO";
                // 只有当用户未手动修改时才自动设置
                if (bodyCodeField.getText() == null || bodyCodeField.getText().isEmpty()) {
                    bodyCodeField.setText(defaultBodyCode);
                }
            }
        }
    }
}
