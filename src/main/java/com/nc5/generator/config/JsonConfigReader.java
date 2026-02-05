package com.nc5.generator.config;

import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON 字段配置文件读取器（仅读取字段配置）
 */
public class JsonConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(JsonConfigReader.class);

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

        // 只读取字段信息
        if (jsonObject.has("headFields")) {
            JsonArray headFieldsArray = jsonObject.getAsJsonArray("headFields");
            List<FieldConfig> headFields = parseFieldArray(headFieldsArray);
            config.setHeadFields(headFields);
        }

        if (jsonObject.has("bodyFields")) {
            JsonArray bodyFieldsArray = jsonObject.getAsJsonArray("bodyFields");
            List<FieldConfig> bodyFields = parseFieldArray(bodyFieldsArray);
            config.setBodyFields(bodyFields);
        }

        logger.info("Successfully read field config from JSON: {}", filePath);
        return config;
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
}
