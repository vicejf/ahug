interface RecentFile {
  name: string;
  path: string;
  lastModified: string;
}

interface Statistics {
  configFiles: number;
  generationCount: number;
  lastGenerated: string;
}

class StoreService {
  private isElectron: boolean;

  constructor() {
    this.isElectron = typeof window !== 'undefined' && window.electronAPI !== undefined;
    
    // 在 Electron 环境中初始化 store
    if (this.isElectron && typeof window !== 'undefined') {
      // 不再需要客户端初始化，直接使用 IPC
    }
  }

  // 移除了 initializeStore 方法，现在直接使用 IPC 调用

  // 获取最近文件列表
  async getRecentFiles(): Promise<RecentFile[]> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeGetRecentFiles) {
      try {
        return await window.electronAPI.storeGetRecentFiles();
      } catch (error) {
        console.error('Failed to get recent files from electron store:', error);
      }
    }
    
    // 回退到 localStorage（开发环境）
    try {
      const stored = localStorage.getItem('ahug_recent_files');
      return stored ? JSON.parse(stored) : [];
    } catch {
      return [];
    }
  }

  // 添加到最近文件
  async addToRecentFiles(file: RecentFile): Promise<void> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeAddToRecentFiles) {
      try {
        await window.electronAPI.storeAddToRecentFiles(file);
        return;
      } catch (error) {
        console.error('Failed to add to recent files in electron store:', error);
      }
    }
    
    // 回退到 localStorage
    try {
      let recentFiles: RecentFile[] = [];
      const stored = localStorage.getItem('ahug_recent_files');
      if (stored) {
        recentFiles = JSON.parse(stored);
      }

      // 移除已存在的文件
      const existingIndex = recentFiles.findIndex((f: RecentFile) => f.path === file.path);
      if (existingIndex >= 0) {
        recentFiles.splice(existingIndex, 1);
      }

      // 添加到开头
      recentFiles.unshift(file);

      // 限制数量
      if (recentFiles.length > 10) {
        recentFiles = recentFiles.slice(0, 10);
      }

      localStorage.setItem('ahug_recent_files', JSON.stringify(recentFiles));
    } catch (error) {
      console.error('Failed to save recent files to localStorage:', error);
    }
  }

  // 清空最近文件
  async clearRecentFiles(): Promise<void> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeClearRecentFiles) {
      try {
        await window.electronAPI.storeClearRecentFiles();
        return;
      } catch (error) {
        console.error('Failed to clear recent files in electron store:', error);
      }
    }
    
    // 回退到 localStorage
    try {
      localStorage.removeItem('ahug_recent_files');
    } catch (error) {
      console.error('Failed to clear recent files from localStorage:', error);
    }
  }

  // 获取统计信息
  async getStatistics(): Promise<Statistics> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeGetStatistics) {
      try {
        return await window.electronAPI.storeGetStatistics();
      } catch (error) {
        console.error('Failed to get statistics from electron store:', error);
      }
    }
    
    // 回退到 localStorage
    try {
      const stored = localStorage.getItem('ahug_statistics');
      return stored ? JSON.parse(stored) : {
        configFiles: 0,
        generationCount: 0,
        lastGenerated: '从未生成'
      };
    } catch {
      return {
        configFiles: 0,
        generationCount: 0,
        lastGenerated: '从未生成'
      };
    }
  }

  // 保存统计信息
  async saveStatistics(stats: Statistics): Promise<void> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeSaveStatistics) {
      try {
        await window.electronAPI.storeSaveStatistics(stats);
        return;
      } catch (error) {
        console.error('Failed to save statistics in electron store:', error);
      }
    }
    
    // 回退到 localStorage
    try {
      localStorage.setItem('ahug_statistics', JSON.stringify(stats));
    } catch (error) {
      console.error('Failed to save statistics to localStorage:', error);
    }
  }

  // 获取设置
  async getSettings(): Promise<Record<string, unknown>> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeGet) {
      try {
        return await window.electronAPI.storeGet('settings', {});
      } catch (error) {
        console.error('Failed to get settings from electron store:', error);
      }
    }
    return {};
  }

  // 保存设置
  async saveSettings(settings: Record<string, unknown>): Promise<void> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeSet) {
      try {
        await window.electronAPI.storeSet('settings', settings);
        return;
      } catch (error) {
        console.error('Failed to save settings in electron store:', error);
      }
    }
  }

  // 获取特定键的值
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  async get(key: string, defaultValue: any = null): Promise<any> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeGet) {
      try {
        return await window.electronAPI.storeGet(key, defaultValue);
      } catch (error) {
        console.error(`Failed to get ${key} from electron store:`, error);
      }
    }
    
    try {
      const stored = localStorage.getItem(`ahug_${key}`);
      return stored ? JSON.parse(stored) : defaultValue;
    } catch {
      return defaultValue;
    }
  }

  // 设置特定键的值
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  async set(key: string, value: any): Promise<void> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeSet) {
      try {
        await window.electronAPI.storeSet(key, value);
        return;
      } catch (error) {
        console.error(`Failed to set ${key} in electron store:`, error);
      }
    }
    
    try {
      localStorage.setItem(`ahug_${key}`, JSON.stringify(value));
    } catch (error) {
      console.error(`Failed to set ${key} in localStorage:`, error);
    }
  }

  // 删除特定键
  async delete(key: string): Promise<void> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeDelete) {
      try {
        await window.electronAPI.storeDelete(key);
        return;
      } catch (error) {
        console.error(`Failed to delete ${key} in electron store:`, error);
      }
    }
    
    try {
      localStorage.removeItem(`ahug_${key}`);
    } catch (error) {
      console.error(`Failed to delete ${key} from localStorage:`, error);
    }
  }

  // 检查键是否存在
  async has(key: string): Promise<boolean> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeHas) {
      try {
        return await window.electronAPI.storeHas(key);
      } catch (error) {
        console.error(`Failed to check ${key} in electron store:`, error);
      }
    }
    
    try {
      return localStorage.getItem(`ahug_${key}`) !== null;
    } catch {
      return false;
    }
  }

  // 清空所有数据
  async clear(): Promise<void> {
    if (this.isElectron && typeof window !== 'undefined' && window.electronAPI?.storeClear) {
      try {
        await window.electronAPI.storeClear();
        return;
      } catch (error) {
        console.error('Failed to clear electron store:', error);
      }
    }
    
    try {
      // 清除所有以 ahug_ 开头的 localStorage 项
      Object.keys(localStorage).forEach(key => {
        if (key.startsWith('ahug_')) {
          localStorage.removeItem(key);
        }
      });
    } catch (error) {
      console.error('Failed to clear localStorage:', error);
    }
  }

  // 获取存储路径（仅 Electron 环境）
  getStorePath(): string | null {
    // Electron store 路径只能在主进程中获取
    return null;
  }
}

export default new StoreService();