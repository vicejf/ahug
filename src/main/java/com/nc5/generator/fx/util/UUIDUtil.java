package com.nc5.generator.fx.util;

import java.util.UUID;

/**
 * UUID工具类
 */
public class UUIDUtil {

    /**
     * 生成36位UUID（带连字符）
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 生成32位UUID（不带连字符）
     */
    public static String generateUUIDNoDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
