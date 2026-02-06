package com.nc5.generator.config;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * 全局生成配置管理器（基于 JavaFX Property + Preferences）
 *
 * 作用：
 * - 为界面层（如 GenerateController）提供可绑定的属性；
 * - 在会话内作为简单的单例缓存；
 * - 通过 Preferences 做轻量持久化，配合 INI 全局配置使用。
 */
public class ConfigManager {

    private static final String PREF_KEY_OUTPUT_DIR = "outputDir";
    private static final String PREF_KEY_SOURCE_PATH = "sourcePath";
    private static final String PREF_KEY_AUTHOR = "author";
    private static final String PREF_KEY_SYNC_AFTER_GENERATE = "syncAfterGenerate";
    private static final String PREF_KEY_GENERATE_CLIENT = "generateClient";
    private static final String PREF_KEY_GENERATE_BUSINESS = "generateBusiness";
    private static final String PREF_KEY_GENERATE_METADATA = "generateMetadata";

    private static final ConfigManager INSTANCE = new ConfigManager();

    private final StringProperty outputDir = new SimpleStringProperty("");
    private final StringProperty sourcePath = new SimpleStringProperty("");
    private final StringProperty author = new SimpleStringProperty("");

    private final BooleanProperty syncAfterGenerate = new SimpleBooleanProperty(true);
    private final BooleanProperty generateClient = new SimpleBooleanProperty(true);
    private final BooleanProperty generateBusiness = new SimpleBooleanProperty(true);
    private final BooleanProperty generateMetadata = new SimpleBooleanProperty(false);

    private final List<Runnable> changeListeners = new ArrayList<>();

    private ConfigManager() {
        // 先从首选项加载一遍
        loadFromPreferences();

        // 任意配置变更时通知监听器
        outputDir.addListener((obs, o, n) -> notifyChangeListeners());
        sourcePath.addListener((obs, o, n) -> notifyChangeListeners());
        author.addListener((obs, o, n) -> notifyChangeListeners());
        syncAfterGenerate.addListener((obs, o, n) -> notifyChangeListeners());
        generateClient.addListener((obs, o, n) -> notifyChangeListeners());
        generateBusiness.addListener((obs, o, n) -> notifyChangeListeners());
        generateMetadata.addListener((obs, o, n) -> notifyChangeListeners());
    }

    public static ConfigManager getInstance() {
        return INSTANCE;
    }

    // ========== 属性访问 ==========

    public StringProperty outputDirProperty() {
        return outputDir;
    }

    public String getOutputDir() {
        return outputDir.get();
    }

    public void setOutputDir(String value) {
        outputDir.set(value);
    }

    public StringProperty sourcePathProperty() {
        return sourcePath;
    }

    public String getSourcePath() {
        return sourcePath.get();
    }

    public void setSourcePath(String value) {
        sourcePath.set(value);
    }

    public StringProperty authorProperty() {
        return author;
    }

    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String value) {
        author.set(value);
    }

    public BooleanProperty syncAfterGenerateProperty() {
        return syncAfterGenerate;
    }

    public boolean isSyncAfterGenerate() {
        return syncAfterGenerate.get();
    }

    public void setSyncAfterGenerate(boolean value) {
        syncAfterGenerate.set(value);
    }

    public BooleanProperty generateClientProperty() {
        return generateClient;
    }

    public boolean isGenerateClient() {
        return generateClient.get();
    }

    public void setGenerateClient(boolean value) {
        generateClient.set(value);
    }

    public BooleanProperty generateBusinessProperty() {
        return generateBusiness;
    }

    public boolean isGenerateBusiness() {
        return generateBusiness.get();
    }

    public void setGenerateBusiness(boolean value) {
        generateBusiness.set(value);
    }

    public BooleanProperty generateMetadataProperty() {
        return generateMetadata;
    }

    public boolean isGenerateMetadata() {
        return generateMetadata.get();
    }

    public void setGenerateMetadata(boolean value) {
        generateMetadata.set(value);
    }

    // ========== 批量设置（供 MainController 使用） ==========

    /**
     * 批量设置配置值，通常由全局 INI 配置或模型同步调用。
     */
    public void setConfigValues(String outputDir,
                                String sourcePath,
                                boolean syncAfterGenerate,
                                boolean generateClient,
                                boolean generateBusiness,
                                boolean generateMetadata,
                                String author) {
        setOutputDir(outputDir);
        setSourcePath(sourcePath);
        setSyncAfterGenerate(syncAfterGenerate);
        setGenerateClient(generateClient);
        setGenerateBusiness(generateBusiness);
        setGenerateMetadata(generateMetadata);
        setAuthor(author);
    }

    // ========== 变更监听 ==========

    public void addChangeListener(Runnable listener) {
        if (listener != null && !changeListeners.contains(listener)) {
            changeListeners.add(listener);
        }
    }

    private void notifyChangeListeners() {
        for (Runnable listener : new ArrayList<>(changeListeners)) {
            try {
                listener.run();
            } catch (Exception ignored) {
                // 保持简单，避免监听器异常影响主流程
            }
        }
    }

    // ========== Preferences 持久化 ==========

    private Preferences prefs() {
        return Preferences.userNodeForPackage(ConfigManager.class);
    }

    private void loadFromPreferences() {
        Preferences p = prefs();
        setOutputDir(p.get(PREF_KEY_OUTPUT_DIR, getOutputDir()));
        setSourcePath(p.get(PREF_KEY_SOURCE_PATH, getSourcePath()));
        setAuthor(p.get(PREF_KEY_AUTHOR, getAuthor()));

        setSyncAfterGenerate(p.getBoolean(PREF_KEY_SYNC_AFTER_GENERATE, isSyncAfterGenerate()));
        setGenerateClient(p.getBoolean(PREF_KEY_GENERATE_CLIENT, isGenerateClient()));
        setGenerateBusiness(p.getBoolean(PREF_KEY_GENERATE_BUSINESS, isGenerateBusiness()));
        setGenerateMetadata(p.getBoolean(PREF_KEY_GENERATE_METADATA, isGenerateMetadata()));
    }

    /**
     * 将当前配置持久化到 Preferences。
     * 由 GenerateController.saveGlobalConfig() 调用。
     */
    public void saveToPreferences() {
        Preferences p = prefs();
        p.put(PREF_KEY_OUTPUT_DIR, getOutputDir() != null ? getOutputDir() : "");
        p.put(PREF_KEY_SOURCE_PATH, getSourcePath() != null ? getSourcePath() : "");
        p.put(PREF_KEY_AUTHOR, getAuthor() != null ? getAuthor() : "");

        p.putBoolean(PREF_KEY_SYNC_AFTER_GENERATE, isSyncAfterGenerate());
        p.putBoolean(PREF_KEY_GENERATE_CLIENT, isGenerateClient());
        p.putBoolean(PREF_KEY_GENERATE_BUSINESS, isGenerateBusiness());
        p.putBoolean(PREF_KEY_GENERATE_METADATA, isGenerateMetadata());
    }
}

