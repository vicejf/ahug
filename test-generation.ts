import CodeGenerator from './src/core/generators/CodeGenerator.ts';
import fs from 'fs';

async function testCodeGeneration() {
  console.log('开始测试代码生成...\n');

  const configPath = './test-config.json';
  const outputDir = './test-output';

  try {
    const configContent = fs.readFileSync(configPath, 'utf-8');
    const config = JSON.parse(configContent);

    console.log('配置信息:');
    console.log(`  单据编码: ${config.billCode}`);
    console.log(`  单据名称: ${config.billName}`);
    console.log(`  模块: ${config.module}`);
    console.log(`  表头字段数: ${config.headFields.length}`);
    console.log(`  表体字段数: ${config.bodyFields.length}`);
    console.log(`  生成客户端: ${config.globalConfig.generateClient}`);
    console.log(`  生成业务逻辑: ${config.globalConfig.generateBusiness}`);
    console.log(`  生成元数据: ${config.globalConfig.generateMetadata}\n`);

    const generator = new CodeGenerator(outputDir);
    const result = await generator.generate(config);

    console.log('\n生成结果:');
    console.log(`  成功: ${result.success}`);
    console.log(`  耗时: ${result.duration}ms`);
    
    if (result.stats) {
      console.log(`  文件数: ${result.stats.fileCount}`);
      console.log(`  代码行数: ${result.stats.codeLines}`);
    }

    if (result.errorMessage) {
      console.log(`  错误: ${result.errorMessage}`);
    }

    console.log(`  输出目录: ${result.outputDir}`);
  } catch (error) {
    console.error('测试失败:', error);
    process.exit(1);
  }
}

testCodeGeneration();
