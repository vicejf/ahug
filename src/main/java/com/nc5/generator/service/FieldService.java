package com.nc5.generator.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nc5.generator.config.FieldConfig;
import com.nc5.generator.fx.model.BillConfigModel;
import com.nc5.generator.fx.model.FieldConfigModel;
import com.nc5.generator.parser.SqlTableParser;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * 字段服务
 * 负责字段的导入、模板管理、持久化等操作
 */
public class FieldService {

    private static final Logger logger = LoggerFactory.getLogger(FieldService.class);
    private static final String DEFAULT_TEMPLATE_PATH = "templates/field-template.json";
    private static final String LAST_SAVE_DIR_KEY = "lastSaveDirectory";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // ==================== 字段导入 ====================

    /**
     * 从内容导入字段（自动识别JSON或SQL）
     */
    public ImportResult importFromContent(String content, ObservableList<FieldConfigModel> targetList) {
        content = content.trim();
        if (isSqlStatement(content)) {
            return importFromSql(content, targetList);
        }
        return importFromJson(content, targetList);
    }

    private boolean isSqlStatement(String content) {
        String upper = content.toUpperCase();
        return upper.contains("CREATE TABLE") || upper.contains("CREATE");
    }

    public ImportResult importFromSql(String sqlContent, ObservableList<FieldConfigModel> targetList) {
        try {
            List<SqlTableParser.SimpleFieldTemplate> sqlFields =
                    SqlTableParser.parseCreateTableSimple(sqlContent);

            if (sqlFields == null || sqlFields.isEmpty()) {
                return ImportResult.error("未从SQL语句中解析到字段");
            }

            int duplicateCount = 0;
            int addedCount = 0;

            for (SqlTableParser.SimpleFieldTemplate template : sqlFields) {
                if (isDuplicate(template.name, targetList)) {
                    duplicateCount++;
                    continue;
                }
                targetList.add(createFieldFromSimpleTemplate(template));
                addedCount++;
            }

            return ImportResult.success(addedCount, duplicateCount);
        } catch (Exception e) {
            return ImportResult.error("SQL解析失败: " + e.getMessage());
        }
    }

    public ImportResult importFromJson(String jsonContent, ObservableList<FieldConfigModel> targetList) {
        try {
            FieldTemplate[] templates = gson.fromJson(jsonContent, FieldTemplate[].class);
            if (templates != null && templates.length > 0) {
                return importFieldTemplates(templates, targetList);
            }

            SqlTableParser.SimpleFieldTemplate[] simpleTemplates =
                    gson.fromJson(jsonContent, SqlTableParser.SimpleFieldTemplate[].class);
            if (simpleTemplates != null && simpleTemplates.length > 0) {
                return importSimpleFieldTemplates(simpleTemplates, targetList);
            }

            FieldConfigList configList = gson.fromJson(jsonContent, FieldConfigList.class);
            if (configList != null && configList.getFields() != null && !configList.getFields().isEmpty()) {
                return importFieldConfigs(configList.getFields(), targetList);
            }

            return ImportResult.error("JSON格式不正确，无法识别字段配置");
        } catch (Exception e) {
            return ImportResult.error("JSON解析失败: " + e.getMessage());
        }
    }

    private ImportResult importFieldTemplates(FieldTemplate[] templates, ObservableList<FieldConfigModel> targetList) {
        int duplicateCount = 0;
        int addedCount = 0;

        for (FieldTemplate template : templates) {
            if (isDuplicate(template.name, targetList)) {
                duplicateCount++;
                continue;
            }
            targetList.add(createFieldFromTemplate(template));
            addedCount++;
        }

        return ImportResult.success(addedCount, duplicateCount);
    }

