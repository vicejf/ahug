package com.nc5.generator.generator.impl;

import com.nc5.generator.config.BillConfig;
import com.nc5.generator.generator.VelocityUtil;
import com.nc5.generator.template.TemplateContext;
import com.nc5.generator.template.TemplateSelector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

public class ImplGenerator {
    public static void generate(BillConfig billConfig, TemplateContext context, File outputDir) throws Exception {
        VelocityContext vc = new VelocityContext();
        vc.put("billPackage", billConfig.getPackageName());
        vc.put("moduleLower", billConfig.getModule().toLowerCase());
        vc.put("billCode", billConfig.getBillCode());
        vc.put("BillVO", billConfig.getBillCode());
        vc.put("bodyCodeList", billConfig.getBodyCodeList());
        
        Template template = VelocityUtil.getTemplate(TemplateSelector.getImplTemplate(billConfig));
        
        String pkgPath = billConfig.getPackageName().replace('.', '/');
        String implPath = outputDir.getAbsolutePath() + "/src/private/" + pkgPath + "/impl/" + billConfig.getModule().toLowerCase() + "/" + billConfig.getBillCode() + "/";
        File dir = new File(implPath);
        if (!dir.exists()) dir.mkdirs();
        
        String className = billConfig.getBillCode() + "ServerImpl.java";
        File outFile = new File(dir, className);
        
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(outFile), Charset.forName("GBK"))) {
            template.merge(vc, writer);
        }
    }
}