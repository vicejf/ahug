# NC5代码生成器

用友NC5单据代码生成器，基于Velocity模板引擎，通过配置文件自动生成完整的单据代码。

## 功能特性

- 配置驱动：使用XML配置文件定义单据结构
- 模板生成：基于Velocity模板引擎生成各类Java文件
- 分层生成：支持VO层、客户端UI层、业务逻辑层代码生成
- 独立运行：不依赖NC项目环境，可单独执行

## 技术栈

- Java 21
- Maven 3.x
- Velocity 1.7
- Dom4j 2.1.3

## 使用说明

### 1. 编译项目

```bash
mvn clean package
```

### 2. 配置单据

在`config`目录下创建XML配置文件，参考`config/aujz-config.xml`。

### 3. 生成代码

```bash
java -jar target/nc5-code-generator-1.0.0-jar-with-dependencies.jar config/your-config.xml
```

生成的代码将输出到`output`目录。

## 项目结构

```
nc5-code-generator/
├── src/main/java/com/nc5/generator/
│   ├── Main.java                    # 入口类
│   ├── config/                      # 配置模型
│   ├── template/                    # 模板引擎
│   └── generator/                   # 代码生成器
├── src/main/resources/
│   └── templates/                   # Velocity模板
├── config/                          # 用户配置文件
└── output/                          # 代码输出目录
```

## 支持的代码类型

- VO层：HVO（表头）、BVO（表体）、AggVO（聚合）
- 客户端UI层：Controller、IPrivateBtn、MyEventHandler、BusinessAction、Delegator
- 业务逻辑层：InsertAction、UpdateAction、DeleteAction、SaveAction




## Launch4j package

Basic
jar: D:\Documents\workspace\hdc-mes\ahug\target\nc5-code-generator-1.0.0-jar-with-dependencies.jar

classpath: D:\Documents\workspace\hdc-mes\ahug\target\nc5-code-generator-1.0.0-jar-with-dependencies.jar

maincalss: com.nc5.generator.fx.CodeGeneratorApp
jre paths:./jre
jvmoptions:--module-path /jre/lib --add-modules javafx.controls,javafx.fxml
