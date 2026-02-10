import type { FieldConfig, FieldTemplate, ImportData, TemplateImportOptions, SqlField } from '../types';
import { v4 as uuidv4 } from 'uuid';

class TemplateService {
  private static readonly TEMPLATES_STORAGE_KEY = 'field_templates';
  // private static readonly BASIC_HEAD_TEMPLATE_FILE = 'head-basic.json';
  // private static readonly BASIC_BODY_TEMPLATE_FILE = 'body-basic.json';
  // private static readonly CUSTOM_TEMPLATES_DIR = './templates';

  constructor() {
    this.initializeBasicTemplates();
  }

  /**
   * Initialize basic templates from files
   */
  private initializeBasicTemplates(): void {
    // In a real implementation, this would load from actual files
    // For now, we'll check if basic templates exist and create them if not
    const headTemplates = this.getAllTemplates('head');
    const bodyTemplates = this.getAllTemplates('body');
    
    const hasBasicHead = headTemplates.some(t => t.type === 'basic');
    const hasBasicBody = bodyTemplates.some(t => t.type === 'basic');
    
    if (!hasBasicHead) {
      // Create default basic head template
      this.createDefaultBasicTemplate('head');
    }
    
    if (!hasBasicBody) {
      // Create default basic body template
      this.createDefaultBasicTemplate('body');
    }
  }

  /**
   * Create default basic template
   */
  private createDefaultBasicTemplate(category: 'head' | 'body'): void {
    const defaultFields: FieldConfig[] = category === 'head' 
      ? [
          {
            name: 'pk_' + (category === 'head' ? 'aujx' : 'aujx_b'),
            label: category === 'head' ? '主键' : '表体主键',
            type: 'UFID',
            dbType: 'VARCHAR2(50)',
            length: 50,
            required: true,
            editable: false,
            primaryKey: true,
            uiType: 'Hidden'
          },
          {
            name: 'billno',
            label: '单据编号',
            type: 'String',
            dbType: 'VARCHAR2(50)',
            length: 50,
            required: true,
            editable: true,
            primaryKey: false,
            uiType: 'Text'
          },
          {
            name: 'billdate',
            label: '单据日期',
            type: 'UFDate',
            dbType: 'DATE',
            length: 0,
            required: true,
            editable: true,
            primaryKey: false,
            uiType: 'Date'
          }
        ]
      : [
          {
            name: 'pk_aujx_b',
            label: '表体主键',
            type: 'UFID',
            dbType: 'VARCHAR2(50)',
            length: 50,
            required: true,
            editable: false,
            primaryKey: true,
            uiType: 'Hidden'
          },
          {
            name: 'pk_aujx',
            label: '主表主键',
            type: 'UFID',
            dbType: 'VARCHAR2(50)',
            length: 50,
            required: true,
            editable: false,
            primaryKey: false,
            uiType: 'Hidden'
          },
          {
            name: 'rowno',
            label: '行号',
            type: 'Integer',
            dbType: 'INT',
            length: 0,
            required: true,
            editable: true,
            primaryKey: false,
            uiType: 'Number'
          }
        ];
    
    this.saveTemplate(
      `基础${category === 'head' ? '表头' : '表体'}字段模板`,
      category,
      defaultFields,
      'basic',
      `包含NC5标准单据的基础${category === 'head' ? '表头' : '表体'}字段`
    );
  }

  /**
   * Get all templates for a specific category (head/body)
   */
  getAllTemplates(category: 'head' | 'body'): FieldTemplate[] {
    const templates = this.loadTemplatesFromStorage();
    return templates.filter(template => template.category === category);
  }

  /**
   * Get basic template for a category
   */
  getBasicTemplate(category: 'head' | 'body'): FieldTemplate | null {
    const templates = this.getAllTemplates(category);
    return templates.find(template => template.type === 'basic') || null;
  }

  /**
   * Save a template (basic or custom)
   */
  saveTemplate(
    name: string,
    category: 'head' | 'body',
    fields: FieldConfig[],
    type: 'basic' | 'custom' = 'custom',
    description?: string
  ): FieldTemplate {
    const template: FieldTemplate = {
      id: uuidv4(),
      name,
      type,
      category,
      fields,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      description
    };

    const templates = this.loadTemplatesFromStorage();
    
    if (type === 'basic') {
      // Remove existing basic template for this category
      const filteredTemplates = templates.filter(t => !(t.category === category && t.type === 'basic'));
      filteredTemplates.push(template);
      this.saveTemplatesToStorage(filteredTemplates);
    } else {
      // For custom templates, just add to the list
      templates.push(template);
      this.saveTemplatesToStorage(templates);
    }

    return template;
  }

