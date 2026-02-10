import type { BillConfig } from '../models';
import TemplateEngine from '../utils/templateEngine';
import path from 'path';

export class ClientGenerator {
  private templateEngine: TemplateEngine;
  private outputDir: string;

  constructor(outputDir: string) {
    this.outputDir = outputDir;
    this.templateEngine = new TemplateEngine();
  }

  async generate(billConfig: BillConfig): Promise<void> {
    console.log('开始生成客户端代码');

    await this.generateController(billConfig);
    await this.generateIPrivateBtn(billConfig);
    await this.generateClientUI(billConfig);
    await this.generateBusinessAction(billConfig);
    await this.generateDelegator(billConfig);
    await this.generateEventHandler(billConfig);
    await this.generateRefModel(billConfig);

    console.log('客户端代码生成完成');
  }

  private async generateController(billConfig: BillConfig): Promise<void> {
    let templatePath: string;
    if (billConfig.billType === 'archive') {
      templatePath = 'client/by-type/archive/Controller_Archive.ejs';
    } else if (billConfig.billType === 'multi') {
      templatePath = 'client/by-type/multi/Controller_Multi.ejs';
    } else {
      templatePath = 'client/by-type/single/Controller_Single.ejs';
    }

    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const outputPath = path.join(
      this.outputDir,
      `src/client/nc/ui/${billConfig.packageName}/${billConfig.billCode.toLowerCase()}/controller/ClientCtrl.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成Controller: ${outputPath}`);
  }

  private async generateIPrivateBtn(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'client/common/IPrivateBtn.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/client/nc/ui/${billConfig.packageName}/${billConfig.billCode.toLowerCase()}/controller/IPrivateBtn.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成IPrivateBtn: ${outputPath}`);
  }

  private async generateClientUI(billConfig: BillConfig): Promise<void> {
    let templatePath: string;
    if (billConfig.billType === 'archive') {
      templatePath = 'client/by-type/archive/ClientUI_Archive.ejs';
    } else if (billConfig.billType === 'multi') {
      templatePath = 'client/by-type/multi/ClientUI_Multi.ejs';
    } else {
      templatePath = 'client/by-type/single/ClientUI_Single.ejs';
    }

    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const outputPath = path.join(
      this.outputDir,
      `src/client/nc/ui/${billConfig.packageName}/${billConfig.billCode.toLowerCase()}/ui/ClientUI.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成ClientUI: ${outputPath}`);
  }

  private async generateBusinessAction(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'client/common/BusinessAction.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/client/nc/ui/${billConfig.packageName}/${billConfig.billCode.toLowerCase()}/action/BusinessAction.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成BusinessAction: ${outputPath}`);
  }

  private async generateDelegator(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'client/common/Delegator.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/client/nc/ui/${billConfig.packageName}/${billConfig.billCode.toLowerCase()}/delegator/ClientDelegator.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成Delegator: ${outputPath}`);
  }

  private async generateEventHandler(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'client/common/EventHandler.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/client/nc/ui/${billConfig.packageName}/${billConfig.billCode.toLowerCase()}/handler/EventHandler.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成EventHandler: ${outputPath}`);
  }

  private async generateRefModel(billConfig: BillConfig): Promise<void> {
    const context = { bill: billConfig, date: new Date().toISOString().split('T')[0] };
    const templatePath = 'client/common/RefModel.ejs';
    const outputPath = path.join(
      this.outputDir,
      `src/client/nc/ui/${billConfig.packageName}/${billConfig.billCode.toLowerCase()}/refmodel/RefModel.java`
    );
    
    await this.templateEngine.renderAndWrite(templatePath, outputPath, context);
    console.log(`生成RefModel: ${outputPath}`);
  }
}

export default ClientGenerator;
