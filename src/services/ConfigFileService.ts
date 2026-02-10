import type { BillConfigData } from '../types';
import FileService from './FileService';

interface SaveConfigResult {
  success: boolean;
  filePath?: string;
  headFieldsPath?: string;
  bodyFieldsPath?: string;
  enumConfigPath?: string;
  error?: string;
}

interface LoadConfigResult {
  success: boolean;
  config?: BillConfigData;
  error?: string;
}

interface BasicConfigInfo {
  billCode: string;
  billName: string;
  billType: string;
  module: string;
  packageName: string;
  bodyCode: string;
  headCode: string;
  author: string;
  description: string;
  headFieldsPath?: string;
  bodyFieldsPath?: string;
  enumConfigPath?: string;
}

interface LoadBasicConfigResult {
  success: boolean;
  basicInfo?: BasicConfigInfo;
  error?: string;
}

class ConfigFileService {
  async saveConfig(config: BillConfigData, filePath: string): Promise<SaveConfigResult> {
    try {
      const billCode = config.basicInfo.billCode || 'config';
      const configDir = this.getConfigDirectory(filePath);

      // 保存基本信息配置为JSON格式
      const basicInfoPath = `${configDir}/${billCode.toLowerCase()}_basic.json`;
      const basicInfoContent = {
        basicInfo: config.basicInfo,
        bodyCodeList: config.bodyCodeList,
        globalConfig: config.globalConfig
      };
      
      const basicSaveResult = await FileService.writeFile(
        basicInfoPath,
        JSON.stringify(basicInfoContent, null, 2)
      );

      if (!basicSaveResult.success) {
        return {
          success: false,
          error: basicSaveResult.error || '保存基本信息配置失败'
        };
      }

      const headFieldsFileName = `${billCode.toLowerCase()}.json`;
      const headFieldsPath = `${configDir}/${headFieldsFileName}`;
      await FileService.writeFile(
        headFieldsPath,
        JSON.stringify(config.headFields, null, 2)
      );

      const bodyFieldsPath = config.basicInfo.billType === 'multi'
        ? `${configDir}/${config.basicInfo.bodyCode?.toLowerCase() || 'body'}.json`
        : undefined;

      if (bodyFieldsPath && config.basicInfo.billType === 'multi') {
        await FileService.writeFile(
          bodyFieldsPath,
          JSON.stringify(config.bodyFields, null, 2)
        );
      }

      // 保存枚举配置
      const enumConfigPath = `${configDir}/${billCode.toLowerCase()}_enums.json`;
      await FileService.writeFile(
        enumConfigPath,
        JSON.stringify(config.enumConfigs, null, 2)
      );

      return {
        success: true,
        filePath: basicInfoPath, // 返回基本信息配置文件路径
        headFieldsPath,
        bodyFieldsPath: bodyFieldsPath,
        enumConfigPath
      };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      return {
        success: false,
        error: errorMessage
      };
    }
  }

  async loadConfig(filePath: string): Promise<LoadConfigResult> {
    // 为了向后兼容，重定向到 loadFullConfig
    return this.loadFullConfig(filePath);
  }





  private getConfigDirectory(filePath: string): string {
    const parts = filePath.split(/[/\\]/);
    parts.pop();
    return parts.join('/') || '.';
  }

