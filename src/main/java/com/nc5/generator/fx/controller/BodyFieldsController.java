package com.nc5.generator.fx.controller;

import com.nc5.generator.fx.model.FieldConfigModel;
import javafx.collections.ObservableList;

/**
 * 表体字段标签页控制器
 */
public class BodyFieldsController extends FieldsController {

    @Override
    protected ObservableList<FieldConfigModel> getFieldsList() {
        return billConfigModel.getBodyFields();
    }

    @Override
    protected boolean isBodyCodeSelectorEnabled() {
        return true;
    }

    @Override
    protected boolean isImportTemplateEnabled() {
        return true;
    }

    @Override
    protected void setupAdditionalColumns() {
        // 表体没有额外列
    }

    @Override
    protected String getFieldCode() {
        // 如果有表体编码选择器且有值，则使用当前选择的编码
        if (bodyCodeCombo != null && bodyCodeCombo.getValue() != null && !bodyCodeCombo.getValue().isEmpty()) {
            return bodyCodeCombo.getValue();
        }
        // 否则使用配置中的 bodyCode
        if (billConfigModel != null && billConfigModel.getBodyCode() != null && !billConfigModel.getBodyCode().isEmpty()) {
            return billConfigModel.getBodyCode();
        }
        // 默认使用 "body"
        return "body";
    }
}
