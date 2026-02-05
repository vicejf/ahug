package com.nc5.generator.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * JSON字段配置文件写入器（仅保存字段配置，不保存其他配置）
 */
public class JsonConfigWriter {
    
    private static final Logger logger = LoggerFactory.getLogger(JsonConfigWriter.class);
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * 将表头和表体字段配置写入同一个JSON文件
     */
    public void write(BillConfig config, String filePath) throws IOException {
        File file = new File(filePath);
        
        // 创建目录
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        // 构建JSON对象，只保存字段信息
        JsonObject jsonObject = new JsonObject();
        
        // 表头字段
        JsonArray headFieldsArray = new JsonArray();
        if (config.getHeadFields() != null) {
            for (FieldConfig field : config.getHeadFields()) {
                headFieldsArray.add(fieldToJsonObject(field));
            }
        }
        jsonObject.add("headFields", headFieldsArray);
        
        // 表体字段
        JsonArray bodyFieldsArray = new JsonArray();
        if (config.getBodyFields() != null) {
            for (FieldConfig field : config.getBodyFields()) {
                bodyFieldsArray.add(fieldToJsonObject(field));
            }
        }
        jsonObject.add("bodyFields", bodyFieldsArray);
        
        // 写入文件
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            String jsonStr = gson.toJson(jsonObject);
            writer.write(jsonStr);
            writer.flush();
            logger.info("字段配置已保存至JSON: {}", filePath);
        }
    }
    
    /**
     * 只将表头字段配置写入JSON文件
     */
    public void writeHeadFields(BillConfig config, String filePath) throws IOException {
        File file = new File(filePath);
        
        // 创建目录
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        // 构建JSON对象，只保存表头字段
        JsonObject jsonObject = new JsonObject();
        
        JsonArray headFieldsArray = new JsonArray();
        if (config.getHeadFields() != null) {
            for (FieldConfig field : config.getHeadFields()) {
                headFieldsArray.add(fieldToJsonObject(field));
            }
        }
        jsonObject.add("headFields", headFieldsArray);
        jsonObject.add("bodyFields", new JsonArray()); // 空表体
        
        // 写入文件
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            String jsonStr = gson.toJson(jsonObject);
            writer.write(jsonStr);
            writer.flush();
            logger.info("表头字段配置已保存至JSON: {}", filePath);
        }
    }
    
    /**
     * 只将表体字段配置写入JSON文件
     */
    public void writeBodyFields(BillConfig config, String filePath) throws IOException {
        File file = new File(filePath);
        
        // 创建目录
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        // 构建JSON对象，只保存表体字段
        JsonObject jsonObject = new JsonObject();
        
        jsonObject.add("headFields", new JsonArray()); // 空表头
        
        JsonArray bodyFieldsArray = new JsonArray();
        if (config.getBodyFields() != null) {
            for (FieldConfig field : config.getBodyFields()) {
                bodyFieldsArray.add(fieldToJsonObject(field));
            }
        }
        jsonObject.add("bodyFields", bodyFieldsArray);
        
        // 写入文件
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            String jsonStr = gson.toJson(jsonObject);
            writer.write(jsonStr);
            writer.flush();
            logger.info("表体字段配置已保存至JSON: {}", filePath);
        }
    }
    
    /**
     * 将 FieldConfig 转换为 JsonObject
     */
    private JsonObject fieldToJsonObject(FieldConfig field) {
        JsonObject obj = new JsonObject();
        
        if (field.getName() != null) {
            obj.addProperty("name", field.getName());
        }
        if (field.getLabel() != null) {
            obj.addProperty("label", field.getLabel());
        }
        if (field.getType() != null) {
            obj.addProperty("type", field.getType());
        }
        if (field.getDbType() != null) {
            obj.addProperty("dbType", field.getDbType());
        }
        if (field.getLength() != null) {
            obj.addProperty("length", field.getLength());
        }
        if (field.getPrecision() != null) {
            obj.addProperty("precision", field.getPrecision());
        }
        if (field.getScale() != null) {
            obj.addProperty("scale", field.getScale());
        }
        if (field.getRequired() != null) {
            obj.addProperty("required", field.getRequired());
        }
        if (field.getPrimaryKey() != null) {
            obj.addProperty("primaryKey", field.getPrimaryKey());
        }
        if (field.getEditable() != null) {
            obj.addProperty("editable", field.getEditable());
        }
        if (field.getVisible() != null) {
            obj.addProperty("visible", field.getVisible());
        }
        if (field.getUiType() != null) {
            obj.addProperty("uiType", field.getUiType());
        }
        if (field.getRefTable() != null) {
            obj.addProperty("refTable", field.getRefTable());
        }
        if (field.getEnumCode() != null) {
            obj.addProperty("enumCode", field.getEnumCode());
        }
        if (field.getDescription() != null) {
            obj.addProperty("description", field.getDescription());
        }
        
        return obj;
    }
}

