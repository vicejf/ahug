package com.nc5.generator.parser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 增强版Oracle SQL建表语句解析器
 * 支持复杂的Oracle SQL语法和各种边界情况
 */
public class SqlTableParser {
    
    /**
     * 字段配置类
     */
    public static class FieldDefinition {
        public String name;           // 字段名
        public String label;          // 中文名/注释
        public String javaType;       // Java类型
        public String dbType;         // 数据库类型（完整）
        public String baseDbType;     // 基础数据库类型
        public Integer length;        // 长度/精度
        public Integer scale;         // 小数位数
        public Boolean required;      // 是否必填
        public Boolean primary;       // 是否主键
        public Boolean unique;        // 是否唯一
        public String defaultValue;   // 默认值
        public Boolean autoIncrement; // 是否自增
        public String comment;        // 注释
        public Boolean editable;      // 是否可编辑
        public String uiType;         // UI类型
        public List<String> constraints = new ArrayList<>(); // 其他约束
    }
    
    /**
     * 表信息类
     */
    public static class TableInfo {
        public String tableName;
        public String tableComment;
        public List<FieldDefinition> fields = new ArrayList<>();
        public List<String> primaryKeys = new ArrayList<>();
        public List<String> uniqueConstraints = new ArrayList<>();
        public Map<String, String> foreignKeys = new HashMap<>();
    }
    
    /**
     * 主解析方法：解析完整的Oracle SQL建表语句
     * @param sql 完整的SQL语句
     * @return 表信息
     */
    public static TableInfo parseCreateTable(String sql) {
        TableInfo tableInfo = new TableInfo();
        
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL语句不能为空");
        }
        
        // 标准化SQL（处理换行、多余空格等）
        String normalizedSql = normalizeSql(sql);

        // 提取表注释
        tableInfo.tableComment = extractTableComment(normalizedSql);
        
        // 提取所有列注释
        Map<String, String> columnComments = extractColumnComments(normalizedSql);
        
        // 提取主键定义（多种形式）
        extractPrimaryKeys(normalizedSql, tableInfo);
        
        // 提取唯一约束
        extractUniqueConstraints(normalizedSql, tableInfo);
        
        // 提取外键约束
        extractForeignKeyConstraints(normalizedSql, tableInfo);
        
        // 提取表定义主体部分
        String tableBody = extractTableBody(normalizedSql);
        
        // 解析字段定义
        parseFieldDefinitions(tableBody, tableInfo, columnComments);
        
