package com.nc5.generator.template;

import com.nc5.generator.config.BillConfig;
import org.apache.velocity.VelocityContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 模板上下文
 */
public class TemplateContext {

    private VelocityContext velocityContext;

    public TemplateContext() {
        this.velocityContext = new VelocityContext();
    }

    /**
     * 设置单据配置
     */
    public void setBillConfig(BillConfig billConfig) {
        velocityContext.put("bill", billConfig);
    }

    /**
     * 设置变量
     */
    public void put(String key, Object value) {
        velocityContext.put(key, value);
    }

    /**
     * 获取Velocity上下文
     */
    public VelocityContext toVelocityContext() {
        // 添加常用的工具方法
        velocityContext.put("now", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        velocityContext.put("year", new SimpleDateFormat("yyyy").format(new Date()));

        return velocityContext;
    }
}
