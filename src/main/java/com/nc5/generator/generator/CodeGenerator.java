package com.nc5.generator.generator;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.config.XmlConfigParser;
import com.nc5.generator.generator.impl.ImplGenerator;
import com.nc5.generator.generator.itf.ItfGenerator;
import com.nc5.generator.generator.rule.RuleGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 代码生成器主类
 */
public class CodeGenerator {

    private static final Logger logger = LoggerFactory.getLogger(CodeGenerator.class);

    private String outputDir;

    private VoGenerator voGenerator;

    private ClientGenerator clientGenerator;

    private BsGenerator bsGenerator;

    private MetadataGenerator metadataGenerator;

    /**
     * 从配置文件加载单据配置
     */
    private BillConfig loadBillConfigFromFile(String configFilePath) {
        try {
            // 目前只支持XML格式
            if (configFilePath.endsWith(".xml")) {
                XmlConfigParser parser = new XmlConfigParser();
                return parser.parse(configFilePath);
            } else {
                throw new IllegalArgumentException("不支持的配置文件格式: " + configFilePath + "。目前只支持XML格式。");
            }
        } catch (Exception e) {
            throw new RuntimeException("加载配置文件失败: " + configFilePath, e);
        }
    }

    public CodeGenerator(String outputDir) {
        this.outputDir = outputDir;
        this.voGenerator = new VoGenerator(outputDir);
        this.clientGenerator = new ClientGenerator(outputDir);
        this.bsGenerator = new BsGenerator(outputDir);
        this.metadataGenerator = new MetadataGenerator(outputDir);
    }

    /**
     * 从配置文件生成代码
     */
    public void generateFromConfig(String configFilePath) throws Exception {
        logger.info("从配置文件生成代码: {}", configFilePath);
        BillConfig billConfig = loadBillConfigFromFile(configFilePath);
        generate(billConfig);
    }

    /**
     * 生成所有代码
     */
    public void generate(BillConfig billConfig) throws Exception {
        logger.info("========================================");
        logger.info("开始生成代码: {}", billConfig.getBillName());
        logger.info("单据编码: {}", billConfig.getBillCode());
        logger.info("========================================");

        // 生成元数据文件（从 GlobalConfig 获取开关）
        if (billConfig.isGenerateMetadata()) {
            metadataGenerator.generate(billConfig);
        }

        // 生成VO层代码（必须生成）
        voGenerator.generate(billConfig);

        // 生成接口层代码（默认生成）
        ItfGenerator.generate(billConfig, new File(outputDir));

        // 生成客户端代码（从 GlobalConfig 获取开关）
        if (billConfig.isGenerateClient()) {
            clientGenerator.generate(billConfig);
        }

        // 生成业务逻辑代码（从 GlobalConfig 获取开关）
        if (billConfig.isGenerateBusiness()) {
            bsGenerator.generate(billConfig);

            // 生成Impl层代码（默认生成）
            ImplGenerator.generate(billConfig, null, new File(outputDir));
        }

        // 生成Rule层代码（默认生成）
        RuleGenerator.generate(billConfig, new File(outputDir));

        logger.info("========================================");
        logger.info("代码生成完成！");
        logger.info("输出目录: {}", outputDir);
        logger.info("========================================");
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }
}
