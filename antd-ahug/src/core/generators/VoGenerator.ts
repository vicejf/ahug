import type { BillConfig } from '../models';
import TemplateEngine from '../utils/templateEngine';
import path from 'path';

export class VoGenerator {
  private templateEngine: TemplateEngine;
  private outputDir: string;

  constructor(outputDir: string) {
    this.outputDir = outputDir;
    this.templateEngine = new TemplateEngine();
  }

  async generate(billConfig: BillConfig): Promise<void> {
    console.log('开始生成VO代码');

    await this.generateHVO(billConfig);

    if (billConfig.billType === 'multi') {
      if (billConfig.bodyCodeList && billConfig.bodyCodeList.length > 0) {
        await this.generateMultipleBVO(billConfig);
      } else {
        await this.generateSingleBVO(billConfig);
      }
    }

    await this.generateAggVO(billConfig);

    console.log('VO代码生成完成');
  }

  private async generateHVO(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'vo/common/HVO.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/public/nc/vo/${billConfig.module}/${billConfig.billCode.toLowerCase()}/${billConfig.billCode}HVO.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成HVO: ${outputPath}`);
  }

  private async generateSingleBVO(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'vo/common/BVO.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/public/nc/vo/${billConfig.module}/${billConfig.billCode.toLowerCase()}/${billConfig.bodyCode}VO.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成BVO: ${outputPath}`);
  }

  private async generateMultipleBVO(billConfig: BillConfig): Promise<void> {
    for (const bodyCode of billConfig.bodyCodeList) {
      const context = { 
        bill: billConfig, 
        currentBodyCode: bodyCode,
        date: new Date().toISOString().split('T')[0]
      };
      const templatePath = 'vo/common/BVO.ejs';
      const outputPath = path.join(
        this.outputDir,
        `src/public/nc/vo/${billConfig.module}/${billConfig.billCode.toLowerCase()}/${bodyCode}VO.java`
      );
      
      await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
      console.log(`生成BVO: ${outputPath}`);
    }
  }

  private async generateAggVO(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'vo/common/AggVO.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/public/nc/vo/${billConfig.module}/${billConfig.billCode.toLowerCase()}/Agg${billConfig.billCode}VO.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成AggVO: ${outputPath}`);
  }
}

export default VoGenerator;
