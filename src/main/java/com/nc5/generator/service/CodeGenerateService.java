package com.nc5.generator.service;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.config.GlobalConfig;
import com.nc5.generator.fx.CodeGeneratorApp;
import com.nc5.generator.generator.CodeGenerator;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

/**
 * 代码生成服务
 * 负责代码生成和同步的核心逻辑
 */
public class CodeGenerateService {

    private final File outputDir;
    private File projectSrcDir;
    private Consumer<String> logConsumer;
    private Consumer<String> statusConsumer;

    public CodeGenerateService(File outputDir) {
        this.outputDir = outputDir;
    }

    public CodeGenerateService(String outputPath) {
        this.outputDir = new File(outputPath);
    }

    /**
     * 设置日志消费者
     */
    public void setLogConsumer(Consumer<String> logConsumer) {
        this.logConsumer = logConsumer;
    }

    /**
     * 设置状态消费者
     */
    public void setStatusConsumer(Consumer<String> statusConsumer) {
        this.statusConsumer = statusConsumer;
    }

    /**
     * 设置项目源码目录
     */
    public void setProjectSrcDir(File projectSrcDir) {
        this.projectSrcDir = projectSrcDir;
    }

    /**
     * 执行代码生成（带阻塞进度弹窗，完成后显示报告）
     * @param config 单据配置
     * @param owner 弹窗所属窗口
     * @param onComplete 完成回调（在报告关闭后执行）
     */
    public void generateWithProgress(BillConfig config, javafx.stage.Window owner, Runnable onComplete) {
        // 创建阻塞式进度弹窗
        Dialog<Void> progressDialog = new Dialog<>();
        if (owner != null) {
            progressDialog.initOwner(owner);
        }
        progressDialog.setTitle("请稍候");
        progressDialog.setHeaderText("正在生成代码...");

        // 创建圆形进度指示器
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(80, 80);

        // 创建状态标签
        Label statusLabel = new Label("准备生成...");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        // 将进度指示器和状态标签放入容器
        VBox contentBox = new VBox(15, progressIndicator, statusLabel);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));
        contentBox.setPrefWidth(300);
        contentBox.setPrefHeight(200);

        // 设置弹窗内容
        progressDialog.getDialogPane().setContent(contentBox);
        progressDialog.getDialogPane().getButtonTypes().clear();

        // 后台生成任务
        Task<GenerationResult> generateTask = new Task<GenerationResult>() {
            @Override
            protected GenerationResult call() throws Exception {
                long startTime = System.currentTimeMillis();
                
                try {
                    updateMessage("初始化代码生成器...");
                    updateProgress(0.1, 1.0);

                    // 为当前单据应用全局配置（生成开关、输出路径等），但不影响单据XML的保存格式
                    // 必须在创建 CodeGenerator 之前调用，以便使用正确的输出目录
                    applyGlobalConfigForGeneration(config);

                    // 添加调试日志
                    log("生成配置检查:");
                    log("  - GenerateClient: " + config.isGenerateClient());
                    log("  - GenerateBusiness: " + config.isGenerateBusiness());
                    log("  - GenerateMetadata: " + config.isGenerateMetadata());
                    log("  - GlobalConfig exists: " + (config.getGlobalConfig() != null));
                    if (config.getGlobalConfig() != null) {
                        log("  - GlobalConfig.GenerateMetadata: " + config.getGlobalConfig().isGenerateMetadata());
                    }

                    // 从全局配置获取输出目录
                    File actualOutputDir = outputDir;
                    if (config.getGlobalConfig() != null && config.getGlobalConfig().getOutputDir() != null 
                            && !config.getGlobalConfig().getOutputDir().isEmpty()) {
                        actualOutputDir = new File(config.getGlobalConfig().getOutputDir());
                    }

                    CodeGenerator generator = new CodeGenerator(actualOutputDir.getAbsolutePath());

                    updateMessage("生成代码中...");
                    updateProgress(0.3, 1.0);

                    generator.generate(config);

                    updateMessage("生成完成");
                    updateProgress(1.0, 1.0);

                    long duration = System.currentTimeMillis() - startTime;
                    
                    // 统计代码
                    CodeStatisticsService statsService = new CodeStatisticsService();
                    CodeStatisticsService.CodeStatistics stats = statsService.countJavaFiles(actualOutputDir);

                    final File finalOutputDir = actualOutputDir;
                    Platform.runLater(() -> {
                        log("代码生成成功！");
                        log("输出目录: " + finalOutputDir.getAbsolutePath());
                        updateStatus("代码生成完成");
                    });

                    return new GenerationResult(true, duration, stats, null, finalOutputDir);

                } catch (Exception e) {
                    Platform.runLater(() -> {
                        log("生成失败: " + e.getMessage());
                    });
                    return new GenerationResult(false, 0, null, e.getMessage(), null);
                }
            }
        };

        // 绑定进度
        progressIndicator.progressProperty().bind(generateTask.progressProperty());
        statusLabel.textProperty().bind(generateTask.messageProperty());

        // 任务完成后的处理 - 显示报告
        generateTask.setOnSucceeded(e -> {
            progressIndicator.progressProperty().unbind();
            statusLabel.textProperty().unbind();
            
            GenerationResult result = generateTask.getValue();
            
            if (result.success) {
                // 切换到报告视图
                showReportInDialog(progressDialog, result, onComplete);
            } else {
                progressDialog.close();
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });

        generateTask.setOnFailed(e -> {
            progressIndicator.progressProperty().unbind();
            statusLabel.textProperty().unbind();
            progressDialog.close();
            if (onComplete != null) {
                onComplete.run();
            }
        });

        // 在新线程中执行任务
        new Thread(generateTask).start();

        // 显示模态弹窗（阻塞用户操作）
        progressDialog.showAndWait();
    }
    
    /**
     * 在弹窗中显示生成报告
     */
    private void showReportInDialog(Dialog<Void> dialog, GenerationResult result, Runnable onComplete) {
        dialog.setTitle("Report");
        dialog.setHeaderText("NC5 Code Generate Complete！");
        
        // 构建报告内容
        String reportText = buildReportText(result);
        
        Label reportLabel = new Label(reportText);
        reportLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 13px;");
        reportLabel.setWrapText(true);
        
        VBox reportBox = new VBox(10, reportLabel);
        reportBox.setPadding(new Insets(20));
        reportBox.setPrefWidth(450);
        
        dialog.getDialogPane().setContent(reportBox);
        dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);
        
        // 点击OK后关闭并执行回调
        dialog.setResultConverter(buttonType -> {
            dialog.close();
            if (onComplete != null) {
                onComplete.run();
            }
            return null;
        });
    }
    
    /**
     * 构建报告文本
     */
    private String buildReportText(GenerationResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("SpendTime: ").append(formatDuration(result.duration)).append("\n\n");
        
        if (result.stats != null) {
            sb.append("文件: ").append(result.stats.fileCount).append(" 个\n");
            sb.append("代码: ").append(String.format("%,d", result.stats.codeLines)).append(" 行\n\n");
        }
        
        sb.append("OutputPath: \n").append(result.outputDir != null ? result.outputDir.getAbsolutePath() : outputDir.getAbsolutePath());
        
        return sb.toString();
    }
    
    /**
     * 格式化耗时
     */
    private String formatDuration(long durationMs) {
        if (durationMs < 1000) {
            return durationMs + " 毫秒";
        } else if (durationMs < 60000) {
            return String.format("%.2f 秒", durationMs / 1000.0);
        } else {
            long minutes = durationMs / 60000;
            long seconds = (durationMs % 60000) / 1000;
            return minutes + " 分 " + seconds + " 秒";
        }
    }

    /**
     * 在代码生成前,直接从全局配置文件中读取配置并应用到当前单据配置中,
     * 仅用于本次生成过程,不影响单据XML的保存/加载。
     */
    private void applyGlobalConfigForGeneration(BillConfig billConfig) {
        com.nc5.generator.config.GlobalConfigManager globalConfigManager = CodeGeneratorApp.getGlobalConfigManager();
        if (globalConfigManager == null) {
            return;
        }
    
        // 直接从全局配置文件中重新加载最新配置
        GlobalConfig appGlobal = globalConfigManager.loadOrCreateDefault();
        if (appGlobal == null) {
            return;
        }
    
        // 以单据自身已有的 GlobalConfig 为基础(保留元数据相关信息),覆盖生成相关的全局配置
        GlobalConfig billGlobal = billConfig.getGlobalConfig();
        if (billGlobal == null) {
            billGlobal = new GlobalConfig();
            billConfig.setGlobalConfig(billGlobal);
        }
    
        billGlobal.setOutputDir(appGlobal.getOutputDir());
        billGlobal.setSourcePath(appGlobal.getSourcePath());
        billGlobal.setAuthor(appGlobal.getAuthor());
        billGlobal.setSyncAfterGenerate(appGlobal.isSyncAfterGenerate());
        billGlobal.setGenerateClient(appGlobal.isGenerateClient());
        billGlobal.setGenerateBusiness(appGlobal.isGenerateBusiness());
        billGlobal.setGenerateMetadata(appGlobal.isGenerateMetadata());
    
        // 确保 GlobalConfig 已经设置到 billConfig 中
        billConfig.setGlobalConfig(billGlobal);
    }

    /**
     * 同步代码到项目源码目录
     */
    public void syncCode(Consumer<Long> onComplete) {
        if (projectSrcDir == null || !projectSrcDir.exists()) {
            throw new IllegalStateException("请先选择有效的项目源码目录");
        }

        Path sourceDir = outputDir.toPath().resolve("src");
        Path targetDir = projectSrcDir.toPath();

        if (!Files.exists(sourceDir)) {
            throw new IllegalStateException("输出目录中不存在 src 文件夹");
        }

        log("\n=== 开始同步代码 ===");
        log("源目录: " + sourceDir);
        log("目标目录: " + targetDir);

        try {
            long copiedCount = Files.walk(sourceDir)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .mapToLong(sourcePath -> {
                    try {
                        Path relativePath = sourceDir.relativize(sourcePath);
                        Path targetPath = targetDir.resolve(relativePath);

                        // 自动创建目标目录
                        Files.createDirectories(targetPath.getParent());

                        // 直接覆盖
                        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                        log("已同步: " + relativePath);
                        return 1;
                    } catch (IOException e) {
                        log("同步失败 [" + sourcePath.getFileName() + "]: " + e.getMessage());
                        return 0;
                    }
                })
                .sum();

            log("同步完成！共处理 " + copiedCount + " 个文件");
            updateStatus("代码同步完成，共处理 " + copiedCount + " 个文件");

            if (onComplete != null) {
                onComplete.accept(copiedCount);
            }
        } catch (IOException e) {
            String errorMsg = "同步过程出错: " + e.getMessage();
            log(errorMsg);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * 清空输出目录
     */
    public void clearOutputDir() throws IOException {
        if (!outputDir.exists()) {
            return;
        }

        File[] files = outputDir.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteRecursive(file);
            }
        }
        log("已清空输出目录: " + outputDir.getAbsolutePath());
    }

    /**
     * 递归删除文件或目录
     */
    private void deleteRecursive(File file) throws IOException {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        Files.deleteIfExists(file.toPath());
    }

    private void log(String message) {
        if (logConsumer != null) {
            Platform.runLater(() -> logConsumer.accept(message));
        }
    }

    private void updateStatus(String message) {
        if (statusConsumer != null) {
            Platform.runLater(() -> statusConsumer.accept(message));
        }
    }

    public File getOutputDir() {
        return outputDir;
    }

    public File getProjectSrcDir() {
        return projectSrcDir;
    }
    
    /**
     * 生成结果内部类
     */
    private static class GenerationResult {
        final boolean success;
        final long duration;
        final CodeStatisticsService.CodeStatistics stats;
        final String errorMessage;
        final File outputDir;
        
        GenerationResult(boolean success, long duration, CodeStatisticsService.CodeStatistics stats, String errorMessage, File outputDir) {
            this.success = success;
            this.duration = duration;
            this.stats = stats;
            this.errorMessage = errorMessage;
            this.outputDir = outputDir;
        }
    }
}