  async loadBasicConfigInfo(filePath: string): Promise<LoadBasicConfigResult> {
    try {
      const readResult = await FileService.readFile(filePath);

      if (!readResult.success) {
        return {
          success: false,
          error: readResult.error || '读取配置文件失败'
        };
      }

      // 解析JSON格式的基本信息
      interface BasicConfigJson {
        basicInfo: BasicConfigInfo;
        bodyCodeList: string[];
        globalConfig: BillConfigData['globalConfig'];
      }
      
      let basicConfig: BasicConfigJson;
      try {
        basicConfig = JSON.parse(readResult.content || '');
      } catch (err) {
        console.error('解析基本信息配置失败:', err);
        return {
          success: false,
          error: '解析基本信息配置失败'
        };
      }

      const basicInfo: BasicConfigInfo = basicConfig.basicInfo;

      const configDir = this.getConfigDirectory(filePath);
      const billCode = basicInfo.billCode;

      // 设置关联文件路径
      basicInfo.headFieldsPath = `${configDir}/${billCode.toLowerCase()}.json`;
      
      if (basicInfo.billType === 'multi' && basicInfo.bodyCode) {
        basicInfo.bodyFieldsPath = `${configDir}/${basicInfo.bodyCode.toLowerCase()}.json`;
      }
      
      basicInfo.enumConfigPath = `${configDir}/${billCode.toLowerCase()}_enums.json`;

      return {
        success: true,
        basicInfo
      };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      return {
        success: false,
        error: errorMessage
      };
    }
  }

  async loadFullConfig(filePath: string): Promise<LoadConfigResult> {
    try {
      const readResult = await FileService.readFile(filePath);

      if (!readResult.success) {
        return {
          success: false,
          error: readResult.error || '读取配置文件失败'
        };
      }

      // 解析JSON格式的基本信息配置
      interface BasicConfigJson {
        basicInfo: BillConfigData['basicInfo'];
        bodyCodeList: string[];
        globalConfig: BillConfigData['globalConfig'];
      }
      
      let basicConfig: BasicConfigJson;
      try {
        basicConfig = JSON.parse(readResult.content || '');
      } catch (error) {
        return {
          success: false,
          error: '解析基本信息配置失败'
        };
      }

      const config: BillConfigData = {
        basicInfo: basicConfig.basicInfo || {
          billCode: '',
          billName: '',
          module: '',
          packageName: '',
          bodyCode: '',
          headCode: '',
          billType: 'single',
          author: '',
          description: ''
        },
        headFields: [],
        bodyFields: [],
        bodyCodeList: basicConfig.bodyCodeList || [],
        enumConfigs: [],
        globalConfig: basicConfig.globalConfig || {
          outputDir: './output',
          sourcePath: '',
          author: 'Flynn Chen',
          syncAfterGenerate: false,
          generateClient: true,
          generateBusiness: true,
          generateMetadata: false
        }
      };

      const configDir = this.getConfigDirectory(filePath);
      const billCode = config.basicInfo.billCode;

      // 加载表头字段
      const headFieldsPath = `${configDir}/${billCode.toLowerCase()}.json`;
      const headFieldsResult = await FileService.readFile(headFieldsPath);
      if (headFieldsResult.success && headFieldsResult.content) {
        try {
          config.headFields = JSON.parse(headFieldsResult.content);
        } catch (e) {
          console.error('Failed to parse head fields:', e);
        }
      }

      // 加载表体字段
      if (config.basicInfo.billType === 'multi' && config.basicInfo.bodyCode) {
        const bodyFieldsPath = `${configDir}/${config.basicInfo.bodyCode.toLowerCase()}.json`;
        const bodyFieldsResult = await FileService.readFile(bodyFieldsPath);
        if (bodyFieldsResult.success && bodyFieldsResult.content) {
          try {
            config.bodyFields = JSON.parse(bodyFieldsResult.content);
          } catch (e) {
            console.error('Failed to parse body fields:', e);
          }
        }
      }

      // 加载枚举配置
      const enumConfigPath = `${configDir}/${billCode.toLowerCase()}_enums.json`;
      const enumConfigResult = await FileService.readFile(enumConfigPath);
      if (enumConfigResult.success && enumConfigResult.content) {
        try {
          config.enumConfigs = JSON.parse(enumConfigResult.content);
        } catch (e) {
          console.error('Failed to parse enum configs:', e);
        }
      }

      return {
        success: true,
        config
      };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      return {
        success: false,
        error: errorMessage
      };
    }
  }


}

export default new ConfigFileService();
