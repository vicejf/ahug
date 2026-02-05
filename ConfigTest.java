package com.nc5.generator.config;

import org.junit.Test;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.File;

/**
 * 测试配置文件的读写
 */
public class ConfigTest {

    @Test
    public void testSourcePathReadWrite() {
        try {
            // 1. 创建配置
            BillConfig config = new BillConfig();
            config.setBillCode("TEST");
            config.setBillName("测试单据");
            config.setSourcePath("D:\\Test\\SourcePath");

            // 2. 写入文件
            XmlConfigWriter writer = new XmlConfigWriter();
            writer.write(config, "D:\\test-config.xml");

            System.out.println("写入完成，请检查 D:\\test-config.xml 文件");

            // 3. 读取文件
            XmlConfigParser parser = new XmlConfigParser();
            BillConfig loadedConfig = parser.parse("D:\\test-config.xml");

            System.out.println("读取的源码路径: " + loadedConfig.getSourcePath());
            System.out.println("原始路径: " + config.getSourcePath());

            if (config.getSourcePath().equals(loadedConfig.getSourcePath())) {
                System.out.println("测试成功！源码路径正确保存和加载");
            } else {
                System.out.println("测试失败！源码路径不匹配");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ConfigTest().testSourcePathReadWrite();
    }
}
