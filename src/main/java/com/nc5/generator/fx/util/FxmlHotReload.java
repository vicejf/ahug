package com.nc5.generator.fx.util;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.fxml.FXMLLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JavaFX FXML 热重载工具类
 *
 * 支持两种热重载方式：
 * 1. 文件监听自动重载：监听 FXML 和 CSS 文件变化，自动重新加载界面
 * 2. 手动快捷键刷新：按 F5 键手动触发界面刷新
 */
public class FxmlHotReload {

    private static final Map<Scene, String> sceneToFXMLMap = new HashMap<>();
    private static final AtomicBoolean isWatching = new AtomicBoolean(false);

    /**
     * 启用 FXML 热重载（自动监听文件变化）
     *
     * @param scene JavaFX 场景
     * @param fxmlPath FXML 文件路径（类路径资源，如 "/view/MainView.fxml"）
     */
    public static void enableHotReload(Scene scene, String fxmlPath) {
        sceneToFXMLMap.put(scene, fxmlPath);

        // 添加 F5 快捷键监听
        scene.setOnKeyPressed(event -> {
            if (event.getCode().getName().equals("F5")) {
                reloadView(scene);
            }
        });

        // 启动文件监听
        startFileWatcher();

        System.out.println("[热重载] 已启用 - 按 F5 手动刷新，或修改 FXML/CSS 文件自动刷新");
    }

    /**
     * 刷新当前视图
     */
    private static void reloadView(Scene scene) {
        String fxmlPath = sceneToFXMLMap.get(scene);
        if (fxmlPath == null) {
            return;
        }

        Platform.runLater(() -> {
            try {
                // 重新加载 FXML
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(FxmlHotReload.class.getResource(fxmlPath));
                Pane newRoot = loader.load();

                // 替换场景的根节点
                scene.setRoot(newRoot);

                // 重新加载 CSS
                scene.getStylesheets().clear();
                String cssPath = FxmlHotReload.class.getResource("/css/application.css").toExternalForm();
                scene.getStylesheets().add(cssPath);

                System.out.println("[热重载] 界面已刷新: " + fxmlPath);

            } catch (IOException e) {
                System.err.println("[热重载] 刷新失败: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * 启动文件监听器
     */
    private static void startFileWatcher() {
        if (isWatching.getAndSet(true)) {
            return; // 已经在监听中
        }

        Thread watchThread = new Thread(() -> {
            try {
                // 获取 resources 目录的绝对路径
                File resourcesDir = new File(FxmlHotReload.class.getResource("/view").toURI());
                File parentDir = resourcesDir.getParentFile();

                WatchService watchService = FileSystems.getDefault().newWatchService();
                parentDir.toPath().register(watchService,
                        StandardWatchEventKinds.ENTRY_MODIFY);

                System.out.println("[热重载] 开始监听文件变化: " + parentDir.getAbsolutePath());

                while (true) {
                    WatchKey key;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException e) {
                        break;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                        Path filename = pathEvent.context();

                        // 只处理 FXML 和 CSS 文件
                        if (filename.toString().endsWith(".fxml") ||
                            filename.toString().endsWith(".css")) {

                            // 触发所有场景的刷新
                            sceneToFXMLMap.keySet().forEach(scene ->
                                reloadView(scene)
                            );
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.err.println("[热重载] 文件监听失败: " + e.getMessage());
                e.printStackTrace();
            }
        });

        watchThread.setDaemon(true);
        watchThread.setName("FXML-Watcher");
        watchThread.start();
    }

    /**
     * 停止热重载
     */
    public static void disableHotReload(Scene scene) {
        sceneToFXMLMap.remove(scene);
        if (sceneToFXMLMap.isEmpty()) {
            isWatching.set(false);
        }
    }
}