  /**
   * Update an existing template
   */
  updateTemplate(templateId: string, fields: FieldConfig[], description?: string): FieldTemplate | null {
    const templates = this.loadTemplatesFromStorage();
    const index = templates.findIndex(t => t.id === templateId);
    
    if (index === -1) return null;
    
    templates[index] = {
      ...templates[index],
      fields,
      updatedAt: new Date().toISOString(),
      description: description || templates[index].description
    };
    
    this.saveTemplatesToStorage(templates);
    return templates[index];
  }

  /**
   * Delete a template
   */
  deleteTemplate(templateId: string): boolean {
    const templates = this.loadTemplatesFromStorage();
    const initialLength = templates.length;
    const filteredTemplates = templates.filter(t => t.id !== templateId);
    
    if (filteredTemplates.length < initialLength) {
      this.saveTemplatesToStorage(filteredTemplates);
      return true;
    }
    return false;
  }

  /**
   * Import fields from various sources
   */
  async importFields(options: TemplateImportOptions): Promise<ImportData> {
    switch (options.source) {
      case 'file':
        return this.importFromFile(options.fileName!, options.fileType!);
      case 'paste':
        return this.importFromPaste(options.content!, options.fileType!);
      case 'basic':
        return this.importFromBasicTemplate(options.category!);
      default:
        throw new Error('Invalid import source');
    }
  }

  /**
   * Import from file
   */
  private async importFromFile(fileName: string, fileType: 'json' | 'sql'): Promise<ImportData> {
    try {
      // In a real implementation, this would use the FileService
      // For now, we'll simulate file reading
      if (fileType === 'json') {
        // Simulate JSON file reading
        const fileContent = await this.simulateFileRead(fileName);
        const data = JSON.parse(fileContent);
        return this.validateAndTransformImportData(data);
      } else if (fileType === 'sql') {
        // Simulate SQL file reading
        const fileContent = await this.simulateFileRead(fileName);
        return this.parseSqlCreateTable(fileContent);
      }
      throw new Error('Unsupported file type');
    } catch (error) {
      throw new Error(`Failed to import from file: ${error}`);
    }
  }

  /**
   * Import from pasted content
   */
  private importFromPaste(content: string, fileType: 'json' | 'sql'): Promise<ImportData> {
    try {
      if (fileType === 'json') {
        const data = JSON.parse(content);
        return Promise.resolve(this.validateAndTransformImportData(data));
      } else if (fileType === 'sql') {
        return Promise.resolve(this.parseSqlCreateTable(content));
      }
      throw new Error('Unsupported content type');
    } catch (error) {
      throw new Error(`Failed to import from paste: ${error}`);
    }
  }

  /**
   * Import from basic template
   */
  private importFromBasicTemplate(category: 'head' | 'body'): Promise<ImportData> {
    const basicTemplate = this.getBasicTemplate(category);
    if (!basicTemplate) {
      throw new Error(`No basic template found for ${category}`);
    }
    
    return Promise.resolve({
      fields: basicTemplate.fields,
      metadata: { templateName: basicTemplate.name }
    });
  }

  /**
   * Parse SQL CREATE TABLE statement
   */
  private parseSqlCreateTable(sqlContent: string): ImportData {
    const fields: FieldConfig[] = [];
    const lines = sqlContent.split('\n');
    
    for (const line of lines) {
      const trimmedLine = line.trim();
      if (trimmedLine.startsWith('`') || trimmedLine.includes('PRIMARY KEY')) {
        const sqlField = this.parseSqlField(trimmedLine);
        if (sqlField) {
          const fieldConfig = this.sqlFieldToFieldConfig(sqlField);
          fields.push(fieldConfig);
        }
      }
    }
    
    return { fields };
  }

