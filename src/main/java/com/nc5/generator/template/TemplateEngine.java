package com.nc5.generator.template;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.Properties;

/**
 * Velocity模板引擎封装
 */
public class TemplateEngine {

    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);

    private VelocityEngine velocityEngine;

    public TemplateEngine() {
        init();
    }

    /**
     * 初始化Velocity引擎
     */
    private void init() {
        Properties props = new Properties();

        // 设置资源加载器为classpath
        props.put("resource.loader", "class");
        props.put("class.resource.loader.description", "Velocity Classpath Resource Loader");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        // 设置编码（模板文件用UTF-8）
        props.put("input.encoding", "UTF-8");
        props.put("output.encoding", "UTF-8");

        // 初始化引擎
        velocityEngine = new VelocityEngine(props);

        logger.info("Velocity模板引擎初始化完成");
    }

    /**
     * 渲染模板
     *
     * @param templatePath 模板路径（如templates/vo/HVO.vm）
     * @param context      上下文
     * @return 渲染结果
     */
    public String render(String templatePath, TemplateContext context) {
        try {
            // 模板文件用UTF-8编码读取
            Template template = velocityEngine.getTemplate(templatePath, "UTF-8");
            VelocityContext velocityContext = context.toVelocityContext();
            // 输出用GBK编码（通过全局output.encoding设置）
            StringWriter writer = new StringWriter();
            template.merge(velocityContext, writer);
            return writer.toString();
        } catch (Exception e) {
            logger.error("模板渲染失败: {}", templatePath, e);
            throw new RuntimeException("模板渲染失败: " + templatePath, e);
        }
    }
}
