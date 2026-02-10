import type { BillConfigData } from '../types';
import FileService from './FileService';

interface SaveConfigResult {
  success: boolean;
  filePath?: string;
  headFieldsPath?: string;
  bodyFieldsPath?: string;
  error?: string;
}

interface LoadConfigResult {
  success: boolean;
  config?: BillConfigData;
  error?: string;
}

class ConfigFileService {
  async saveConfig(config: BillConfigData, filePath: string): Promise<SaveConfigResult> {
    try {
      const billCode = config.basicInfo.billCode || 'config';
      const configDir = this.getConfigDirectory(filePath);

      const xmlContent = this.toXmlConfig(config);
      const saveResult = await FileService.writeFile(filePath, xmlContent);

      if (!saveResult.success) {
        return {
          success: false,
          error: saveResult.error || '保存配置文件失败'
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

      return {
        success: true,
        filePath,
        headFieldsPath,
        bodyFieldsPath: bodyFieldsPath
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
    try {
      const readResult = await FileService.readFile(filePath);

      if (!readResult.success) {
        return {
          success: false,
          error: readResult.error || '读取配置文件失败'
        };
      }

      const config = this.parseXmlConfig(readResult.content || '');

      if (!config) {
        return {
          success: false,
          error: '解析配置文件失败'
        };
      }

      const configDir = this.getConfigDirectory(filePath);
      const billCode = config.basicInfo.billCode;

      const headFieldsPath = `${configDir}/${billCode.toLowerCase()}.json`;
      const headFieldsResult = await FileService.readFile(headFieldsPath);

      if (headFieldsResult.success && headFieldsResult.content) {
        try {
          config.headFields = JSON.parse(headFieldsResult.content);
        } catch (e) {
          console.error('Failed to parse head fields:', e);
        }
      }

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

  private toXmlConfig(config: BillConfigData): string {
    return `<?xml version="1.0" encoding="UTF-8"?>
<billConfig>
    <basicInfo>
        <billCode>${this.escapeXml(config.basicInfo.billCode)}</billCode>
        <billName>${this.escapeXml(config.basicInfo.billName)}</billName>
        <module>${this.escapeXml(config.basicInfo.module)}</module>
        <packageName>${this.escapeXml(config.basicInfo.packageName)}</packageName>
        <bodyCode>${this.escapeXml(config.basicInfo.bodyCode)}</bodyCode>
        <billType>${this.escapeXml(config.basicInfo.billType)}</billType>
        <author>${this.escapeXml(config.basicInfo.author)}</author>
        <description>${this.escapeXml(config.basicInfo.description)}</description>
    </basicInfo>

    <globalConfig>
        <outputDir>${this.escapeXml(config.globalConfig.outputDir)}</outputDir>
        <sourcePath>${this.escapeXml(config.globalConfig.sourcePath)}</sourcePath>
        <author>${this.escapeXml(config.globalConfig.author)}</author>
        <syncAfterGenerate>${config.globalConfig.syncAfterGenerate}</syncAfterGenerate>
        <generateClient>${config.globalConfig.generateClient}</generateClient>
        <generateBusiness>${config.globalConfig.generateBusiness}</generateBusiness>
        <generateMetadata>${config.globalConfig.generateMetadata}</generateMetadata>
    </globalConfig>

    <enumConfigs>
        ${config.enumConfigs.map(enumConfig => `
        <enumConfig>
            <name>${this.escapeXml(enumConfig.name)}</name>
            <displayName>${this.escapeXml(enumConfig.displayName)}</displayName>
            <className>${this.escapeXml(enumConfig.className)}</className>
            <items>
                ${enumConfig.items.map(item => `
                <item>
                    <display>${this.escapeXml(item.display)}</display>
                    <value>${this.escapeXml(item.value)}</value>
                </item>`).join('')}
            </items>
        </enumConfig>`).join('')}
    </enumConfigs>

    <bodyCodeList>
        ${config.bodyCodeList.map(code => `<code>${this.escapeXml(code)}</code>`).join('\n        ')}
    </bodyCodeList>
</billConfig>`;
  }

  private parseXmlConfig(xmlContent: string): BillConfigData | null {
    try {
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(xmlContent, 'text/xml');

      const getTextContent = (selector: string): string => {
        const element = xmlDoc.querySelector(selector);
        return element?.textContent?.trim() || '';
      };

      const getBooleanContent = (selector: string): boolean => {
        const text = getTextContent(selector);
        return text === 'true';
      };

      const enumConfigElements = xmlDoc.querySelectorAll('enumConfig');
      const enumConfigs = Array.from(enumConfigElements).map(enumEl => {
        const itemElements = enumEl.querySelectorAll('item');
        const items = Array.from(itemElements).map(itemEl => ({
          display: itemEl.querySelector('display')?.textContent?.trim() || '',
          value: itemEl.querySelector('value')?.textContent?.trim() || ''
        }));

        return {
          name: enumEl.querySelector('name')?.textContent?.trim() || '',
          displayName: enumEl.querySelector('displayName')?.textContent?.trim() || '',
          className: enumEl.querySelector('className')?.textContent?.trim() || '',
          items
        };
      });

      const codeElements = xmlDoc.querySelectorAll('bodyCodeList code');
      const bodyCodeList = Array.from(codeElements).map(el => el.textContent?.trim() || '').filter(Boolean);

      return {
        basicInfo: {
          billCode: getTextContent('billCode'),
          billName: getTextContent('billName'),
          module: getTextContent('module'),
          packageName: getTextContent('packageName'),
          bodyCode: getTextContent('bodyCode'),
          headCode: getTextContent('headCode'),
          billType: getTextContent('billType') || 'single',
          author: getTextContent('author'),
          description: getTextContent('description')
        },
        headFields: [],
        bodyFields: [],
        bodyCodeList,
        enumConfigs,
        globalConfig: {
          outputDir: getTextContent('outputDir') || './output',
          sourcePath: getTextContent('sourcePath') || '',
          author: getTextContent('author') || 'Flynn Chen',
          syncAfterGenerate: getBooleanContent('syncAfterGenerate'),
          generateClient: getBooleanContent('generateClient'),
          generateBusiness: getBooleanContent('generateBusiness'),
          generateMetadata: getBooleanContent('generateMetadata')
        }
      };
    } catch (error) {
      console.error('Failed to parse XML config:', error);
      return null;
    }
  }

  private escapeXml(str: string): string {
    return str
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&apos;');
  }

  private getConfigDirectory(filePath: string): string {
    const parts = filePath.split(/[/\\]/);
    parts.pop();
    return parts.join('/') || '.';
  }
}

export default new ConfigFileService();
