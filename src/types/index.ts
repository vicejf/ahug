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

export interface SqlField {
  columnName: string;
  dataType: string;
  dataLength?: number;
  nullable: boolean;
  defaultValue?: string;
  comment?: string;
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

// Template interfaces for import/export functionality
export interface FieldTemplate {
  id: string;
  name: string;
  type: 'basic' | 'custom';
  category: 'head' | 'body';
  fields: FieldConfig[];
  createdAt: string;
  updatedAt: string;
  description?: string;
}

export interface ImportData {
  fields: FieldConfig[];
  metadata?: Record<string, unknown>;
}

export interface TemplateImportOptions {
  source: 'file' | 'paste' | 'basic';
  fileType?: 'json' | 'sql';
  fileName?: string;
  content?: string;
  category?: 'head' | 'body';
}
