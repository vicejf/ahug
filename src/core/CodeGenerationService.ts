import { ipcMain } from 'electron';
import fs from 'fs';
import path from 'path';
import CodeGenerator from './generators/CodeGenerator';
import type { BillConfig } from './models';

export class CodeGenerationService {
  private static instance: CodeGenerationService;

  private constructor() {
    this.registerIpcHandlers();
  }

  static getInstance(): CodeGenerationService {
    if (!CodeGenerationService.instance) {
      CodeGenerationService.instance = new CodeGenerationService();
    }
    return CodeGenerationService.instance;
  }

  private registerIpcHandlers(): void {
    ipcMain.handle('code-generation:generate', async (_event, configPath: string, outputDir?: string) => {
      try {
        const config = await this.loadConfig(configPath);
        const actualOutputDir = outputDir || config.globalConfig?.outputDir || path.join(path.dirname(configPath), 'output');
        
        const generator = new CodeGenerator(actualOutputDir);
        const result = await generator.generate(config);
        
        return result;
      } catch (error) {
        return {
          success: false,
          duration: 0,
          errorMessage: error instanceof Error ? error.message : '未知错误'
        };
      }
    });

    ipcMain.handle('code-generation:validate', async (_event, configPath: string) => {
      try {
        const config = await this.loadConfig(configPath);
        const errors = this.validateConfig(config);
        return { valid: errors.length === 0, errors };
      } catch (error) {
        return {
          valid: false,
          errors: [error instanceof Error ? error.message : '未知错误']
        };
      }
    });
  }

  private async loadConfig(configPath: string): Promise<BillConfig> {
    const content = fs.readFileSync(configPath, 'utf-8');
    return JSON.parse(content);
  }

  private validateConfig(config: BillConfig): string[] {
    const errors: string[] = [];

    if (!config.billCode) {
      errors.push('单据编码不能为空');
    }

    if (!config.billName) {
      errors.push('单据名称不能为空');
    }

    if (!config.module) {
      errors.push('模块名不能为空');
    }

    if (!config.packageName) {
      errors.push('包名不能为空');
    }

    if (!config.headFields || config.headFields.length === 0) {
      errors.push('表头字段不能为空');
    }

    if (config.billType === 'multi' && (!config.bodyFields || config.bodyFields.length === 0)) {
      errors.push('多表体类型必须有表体字段');
    }

    return errors;
  }
}

export default CodeGenerationService;
