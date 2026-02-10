import type { BillConfig } from '../models';
import TemplateEngine from '../utils/templateEngine';
import path from 'path';

export class ImplGenerator {
  private templateEngine: TemplateEngine;
  private outputDir: string;

  constructor(outputDir: string) {
    this.outputDir = outputDir;
    this.templateEngine = new TemplateEngine();
  }

  static async generate(billConfig: BillConfig, _impl: any, outputDir: string): Promise<void> {
    const generator = new ImplGenerator(outputDir);
    await generator.generate(billConfig);
  }

  async generate(billConfig: BillConfig): Promise<void> {
    console.log('开始生成实现层代码');

    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'impl/by-type/ServerImpl.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/private/nc/bs/${billConfig.module}/${billConfig.billCode.toLowerCase()}/${billConfig.billCode}Impl.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成实现: ${outputPath}`);
  }
}

export default ImplGenerator;
