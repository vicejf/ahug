// Configuration service for handling bill configurations

interface BasicInfo {
  billCode: string;
  billName: string;
  module: string;
  packageName: string;
  bodyCode: string;
  billType: string;
  author: string;
  description: string;
}

interface FieldPaths {
  headFieldsPath: string;
  bodyFieldsPath: string;
}

interface GenerateOptions {
  generateClient: boolean;
  generateBusiness: boolean;
  generateMetadata: boolean;
  syncAfterGenerate: boolean;
}

interface MetadataSwitches {
  enablePubBillInterface: boolean;
  enableUser: boolean;
  enableBillStatus: boolean;
}

interface GlobalConfig {
  generateOptions: GenerateOptions;
  metadataSwitches: MetadataSwitches;
}

interface BillConfig {
  basicInfo: BasicInfo;
  fieldPaths: FieldPaths;
  globalConfig: GlobalConfig;
}

interface Validation {
  isValid: boolean;
  errors: string[];
}

interface ParseResult {
  success: boolean;
  config?: BillConfig;
  error?: string;
}

class ConfigService {
  private defaultConfig: BillConfig;

  constructor() {
    this.defaultConfig = {
      basicInfo: {
        billCode: '',
        billName: '',
        module: '',
        packageName: '',
        bodyCode: '',
        billType: 'single',
        author: '',
        description: ''
      },
      fieldPaths: {
        headFieldsPath: '',
        bodyFieldsPath: ''
      },
      globalConfig: {
        generateOptions: {
          generateClient: true,
          generateBusiness: true,
          generateMetadata: false,
          syncAfterGenerate: false
        },
        metadataSwitches: {
          enablePubBillInterface: true,
          enableUser: true,
          enableBillStatus: true
        }
      }
    };
  }

  // Parse XML configuration
  parseXmlConfig(xmlContent: string): ParseResult {
    // In a real implementation, this would use a proper XML parser
    // For now, we'll return a basic structure
    try {
      // Simple XML parsing simulation
      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(xmlContent, "text/xml");

      // Extract basic info (simplified)
      const billCode = xmlDoc.querySelector('billCode')?.textContent || '';
      const billName = xmlDoc.querySelector('billName')?.textContent || '';

      return {
        success: true,
        config: {
          ...this.defaultConfig,
          basicInfo: {
            ...this.defaultConfig.basicInfo,
            billCode,
            billName
          }
        }
      };
    } catch (error) {
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';
      return {
        success: false,
        error: 'Failed to parse XML configuration: ' + errorMessage
      };
    }
  }

  // Convert configuration to XML
  toXmlConfig(config: BillConfig): string {
    return `<?xml version="1.0" encoding="UTF-8"?>
<billConfig>
    <basicInfo>
        <billCode>${config.basicInfo.billCode}</billCode>
        <billName>${config.basicInfo.billName}</billName>
        <module>${config.basicInfo.module}</module>
        <packageName>${config.basicInfo.packageName}</packageName>
        <bodyCode>${config.basicInfo.bodyCode}</bodyCode>
        <billType>${config.basicInfo.billType}</billType>
        <author>${config.basicInfo.author}</author>
        <description>${config.basicInfo.description}</description>
    </basicInfo>

    <fieldPaths>
        <headFieldsPath>${config.fieldPaths.headFieldsPath}</headFieldsPath>
        <bodyFieldsPath>${config.fieldPaths.bodyFieldsPath}</bodyFieldsPath>
    </fieldPaths>

    <globalConfig>
        <generateOptions>
            <generateClient>${config.globalConfig.generateOptions.generateClient}</generateClient>
            <generateBusiness>${config.globalConfig.generateOptions.generateBusiness}</generateBusiness>
            <generateMetadata>${config.globalConfig.generateOptions.generateMetadata}</generateMetadata>
            <syncAfterGenerate>${config.globalConfig.generateOptions.syncAfterGenerate}</syncAfterGenerate>
        </generateOptions>

        <metadataSwitches>
            <enablePubBillInterface>${config.globalConfig.metadataSwitches.enablePubBillInterface}</enablePubBillInterface>
            <enableUser>${config.globalConfig.metadataSwitches.enableUser}</enableUser>
            <enableBillStatus>${config.globalConfig.metadataSwitches.enableBillStatus}</enableBillStatus>
        </metadataSwitches>
    </globalConfig>
</billConfig>`;
  }

  // Validate configuration
  validateConfig(config: BillConfig): Validation {
    const errors: string[] = [];

    if (!config.basicInfo.billCode) {
      errors.push('Bill code is required');
    }

    if (!config.basicInfo.packageName) {
      errors.push('Package name is required');
    }

    if (!config.fieldPaths.headFieldsPath) {
      errors.push('Head fields path is required');
    }

    return {
      isValid: errors.length === 0,
      errors
    };
  }

  // Get default configuration
  getDefaultConfig(): BillConfig {
    return JSON.parse(JSON.stringify(this.defaultConfig));
  }

  // Merge configurations
  mergeConfigs(baseConfig: BillConfig, overrideConfig: Partial<BillConfig>): BillConfig {
    return {
      ...baseConfig,
      ...overrideConfig,
      basicInfo: {
        ...baseConfig.basicInfo,
        ...(overrideConfig.basicInfo || {})
      },
      fieldPaths: {
        ...baseConfig.fieldPaths,
        ...(overrideConfig.fieldPaths || {})
      },
      globalConfig: {
        ...baseConfig.globalConfig,
        ...(overrideConfig.globalConfig || {}),
        generateOptions: {
          ...baseConfig.globalConfig.generateOptions,
          ...(overrideConfig.globalConfig?.generateOptions || {})
        },
        metadataSwitches: {
          ...baseConfig.globalConfig.metadataSwitches,
          ...(overrideConfig.globalConfig?.metadataSwitches || {})
        }
      }
    };
  }
}

export default new ConfigService();
