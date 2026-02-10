import type { BillConfig, GenerationResult, CodeStatistics } from '../models';
import VoGenerator from './VoGenerator';
import BsGenerator from './BsGenerator';
import ClientGenerator from './ClientGenerator';
import MetadataGenerator from './MetadataGenerator';
import ItfGenerator from './ItfGenerator';
import ImplGenerator from './ImplGenerator';
import RuleGenerator from './RuleGenerator';
import path from 'path';
import fs from 'fs';

export class CodeGenerator {
  private outputDir: string;
  private voGenerator: VoGenerator;
  private clientGenerator: ClientGenerator;
  private bsGenerator: BsGenerator;
  private metadataGenerator: MetadataGenerator;

  constructor(outputDir: string) {
    this.outputDir = outputDir;
    this.voGenerator = new VoGenerator(outputDir);
    this.clientGenerator = new ClientGenerator(outputDir);
    this.bsGenerator = new BsGenerator(outputDir);
    this.metadataGenerator = new MetadataGenerator(outputDir);
  }

  async generate(billConfig: BillConfig): Promise<GenerationResult> {
    const startTime = Date.now();
    
    console.log('========================================');
    console.log(`开始生成代码: ${billConfig.billName}`);
    console.log(`单据编码: ${billConfig.billCode}`);
    console.log('========================================');

    try {
      if (billConfig.globalConfig && billConfig.globalConfig.generateMetadata) {
        await this.metadataGenerator.generate(billConfig);
      }

      await this.voGenerator.generate(billConfig);

      if (billConfig.globalConfig && billConfig.globalConfig.generateClient) {
        await this.clientGenerator.generate(billConfig);
      }

      if (billConfig.globalConfig && billConfig.globalConfig.generateBusiness) {
        await ItfGenerator.generate(billConfig, this.outputDir);
        await this.bsGenerator.generate(billConfig);
        await ImplGenerator.generate(billConfig, null, this.outputDir);
        await RuleGenerator.generate(billConfig, this.outputDir);
      }

      const duration = Date.now() - startTime;
      const stats = await this.countJavaFiles(this.outputDir);

      console.log('========================================');
      console.log('代码生成完成！');
      console.log(`输出目录: ${this.outputDir}`);
      console.log('========================================');

      return {
        success: true,
        duration,
        stats,
        outputDir: this.outputDir
      };
    } catch (error) {
      const duration = Date.now() - startTime;
      console.error('代码生成失败:', error);
      
      return {
        success: false,
        duration,
        errorMessage: error instanceof Error ? error.message : '未知错误'
      };
    }
  }

  private async countJavaFiles(dir: string): Promise<CodeStatistics> {
    let fileCount = 0;
    let codeLines = 0;

    const walkDir = (currentPath: string) => {
      const files = fs.readdirSync(currentPath);
      
      for (const file of files) {
        const filePath = path.join(currentPath, file);
        const stat = fs.statSync(filePath);
        
        if (stat.isDirectory()) {
          walkDir(filePath);
        } else if (file.endsWith('.java')) {
          fileCount++;
          const content = fs.readFileSync(filePath, 'utf-8');
          codeLines += content.split('\n').length;
        }
      }
    };

    walkDir(dir);

    return { fileCount, codeLines };
  }

  getOutputDir(): string {
    return this.outputDir;
  }

  setOutputDir(outputDir: string): void {
    this.outputDir = outputDir;
    this.voGenerator = new VoGenerator(outputDir);
    this.clientGenerator = new ClientGenerator(outputDir);
    this.bsGenerator = new BsGenerator(outputDir);
    this.metadataGenerator = new MetadataGenerator(outputDir);
  }
}

export default CodeGenerator;