        return tableInfo;
    }
    
    /**
     * 标准化SQL：处理换行、制表符、多余空格等
     */
    private static String normalizeSql(String sql) {
        if (sql == null) return "";

        // 标准化引号（统一为双引号）
        sql = sql.replaceAll("`", "\"");

        // 先移除注释（保留换行以便正确处理行尾注释）
        sql = removeComments(sql);

        // 移除注释后再处理换行和空格
        sql = sql.replaceAll("[\\t\\r]+", " ");
        // 只将换行符替换为单个空格，不要合并所有空格（保留字段间的分隔）
        sql = sql.replaceAll("\\n+", " ");
        sql = sql.replaceAll("  +", " ");

        // 移除语句末尾的分号
        sql = sql.trim().replaceAll(";\\s*$", "");

        return sql.trim();
    }
    
    /**
     * 移除SQL注释
     */
    private static String removeComments(String sql) {
        // 先移除多行注释 /* ... */
        Pattern multiLineComment = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
        sql = multiLineComment.matcher(sql).replaceAll(" ");

        // 再移除单行注释 -- ... (包括行尾的注释)
        Pattern singleLineComment = Pattern.compile("--[^\\n]*");
        sql = singleLineComment.matcher(sql).replaceAll("");

        return sql;
    }

    /**
     * 提取表名
     */
    private static String extractTableName(String sql) {
        // 匹配 CREATE TABLE 表名
        Pattern pattern = Pattern.compile(
            "CREATE\\s+(?:GLOBAL\\s+TEMPORARY\\s+)?TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?\"?([\\w_$]+)\"?\\s*\\(",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = pattern.matcher(sql);
        if (matcher.find()) {
            return matcher.group(1).toUpperCase();
        }
        
        throw new IllegalArgumentException("无法提取表名");
    }
    
    /**
     * 提取表注释
     */
    private static String extractTableComment(String sql) {
        Pattern pattern = Pattern.compile(
            "COMMENT\\s+ON\\s+TABLE\\s+\"?\\w+\"?\\s+IS\\s+'([^']*)'",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = pattern.matcher(sql);
        return matcher.find() ? matcher.group(1).trim() : null;
    }
    
    /**
     * 提取列注释
     */
    private static Map<String, String> extractColumnComments(String sql) {
        Map<String, String> comments = new HashMap<>();
        
        Pattern pattern = Pattern.compile(
            "COMMENT\\s+ON\\s+COLUMN\\s+(?:\\w+\\.)?\"?([\\w_$]+)\"?\\.\"?([\\w_$]+)\"?\\s+IS\\s+'([^']*)'",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String tableName = matcher.group(1);
            String columnName = matcher.group(2).toLowerCase();
            String comment = matcher.group(3).trim();
            comments.put(columnName, comment);
        }

        return comments;
    }
    
    /**
     * 提取表主体部分（括号内的内容）
     */
    private static String extractTableBody(String sql) {
        // 查找第一个左括号
        int start = sql.indexOf('(');
        if (start == -1) {
            throw new IllegalArgumentException("无效的CREATE TABLE语句：缺少左括号");
        }
        
        int end = -1;
        int depth = 0;
        
        for (int i = start; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    end = i;
                    break;
                }
            }
        }
        
        if (end == -1) {
            throw new IllegalArgumentException("无效的CREATE TABLE语句：括号不匹配");
        }
        
        return sql.substring(start + 1, end).trim();
    }
    
    /**
     * 提取主键定义
     */
    private static void extractPrimaryKeys(String sql, TableInfo tableInfo) {
        // 方式1：单独的PRIMARY KEY约束
        Pattern pattern1 = Pattern.compile(
            "(?:CONSTRAINT\\s+\\w+\\s+)?PRIMARY\\s+KEY\\s*\\(([^)]+)\\)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = pattern1.matcher(sql);
        if (matcher.find()) {
            String[] keys = matcher.group(1).split(",");
            for (String key : keys) {
                tableInfo.primaryKeys.add(key.trim().replaceAll("[\"']", "").toUpperCase());
            }
        }
        
        // 方式2：在表定义后指定的PRIMARY KEY
        Pattern pattern2 = Pattern.compile(
            "\\bPRIMARY\\s+KEY\\s*\\(([^)]+)\\)\\s*(?:,|$)",
            Pattern.CASE_INSENSITIVE
        );
        
        matcher = pattern2.matcher(sql);
        while (matcher.find()) {
            String[] keys = matcher.group(1).split(",");
            for (String key : keys) {
                String normalizedKey = key.trim().replaceAll("[\"']", "").toLowerCase();
                if (!tableInfo.primaryKeys.contains(normalizedKey)) {
                    tableInfo.primaryKeys.add(normalizedKey);
                }
            }
        }
    }
    
    /**
     * 提取唯一约束
     */
    private static void extractUniqueConstraints(String sql, TableInfo tableInfo) {
        Pattern pattern = Pattern.compile(
            "(?:CONSTRAINT\\s+\\w+\\s+)?UNIQUE\\s*\\(([^)]+)\\)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String[] fields = matcher.group(1).split(",");
            for (String field : fields) {
                tableInfo.uniqueConstraints.add(field.trim().replaceAll("[\"']", "").toLowerCase());
            }
        }
    }
    
    /**
     * 提取外键约束
     */
    private static void extractForeignKeyConstraints(String sql, TableInfo tableInfo) {
        Pattern pattern = Pattern.compile(
            "CONSTRAINT\\s+(\\w+)\\s+FOREIGN\\s+KEY\\s*\\(([^)]+)\\)\\s+REFERENCES\\s+\\w+\\.?\\w+\\s*\\(([^)]+)\\)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            String constraintName = matcher.group(1);
            String localField = matcher.group(2).trim().replaceAll("[\"']", "").toUpperCase();
            String referencedField = matcher.group(3).trim().replaceAll("[\"']", "").toUpperCase();
            tableInfo.foreignKeys.put(localField, referencedField);
        }
    }
    
    /**
     * 解析字段定义
     */
    private static void parseFieldDefinitions(String tableBody, TableInfo tableInfo, 
                                             Map<String, String> columnComments) {
        // 分割字段定义，正确处理括号嵌套
        List<String> definitions = splitFieldDefinitions(tableBody);
        
        for (String definition : definitions) {
            definition = definition.trim();
            if (definition.isEmpty()) continue;
            
            // 跳过约束定义
            if (isConstraintDefinition(definition)) {
                continue;
            }
            
            // 解析字段定义
            FieldDefinition field = parseSingleFieldDefinition(definition);
            if (field != null) {
                // 设置主键标识
                field.primary = tableInfo.primaryKeys.contains(field.name);
                
                // 设置唯一标识
                field.unique = tableInfo.uniqueConstraints.contains(field.name) || 
                              field.constraints.contains("UNIQUE");
                
                // 设置必填标识（主键或NOT NULL）
                field.required = field.primary || 
                               field.constraints.contains("NOT NULL") ||
                               definition.toUpperCase().contains(" NOT NULL ");
                
                // 设置注释
                if (columnComments.containsKey(field.name)) {
                    field.comment = columnComments.get(field.name);
                    field.label = field.comment;
                } else if (field.label == null) {
                    field.label = field.name;
                }

                // 设置可编辑标识（非主键且非自增字段通常可编辑）
                field.autoIncrement = field.autoIncrement != null && field.autoIncrement;
                field.editable = !field.primary && !field.autoIncrement;

                // 推断UI类型
                field.uiType = inferUiType(field);

                tableInfo.fields.add(field);
            }
        }
    }
    
    /**
     * 分割字段定义，正确处理括号嵌套
     */
    private static List<String> splitFieldDefinitions(String body) {
        List<String> definitions = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int parenthesesDepth = 0;
        int quotesDepth = 0;
        char lastChar = 0;
        
        for (int i = 0; i < body.length(); i++) {
            char c = body.charAt(i);
            
            // 处理引号
            if (c == '\'' && lastChar != '\\') {
                quotesDepth = (quotesDepth == 0) ? 1 : 0;
            }
            
            // 处理括号
            if (quotesDepth == 0) {
                if (c == '(') {
                    parenthesesDepth++;
                } else if (c == ')') {
                    parenthesesDepth--;
                } else if (c == ',' && parenthesesDepth == 0) {
                    definitions.add(current.toString().trim());
                    current = new StringBuilder();
                    continue;
                }
            }
            
            current.append(c);
            lastChar = c;
        }
        
        if (current.length() > 0) {
            definitions.add(current.toString().trim());
        }
        
        return definitions;
    }
    
    /**
     * 判断是否为约束定义
     */
    private static boolean isConstraintDefinition(String definition) {
        String upperDef = definition.toUpperCase();
        return upperDef.startsWith("CONSTRAINT ") ||
               upperDef.startsWith("PRIMARY KEY") ||
               upperDef.startsWith("FOREIGN KEY") ||
               upperDef.startsWith("UNIQUE ") ||
               upperDef.startsWith("CHECK ") ||
               upperDef.startsWith("REFERENCES ");
    }
    
    /**
     * 解析单个字段定义
     */
    private static FieldDefinition parseSingleFieldDefinition(String definition) {
        FieldDefinition field = new FieldDefinition();
        
        // 提取字段名（考虑引号）
        String[] parts = definition.split("\\s+", 2);
        if (parts.length < 2) {
            return null;
        }
        
        field.name = normalizeIdentifier(parts[0]);
        
        // 解析类型和约束
        parseTypeAndConstraints(parts[1], field);
        
        return field;
    }
    
    /**
     * 标准化标识符（去除引号，转为小写）
     */
    private static String normalizeIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return identifier;
        }

        // 去除引号
        identifier = identifier.replaceAll("^[\"']+|[\"']+$", "");

        // 转为小写
        return identifier.toLowerCase();
    }
    
    /**
     * 解析类型和约束
     */
    private static void parseTypeAndConstraints(String typeAndConstraints, FieldDefinition field) {
        // 分离类型和约束部分
        String[] parts = typeAndConstraints.split("\\s+(?=[A-Z]+\\b)", 2);
        String typePart = parts[0];
        String constraintPart = parts.length > 1 ? parts[1] : "";
        
        // 解析数据库类型
        parseDbType(typePart, field);
        
        // 解析约束
        parseConstraints(constraintPart, field);
    }
    
    /**
     * 解析数据库类型
     */
    private static void parseDbType(String typePart, FieldDefinition field) {
        // 匹配类型和参数：VARCHAR2(50) 或 NUMBER(10,2)
        Pattern pattern = Pattern.compile(
            "([A-Z]+\\d*)(?:\\(([^)]+)\\))?",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = pattern.matcher(typePart);
        if (matcher.find()) {
            String baseType = matcher.group(1).toUpperCase();
            String params = matcher.group(2);
            
            field.baseDbType = baseType;
            field.dbType = params != null ? baseType + "(" + params + ")" : baseType;
            
            // 解析类型参数
            if (params != null) {
                parseTypeParameters(params, field);
            }
            
            // 推断Java类型
            field.javaType = inferJavaType(baseType, field.length, field.scale);
        }
    }
    
    /**
     * 解析类型参数（长度、精度、小数位数）
     */
    private static void parseTypeParameters(String params, FieldDefinition field) {
        String[] paramParts = params.split(",");
        
        try {
            if (paramParts.length > 0) {
                field.length = Integer.parseInt(paramParts[0].trim());
            }
            
            if (paramParts.length > 1) {
                field.scale = Integer.parseInt(paramParts[1].trim());
            }
        } catch (NumberFormatException e) {
            // 忽略参数解析错误
        }
    }
    
    /**
     * 解析约束
     */
    private static void parseConstraints(String constraints, FieldDefinition field) {
        if (constraints == null || constraints.isEmpty()) {
            return;
        }
        
        // 分割约束关键字
        String[] constraintKeywords = {
            "NOT NULL", "NULL", "UNIQUE", "PRIMARY KEY", 
            "CHECK", "DEFAULT", "REFERENCES", "AUTO_INCREMENT",
            "GENERATED", "IDENTITY"
        };
        
        // 按约束关键字分割
        String upperConstraints = " " + constraints.toUpperCase() + " ";
        for (String keyword : constraintKeywords) {
            if (upperConstraints.contains(" " + keyword + " ")) {
                field.constraints.add(keyword);
                
                // 提取默认值
                if (keyword.equals("DEFAULT")) {
                    field.defaultValue = extractDefaultValue(constraints, keyword);
                }
                
                // 检查是否自增
                if (keyword.equals("AUTO_INCREMENT") || 
                    keyword.equals("GENERATED") || 
                    keyword.equals("IDENTITY")) {
                    field.autoIncrement = true;
                }
            }
        }
    }
    
    /**
     * 提取默认值
     */
    private static String extractDefaultValue(String constraints, String keyword) {
        Pattern pattern = Pattern.compile(
            keyword + "\\s+([^\\s]+(?:\\s+[^\\s]+)*?)(?=\\s+[A-Z]+\\b|$)",
            Pattern.CASE_INSENSITIVE
        );
        
        Matcher matcher = pattern.matcher(constraints);
        return matcher.find() ? matcher.group(1).trim() : null;
    }
    
    /**
     * 推断Java类型
     */
    private static String inferJavaType(String dbType, Integer length, Integer scale) {
        String upperType = dbType.toUpperCase();
        
        switch (upperType) {
            case "VARCHAR2":
            case "NVARCHAR2":
            case "VARCHAR":
            case "NVARCHAR":
            case "CHAR":
            case "NCHAR":
            case "CLOB":
            case "NCLOB":
            case "LONG":
            case "RAW":
            case "LONG RAW":
                return "String";
                
            case "NUMBER":
            case "NUMERIC":
            case "DECIMAL":
                if (scale != null && scale > 0) {
                    return "BigDecimal";
                } else if (length != null && length > 9) {
                    return "Long";
                } else {
                    return "Integer";
                }
                
            case "INTEGER":
            case "INT":
            case "SMALLINT":
                return "Integer";
                
            case "BIGINT":
                return "Long";
                
            case "FLOAT":
            case "REAL":
            case "DOUBLE":
            case "BINARY_FLOAT":
            case "BINARY_DOUBLE":
                return "Double";
                
            case "DATE":
            case "TIMESTAMP":
            case "TIMESTAMP(6)":
            case "TIMESTAMP(9)":
                return "Date";
                
            case "BLOB":
            case "BFILE":
                return "byte[]";
                
            case "BOOLEAN":
            case "BIT":
                return "Boolean";
                
            default:
                return "String";
        }
    }
    
    /**
     * 推断UI类型
     */
    private static String inferUiType(FieldDefinition field) {
        String dbType = field.baseDbType.toUpperCase();
        
        if (dbType.contains("DATE") || dbType.contains("TIMESTAMP")) {
            return "DateTime";
        } else if (dbType.contains("BLOB") || dbType.contains("BFILE")) {
            return "FileUpload";
        } else if (dbType.contains("BOOLEAN") || dbType.contains("BIT")) {
            return "CheckBox";
        } else if (field.length != null && field.length > 200) {
            return "TextArea";
        } else if (field.unique) {
            return "UniqueInput";
        } else if (field.primary) {
            return "PrimaryKey";
        } else {
            return "Text";
        }
    }
    
    /**
     * 工具方法：生成简化字段列表（兼容旧接口）
     */
    public static List<SimpleFieldTemplate> parseToSimpleFields(String sql) {
        TableInfo tableInfo = parseCreateTable(sql);
        List<SimpleFieldTemplate> result = new ArrayList<>();
        
        for (FieldDefinition field : tableInfo.fields) {
            SimpleFieldTemplate simple = new SimpleFieldTemplate();
            simple.name = field.name;
            simple.label = field.label;
            simple.type = field.javaType;
            simple.dbType = field.dbType;
            simple.length = field.length;
            simple.required = field.required;
            simple.primary = field.primary;
            simple.comment = field.comment;
            simple.editable = field.editable;
            simple.uiType = field.uiType;
            result.add(simple);
        }
        
        return result;
    }
    
    /**
     * 测试用例
     */
    public static void main(String[] args) {
        String testSql = """
            CREATE TABLE EMPLOYEES (
                EMPLOYEE_ID NUMBER(6) PRIMARY KEY,
                FIRST_NAME VARCHAR2(20) NOT NULL,
                LAST_NAME VARCHAR2(25) NOT NULL,
                EMAIL VARCHAR2(25) UNIQUE NOT NULL,
                PHONE_NUMBER VARCHAR2(20),
                HIRE_DATE DATE DEFAULT SYSDATE NOT NULL,
                JOB_ID VARCHAR2(10) NOT NULL,
                SALARY NUMBER(8,2),
                COMMISSION_PCT NUMBER(2,2),
                MANAGER_ID NUMBER(6),
                DEPARTMENT_ID NUMBER(4),
                CONSTRAINT EMP_EMP_ID_PK PRIMARY KEY (EMPLOYEE_ID),
                CONSTRAINT EMP_DEPT_FK FOREIGN KEY (DEPARTMENT_ID) REFERENCES DEPARTMENTS(DEPARTMENT_ID),
                CONSTRAINT EMP_EMAIL_UK UNIQUE (EMAIL),
                CONSTRAINT EMP_SALARY_MIN CHECK (SALARY > 0)
            );
            
            COMMENT ON TABLE EMPLOYEES IS '员工信息表';
            COMMENT ON COLUMN EMPLOYEES.EMPLOYEE_ID IS '员工ID';
            COMMENT ON COLUMN EMPLOYEES.FIRST_NAME IS '名';
            COMMENT ON COLUMN EMPLOYEES.LAST_NAME IS '姓';
            COMMENT ON COLUMN EMPLOYEES.EMAIL IS '邮箱';
            COMMENT ON COLUMN EMPLOYEES.HIRE_DATE IS '入职日期';
            """;
        
        try {
            TableInfo tableInfo = parseCreateTable(testSql);
            System.out.println("表名: " + tableInfo.tableName);
            System.out.println("表注释: " + tableInfo.tableComment);
            System.out.println("\n字段列表:");
            
            for (FieldDefinition field : tableInfo.fields) {
                System.out.printf("  %-20s %-15s %-10s %-5s %-10s %s%n",
                    field.name,
                    field.dbType,
                    field.javaType,
                    field.primary ? "PK" : "",
                    field.required ? "NOT NULL" : "",
                    field.comment);
            }
            
            System.out.println("\n主键字段: " + tableInfo.primaryKeys);
            System.out.println("唯一约束字段: " + tableInfo.uniqueConstraints);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 向后兼容方法：返回SimpleFieldTemplate列表
     * @param sql SQL建表语句
     * @return 简化字段模板列表
     */
    public static List<SimpleFieldTemplate> parseCreateTableSimple(String sql) {
        TableInfo tableInfo = parseCreateTable(sql);
        List<SimpleFieldTemplate> result = new ArrayList<>();

        for (FieldDefinition field : tableInfo.fields) {
            SimpleFieldTemplate template = new SimpleFieldTemplate();
            template.name = field.name;
            template.label = field.comment != null && !field.comment.isEmpty() ? field.comment : field.name;
            template.type = field.javaType != null ? field.javaType : "String";
            template.dbType = field.dbType != null ? field.dbType : "VARCHAR2(50)";
            template.length = field.length;
            template.required = field.required;
            template.primary = field.primary;
            template.comment = field.comment;
            template.editable = field.editable;
            template.uiType = field.uiType;
            result.add(template);
        }

        return result;
    }

    /**
     * 为了向后兼容，保留原始的SimpleFieldTemplate类
     */
    public static class SimpleFieldTemplate {
        public String name;
        public String label;
        public String type;
        public String dbType;
        public Integer length;
        public Boolean required;
        public Boolean primary;
        public String comment;
        public Boolean editable;
        public String uiType;
    }
}