// File service for handling file operations through Electron

interface FileResult {
  success: boolean;
  filePath?: string;
  content?: string;
  error?: string;
}

interface DirectoryFile {
  name: string;
  isDirectory: boolean;
  size: number;
}

interface DirectoryResult {
  success: boolean;
  files?: DirectoryFile[];
  error?: string;
}

class FileService {
  private isElectron: boolean;

  constructor() {
    // Check if we're in Electron environment
    this.isElectron = typeof window !== 'undefined' && window.electronAPI !== undefined;
  }

  async openFile(): Promise<FileResult> {
    if (this.isElectron) {
      try {
        const result = await window.electronAPI!.openFile();
        if (result) {
          return {
            success: true,
            filePath: result.filePath,
            content: result.content
          };
        }
        return { success: false, error: 'No file selected' };
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Unknown error';
        return { success: false, error: errorMessage };
      }
    } else {
      // Fallback for web version - simulate file opening
      return {
        success: true,
        filePath: 'sample/config.xml',
        content: '<!-- Sample configuration content -->'
      };
    }
  }

  async saveFile(content: string, defaultPath: string = ''): Promise<FileResult> {
    if (this.isElectron) {
      try {
        const result = await window.electronAPI!.saveFile(defaultPath);
        if (!result.canceled) {
          const writeResult = await window.electronAPI!.writeFile(result.filePath, content);
          return {
            success: writeResult.success,
            filePath: result.filePath,
            error: writeResult.error
          };
        }
        return { success: false, error: 'Save cancelled' };
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Unknown error';
        return { success: false, error: errorMessage };
      }
    } else {
      // Fallback for web version - download as file
      const blob = new Blob([content], { type: 'application/xml' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = defaultPath || 'config.xml';
      a.click();
      URL.revokeObjectURL(url);
      return { success: true, filePath: defaultPath || 'config.xml' };
    }
  }

  async readFile(filePath: string): Promise<FileResult> {
    if (this.isElectron) {
      try {
        return await window.electronAPI!.readFile(filePath);
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Unknown error';
        return { success: false, error: errorMessage };
      }
    } else {
      // Fallback for web version
      return {
        success: true,
        content: '<!-- Sample file content -->'
      };
    }
  }

  async writeFile(filePath: string, content: string): Promise<FileResult> {
    if (this.isElectron) {
      try {
        return await window.electronAPI!.writeFile(filePath, content);
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Unknown error';
        return { success: false, error: errorMessage };
      }
    } else {
      // Fallback for web version
      return { success: true };
    }
  }

  async fileExists(filePath: string): Promise<boolean> {
    if (this.isElectron) {
      try {
        return await window.electronAPI!.fileExists(filePath);
      } catch (error) {
        return false;
      }
    } else {
      // Fallback for web version
      return true;
    }
  }

  async listDirectory(dirPath: string): Promise<DirectoryResult> {
    if (this.isElectron) {
      try {
        return await window.electronAPI!.listDirectory(dirPath);
      } catch (error) {
        const errorMessage = error instanceof Error ? error.message : 'Unknown error';
        return { success: false, error: errorMessage };
      }
    } else {
      // Fallback for web version
      return {
        success: true,
        files: [
          { name: 'sample1.xml', isDirectory: false, size: 1024 },
          { name: 'sample2.xml', isDirectory: false, size: 2048 },
          { name: 'configs', isDirectory: true, size: 0 }
        ]
      };
    }
  }
}

export default new FileService();