    private ImportResult importSimpleFieldTemplates(SqlTableParser.SimpleFieldTemplate[] templates,
                                                    ObservableList<FieldConfigModel> targetList) {
        int duplicateCount = 0;
        int addedCount = 0;

        for (SqlTableParser.SimpleFieldTemplate template : templates) {
            if (isDuplicate(template.name, targetList)) {
                duplicateCount++;
                continue;
            }
            targetList.add(createFieldFromSimpleTemplate(template));
            addedCount++;
        }

        return ImportResult.success(addedCount, duplicateCount);
    }

    private ImportResult importFieldConfigs(List<FieldConfig> fieldConfigs, ObservableList<FieldConfigModel> targetList) {
        int duplicateCount = 0;
        int addedCount = 0;

        for (FieldConfig fieldConfig : fieldConfigs) {
            if (isDuplicate(fieldConfig.getName(), targetList)) {
                duplicateCount++;
                continue;
            }
            targetList.add(new FieldConfigModel(fieldConfig));
            addedCount++;
        }

        return ImportResult.success(addedCount, duplicateCount);
    }

    private boolean isDuplicate(String name, ObservableList<FieldConfigModel> targetList) {
        return targetList.stream().anyMatch(f -> f.getName().equals(name));
    }

    private FieldConfigModel createFieldFromTemplate(FieldTemplate template) {
        FieldConfigModel field = new FieldConfigModel();
        field.setName(template.name);
        field.setLabel(template.label);
        field.setType(template.type);
        field.setDbType(template.dbType);
        field.setLength(template.length);
        field.setUiType(template.uiType);
        field.setRequired(template.required);
        field.setPrimaryKey(template.primaryKey);
        field.setEditable(template.editable);
        return field;
    }

    private FieldConfigModel createFieldFromSimpleTemplate(SqlTableParser.SimpleFieldTemplate template) {
        FieldConfigModel field = new FieldConfigModel();
        field.setName(template.name);
        field.setLabel(template.label != null && !template.label.isEmpty() ? template.label : template.name);
        field.setType(template.type != null ? template.type : "String");
        field.setDbType(template.dbType != null ? template.dbType : "VARCHAR2(50)");
        field.setLength(template.length != null ? template.length : 50);
        field.setUiType(template.uiType != null ? template.uiType : "Text");
        field.setRequired(template.required != null && template.required);
        field.setPrimaryKey(template.primary != null && template.primary);
        field.setEditable(template.editable != null ? template.editable : true);
        return field;
    }

    // ==================== 模板管理 ====================

