package com.nc5.generator.generator;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

/**
 * Velocity模板工具类
 */
public class VelocityUtil {

    private static final Logger logger = LoggerFactory.getLogger(VelocityUtil.class);

    private static VelocityEngine engine;

    static {
        // 初始化Velocity引擎
        engine = new VelocityEngine();
        engine.setProperty("resource.loader", "class");
        engine.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        engine.init();
    }

    /**
     * 获取模板
     */
    public static Template getTemplate(String templatePath) {
        try {
            return engine.getTemplate(templatePath, "UTF-8");
        } catch (Exception e) {
            logger.error("加载模板失败: {}", templatePath, e);
            throw new RuntimeException("加载模板失败: " + templatePath, e);
        }
    }

    /**
     * 合并模板
     */
    public static String merge(Template template, VelocityContext context) {
        try (StringWriter writer = new StringWriter()) {
            template.merge(context, writer);
            return writer.toString();
        } catch (Exception e) {
            logger.error("合并模板失败", e);
            throw new RuntimeException("合并模板失败", e);
        }
    }

    /**
     * 合并模板（通过模板路径）
     */
    public static String merge(String templatePath, VelocityContext context) {
        Template template = getTemplate(templatePath);
        return merge(template, context);
    }
}
