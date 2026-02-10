export interface FieldConfig {
  name: string;
  label: string;
  type: string;
  dbType: string;
  length: number;
  required: boolean;
  editable: boolean;
  primaryKey?: boolean;
  uiType?: string;
}

export interface EnumConfig {
  name: string;
  displayName: string;
  className: string;
  items: EnumItem[];
}

export interface EnumItem {
  display: string;
  value: string;
}

export interface BillConfigData {
  basicInfo: {
    billCode: string;
    billName: string;
    module: string;
    packageName: string;
    bodyCode: string;
    headCode: string;
    billType: string;
    author: string;
    description: string;
  };
  headFields: FieldConfig[];
  bodyFields: FieldConfig[];
  bodyCodeList: string[];
  enumConfigs: EnumConfig[];
  globalConfig: {
    outputDir: string;
    sourcePath: string;
    author: string;
    syncAfterGenerate: boolean;
    generateClient: boolean;
    generateBusiness: boolean;
    generateMetadata: boolean;
  };
}
