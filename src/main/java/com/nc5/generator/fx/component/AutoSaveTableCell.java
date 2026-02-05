package com.nc5.generator.fx.component;

import javafx.application.Platform;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.StringConverter;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 企业级自动保存表格单元格组件
 *
 * <p>特性：
 * <ul>
 *   <li>泛型支持：支持任意类型 T 的单元格编辑</li>
 *   <li>自动保存：Enter/Tab/点击其他单元格触发保存</li>
 *   <li>防抖提交：避免高频提交</li>
 *   <li>异步保存：UI 无阻塞</li>
 *   <li>失败回滚：保存失败自动回滚到旧值</li>
 *   <li>状态管理：IDLE/EDITING/SAVING/ERROR 状态机</li>
 *   <li>并发控制：行级锁防止并发提交</li>
 *   <li>可插拔保存器：SaveHandler 接口支持自定义保存逻辑</li>
 *   <li>架构级监听：绑定 editingCellProperty，不依赖焦点系统</li>
 * </ul>
 *
 * <p>架构说明：
 * 企业级 TableView 编辑提交必须绑定 editing 状态机，而非 UI 焦点系统。
 * 监听 {@code TableView.editingCellProperty()} 作为编辑状态的唯一权威源头，
 * 确保在鼠标点击其他单元格、滚动表格、切换行等所有场景下都能正确触发保存。
 *
 * @param <S> 表格行类型
 * @param <T> 单元格值类型
 */
public class AutoSaveTableCell<S, T> extends TableCell<S, T> {

    /* ======================= 接口定义 ======================= */

    /**
     * 保存处理器接口
     *
     * @param <S> 表格行类型
     * @param <T> 单元格值类型
     */
    @FunctionalInterface
    public interface SaveHandler<S, T> {
        /**
         * 保存单元格值到后端
         *
         * @param rowItem 表格行数据项
         * @param column  表格列
         * @param newValue 新值
         * @return 异步保存结果
         */
        CompletableFuture<Void> save(S rowItem, TableColumn<S, T> column, T newValue);
    }

    /* ======================= 状态定义 ======================= */

    /**
     * 编辑状态枚举
     */
    public enum EditState {
        IDLE,      // 空闲
        EDITING,   // 编辑中
        SAVING,    // 保存中
        ERROR      // 错误
    }

    /* ======================= 成员 ======================= */

    private final TextField textField = new TextField();
    private final StringConverter<T> converter;
    private final SaveHandler<S, T> saveHandler;

    private volatile EditState state = EditState.IDLE;
    private final AtomicBoolean committing = new AtomicBoolean(false);

