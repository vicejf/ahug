package com.nc5.generator.fx;

import com.nc5.generator.config.GlobalConfig;
import com.nc5.generator.config.GlobalConfigManager;
import com.nc5.generator.fx.util.FxmlHotReload;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * NC5代码生成器 JavaFX 应用入口
 */
public class CodeGeneratorApp extends Application {

    // 开发模式标志：设置为 true 启用热重载
    private static final boolean DEV_MODE = true;
    
    // 全局配置管理器
    private static GlobalConfigManager globalConfigManager;
    
    /**
     * 获取全局配置管理器
     */
    public static GlobalConfigManager getGlobalConfigManager() {
        return globalConfigManager;
    }
    
    /**
     * 获取全局配置
     */
    public static GlobalConfig getGlobalConfig() {
        return globalConfigManager != null ? globalConfigManager.getGlobalConfig() : null;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // 加载主界面
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/MainView.fxml"));
            Parent root = loader.load();

            // 创建场景
            Scene scene = new Scene(root);

            // 加载样式表
            String cssPath = getClass().getResource("/css/application.css").toExternalForm();
            scene.getStylesheets().add(cssPath);

            // 设置窗口
            primaryStage.setTitle("NC5代码生成器");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);

            // 设置窗口关闭事件
            primaryStage.setOnCloseRequest(event -> {
                Platform.exit();
            });

            primaryStage.show();

            // 开发模式下启用热重载
            if (DEV_MODE) {
                FxmlHotReload.enableHotReload(scene, "/view/MainView.fxml");
            }

        } catch (IOException e) {
            System.err.println("无法加载主界面: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        // 初始化全局配置管理器
        System.out.println("NC5代码生成器启动中...");
        globalConfigManager = new GlobalConfigManager();
        globalConfigManager.initialize();
        System.out.println("全局配置已" + (globalConfigManager.exists() ? "加载" : "创建"));
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // 清理操作
        System.out.println("NC5代码生成器已关闭");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
