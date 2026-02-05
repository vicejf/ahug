package com.nc5.generator.config;

import java.util.ArrayList;
import java.util.List;

/**
 * 枚举配置模型
 */
public class EnumConfig {

    /**
     * 枚举ID
     */
    private String id;

    /**
     * 枚举名称
     */
    private String name;

    /**
     * 枚举显示名称
     */
    private String displayName;

    /**
     * 枚举类名
     */
    private String className;

    /**
     * 枚举项列表
     */
    private List<EnumItem> items = new ArrayList<>();

    public EnumConfig() {
    }

    public EnumConfig(String id, String name, String displayName, String className) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<EnumItem> getItems() {
        return items;
    }

    public void setItems(List<EnumItem> items) {
        this.items = items;
    }

    /**
     * 枚举项
     */
    public static class EnumItem {
        /**
         * 枚举项ID
         */
        private String id;

        /**
         * 枚举值
         */
        private String value;

        /**
         * 枚举显示文本
         */
        private String display;

        public EnumItem() {
        }

        public EnumItem(String id, String value, String display) {
            this.id = id;
            this.value = value;
            this.display = display;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }
    }
}
