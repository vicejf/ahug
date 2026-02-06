# CODEBUDDY.md This file provides guidance to CodeBuddy when working with code in this repository.

## 常用命令

- **构建项目**: `mvn clean package` - 清理并打包项目，生成可执行JAR文件
- **运行GUI界面**: `mvn javafx:run` - 通过Maven插件启动JavaFX图形界面
- **CLI代码生成**: `java -jar target/nc5-code-generator-1.0.0-jar-with-dependencies.jar config/document-directory/config.xml output/generated-directory/` - 命令行模式生成NC5代码
- **手动运行GUI**: `java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar target/nc5-code-generator-gui.jar` - 直接Java命令运行GUI

## 项目架构概述

本项目是有道NC5文档代码生成器，基于Velocity模板引擎的Java工具，通过XML配置文件驱动，自动生成NC5系统文档代码。

### 核心特性

- **配置驱动**: XML定义结构 + JSON定义字段，实现声明式代码生成
- **双模式运行**: 支持GUI图形界面和CLI命令行两种使用方式
- **分层生成**: 按VO层 → Client层 → 业务逻辑层顺序生成完整代码结构
- **NC5兼容**: 强制GBK编码，符合NC5标准代码结构和命名规范
- **独立运行**: 不依赖完整NC环境，可单独部署使用

### 技术栈

- **Java 21**: 基础运行时环境
- **JavaFX 21.0.3**: 图形界面框架
- **Velocity 1.7**: 模板引擎核心
- **Maven 3.x**: 项目构建和依赖管理
- **GBK编码**: NC5兼容性强制要求

### 架构设计

项目采用配置驱动的模板化生成架构。核心流程：XML配置文件定义文档结构和元数据，JSON文件定义具体字段属性，Velocity模板引擎根据配置渲染生成Java代码。生成过程分为三个层次：VO层生成值对象(HVO/BVO/AggVO)，Client层生成数据访问组件，BS层生成业务逻辑处理。

目录结构遵循标准Maven布局，`src/main/resources/templates/`包含核心Velocity模板，`config/`存放用户配置文件，`output/`为代码生成输出目录。GUI和CLI共享相同的生成引擎，通过不同的入口类实现统一的功能逻辑。

### 关键约束

所有生成的Java文件必须使用GBK编码确保NC5环境兼容性。文档编码采用4位大写字母格式(如AUJX)。JavaFX运行需要正确的模块路径配置。生成器设计为独立工具，可在无NC环境的开发机上运行。