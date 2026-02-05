package com.nc5.generator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 代码统计服务
 * 负责统计生成代码的行数、文件数等信息
 */
public class CodeStatisticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CodeStatisticsService.class);
    
    /**
     * 统计指定目录下的 Java 文件
     */
    public CodeStatistics countJavaFiles(java.io.File outputDir) {
        if (!outputDir.exists() || !outputDir.isDirectory()) {
            return new CodeStatistics(0, 0, 0, 0, 0);
        }
        
        try {
            return Files.walk(outputDir.toPath())
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(this::analyzeFile)
                    .reduce(new CodeStatistics(0, 0, 0, 0, 0), CodeStatistics::merge);
        } catch (Exception e) {
            logger.error("统计代码文件失败", e);
            return new CodeStatistics(0, 0, 0, 0, 0);
        }
    }
    
    /**
     * 分析单个文件的代码统计信息
     */
    private CodeStatistics analyzeFile(Path filePath) {
        try {
            List<String> lines = Files.readAllLines(filePath, java.nio.charset.Charset.forName("GBK"));
            int total = lines.size();
            int blank = 0;
            int comment = 0;
            int code = 0;
            
            boolean inBlockComment = false;
            
            for (String line : lines) {
                String trimmed = line.trim();
                
                if (trimmed.isEmpty()) {
                    blank++;
                } else if (inBlockComment) {
                    comment++;
                    if (trimmed.endsWith("*/")) {
                        inBlockComment = false;
                    }
                } else if (trimmed.startsWith("//")) {
                    comment++;
                } else if (trimmed.startsWith("/*")) {
                    comment++;
                    if (!trimmed.endsWith("*/")) {
                        inBlockComment = true;
                    }
                } else {
                    code++;
                }
            }
            
            return new CodeStatistics(1, total, code, blank, comment);
        } catch (Exception e) {
            return new CodeStatistics(1, 0, 0, 0, 0);
        }
    }
    
    /**
     * 代码统计结果类
     */
    public static class CodeStatistics {
        public final int fileCount;
        public final long totalLines;
        public final long codeLines;
        public final long blankLines;
        public final long commentLines;
        
        public CodeStatistics(int fileCount, long totalLines, long codeLines, long blankLines, long commentLines) {
            this.fileCount = fileCount;
            this.totalLines = totalLines;
            this.codeLines = codeLines;
            this.blankLines = blankLines;
            this.commentLines = commentLines;
        }
        
        public CodeStatistics merge(CodeStatistics other) {
            return new CodeStatistics(
                this.fileCount + other.fileCount,
                this.totalLines + other.totalLines,
                this.codeLines + other.codeLines,
                this.blankLines + other.blankLines,
                this.commentLines + other.commentLines
            );
        }
    }
}