  /**
   * Parse individual SQL field definition
   */
  private parseSqlField(line: string): SqlField | null {
    // Simple regex to extract column definition
    const fieldRegex = /`([^`]+)`\s+([^\s,]+)/;
    const match = line.match(fieldRegex);
    
    if (!match) return null;
    
    const [, columnName, dataTypeWithLength] = match;
    const [dataType, lengthStr] = dataTypeWithLength.split('(');
    const dataLength = lengthStr ? parseInt(lengthStr.replace(')', '')) : undefined;
    
    return {
      columnName,
      dataType: dataType.toLowerCase(),
      dataLength,
      nullable: !line.includes('NOT NULL'),
      defaultValue: line.includes('DEFAULT') ? this.extractDefaultValue(line) : undefined,
      comment: line.includes('COMMENT') ? this.extractComment(line) : undefined
    };
  }

  /**
   * Extract default value from SQL line
   */
  private extractDefaultValue(line: string): string | undefined {
    const defaultMatch = line.match(/DEFAULT\s+'([^']+)'/i);
    return defaultMatch ? defaultMatch[1] : undefined;
  }

  /**
   * Extract comment from SQL line
   */
  private extractComment(line: string): string | undefined {
    const commentMatch = line.match(/COMMENT\s+'([^']+)'/i);
    return commentMatch ? commentMatch[1] : undefined;
  }

  /**
   * Convert SQL field to FieldConfig
   */
  private sqlFieldToFieldConfig(sqlField: SqlField): FieldConfig {
    const typeMapping: Record<string, string> = {
      'varchar': 'String',
      'int': 'Integer',
      'bigint': 'Long',
      'decimal': 'BigDecimal',
      'datetime': 'UFDateTime',
      'date': 'UFDate',
      'tinyint': 'Boolean'
    };

    const dbTypeMapping: Record<string, string> = {
      'varchar': 'VARCHAR',
      'int': 'INT',
      'bigint': 'BIGINT',
      'decimal': 'DECIMAL',
      'datetime': 'DATETIME',
      'date': 'DATE',
      'tinyint': 'TINYINT'
    };

    return {
      name: sqlField.columnName,
      label: sqlField.comment || sqlField.columnName,
      type: typeMapping[sqlField.dataType] || 'String',
      dbType: dbTypeMapping[sqlField.dataType] || 'VARCHAR',
      length: sqlField.dataLength || 50,
      required: !sqlField.nullable,
      editable: true,
      primaryKey: sqlField.columnName.toLowerCase().includes('pk') || sqlField.columnName.toLowerCase().includes('id')
    };
  }

  /**
   * Validate and transform imported data
   */
  private validateAndTransformImportData(data: Record<string, unknown>): ImportData {
    if (!data.fields || !Array.isArray(data.fields)) {
      throw new Error('Invalid data format: fields array is required');
    }

    const fields: FieldConfig[] = data.fields.map((field: Record<string, unknown>) => ({
      name: (field.name as string) || '',
      label: (field.label as string) || (field.name as string) || '',
      type: (field.type as string) || 'String',
      dbType: (field.dbType as string) || 'VARCHAR',
      length: (field.length as number) || 50,
      required: (field.required as boolean) ?? false,
      editable: (field.editable as boolean) ?? true,
      primaryKey: (field.primaryKey as boolean) ?? false,
      uiType: field.uiType as string
    }));

    return {
      fields,
      metadata: data.metadata as Record<string, unknown> | undefined
    };
  }

  /**
   * Load templates from localStorage
   */
  private loadTemplatesFromStorage(): FieldTemplate[] {
    try {
      const stored = localStorage.getItem(TemplateService.TEMPLATES_STORAGE_KEY);
      return stored ? JSON.parse(stored) : [];
    } catch (error) {
      console.error('Failed to load templates from storage:', error);
      return [];
    }
  }

  /**
   * Save templates to localStorage
   */
  private saveTemplatesToStorage(templates: FieldTemplate[]): void {
    try {
      localStorage.setItem(TemplateService.TEMPLATES_STORAGE_KEY, JSON.stringify(templates));
    } catch (error) {
      console.error('Failed to save templates to storage:', error);
    }
  }

  /**
   * Simulate file reading (in real implementation, this would use FileService)
   */
  private async simulateFileRead(_fileName: string): Promise<string> {
    // This is a placeholder - in real implementation, integrate with FileService
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    return Promise.resolve(`{"fields": []}`);
  }
}

export default new TemplateService();