package com.nc5.generator.generator.itf;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.generator.VelocityUtil;
import com.nc5.generator.template.TemplateSelector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public class ItfGenerator {
    public static void generate(BillConfig billConfig, File outputDir) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(new Date());
        String author = billConfig.getAuthor() != null ? billConfig.getAuthor() : "auto-gen";

        VelocityContext vc = new VelocityContext();
        vc.put("billPackage", billConfig.getPackageName());
        vc.put("moduleLower", billConfig.getModule().toLowerCase());
        vc.put("billCode", billConfig.getBillCode());
        vc.put("BillVO", billConfig.getBillCode());
        vc.put("billName", billConfig.getBillName());
        vc.put("bodyCodeList", billConfig.getBodyCodeList());
        vc.put("author", author);
        vc.put("date", dateStr);

        Template template = VelocityUtil.getTemplate(TemplateSelector.getItfTemplate(billConfig));

        // NC5 的 public 接口路径对应 src/public/...
        String pkgPath = billConfig.getPackageName().replace('.', '/');
        String itfPath = outputDir.getAbsolutePath() + "/src/public/" + pkgPath + "/itf/" + billConfig.getModule().toLowerCase() + "/" + billConfig.getBillCode() + "/";
        File dir = new File(itfPath);
        if (!dir.exists()) dir.mkdirs();

        String className = "I" + billConfig.getBillCode() + "Server.java";
        File outFile = new File(dir, className);

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(outFile), Charset.forName("GBK"))) {
            template.merge(vc, writer);
        }
    }
}