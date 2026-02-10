import type { BillConfigData } from '../types';
import StoreService from './StoreService';

interface RecentFile {
  name: string;
  path: string;
  lastModified: string;
}

class ConfigManager {
  private currentConfigPath: string | null = null;
  private recentFiles: RecentFile[] = [];
  private isModified: boolean = false;
  private readonly MAX_RECENT_FILES = 10;

  constructor() {
    this.loadRecentFiles();
    // statistics 现在由 StoreService 管理，不需要在这里加载
  }

  getCurrentConfigPath(): string | null {
    return this.currentConfigPath;
  }

  setCurrentConfigPath(path: string | null) {
    this.currentConfigPath = path;
  }

  isConfigModified(): boolean {
    return this.isModified;
  }

  setModified(modified: boolean) {
    this.isModified = modified;
  }

  getRecentFiles(): RecentFile[] {
    return this.recentFiles;
  }

  addToRecentFiles(filePath: string) {
    const existingIndex = this.recentFiles.findIndex(f => f.path === filePath);
    if (existingIndex >= 0) {
      this.recentFiles.splice(existingIndex, 1);
    }

    const file: RecentFile = {
      name: this.getFileName(filePath),
      path: filePath,
      lastModified: new Date().toISOString()
    };

    this.recentFiles.unshift(file);

    if (this.recentFiles.length > this.MAX_RECENT_FILES) {
      this.recentFiles = this.recentFiles.slice(0, this.MAX_RECENT_FILES);
    }

    this.saveRecentFiles();
  }

  getMostRecentFile(): string | null {
    return this.recentFiles.length > 0 ? this.recentFiles[0].path : null;
  }

  private getFileName(filePath: string): string {
    const parts = filePath.split(/[/\\]/);
    return parts[parts.length - 1];
  }

  private async loadRecentFiles() {
    try {
      this.recentFiles = await StoreService.getRecentFiles();
    } catch (error) {
      console.error('Failed to load recent files:', error);
      this.recentFiles = [];
    }
  }

  private async saveRecentFiles() {
    try {
      // 逐个添加而不是替换整个数组，这样可以保持正确的顺序和限制
      for (const file of this.recentFiles) {
        await StoreService.addToRecentFiles(file);
      }
      // 更新本地缓存
      this.recentFiles = await StoreService.getRecentFiles();
    } catch (error) {
      console.error('Failed to save recent files:', error);
    }
  }

  async clearRecentFiles() {
    await StoreService.clearRecentFiles();
    this.recentFiles = [];
  }

  async loadStatistics() {
    return await StoreService.getStatistics();
  }

  async saveStatistics(stats: { configFiles: number; generationCount: number; lastGenerated: string }) {
    await StoreService.saveStatistics(stats);
  }

  async incrementGenerationCount() {
    const stats = await this.loadStatistics();
    stats.generationCount++;
    stats.lastGenerated = new Date().toLocaleString();
    await this.saveStatistics(stats);
  }

  async incrementConfigCount() {
    const stats = await this.loadStatistics();
    stats.configFiles++;
    await this.saveStatistics(stats);
  }

  generateDefaultFileName(billCode: string): string {
    const code = billCode || 'config';
    return `${code.toLowerCase()}-config.xml`;
  }

  validateConfig(config: BillConfigData): { isValid: boolean; errors: string[] } {
    const errors: string[] = [];

    if (!config.basicInfo.billCode) {
      errors.push('单据编码不能为空');
    }

    if (!config.basicInfo.billName) {
      errors.push('单据名称不能为空');
    }

    if (!config.basicInfo.packageName) {
      errors.push('包名不能为空');
    }

    if (config.basicInfo.billType === 'multi' && !config.basicInfo.bodyCode) {
      errors.push('多表体类型需要设置表体编码');
    }

    if (config.headFields.length === 0) {
      errors.push('表头字段不能为空');
    }

    return {
      isValid: errors.length === 0,
      errors
    };
  }

  clearConfig(): BillConfigData {
    return {
      basicInfo: {
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
      bodyCodeList: [],
      enumConfigs: [],
      globalConfig: {
        outputDir: './output',
        sourcePath: '',
        author: 'Flynn Chen',
        syncAfterGenerate: false,
        generateClient: true,
        generateBusiness: true,
        generateMetadata: false
      }
    };
  }
}

export default new ConfigManager();
