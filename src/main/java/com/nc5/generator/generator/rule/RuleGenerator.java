package com.nc5.generator.generator.rule;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.generator.VelocityUtil;
import com.nc5.generator.template.TemplateSelector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public class RuleGenerator {
    // 默认生成的 Rule 类型及描述
    private static final List<RuleType> DEFAULT_RULE_TYPES = Arrays.asList(
        new RuleType("AddNewBillCodeRule", "新增单据时生成单据编号规则"),
        new RuleType("ApproveAfterRule", "审批后规则"),
        new RuleType("ApproveToScada", "审批后同步到SCADA"),
        new RuleType("DelAfterRule", "删除后规则"),
        new RuleType("SaveAfterRule", "保存后规则"),
        new RuleType("SaveBeforeRule", "保存前规则"),
        new RuleType("UnApproveAfterRule", "取消审批后规则")
    );

    public static void generate(BillConfig billConfig, File outputDir) throws Exception {
        generate(billConfig, outputDir, DEFAULT_RULE_TYPES);
    }

    public static void generate(BillConfig billConfig, File outputDir, List<RuleType> ruleTypes) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(new Date());
        String author = billConfig.getAuthor() != null ? billConfig.getAuthor() : "auto-gen";

        for (RuleType rt : ruleTypes) {
            VelocityContext vc = new VelocityContext();
            vc.put("billPackage", billConfig.getPackageName());
            vc.put("moduleLower", billConfig.getModule().toLowerCase());
            vc.put("billCode", billConfig.getBillCode());
            vc.put("BillVO", billConfig.getBillCode());
            vc.put("billName", billConfig.getBillName());
            vc.put("ruleType", rt.getType());
            vc.put("ruleDesc", rt.getDesc());
            vc.put("author", author);
            vc.put("date", dateStr);

            Template template = VelocityUtil.getTemplate(TemplateSelector.getRuleTemplate(billConfig));

            String pkgPath = billConfig.getPackageName().replace('.', '/');
            String rulePath = outputDir.getAbsolutePath() + "/src/private/" + pkgPath + "/rule/" + billConfig.getModule().toLowerCase() + "/" + billConfig.getBillCode() + "/";
            File dir = new File(rulePath);
            if (!dir.exists()) dir.mkdirs();

            String className = billConfig.getBillCode() + rt.getType() + ".java";
            File outFile = new File(dir, className);

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(outFile), Charset.forName("GBK"))) {
                template.merge(vc, writer);
            }
        }
    }

    // 内部类：Rule类型与描述
    public static class RuleType {
        private String type;
        private String desc;

        public RuleType(String type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public String getType() { return type; }
        public String getDesc() { return desc; }
    }
}