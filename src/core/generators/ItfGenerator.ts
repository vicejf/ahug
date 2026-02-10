import type { BillConfig } from '../models';
import TemplateEngine from '../utils/templateEngine';
import path from 'path';

export class ItfGenerator {
  private templateEngine: TemplateEngine;
  private outputDir: string;

  constructor(outputDir: string) {
    this.outputDir = outputDir;
    this.templateEngine = new TemplateEngine();
  }

  static async generate(billConfig: BillConfig, outputDir: string): Promise<void> {
    const generator = new ItfGenerator(outputDir);
    await generator.generate(billConfig);
  }

  async generate(billConfig: BillConfig): Promise<void> {
    console.log('开始生成接口层代码');

    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'itf/by-type/IServer.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/public/nc/itf/${billConfig.module}/${billConfig.billCode.toLowerCase()}/I${billConfig.billCode}.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成接口: ${outputPath}`);
  }
}

export default ItfGenerator;