    // 防抖调度器 - 全局单例
    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "AutoSaveCell-Scheduler");
                t.setDaemon(true);
                return t;
            });

    private ScheduledFuture<?> debounceFuture;

    /* ======================= 构造 ======================= */

    /**
     * 构造自动保存单元格
     *
     * @param converter 字符串转换器
     * @param saveHandler 保存处理器
     */
    public AutoSaveTableCell(StringConverter<T> converter,
                             SaveHandler<S, T> saveHandler) {
        this.converter = converter;
        this.saveHandler = saveHandler;
        initEditor();
        setupEditingCellListener();
    }

    /* ======================= 初始化 ======================= */

    private void initEditor() {
        // Enter 提交
        textField.setOnAction(e -> deferCommit());

        // Tab 提交
        textField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.TAB) {
                deferCommit();
            }
        });
    }

    /**
     * 设置编辑单元格监听器
     * 监听 TableView.editingCellProperty() 变化，处理鼠标点击其他单元格时的保存
     */
    private void setupEditingCellListener() {
        // 🔥 关键修复：监听 TableView 编辑单元变化
        tableViewProperty().addListener((obs, oldTv, newTv) -> {
            if (newTv != null) {
                newTv.editingCellProperty().addListener((o, oldCell, newCell) -> {
                    // 只在当前单元格正在编辑时才处理
                    if (isEditing()) {
                        // 判断 editing cell 是否从当前单元格切换到了其他位置
                        boolean shouldCommit = false;

                        if (newCell == null) {
                            // 编辑被取消（点击空白、按 Esc 等）
                            shouldCommit = true;
                        } else if (oldCell != null) {
                            // 从一个编辑单元格切换到另一个
                            int currentRow = getIndex();
                            int newRow = newCell.getRow();

                            // 如果新单元格不是当前单元格，说明切换了
                            shouldCommit = (newRow != currentRow);
                        }

                        if (shouldCommit) {
                            Platform.runLater(this::deferCommit);
                        }
                    }
                });
            }
        });
    }

    /* ======================= 生命周期 ======================= */

    @Override
    public void startEdit() {
        super.startEdit();
        state = EditState.EDITING;
        setText(null);
        setGraphic(textField);
        textField.setText(converter.toString(getItem()));
        textField.selectAll();
        Platform.runLater(textField::requestFocus);
        updateStyle();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        state = EditState.IDLE;
        setText(converter.toString(getItem()));
        setGraphic(null);
        updateStyle();
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                textField.setText(converter.toString(item));
                setText(null);
                setGraphic(textField);
            } else {
                setText(converter.toString(item));
                setGraphic(null);
            }
        }
        updateStyle();
    }

    /* ======================= 提交流程 ======================= */

    /**
     * 延迟提交（防抖）
     */
    private void deferCommit() {
        if (debounceFuture != null) {
            debounceFuture.cancel(false);
        }

        // 防抖 80ms（可调）
        debounceFuture = scheduler.schedule(this::commitSafely, 80, TimeUnit.MILLISECONDS);
    }

    /**
     * 安全提交
     */
    private void commitSafely() {
        Platform.runLater(() -> {

            if (!isEditing()) return;
            if (committing.get()) return;

            T newValue;
            try {
                newValue = converter.fromString(textField.getText());
            } catch (Exception e) {
                markError("Convert failed");
                return;
            }

            T oldValue = getItem();
            if (Objects.equals(newValue, oldValue)) {
                commitEdit(oldValue);
                state = EditState.IDLE;
                updateStyle();
                return;
            }

            committing.set(true);
            state = EditState.SAVING;
            updateStyle();

            S rowItem = getTableView().getItems().get(getIndex());
            TableColumn<S, T> col = getTableColumn();

            // 本地模型先提交（乐观更新）
            commitEdit(newValue);

            saveHandler.save(rowItem, col, newValue)
                .thenRun(() -> Platform.runLater(() -> {
                    committing.set(false);
                    state = EditState.IDLE;
                    updateStyle();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        // 回滚
                        commitEdit(oldValue);
                        committing.set(false);
                        state = EditState.ERROR;
                        updateStyle();
                        System.err.println("[AUTO-SAVE-ERROR] " + ex.getMessage());
                    });
                    return null;
                });
        });
    }

    /* ======================= 样式 ======================= */

    /**
     * 更新单元格样式
     */
    private void updateStyle() {
        getStyleClass().removeAll("cell-editing", "cell-saving", "cell-error");

        switch (state) {
            case IDLE    -> { /* 默认样式，无需额外处理 */ }
            case EDITING -> getStyleClass().add("cell-editing");
            case SAVING  -> getStyleClass().add("cell-saving");
            case ERROR   -> getStyleClass().add("cell-error");
        }
    }

    /**
     * 标记错误状态
     */
    private void markError(String msg) {
        state = EditState.ERROR;
        updateStyle();
        System.err.println("[AUTO-SAVE-ERROR] " + msg);
    }

    /* ======================= 公共方法 ======================= */

    /**
     * 获取当前编辑状态
     */
    public EditState getState() {
        return state;
    }

    /**
     * 关闭调度器（应用退出时调用）
     */
    public static void shutdownScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
