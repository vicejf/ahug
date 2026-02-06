package com.nc5.generator.config;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON 字段配置文件处理器
 * 统一处理 JSON 配置文件的读写操作
 */
public class JsonConfigFile {

    private static final Logger logger = LoggerFactory.getLogger(JsonConfigFile.class);
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 从 JSON 文件读取字段配置，返回包含这些字段的 BillConfig 对象
     * 其他配置信息保持为 null，由调用者从 XML 中加载
     *
     * @param filePath JSON 文件路径
     * @return BillConfig 对象（仅包含字段配置）
     * @throws IOException 如果文件读取失败
     */
    public BillConfig readFieldsOnly(String filePath) throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

        BillConfig config = new BillConfig();

        // 读取表头字段
        if (jsonObject.has("headFields")) {
            JsonArray headFieldsArray = jsonObject.getAsJsonArray("headFields");
            List<FieldConfig> headFields = parseFieldArray(headFieldsArray);
            config.setHeadFields(headFields);
        }

        // 读取表体字段
        if (jsonObject.has("bodyFields")) {
            JsonArray bodyFieldsArray = jsonObject.getAsJsonArray("bodyFields");
            List<FieldConfig> bodyFields = parseFieldArray(bodyFieldsArray);
            config.setBodyFields(bodyFields);
        }

        logger.info("成功从 JSON 读取字段配置: {}", filePath);
        return config;
    }

    /**
     * 将表头和表体字段配置写入同一个 JSON 文件
     *
     * @param config   BillConfig 对象
     * @param filePath JSON 文件路径
     * @throws IOException 如果文件写入失败
     */
    public void write(BillConfig config, String filePath) throws IOException {
        File file = new File(filePath);

        // 创建目录
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 构建 JSON 对象，只保存字段信息
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
            logger.info("字段配置已保存至 JSON: {}", filePath);
        }
    }

    /**
     * 只将表头字段配置写入 JSON 文件
     *
     * @param config   BillConfig 对象
     * @param filePath JSON 文件路径
     * @throws IOException 如果文件写入失败
     */
    public void writeHeadFields(BillConfig config, String filePath) throws IOException {
        File file = new File(filePath);

        // 创建目录
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 构建 JSON 对象，只保存表头字段
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
            logger.info("表头字段配置已保存至 JSON: {}", filePath);
        }
    }

    /**
     * 只将表体字段配置写入 JSON 文件
     *
     * @param config   BillConfig 对象
     * @param filePath JSON 文件路径
     * @throws IOException 如果文件写入失败
     */
    public void writeBodyFields(BillConfig config, String filePath) throws IOException {
        File file = new File(filePath);

        // 创建目录
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 构建 JSON 对象，只保存表体字段
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
            logger.info("表体字段配置已保存至 JSON: {}", filePath);
        }
    }

    /**
     * 解析字段数组
     */
    private List<FieldConfig> parseFieldArray(JsonArray jsonArray) {
        List<FieldConfig> fields = new ArrayList<>();
        for (JsonElement element : jsonArray) {
            if (element.isJsonObject()) {
                JsonObject fieldObj = element.getAsJsonObject();
                FieldConfig field = parseFieldConfig(fieldObj);
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * 解析单个字段配置
     */
    private FieldConfig parseFieldConfig(JsonObject fieldObj) {
        FieldConfig field = new FieldConfig();

        if (fieldObj.has("name")) {
            field.setName(fieldObj.get("name").getAsString());
        }
        if (fieldObj.has("label")) {
            field.setLabel(fieldObj.get("label").getAsString());
        }
        if (fieldObj.has("type")) {
            field.setType(fieldObj.get("type").getAsString());
        }
        if (fieldObj.has("dbType")) {
            field.setDbType(fieldObj.get("dbType").getAsString());
        }
        if (fieldObj.has("length")) {
            field.setLength(fieldObj.get("length").getAsInt());
        }
        if (fieldObj.has("precision")) {
            field.setPrecision(fieldObj.get("precision").getAsInt());
        }
        if (fieldObj.has("scale")) {
            field.setScale(fieldObj.get("scale").getAsInt());
        }
        if (fieldObj.has("required")) {
            field.setRequired(fieldObj.get("required").getAsBoolean());
        }
        if (fieldObj.has("primaryKey")) {
            field.setPrimaryKey(fieldObj.get("primaryKey").getAsBoolean());
        }
        if (fieldObj.has("editable")) {
            field.setEditable(fieldObj.get("editable").getAsBoolean());
        }
        if (fieldObj.has("visible")) {
            field.setVisible(fieldObj.get("visible").getAsBoolean());
        }
        if (fieldObj.has("uiType")) {
            field.setUiType(fieldObj.get("uiType").getAsString());
        }
        if (fieldObj.has("refTable")) {
            field.setRefTable(fieldObj.get("refTable").getAsString());
        }
        if (fieldObj.has("enumCode")) {
            field.setEnumCode(fieldObj.get("enumCode").getAsString());
        }
        if (fieldObj.has("description")) {
            field.setDescription(fieldObj.get("description").getAsString());
        }

        return field;
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
