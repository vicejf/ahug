package com.nc5.generator.config;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 单据类型枚举
 * 定义系统中支持的所有单据类型，支持全局静态访问
 */
public enum BillType {
    SINGLE("single", "单表头类型", true),
    MULTI("multi", "多表体类型", true),
    ARCHIVE("archive", "档案类型", true);

    private final String code;
    private final String description;
    private final boolean enabled;

    // 静态缓存，提高性能
    private static final Map<String, BillType> CODE_MAP = new HashMap<>();
    private static final Map<String, BillType> DESC_MAP = new HashMap<>();
    private static final List<BillType> ALL_TYPES = Arrays.asList(values());
    private static final Map<String, String> CODE_DESC_MAP = new LinkedHashMap<>();
    
    static {
        // 初始化静态映射
        for (BillType type : values()) {
            CODE_MAP.put(type.code, type);
            DESC_MAP.put(type.description, type);
            if (type.enabled) {
                CODE_DESC_MAP.put(type.code, type.description);
            }
        }
    }

    BillType(String code, String description, boolean enabled) {
        this.code = code;
        this.description = description;
        this.enabled = enabled;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 根据code获取枚举值
     */
    public static BillType fromCode(String code) {
        return CODE_MAP.getOrDefault(code, SINGLE);
    }

    /**
     * 根据描述获取枚举值
     */
    public static BillType fromDescription(String description) {
        return DESC_MAP.getOrDefault(description, SINGLE);
    }

    /**
     * 获取所有启用的单据类型列表
     */
    public static List<BillType> getAllEnabledTypes() {
        return ALL_TYPES.stream()
                .filter(BillType::isEnabled)
                .collect(Collectors.toList());
    }

    /**
     * 获取code->description的映射表，适用于UI下拉框
     */
    public static Map<String, String> getCodeDescMap() {
        return new LinkedHashMap<>(CODE_DESC_MAP);
    }

    /**
     * 获取所有启用的code列表
     */
    public static List<String> getAllEnabledCodes() {
        return ALL_TYPES.stream()
                .filter(BillType::isEnabled)
                .map(BillType::getCode)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有启用的description列表
     */
    public static List<String> getAllEnabledDescriptions() {
        return ALL_TYPES.stream()
                .filter(BillType::isEnabled)
                .map(BillType::getDescription)
                .collect(Collectors.toList());
    }

    /**
     * 判断是否为有效的单据类型code
     */
    public static boolean isValidCode(String code) {
        return CODE_MAP.containsKey(code);
    }

    /**
     * 获取默认单据类型
     */
    public static BillType getDefault() {
        return SINGLE;
    }
}