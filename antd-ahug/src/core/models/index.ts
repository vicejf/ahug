export interface FieldConfig {
  name: string;
  label: string;
  type: string;
  dbType: string;
  length?: number;
  required: boolean;
  primaryKey: boolean;
  defaultValue?: string;
  uiType?: string;
  editable?: boolean;
  visible?: boolean;
  children?: FieldConfig[];
  id?: string;
  dataType?: string;
  typeDisplayName?: string;
  refModelName?: string;
  precision?: number;
  scale?: number;
  refTable?: string;
  enumCode?: string;
  description?: string;
}

export interface EnumItem {
  id?: string;
  value: string;
  display: string;
}

export interface EnumConfig {
  id?: string;
  name: string;
  displayName: string;
  className: string;
  items: EnumItem[];
}

export interface GlobalConfig {
  sourcePath?: string;
  outputDir?: string;
  syncAfterGenerate?: boolean;
  generateClient?: boolean;
  generateBusiness?: boolean;
  generateMetadata?: boolean;
  author?: string;
  componentId?: string;
  mainEntityId?: string;
  enums?: EnumConfig[];
  enablePubBillInterface?: boolean;
  enableUser?: boolean;
  enableBillStatus?: boolean;
  pkFieldId?: string;
  billNoFieldId?: string;
  corpFieldId?: string;
  busiTypeFieldId?: string;
  operatorIdFieldId?: string;
  approverFieldId?: string;
  billStatusFieldId?: string;
  approveNoteFieldId?: string;
  approveDateFieldId?: string;
  billDateFieldId?: string;
  billTypeFieldId?: string;
  refPubBillId?: string;
  refUserId?: string;
  refBillStatusId?: string;
  connectionPubBillId?: string;
  connectionUserId?: string;
  connectionBillStatusId?: string;
}

export interface BillConfig {
  headCode?: string;
  billCode: string;
  billName: string;
  module: string;
  packageName: string;
  bodyCode?: string;
  headFieldsPath?: string;
  bodyFieldsPath?: string;
  headFields: FieldConfig[];
  bodyFields: FieldConfig[];
  bodyCodeList: string[];
  billType: 'single' | 'multi' | 'archive';
  description?: string;
  globalConfig?: GlobalConfig;
}

export interface GenerationOptions {
  outputDir: string;
  sourcePath?: string;
  syncAfterGenerate?: boolean;
}

export interface GenerationResult {
  success: boolean;
  duration: number;
  stats?: CodeStatistics;
  errorMessage?: string;
  outputDir?: string;
}

export interface CodeStatistics {
  fileCount: number;
  codeLines: number;
}
