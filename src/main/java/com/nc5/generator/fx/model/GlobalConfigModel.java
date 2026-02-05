package com.nc5.generator.fx.model;

import com.nc5.generator.config.GlobalConfig;
import javafx.beans.property.*;

/**
 * GlobalConfig的JavaFX属性包装类
 * 用于在UI中绑定和编辑全局配置
 */
public class GlobalConfigModel {

    private final BooleanProperty generateClient = new SimpleBooleanProperty(true);
    private final BooleanProperty generateBusiness = new SimpleBooleanProperty(true);
    private final BooleanProperty generateMetadata = new SimpleBooleanProperty(false);
    private final StringProperty author = new SimpleStringProperty("Flynn Chen");
    private final StringProperty sourcePath = new SimpleStringProperty("");
    private final StringProperty outputDir = new SimpleStringProperty("");
    private final BooleanProperty syncAfterGenerate = new SimpleBooleanProperty(true);

    public GlobalConfigModel() {
    }

    public GlobalConfigModel(GlobalConfig config) {
        fromGlobalConfig(config);
    }

    /**
     * 从GlobalConfig加载数据
     */
    public void fromGlobalConfig(GlobalConfig config) {
        if (config == null) {
            return;
        }

        generateClient.set(config.isGenerateClient());
        generateBusiness.set(config.isGenerateBusiness());
        generateMetadata.set(config.isGenerateMetadata());
        author.set(config.getAuthor() != null && !config.getAuthor().isEmpty() ? config.getAuthor() : "Flynn Chen");
        sourcePath.set(config.getSourcePath() != null ? config.getSourcePath() : "");
        outputDir.set(config.getOutputDir() != null ? config.getOutputDir() : "");
        syncAfterGenerate.set(config.isSyncAfterGenerate());
    }

    /**
     * 转换为GlobalConfig
     */
    public GlobalConfig toGlobalConfig() {
        GlobalConfig config = new GlobalConfig();
        config.setGenerateClient(generateClient.get());
        config.setGenerateBusiness(generateBusiness.get());
        config.setGenerateMetadata(generateMetadata.get());
        config.setAuthor(author.get());
        config.setSourcePath(sourcePath.get());
        config.setOutputDir(outputDir.get());
        config.setSyncAfterGenerate(syncAfterGenerate.get());
        return config;
    }

    /**
     * 清空所有数据
     */
    public void clear() {
        generateClient.set(true);
        generateBusiness.set(true);
        generateMetadata.set(false);
        author.set("Flynn Chen");
        sourcePath.set("");
        outputDir.set("");
        syncAfterGenerate.set(true);
    }

    // Property getters
    public BooleanProperty generateClientProperty() { return generateClient; }
    public BooleanProperty generateBusinessProperty() { return generateBusiness; }
    public BooleanProperty generateMetadataProperty() { return generateMetadata; }
    public StringProperty authorProperty() { return author; }
    public StringProperty sourcePathProperty() { return sourcePath; }
    public StringProperty outputDirProperty() { return outputDir; }
    public BooleanProperty syncAfterGenerateProperty() { return syncAfterGenerate; }

    // Getters
    public boolean isGenerateClient() { return generateClient.get(); }
    public boolean isGenerateBusiness() { return generateBusiness.get(); }
    public boolean isGenerateMetadata() { return generateMetadata.get(); }
    public String getAuthor() { return author.get(); }
    public String getSourcePath() { return sourcePath.get(); }
    public String getOutputDir() { return outputDir.get(); }
    public boolean isSyncAfterGenerate() { return syncAfterGenerate.get(); }

    // Setters
    public void setGenerateClient(boolean value) { generateClient.set(value); }
    public void setGenerateBusiness(boolean value) { generateBusiness.set(value); }
    public void setGenerateMetadata(boolean value) { generateMetadata.set(value); }
    public void setAuthor(String value) { author.set(value); }
    public void setSourcePath(String value) { sourcePath.set(value); }
    public void setOutputDir(String value) { outputDir.set(value); }
    public void setSyncAfterGenerate(boolean value) { syncAfterGenerate.set(value); }
}
