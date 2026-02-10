import fs from 'fs';
import path from 'path';
import type { BillConfig, EnumConfig } from '../models';
import TemplateEngine from '../utils/templateEngine';
import { generateUUID } from '../utils/uuid';

export class MetadataGenerator {
  private templateEngine: TemplateEngine;
  private outputDir: string;

  constructor(outputDir: string) {
    this.outputDir = outputDir;
    this.templateEngine = new TemplateEngine();
  }

  async generate(billConfig: BillConfig): Promise<void> {
    console.log('=== 元数据生成开始 ===');
    console.log(`单据编码: ${billConfig.billCode}`);
    console.log(`单据名称: ${billConfig.billName}`);
    
    if (!billConfig.globalConfig || !billConfig.globalConfig.generateMetadata) {
      console.log('跳过元数据生成：未启用元数据生成');
      return;
    }

    console.log('开始生成元数据文件...');

    await this.initMetadataConfig(billConfig);

    const context = { 
      bill: billConfig, 
      now: new Date().toISOString() 
    };
    const templatePath = 'metadata/common/SingleHeadBMF.ejs';
    const outputFile = path.join(this.outputDir, `metadata/${billConfig.billCode.toLowerCase()}.bmf`);
    
    const dir = path.dirname(outputFile);
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }

    await this.templateEngine.renderAndWrite(templatePath, outputFile, context);
    console.log(`元数据文件生成成功: ${outputFile}`);
  }

  private async initMetadataConfig(billConfig: BillConfig): Promise<void> {
    const globalConfig = billConfig.globalConfig;
    if (!globalConfig) {
      return;
    }

    if (!globalConfig.componentId) {
      globalConfig.componentId = generateUUID();
    }

    if (!globalConfig.mainEntityId) {
      globalConfig.mainEntityId = generateUUID();
    }

    for (const enumConfig of globalConfig.enums || []) {
      if (!enumConfig.id) {
        enumConfig.id = generateUUID();
      }

      for (const item of enumConfig.items) {
        if (!item.id) {
          item.id = generateUUID();
        }
      }
    }

    for (const field of billConfig.headFields) {
      if (!field.id) {
        field.id = generateUUID();
      }

      if (!field.dataType) {
        field.dataType = this.getDataTypeId(field.type, field.refModelName, globalConfig.enums || []);
      }

      if (!field.typeDisplayName) {
        field.typeDisplayName = this.getTypeDisplayName(field.type);
      }

      if (field.uiType === 'Combo' && !field.refModelName) {
        field.refModelName = this.inferRefModelName(field.label);
      }
    }

    if (globalConfig.enablePubBillInterface) {
      if (!globalConfig.refPubBillId) {
        globalConfig.refPubBillId = generateUUID();
      }
      if (!globalConfig.connectionPubBillId) {
        globalConfig.connectionPubBillId = generateUUID();
      }
    }

    if (globalConfig.enableUser) {
      if (!globalConfig.refUserId) {
        globalConfig.refUserId = generateUUID();
      }
      if (!globalConfig.connectionUserId) {
        globalConfig.connectionUserId = generateUUID();
      }
    }

    if (globalConfig.enableBillStatus) {
      if (!globalConfig.refBillStatusId) {
        globalConfig.refBillStatusId = generateUUID();
      }
      if (!globalConfig.connectionBillStatusId) {
        globalConfig.connectionBillStatusId = generateUUID();
      }
    }

    for (const field of billConfig.headFields) {
      if (field.primaryKey && !globalConfig.pkFieldId) {
        globalConfig.pkFieldId = field.id;
      }

      if (field.name === 'billno' && !globalConfig.billNoFieldId) {
        globalConfig.billNoFieldId = field.id;
      }

      if (field.name === 'corp' && !globalConfig.corpFieldId) {
        globalConfig.corpFieldId = field.id;
      }

      if (field.name === 'vbusitype' && !globalConfig.busiTypeFieldId) {
        globalConfig.busiTypeFieldId = field.id;
      }

      if (field.name === 'operatorid' && !globalConfig.operatorIdFieldId) {
        globalConfig.operatorIdFieldId = field.id;
      }

      if (field.name === 'reviewer' && !globalConfig.approverFieldId) {
        globalConfig.approverFieldId = field.id;
      }

      if (field.name === 'vstatus' && !globalConfig.billStatusFieldId) {
        globalConfig.billStatusFieldId = field.id;
      }

      if (field.name === 'vbillstatus' && !globalConfig.billStatusFieldId) {
        globalConfig.billStatusFieldId = field.id;
      }

      if (field.name === 'reviewnote' && !globalConfig.approveNoteFieldId) {
        globalConfig.approveNoteFieldId = field.id;
      }

      if (field.name === 'reviewdate' && !globalConfig.approveDateFieldId) {
        globalConfig.approveDateFieldId = field.id;
      }

      if (field.name === 'billdate' && !globalConfig.billDateFieldId) {
        globalConfig.billDateFieldId = field.id;
      }

      if (field.name === 'createdate' && !globalConfig.billDateFieldId) {
        globalConfig.billDateFieldId = field.id;
      }

      if (field.name === 'vbilltype' && !globalConfig.billTypeFieldId) {
        globalConfig.billTypeFieldId = field.id;
      }
    }
  }

  private getDataTypeId(javaType: string | undefined, _refModelName: string | undefined, enums: EnumConfig[]): string {
    if (!javaType) {
      return 'BS000010000100001001';
    }

    if (javaType.toLowerCase().startsWith('enum:')) {
      const enumName = javaType.substring(5);
      for (const enumConfig of enums) {
        if (enumName === enumConfig.name) {
          return enumConfig.id || 'BS000010000100001001';
        }
      }
    }

    switch (javaType.toLowerCase()) {
      case 'string':
        return 'BS000010000100001001';
      case 'integer':
      case 'int':
        return 'BS000010000100001002';
      case 'double':
      case 'decimal':
        return 'BS000010000100001003';
      case 'ufdate':
        return 'BS000010000100001033';
      case 'ufboolean':
        return 'BS000010000100001032';
      case 'ufdouble':
        return 'BS000010000100001004';
      case 'ufid':
        return 'BS000010000100001051';
      case 'enum':
        return 'BS000010000100001001';
      default:
        return 'BS000010000100001001';
    }
  }

  private getTypeDisplayName(javaType: string | undefined): string {
    if (!javaType) {
      return 'String';
    }

    switch (javaType.toLowerCase()) {
      case 'string':
        return 'String';
      case 'integer':
      case 'int':
        return 'Integer';
      case 'double':
        return 'Double';
      case 'ufdate':
        return 'UFDate';
      case 'ufboolean':
        return 'UFBoolean';
      case 'ufdouble':
        return 'UFDouble';
      case 'ufid':
        return 'UFID';
      case 'enum':
        return '状态';
      default:
        return 'String';
    }
  }

  private inferRefModelName(label: string | undefined): string | undefined {
    if (!label) {
      return undefined;
    }

    if (label.includes('操作员') || label.includes('制单人') || label.includes('审批人')) {
      return '操作员';
    } else if (label.includes('公司') || label.includes('组织')) {
      return '公司目录(集团)';
    } else if (label.includes('业务类型')) {
      return '业务类型';
    } else if (label.includes('单据类型')) {
      return '影响因素单据类型';
    } else if (label.includes('部门')) {
      return '部门';
    } else if (label.includes('物料') || label.includes('产品')) {
      return '物料基本(集团)';
    }

    return undefined;
  }
}

export default MetadataGenerator;
