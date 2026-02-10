import type { BillConfig } from '../models';
import TemplateEngine from '../utils/templateEngine';
import path from 'path';

export class BsGenerator {
  private templateEngine: TemplateEngine;
  private outputDir: string;

  constructor(outputDir: string) {
    this.outputDir = outputDir;
    this.templateEngine = new TemplateEngine();
  }

  async generate(billConfig: BillConfig): Promise<void> {
    console.log('开始生成业务逻辑代码');

    await this.generateInsertAction(billConfig);
    await this.generateUpdateAction(billConfig);
    await this.generateDeleteAction(billConfig);
    await this.generateSaveAction(billConfig);
    await this.generateQueryAction(billConfig);
    await this.generatePubActions(billConfig);

    console.log('业务逻辑代码生成完成');
  }

  private async generateInsertAction(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'bs/common/InsertAction.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/private/nc/bs/${billConfig.module}/${billConfig.billCode.toLowerCase()}/InsertAction.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成InsertAction: ${outputPath}`);
  }

  private async generateUpdateAction(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'bs/common/UpdateAction.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/private/nc/bs/${billConfig.module}/${billConfig.billCode.toLowerCase()}/UpdateAction.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成UpdateAction: ${outputPath}`);
  }

  private async generateDeleteAction(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'bs/common/DeleteAction.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/private/nc/bs/${billConfig.module}/${billConfig.billCode.toLowerCase()}/DeleteAction.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成DeleteAction: ${outputPath}`);
  }

  private async generateSaveAction(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'bs/common/SaveAction.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/public/nc/action/${billConfig.module}/${billConfig.billCode.toLowerCase()}/N_${billConfig.billCode}_SAVE.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成SaveAction: ${outputPath}`);
  }

  private async generateQueryAction(billConfig: BillConfig): Promise<void> {
    let templatePath: string;
    if (billConfig.billType === 'archive') {
      templatePath = 'bs/by-type/archive/QueryAction_Archive.ejs';
    } else if (billConfig.billType === 'multi') {
      templatePath = 'bs/by-type/multi/QueryAction_Multi.ejs';
    } else {
      templatePath = 'bs/by-type/single/QueryAction_Single.ejs';
    }

    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const outputPath = path.join(
      this.outputDir,
      `src/private/nc/bs/${billConfig.module}/${billConfig.billCode.toLowerCase()}/QueryAction.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成QueryAction: ${outputPath}`);
  }

  private async generatePubActions(billConfig: BillConfig): Promise<void> {
    console.log('开始生成发布动作类');
    
    const pubActions = [
      { actionName: 'DELETE', templateFile: 'PubAction_DELETE.ejs' },
      { actionName: 'EDIT', templateFile: 'PubAction_EDIT.ejs' },
      { actionName: 'FREEZE', templateFile: 'PubAction_Simple.ejs' },
      { actionName: 'UNFREEZE', templateFile: 'PubAction_Simple.ejs' },
      { actionName: 'WRITE', templateFile: 'PubAction_WRITE.ejs' },
      { actionName: 'WRITEBATCH', templateFile: 'PubAction_WRITEBATCH.ejs' },
      { actionName: 'SAVE', templateFile: 'PubAction_SAVE.ejs' },
      { actionName: 'APPROVE', templateFile: 'PubAction_Approve.ejs' }
    ];
    
    for (const action of pubActions) {
      await this.generatePubAction(billConfig, action.actionName, action.templateFile);
    }
    
    console.log('发布动作类生成完成');
  }
  
  private async generatePubAction(billConfig: BillConfig, actionName: string, templateFile: string): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = `bs/common/${templateFile}`;
    const outputPath = path.join(
      this.outputDir,
      `src/private/nc/bs/pub/action/N_${billConfig.billCode}_${actionName}.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成发布动作类: N_${billConfig.billCode}${actionName}`);
  }
}

export default BsGenerator;
