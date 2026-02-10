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
    };
  }
}

export {};
