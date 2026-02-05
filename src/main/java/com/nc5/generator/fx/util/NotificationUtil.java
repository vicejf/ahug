package com.nc5.generator.fx.util;

import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXSnackbarLayout;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * 通知工具类 - 使用 JFXSnackbar 实现轻量级通知
 */
public class NotificationUtil {

    private static JFXSnackbar snackbar;

    /**
     * 初始化通知工具
     * @param rootPane 根容器
     */
    public static void initialize(Pane rootPane) {
        if (rootPane != null && snackbar == null) {
            snackbar = new JFXSnackbar(rootPane);
        }
    }

    /**
     * 显示提示信息
     * @param message 消息内容
     */
    public static void showInfo(String message) {
        if (snackbar == null) {
            System.err.println("NotificationUtil 未初始化，请先调用 initialize() 方法");
            return;
        }

        JFXSnackbarLayout layout = new JFXSnackbarLayout(message);
        applyStyle(layout, "#2196F3");

        snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(
            layout,
            Duration.seconds(3),
            null
        ));
    }

    /**
     * 显示警告信息
     * @param message 消息内容
     */
    public static void showWarning(String message) {
        if (snackbar == null) {
            System.err.println("NotificationUtil 未初始化，请先调用 initialize() 方法");
            return;
        }

        JFXSnackbarLayout layout = new JFXSnackbarLayout(message);
        applyStyle(layout, "#FF9800");

        snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(
            layout,
            Duration.seconds(4),
            null
        ));
    }

    /**
     * 显示错误信息
     * @param message 消息内容
     */
    public static void showError(String message) {
        if (snackbar == null) {
            System.err.println("NotificationUtil 未初始化，请先调用 initialize() 方法");
            return;
        }

        JFXSnackbarLayout layout = new JFXSnackbarLayout(message);
        applyStyle(layout, "#F44336");

        snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(
            layout,
            Duration.seconds(5),
            null
        ));
    }

    /**
     * 显示自定义时长的提示信息
     * @param message 消息内容
     * @param duration 显示时长（秒）
     */
    public static void showMessage(String message, double duration) {
        if (snackbar == null) {
            System.err.println("NotificationUtil 未初始化，请先调用 initialize() 方法");
            return;
        }

        JFXSnackbarLayout layout = new JFXSnackbarLayout(message);
        applyStyle(layout, "#2196F3");

        snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(
            layout,
            Duration.seconds(duration),
            null
        ));
    }

    /**
     * 显示带操作按钮的通知
     * 注意：JFXSnackbar 在当前版本中不支持自定义按钮，此方法仅显示普通通知
     * @param message 消息内容
     * @param buttonText 按钮文本（当前版本忽略）
     * @param action 按钮点击事件（当前版本直接执行）
     */
    public static void showAction(String message, String buttonText, Runnable action) {
        if (snackbar == null) {
            System.err.println("NotificationUtil 未初始化，请先调用 initialize() 方法");
            return;
        }

        // 直接执行操作并显示通知
        action.run();

        JFXSnackbarLayout layout = new JFXSnackbarLayout(message);
        applyStyle(layout, "#4CAF50");

        snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(
            layout,
            Duration.seconds(3),
            null
        ));
    }

    /**
     * 应用样式到 Snackbar
     * @param layout Snackbar 布局
     * @param backgroundColor 背景颜色
     */
    private static void applyStyle(JFXSnackbarLayout layout, String backgroundColor) {
        // 设置整体样式
        layout.setStyle("-fx-background-color: " + backgroundColor + "; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0); -fx-padding: 10px 20px;");

        // 查找并设置内容 Label 的样式
        for (javafx.scene.Node child : layout.getChildren()) {
            if (child instanceof Label) {
                Label label = (Label) child;
                label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-font-family: 'Microsoft YaHei', 'SimHei', Arial;");
            }
        }
    }
}