    /**
     * 从模板文件导入基本字段
     */
    public TemplateResult importBasicFields(ObservableList<FieldConfigModel> targetList,
                                            BillConfigModel billConfigModel,
                                            String templateResourcePath) {
        addPrimaryKeyField(targetList, billConfigModel);

        try {
            String path = templateResourcePath != null ? templateResourcePath : DEFAULT_TEMPLATE_PATH;
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);

            if (inputStream == null) {
                return TemplateResult.error("模板文件未找到: " + path);
            }

            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                FieldTemplate[] templates = gson.fromJson(reader, FieldTemplate[].class);

                int duplicateCount = 0;
                int addedCount = 0;

                for (FieldTemplate template : templates) {
                    if (isDuplicate(template.name, targetList)) {
                        duplicateCount++;
                        continue;
                    }
                    targetList.add(createFieldFromTemplate(template));
                    addedCount++;
                }

                return TemplateResult.success(addedCount, duplicateCount);
            }
        } catch (Exception e) {
            return TemplateResult.error("导入基本字段失败: " + e.getMessage());
        }
    }

    /**
     * 添加主键字段
     */
    public void addPrimaryKeyField(ObservableList<FieldConfigModel> targetList,
                                   BillConfigModel billConfigModel) {
        if (billConfigModel == null || billConfigModel.getBillCode() == null
                || billConfigModel.getBillCode().isEmpty()) {
            return;
        }

        String billCode = billConfigModel.getBillCode();
        String suffix = determineSuffix(billConfigModel);
        String pkFieldName = "pk_" + billCode.toLowerCase() + "_" + suffix;

        if (targetList.stream().anyMatch(f -> f.getName().equals(pkFieldName))) {
            return;
        }

        FieldConfigModel pkField = createPrimaryKeyField(pkFieldName);
        targetList.add(0, pkField);
    }

    private String determineSuffix(BillConfigModel billConfigModel) {
        return "h";
    }

    private FieldConfigModel createPrimaryKeyField(String name) {
        FieldConfigModel field = new FieldConfigModel();
        field.setName(name);
        field.setLabel("主键");
        field.setType("String");
        field.setDbType("VARCHAR2(20)");
        field.setLength(20);
        field.setUiType("Text");
        field.setRequired(true);
        field.setPrimaryKey(true);
        field.setEditable(false);
        return field;
    }

    /**
     * 从源字段复制创建新字段
     */
    public FieldConfigModel copyFieldFrom(FieldConfigModel source) {
        FieldConfigModel newField = new FieldConfigModel();
        newField.setName(source.getName());
        newField.setLabel(source.getLabel());
        newField.setType(source.getType());
        newField.setDbType(source.getDbType());
        newField.setLength(source.getLength());
        newField.setRequired(source.isRequired());
        newField.setPrimaryKey(source.isPrimaryKey());
        newField.setUiType(source.getUiType());
        newField.setEditable(source.isEditable());
        return newField;
    }

    // ==================== 持久化 ====================

    /**
     * 保存字段到JSON文件
     */
    public boolean saveFieldsToFile(ObservableList<FieldConfigModel> fields,
                                    File file,
                                    BillConfigModel billConfigModel) {
        try {
            List<FieldConfig> fieldConfigs = new ArrayList<>();
            for (FieldConfigModel model : fields) {
                fieldConfigs.add(model.toFieldConfig());
            }

            FieldConfigList configList = new FieldConfigList();
            configList.setFields(fieldConfigs);

            if (billConfigModel != null) {
                configList.setBillCode(billConfigModel.getBillCode());
                configList.setBillName(billConfigModel.getBillName());
                configList.setBillType(billConfigModel.getBillType());
                configList.setExportTime(java.time.LocalDateTime.now().toString());
            }

            String json = gson.toJson(configList);
            try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                writer.write(json);
            }

            saveLastDirectory(file.getParentFile());
            return true;
        } catch (Exception e) {
            logger.error("保存字段配置失败", e);
            return false;
        }
    }

    /**
     * 从JSON文件加载字段
     */
    public List<FieldConfigModel> loadFieldsFromFile(File file) {
        try (Reader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            FieldConfigList configList = gson.fromJson(reader, FieldConfigList.class);

            if (configList != null && configList.getFields() != null) {
                List<FieldConfigModel> result = new ArrayList<>();
                for (FieldConfig fieldConfig : configList.getFields()) {
                    result.add(new FieldConfigModel(fieldConfig));
                }
                return result;
            }
        } catch (Exception e) {
            logger.error("加载字段配置失败: {}", file.getAbsolutePath(), e);
        }
        return new ArrayList<>();
    }

    /**
     * 从路径加载字段（支持相对路径）
     */
    public List<FieldConfigModel> loadFieldsFromPath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return new ArrayList<>();
        }

        File file = new File(filePath);
        if (!file.isAbsolute()) {
            file = new File(getConfigDirectory(), filePath);
        }

        if (!file.exists()) {
            logger.warn("字段配置文件不存在: {}", file.getPath());
            return new ArrayList<>();
        }

        return loadFieldsFromFile(file);
    }

    /**
     * 获取默认文件名
     */
    public String getDefaultFileName(String billCode, String fieldCode) {
        if (billCode != null && !billCode.isEmpty()) {
            return billCode + "-" + (fieldCode != null ? fieldCode : "fields") + ".json";
        }
        return "fields.json";
    }

    /**
     * 获取上次保存的目录
     */
    public File getLastSaveDirectory() {
        try {
            Preferences prefs = Preferences.userNodeForPackage(FieldService.class);
            String lastDir = prefs.get(LAST_SAVE_DIR_KEY, null);
            if (lastDir != null) {
                File dir = new File(lastDir);
                if (dir.exists() && dir.isDirectory()) {
                    return dir;
                }
            }
        } catch (Exception e) {
            logger.warn("获取上次保存目录失败", e);
        }
        return getConfigDirectory();
    }

    /**
     * 保存最后使用的目录
     */
    public void saveLastDirectory(File directory) {
        if (directory != null && directory.isDirectory()) {
            try {
                Preferences prefs = Preferences.userNodeForPackage(FieldService.class);
                prefs.put(LAST_SAVE_DIR_KEY, directory.getAbsolutePath());
            } catch (Exception e) {
                logger.warn("保存上次目录失败", e);
            }
        }
    }

    /**
     * 获取配置目录
     */
    public File getConfigDirectory() {
        File configDir = new File(System.getProperty("user.dir"), "config");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        return configDir;
    }

    /**
     * 将绝对路径转换为相对路径
     */
    public String toRelativePath(String absolutePath) {
        try {
            return getConfigDirectory().toPath()
                    .relativize(new File(absolutePath).toPath()).toString();
        } catch (Exception e) {
            return absolutePath;
        }
    }

    // ==================== 数据类 ====================

    public static class FieldTemplate {
        public String name;
        public String label;
        public String type;
        public String dbType;
        public int length;
        public String uiType;
        public boolean required;
        public boolean primaryKey;
        public boolean editable;
    }

    public static class FieldConfigList {
        private String billCode;
        private String billName;
        private String billType;
        private String exportTime;
        private List<FieldConfig> fields;

        public String getBillCode() { return billCode; }
        public void setBillCode(String billCode) { this.billCode = billCode; }
        public String getBillName() { return billName; }
        public void setBillName(String billName) { this.billName = billName; }
        public String getBillType() { return billType; }
        public void setBillType(String billType) { this.billType = billType; }
        public String getExportTime() { return exportTime; }
        public void setExportTime(String exportTime) { this.exportTime = exportTime; }
        public List<FieldConfig> getFields() { return fields; }
        public void setFields(List<FieldConfig> fields) { this.fields = fields; }
    }

    // ==================== 结果类 ====================

    public static class ImportResult {
        public final boolean success;
        public final int addedCount;
        public final int duplicateCount;
        public final String errorMessage;

        private ImportResult(boolean success, int addedCount, int duplicateCount, String errorMessage) {
            this.success = success;
            this.addedCount = addedCount;
            this.duplicateCount = duplicateCount;
            this.errorMessage = errorMessage;
        }

        public static ImportResult success(int addedCount, int duplicateCount) {
            return new ImportResult(true, addedCount, duplicateCount, null);
        }

        public static ImportResult error(String message) {
            return new ImportResult(false, 0, 0, message);
        }

        public String getMessage() {
            if (!success) return errorMessage;
            String msg = "已导入 " + addedCount + " 个字段";
            if (duplicateCount > 0) {
                msg += "（跳过 " + duplicateCount + " 个重复字段）";
            }
            return msg;
        }
    }

    public static class TemplateResult {
        public final boolean success;
        public final int addedCount;
        public final int duplicateCount;
        public final String errorMessage;

        private TemplateResult(boolean success, int addedCount, int duplicateCount, String errorMessage) {
            this.success = success;
            this.addedCount = addedCount;
            this.duplicateCount = duplicateCount;
            this.errorMessage = errorMessage;
        }

        public static TemplateResult success(int addedCount, int duplicateCount) {
            return new TemplateResult(true, addedCount, duplicateCount, null);
        }

        public static TemplateResult error(String message) {
            return new TemplateResult(false, 0, 0, message);
        }

        public String getMessage() {
            if (!success) return errorMessage;
            String msg = "已导入 " + addedCount + " 个基本字段";
            if (duplicateCount > 0) {
                msg += "（跳过 " + duplicateCount + " 个重复字段）";
            }
            return msg;
        }
    }
}
