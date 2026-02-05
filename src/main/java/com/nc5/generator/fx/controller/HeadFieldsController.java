package com.nc5.generator.fx.controller;

import com.nc5.generator.fx.model.FieldConfigModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

/**
 * 表头字段标签页控制器
 */
public class HeadFieldsController extends BaseFieldsController {

    @FXML private TableColumn<FieldConfigModel, Boolean> primaryKeyColumn;
    @FXML private TableColumn<FieldConfigModel, String> uiTypeColumn;

    @Override
    protected ObservableList<FieldConfigModel> getFieldsList() {
        return billConfigModel.getHeadFields();
    }

    @Override
    protected boolean isEnumConfigListenerEnabled() {
        return true;
    }

    @Override
    protected boolean isImportTemplateEnabled() {
        return true;
    }

    @Override
    protected String getFieldCode() {
        return "head";
    }

    @Override
    protected void setupAdditionalColumns() {
        // 主键
        primaryKeyColumn.setCellValueFactory(cellData -> cellData.getValue().primaryKeyProperty());
        primaryKeyColumn.setCellFactory(CheckBoxTableCell.forTableColumn(primaryKeyColumn));

        // UI类型
        uiTypeColumn.setCellValueFactory(cellData -> cellData.getValue().uiTypeProperty());
        uiTypeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    }
}
