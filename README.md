# NC5代码生成器

<div align="center">

**基于Velocity模板引擎的用友NC5单据代码生成器**

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.3-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.x-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

[功能特性](#功能特性) • [快速开始](#快速开始) • [使用说明](#使用说明) • [配置指南](#配置指南) • [常见问题](#常见问题)

</div>

---

## 📖 项目概述

NC5代码生成器是一款专为用友NC5系统设计的代码生成工具,通过XML配置文件驱动,使用Velocity模板引擎自动生成完整的单据代码。该工具支持图形界面和命令行两种操作模式,可独立运行,不依赖NC项目环境。

### 核心特点

- ✨ **配置驱动**: 使用结构化XML配置文件定义单据结构
- 🎨 **双模式支持**: 提供图形界面(GUI)和命令行(CLI)两种使用方式
- 🔧 **分层生成**: 支持VO层、客户端UI层、业务逻辑层代码生成
- 📦 **独立运行**: 无需依赖完整的NC项目环境
- 🚀 **GBK编码支持**: 生成的Java文件采用GBK编码,完美兼容NC5系统
- 🔄 **代码同步**: 支持生成后自动同步到项目源码目录
- 💾 **配置持久化**: 全局配置自动保存,支持最近文件快速访问

---

## 🎯 功能特性

### 支持的代码类型

| 代码类型 | 说明 |
|---------|------|
| **VO层** | HVO(表头)、BVO(表体)、AggVO(聚合) |
| **客户端UI层** | Controller、IPrivateBtn、MyEventHandler、BusinessAction、Delegator |
| **业务逻辑层** | InsertAction、UpdateAction、DeleteAction、SaveAction |
| **元数据** | 支持PubBillInterface、User、BillStatus等元数据配置 |

### 核心功能

- 📝 **可视化配置**: 通过友好的GUI界面配置单据信息、字段、枚举等
- 🔍 **实时验证**: 字段配置实时验证,确保数据准确性
- 📊 **代码预览**: 生成前预览代码结构,支持在线查看
- ⚡ **快速生成**: 一键生成所有代码,支持进度显示
- 🗂️ **文件管理**: 智能管理配置文件,支持最近文件列表
- 🎛️ **全局配置**: 可配置作者、输出目录、源码路径等全局参数

---

## 🛠 技术栈

| 技术 | 版本 | 说明 |
|-----|------|-----|
| Java | 21 | 核心开发语言 |
| JavaFX | 21.0.3 | 图形界面框架 |
| JFoenix | 9.0.10 | Material Design组件库 |
| Velocity | 1.7 | 模板引擎 |
| Dom4j | 2.1.3 | XML解析 |
| Maven | 3.x | 项目构建工具 |
| Gson | 2.10.1 | JSON处理 |

---

## 🚀 快速开始

### 环境要求

- JDK 21 或更高版本
- Maven 3.x
- Windows/Linux/macOS 操作系统

### 安装步骤

#### 1. 克隆或下载项目

```bash
git clone <repository-url>
cd nc5-code-generator
```

#### 2. 编译项目

```bash
# 清理并打包
mvn clean package

# 编译完成后,将在 target/ 目录下生成以下文件:
# - nc5-code-generator-1.0.0-jar-with-dependencies.jar  (命令行模式)
# - nc5-code-generator-gui.jar                           (图形界面模式)
```

#### 3. 运行应用

**图形界面模式:**

```bash
# 方式一: 使用Maven插件运行
mvn javafx:run

# 方式二: 使用Java命令运行(需指定JavaFX模块路径)
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml ^
     -jar target/nc5-code-generator-gui.jar
```

**命令行模式:**

```bash
java -jar target/nc5-code-generator-1.0.0-jar-with-dependencies.jar config/your-config.xml [output]
```

---

## 📖 使用说明

### 图形界面模式

#### 1. 主界面说明

应用启动后,主界面包含以下选项卡:

- **基本信息**: 配置单据编码、名称、模块、包名等基础信息
- **表头字段**: 配置单据表头字段信息
- **表体字段**: 配置单据表体字段信息(仅多行单据显示)
- **枚举配置**: 配置枚举类型字段
- **代码生成**: 配置生成选项并执行代码生成
- **代码预览**: 预览生成的代码文件结构

#### 2. 配置单据

**步骤一: 新建或打开配置**

- 点击 `文件` → `新建` 创建新配置
- 点击 `文件` → `打开` 打开已有配置文件
- 最近文件会显示在 `文件` → `最近文件` 菜单中

**步骤二: 填写基本信息**

在"基本信息"选项卡中填写:
- **单据编码**: 单据的唯一标识(必填,如"AUJX")
- **单据名称**: 单据的显示名称
- **模块**: 所属模块名称
- **包名**: Java包名
- **单据类型**: `single`(单据) 或 `multi`(多行单据)
- **作者**: 代码作者
- **描述**: 单据描述信息

**步骤三: 配置字段**

在"表头字段"或"表体字段"选项卡中添加字段:

| 字段属性 | 说明 |
|---------|------|
| 字段名 | Java字段名(英文) |
| 显示名称 | 界面显示标签 |
| 数据类型 | String, Integer, UFDate等 |
| 数据库类型 | VARCHAR2, NUMBER, DATE等 |
| 长度 | 字段长度 |
| UI类型 | Text, Date, Reference等 |
| 必填 | 是否必填 |
| 主键 | 是否主键 |
| 可编辑 | 是否可编辑 |

**步骤四: 配置枚举(可选)**

如果需要使用枚举类型字段,在"枚举配置"选项卡中添加枚举项。

#### 3. 生成代码

**步骤一: 配置生成选项**

在"代码生成"选项卡中配置:
- **输出目录**: 代码生成的目标目录
- **项目源码目录**: 同步代码的目标项目源码目录
- **生成选项**:
  - ☑ 生成客户端代码
  - ☑ 生成业务逻辑代码
  - ☑ 生成元数据
  - ☑ 生成后同步到项目
- **元数据开关**: 配置各类元数据的启用状态

**步骤二: 执行生成**

点击 `生成代码` 按钮,系统将:
1. 验证配置信息的完整性
2. 显示生成进度
3. 生成所有代码文件到输出目录
4. 如果勾选了"生成后同步",则自动同步到项目源码目录

**步骤三: 预览代码**

在"代码预览"选项卡中可以:
- 查看生成的文件树结构
- 点击文件查看代码内容
- 直接打开文件所在目录

### 命令行模式

#### 基本用法

```bash
java -jar nc5-code-generator-1.0.0-jar-with-dependencies.jar <配置文件路径> [输出目录]
```

#### 参数说明

| 参数 | 必需 | 说明 |
|-----|------|------|
| 配置文件路径 | 是 | XML格式的配置文件路径 |
| 输出目录 | 否 | 代码输出目录,默认为`output` |

#### 使用示例

```bash
# 使用默认输出目录
java -jar nc5-code-generator-1.0.0-jar-with-dependencies.jar config/aujx/aujx-config.xml

# 指定输出目录
java -jar nc5-code-generator-1.0.0-jar-with-dependencies.jar config/aujx/aujx-config.xml my-output
```

---

## ⚙️ 配置指南

### 配置文件结构

配置文件采用XML格式,主要包含以下部分:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<billConfig>
    <!-- 基本信息配置 -->
    <basicInfo>
        <billCode>AUJX</billCode>
        <billName>测试单据</billName>
        <module>mymodule</module>
        <packageName>com.nc5.mymodule</packageName>
        <bodyCode>AUJXBVO</bodyCode>
        <billType>single</billType>
        <description>这是一个测试单据</description>
    </basicInfo>

    <!-- 字段配置文件路径 -->
    <fieldPaths>
        <headFieldsPath>AUJX.json</headFieldsPath>
        <bodyFieldsPath>AUJXBVO.json</bodyFieldsPath>
    </fieldPaths>

    <!-- 全局配置 -->
    <globalConfig>
        <!-- 代码生成选项 -->
        <generateOptions>
            <generateClient>true</generateClient>
            <generateBusiness>true</generateBusiness>
            <generateMetadata>false</generateMetadata>
            <syncAfterGenerate>false</syncAfterGenerate>
        </generateOptions>

        <!-- 元数据生成开关 -->
        <metadataSwitches>
            <enablePubBillInterface>true</enablePubBillInterface>
            <enableUser>true</enableUser>
            <enableBillStatus>true</enableBillStatus>
        </metadataSwitches>
    </globalConfig>
</billConfig>
```

### 字段配置文件示例

字段配置采用JSON格式:

```json
[
  {
    "name": "pk_org",
    "label": "组织",
    "type": "String",
    "dbType": "VARCHAR2(20)",
    "length": 20,
    "uiType": "Text",
    "required": true,
    "primaryKey": true,
    "editable": false
  },
  {
    "name": "billno",
    "label": "单据号",
    "type": "String",
    "dbType": "VARCHAR2(50)",
    "length": 50,
    "uiType": "Text",
    "required": true,
    "primaryKey": false,
    "editable": true
  }
]
```

### 配置文件组织

建议的配置文件组织结构:

```
nc5-code-generator/
├── config/
│   ├── aujx/                    # 单据AUJX的配置目录
│   │   ├── aujx-config.xml      # 主配置文件
│   │   ├── AUJX.json            # 表头字段配置
│   │   └── AUJXBVO.json         # 表体字段配置
│   ├── aujy/                    # 单据AUJY的配置目录
│   │   └── ...
│   └── global_cfg.ini           # 全局配置文件
├── output/                      # 代码输出目录
└── src/                         # 项目源码目录
```

---

## 🔧 高级功能

### 全局配置

全局配置文件(`global_cfg.ini`)存储在配置目录中,包含以下信息:

- 作者信息
- 默认输出目录
- 默认源码路径
- 最近打开的文件列表

全局配置会自动加载和应用,提高工作效率。

### 开发模式热重载

在开发模式下启用FXML热重载功能:

```java
// CodeGeneratorApp.java
private static final boolean DEV_MODE = true;
```

启用后,修改FXML文件时无需重启应用即可看到效果。

### Launch4j打包配置

如需将应用打包为Windows可执行文件,可使用Launch4j:

**基本配置:**
- Jar路径: `target/nc5-code-generator-gui.jar`
- 主类: `com.nc5.generator.fx.CodeGeneratorApp`
- JRE路径: `./jre`
- JVM参数: `--module-path /jre/lib --add-modules javafx.controls,javafx.fxml`

---

## ❓ 常见问题

### Q1: 生成的代码文件编码是什么?

**A:** 所有生成的Java文件均采用GBK编码,以兼容用友NC5系统。

### Q2: 如何修改代码模板?

**A:** 模板文件位于 `src/main/resources/templates/` 目录下,可以根据需要修改Velocity模板文件。

### Q3: 图形界面模式无法启动怎么办?

**A:** 请确保:
- 已安装JavaFX SDK
- 正确设置了 `--module-path` 和 `--add-modules` 参数
- JDK版本为21或更高

### Q4: 如何添加新的字段类型?

**A:** 可以在模板文件中添加新的类型定义,并在配置中引用。

### Q5: 支持批量生成多个单据吗?

**A:** 当前版本不支持批量生成,需要逐个配置和生成。可以通过命令行模式编写批处理脚本实现。

### Q6: 如何自定义生成代码的包结构?

**A:** 在配置文件中设置 `packageName` 属性即可自定义包结构。

---

## 📚 项目结构

```
nc5-code-generator/
├── src/main/java/com/nc5/generator/
│   ├── Main.java                    # 命令行入口
│   ├── fx/                          # JavaFX图形界面
│   │   ├── CodeGeneratorApp.java    # 应用启动类
│   │   ├── controller/              # 控制器
│   │   ├── model/                   # 数据模型
│   │   └── util/                    # 工具类
│   ├── config/                      # 配置模型
│   ├── generator/                   # 代码生成器
│   └── service/                     # 业务服务
├── src/main/resources/
│   ├── view/                        # FXML界面文件
│   ├── css/                         # 样式文件
│   └── templates/                   # Velocity模板
│       ├── vo/                      # VO层模板
│       ├── client/                  # 客户端模板
│       ├── bs/                      # 业务逻辑模板
│       ├── impl/                    # 实现类模板
│       ├── itf/                     # 接口模板
│       ├── METADATA/                # 元数据模板
│       └── rule/                    # 规则模板
├── config/                          # 用户配置文件
├── output/                          # 代码输出目录
├── target/                          # Maven构建输出
├── pom.xml                          # Maven配置文件
└── README.md                        # 项目文档
```

---

## 🤝 贡献指南

欢迎贡献代码、报告问题或提出改进建议!

### 贡献流程

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码规范

- 遵循Java代码规范
- 使用有意义的变量和方法命名
- 添加必要的注释说明
- 确保代码能够通过编译和测试

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件。

---

## 👨‍💻 作者

**Flynn Chen**

- 📧 邮箱: vicejf@live.com
- 💬 项目主页: [项目链接]

---

## 📞 联系方式

如有问题或建议,欢迎通过以下方式联系:

- 📧 发送邮件至: vicejf@live.com
- 🐛 提交Issue: [GitHub Issues]
- 💬 参与讨论: [GitHub Discussions]

---

## 🙏 致谢

感谢所有为本项目做出贡献的开发者!

---

<div align="center">

**如果这个项目对你有帮助,请给一个 ⭐️ Star 支持一下!**

Made with ❤️ by Flynn Chen

</div>
