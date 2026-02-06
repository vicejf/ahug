package com.nc5.generator.service;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.config.JsonConfigFile;
import com.nc5.generator.config.XmlConfigParser;
import com.nc5.generator.config.XmlConfigWriter;
import com.nc5.generator.fx.model.BillConfigModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * 配置文件管理服务
 * 负责配置文件的加载、保存、最近文件管理等
 */
public class ConfigFileService {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigFileService.class);
    private static final int MAX_RECENT_FILES = 10;
    private static final String RECENT_FILES_FILENAME = "recent_files.txt";

    // 单据配置模型（承载当前正在编辑的配置）
    private final BillConfigModel billConfigModel;

    // JSON 字段配置读写器（单一入口，避免在各处重复 new）
    private final JsonConfigFile jsonConfigFile = new JsonConfigFile();

    private final ObservableList<String> recentFiles = FXCollections.observableArrayList();
    private File currentConfigFile;
    
    public ConfigFileService(BillConfigModel billConfigModel) {
        this.billConfigModel = billConfigModel;
        loadRecentFiles();
    }
    
    // ==================== 当前文件管理 ====================
    
    public File getCurrentConfigFile() {
        return currentConfigFile;
    }
    
    public void setCurrentConfigFile(File file) {
        this.currentConfigFile = file;
    }
    
    // ==================== 最近文件管理 ====================
    
    public ObservableList<String> getRecentFiles() {
        return recentFiles;
    }
    
    public void addToRecentFiles(String filePath) {
        recentFiles.remove(filePath);
        recentFiles.add(0, filePath);
        while (recentFiles.size() > MAX_RECENT_FILES) {
            recentFiles.remove(recentFiles.size() - 1);
        }
        saveRecentFiles();
    }
    
    public void clearRecentFiles() {
        recentFiles.clear();
        saveRecentFiles();
    }
    
    public String getMostRecentFile() {
        return recentFiles.isEmpty() ? null : recentFiles.get(0);
    }
    
    private void saveRecentFiles() {
        File recentFile = getRecentFilesFile();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(recentFile, StandardCharsets.UTF_8))) {
            for (String filePath : recentFiles) {
                writer.write(filePath);
                writer.newLine();
            }
        } catch (IOException e) {
            logger.error("保存最近文件列表失败: {}", e.getMessage());
        }
    }
    
    private void loadRecentFiles() {
        File recentFile = getRecentFilesFile();
        if (!recentFile.exists()) return;
        
        try {
            List<String> lines = Files.readAllLines(recentFile.toPath(), StandardCharsets.UTF_8);
            recentFiles.clear();
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty() && new File(line).exists()) {
                    recentFiles.add(line);
                }
            }
        } catch (IOException e) {
            logger.error("加载最近文件列表失败: {}", e.getMessage());
        }
    }
    
    private File getRecentFilesFile() {
        return new File(System.getProperty("user.dir"), RECENT_FILES_FILENAME);
    }
    
    // ==================== 文件加载/保存 ====================
    
    public boolean loadConfigFile(File file, ConfigLoadCallback callback) {
        try {
            BillConfig config;
            String filePath = file.getAbsolutePath();
            File parentDir = file.getParentFile();
            
            if (filePath.endsWith(".json")) {
                config = loadFromJson(filePath, parentDir);
                if (config == null) return false;
                String xmlPath = filePath.substring(0, filePath.length() - 5) + ".xml";
                file = new File(xmlPath);
                parentDir = file.getParentFile();
            } else {
                config = loadFromXml(filePath);
                parentDir = new File(filePath).getParentFile();
            }
            
            loadFieldConfigs(config, parentDir);
            billConfigModel.fromBillConfig(config);
            currentConfigFile = file;
            
            if (callback != null) callback.onSuccess(file);
            return true;
        } catch (Exception e) {
            logger.error("加载配置文件失败", e);
            if (callback != null) callback.onError(e);
            return false;
        }
    }
    
    private BillConfig loadFromJson(String filePath, File parentDir) throws Exception {
        String xmlPath = filePath.substring(0, filePath.length() - 5) + ".xml";
        File xmlFile = new File(xmlPath);
        BillConfig config;
        if (xmlFile.exists()) {
            config = new XmlConfigParser().parse(xmlPath);
            // 使用 JSON 文件中的字段信息覆盖 XML 中的字段定义
            mergeFieldsFromJsonFile(config, new File(filePath), "JSON");
        } else {
            config = jsonConfigFile.readFieldsOnly(filePath);
        }
        return config;
    }
    
    private BillConfig loadFromXml(String filePath) throws Exception {
        String xmlPath = filePath.endsWith(".xml") ? filePath : filePath + ".xml";
        return new XmlConfigParser().parse(xmlPath);
    }
    
    private void loadFieldConfigs(BillConfig config, File parentDir) {
        if (config.getHeadFieldsPath() != null && !config.getHeadFieldsPath().isEmpty()) {
            File headFieldsFile = resolveFile(config.getHeadFieldsPath(), parentDir);
            if (headFieldsFile.exists()) {
                mergeFieldsFromJsonFile(config, headFieldsFile, "表头");
            }
        }

        if (config.getBodyFieldsPath() != null && !config.getBodyFieldsPath().isEmpty()) {
            File bodyFieldsFile = resolveFile(config.getBodyFieldsPath(), parentDir);
            if (bodyFieldsFile.exists()) {
                mergeFieldsFromJsonFile(config, bodyFieldsFile, "表体");
            }
        }
    }
    
    private File resolveFile(String path, File parentDir) {
        File file = new File(path);
        return file.isAbsolute() ? file : new File(parentDir, path);
    }
    
    public boolean saveConfigFile(File file, ConfigSaveCallback callback) {
        try {
            BillConfig config = billConfigModel.toBillConfig();
            String filePath = file.getAbsolutePath();
            
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            String xmlPath = getXmlPath(filePath);
            String billCode = config.getBillCode();
            String bodyCode = config.getBodyCode();
            
            String headFieldsFileName = getHeadFieldsFileName(billCode);
            String headFieldsPath = new File(parentDir, headFieldsFileName).getAbsolutePath();
            
            String bodyFieldsFileName = getBodyFieldsFileName(billCode, bodyCode);
            String bodyFieldsPath = new File(parentDir, bodyFieldsFileName).getAbsolutePath();
            
            config.setHeadFieldsPath(headFieldsFileName);
            config.setBodyFieldsPath(bodyFieldsFileName);

            new XmlConfigWriter().write(config, xmlPath);

            if (config.getHeadFields() != null && !config.getHeadFields().isEmpty()) {
                jsonConfigFile.writeHeadFields(config, headFieldsPath);
            }
            if (config.getBodyFields() != null && !config.getBodyFields().isEmpty()) {
                jsonConfigFile.writeBodyFields(config, bodyFieldsPath);
            }
            
            currentConfigFile = new File(xmlPath);
            
            if (callback != null) {
                callback.onSuccess(currentConfigFile, headFieldsFileName, bodyFieldsFileName);
            }
            return true;
        } catch (Exception e) {
            logger.error("保存配置文件失败", e);
            if (callback != null) callback.onError(e);
            return false;
        }
    }
    
    private String getXmlPath(String filePath) {
        if (filePath.endsWith(".xml")) return filePath;
        if (filePath.endsWith(".json")) return filePath.substring(0, filePath.length() - 5) + ".xml";
        return filePath + ".xml";
    }
    
    private String getHeadFieldsFileName(String billCode) {
        return (billCode != null && !billCode.isEmpty() ? billCode : "head") + ".json";
    }
    
    private String getBodyFieldsFileName(String billCode, String bodyCode) {
        if (bodyCode != null && !bodyCode.isEmpty()) return bodyCode + ".json";
        if (billCode != null && !billCode.isEmpty()) return billCode + "BVO.json";
        return "body.json";
    }

    /**
     * 从指定 JSON 字段配置文件中读取字段，并合并到目标 BillConfig 中。
     * 该方法是 JsonConfigFile 的唯一调用入口，保证字段 JSON 的读写逻辑集中在本服务层。
     *
     * @param target    需要合并字段配置的 BillConfig
     * @param jsonFile  字段配置 JSON 文件
     * @param fieldType 日志用字段类型描述（如“表头”、“表体”、“JSON”）
     */
    private void mergeFieldsFromJsonFile(BillConfig target, File jsonFile, String fieldType) {
        try {
            BillConfig jsonConfig = jsonConfigFile.readFieldsOnly(jsonFile.getAbsolutePath());

            if (jsonConfig.getHeadFields() != null && !jsonConfig.getHeadFields().isEmpty()) {
                target.setHeadFields(jsonConfig.getHeadFields());
            }
            if (jsonConfig.getBodyFields() != null && !jsonConfig.getBodyFields().isEmpty()) {
                target.setBodyFields(jsonConfig.getBodyFields());
            }
        } catch (Exception e) {
            logger.warn("加载{}字段配置失败: {}", fieldType, jsonFile.getAbsolutePath(), e);
        }
    }
    
    // ==================== 对话框 ====================
    
    public File showSaveDialog(Window ownerWindow, File initialDirectory, String defaultFileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存全局配置");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML配置文件", "*.xml"),
                new FileChooser.ExtensionFilter("JSON字段配置", "*.json"));
        
        if (initialDirectory != null && initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }
        fileChooser.setInitialFileName(defaultFileName);
        
        return fileChooser.showSaveDialog(ownerWindow);
    }
    
    public File showOpenDialog(Window ownerWindow, File initialDirectory) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开全局配置");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML配置文件", "*.xml"),
                new FileChooser.ExtensionFilter("JSON字段配置", "*.json"));
        
        if (initialDirectory != null && initialDirectory.exists() && initialDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }
        
        return fileChooser.showOpenDialog(ownerWindow);
    }
    
    // ==================== 回调接口 ====================
    
    public interface ConfigLoadCallback {
        void onSuccess(File file);
        void onError(Exception e);
    }
    
    public interface ConfigSaveCallback {
        void onSuccess(File xmlFile, String headFieldsFileName, String bodyFieldsFileName);
        void onError(Exception e);
    }
}
