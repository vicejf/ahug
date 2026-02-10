import type { BillConfig } from '../models';
import TemplateEngine from '../utils/templateEngine';
import path from 'path';

export class RuleGenerator {
  private templateEngine: TemplateEngine;
  private outputDir: string;

  constructor(outputDir: string) {
    this.outputDir = outputDir;
    this.templateEngine = new TemplateEngine();
  }

  static async generate(billConfig: BillConfig, outputDir: string): Promise<void> {
    const generator = new RuleGenerator(outputDir);
    await generator.generate(billConfig);
  }

  async generate(billConfig: BillConfig): Promise<void> {
    console.log('开始生成规则层代码');

    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'rule/by-type/Rule.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/private/nc/bs/${billConfig.module}/${billConfig.billCode.toLowerCase()}/Rule.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成规则: ${outputPath}`);
  }
}

export default RuleGenerator;
