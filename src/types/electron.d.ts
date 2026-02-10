// Global type declarations for Electron API

declare global {
  interface Window {
    electronAPI?: {
      // File operations
      openFile: () => Promise<{
        filePath: string;
        content: string;
      } | null>;
      saveFile: (defaultPath: string) => Promise<{
        canceled: boolean;
        filePath: string;
      }>;
      writeFile: (filePath: string, content: string) => Promise<{
        success: boolean;
        error?: string;
      }>;
      readFile: (filePath: string) => Promise<{
        success: boolean;
        content: string;
        error?: string;
      }>;
      fileExists: (filePath: string) => Promise<boolean>;
      listDirectory: (dirPath: string) => Promise<{
        success: boolean;
        files: Array<{
          name: string;
          isDirectory: boolean;
          size: number;
        }>;
        error?: string;
      }>;
      // Directory selection
      selectDirectory: () => Promise<string | null>;
      selectOutputDirectory: () => Promise<string | null>;
      // Code generation
      generateCode: (configPath: string, outputDir?: string) => Promise<{
        success: boolean;
        duration: number;
        stats?: {
          fileCount: number;
          codeLines: number;
        };
        errorMessage?: string;
        outputDir?: string;
      }>;
      validateConfig: (configPath: string) => Promise<{
        valid: boolean;
        errors: string[];
      }>;
      // Store operations
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      storeGet: (key: string, defaultValue?: any) => Promise<any>;
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      storeSet: (key: string, value: any) => Promise<void>;
      storeDelete: (key: string) => Promise<void>;
      storeHas: (key: string) => Promise<boolean>;
      storeClear: () => Promise<void>;
      storeGetRecentFiles: () => Promise<Array<{ name: string; path: string; lastModified: string }>>;
      storeAddToRecentFiles: (file: { name: string; path: string; lastModified: string }) => Promise<void>;
      storeClearRecentFiles: () => Promise<void>;
      storeGetStatistics: () => Promise<{ configFiles: number; generationCount: number; lastGenerated: string }>;
      storeSaveStatistics: (stats: { configFiles: number; generationCount: number; lastGenerated: string }) => Promise<void>;
      // Config file operations
      selectConfigDirectory: () => Promise<{ success: boolean; path?: string; error?: string }>;
      scanConfigFiles: () => Promise<{ success: boolean; files?: string[]; error?: string }>;
      getFileStats: (filePath: string) => Promise<{ success: boolean; stats?: { mtime: Date; size: number }; error?: string }>;
    };
  }
}

export {};
