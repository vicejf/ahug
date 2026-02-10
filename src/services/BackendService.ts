// Backend service for communicating with Node.js + EJS backend

interface GenerationResult {
  success: boolean;
  duration: number;
  stats?: CodeStatistics;
  errorMessage?: string;
  outputDir?: string;
}

interface CodeStatistics {
  fileCount: number;
  codeLines: number;
}

interface ValidationResult {
  valid: boolean;
  errors: string[];
}

class BackendService {
  private isElectron: boolean;

  constructor() {
    this.isElectron = typeof window !== 'undefined' && window.electronAPI !== undefined;
  }

  // Generate code using Node.js + EJS backend
  async generateCode(configPath: string, outputDir?: string): Promise<GenerationResult> {
    if (this.isElectron && window.electronAPI?.generateCode) {
      try {
        const result = await window.electronAPI.generateCode(configPath, outputDir);
        return result;
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : '未知错误';
        return {
          success: false,
          duration: 0,
          errorMessage
        };
      }
    } else {
      // Fallback for web version - simulate code generation
      console.log('Simulating code generation for:', configPath);
      return {
        success: true,
        duration: 1000,
        stats: {
          fileCount: 10,
          codeLines: 500
        },
        outputDir: outputDir || './output'
      };
    }
  }

  // Validate configuration
  async validateConfig(configPath: string): Promise<ValidationResult> {
    if (this.isElectron && window.electronAPI?.validateConfig) {
      try {
        const result = await window.electronAPI.validateConfig(configPath);
        return result;
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : '未知错误';
        return {
          valid: false,
          errors: [errorMessage]
        };
      }
    } else {
      // Fallback for web version
      return {
        valid: true,
        errors: []
      };
    }
  }

  // Check if backend is available
  async checkBackendStatus(): Promise<{ available: boolean; error?: string }> {
    if (this.isElectron && window.electronAPI) {
      return { available: true };
    } else {
      return {
        available: false,
        error: 'Electron API not available'
      };
    }
  }

  // Format duration for display
  formatDuration(duration: number): string {
    if (duration < 1000) {
      return `${duration}ms`;
    } else if (duration < 60000) {
      return `${(duration / 1000).toFixed(2)}s`;
    } else {
      const minutes = Math.floor(duration / 60000);
      const seconds = ((duration % 60000) / 1000).toFixed(0);
      return `${minutes}m ${seconds}s`;
    }
  }

  // Format code statistics for display
  formatStats(stats?: CodeStatistics): string {
    if (!stats) {
      return '无统计数据';
    }
    return `文件: ${stats.fileCount} 个, 代码: ${stats.codeLines.toLocaleString()} 行`;
  }
}

export default new BackendService();
