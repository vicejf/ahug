import { app, BrowserWindow, dialog, ipcMain } from 'electron';
import Store from 'electron-store';
import net from 'net';
import path from 'path';
import { fileURLToPath } from 'url';
import { promises as fsPromises } from 'fs';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

let mainWindow;
let store;

// 检测可用的 Vite 端口
async function findVitePort() {
  const ports = [5173, 5174, 5175, 5176, 5177, 5178, 5179, 5180];
  for (const port of ports) {
    try {
      await new Promise((resolve, reject) => {
        const socket = net.connect(port, 'localhost');
        socket.on('connect', () => {
          socket.end();
          resolve(port);
        });
        socket.on('error', reject);
      });
      return port;
    } catch {
      continue;
    }
  }
  return null;
}

async function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1400,
    height: 900,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false,
    },
  });

  // 开发环境加载 Vite 开发服务器
  const vitePort = await findVitePort();
  if (vitePort) {
    const url = `http://localhost:${vitePort}`;
    console.log('Loading Vite dev server at:', url);
    mainWindow.loadURL(url);
    mainWindow.webContents.openDevTools();
  } else {
    // 生产环境加载打包后的文件
    mainWindow.loadFile(path.join(__dirname, '../dist/index.html'));
  }
}

app.whenReady().then(async () => {
  // 初始化 electron-store
  store = new Store({
    name: 'ahug-config'
  });
  
  await createWindow();

  app.on('activate', async () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      await createWindow();
    }
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

// IPC 处理 - 选择目录
ipcMain.handle('select-directory', async () => {
  const result = await dialog.showOpenDialog(mainWindow, {
    properties: ['openDirectory'],
    title: '选择项目源目录',
  });
  return result.canceled ? null : result.filePaths[0];
});

// IPC 处理 - 选择输出目录
ipcMain.handle('select-output-directory', async () => {
  const result = await dialog.showOpenDialog(mainWindow, {
    properties: ['openDirectory'],
    title: '选择输出目录',
  });
  return result.canceled ? null : result.filePaths[0];
});

// IPC 处理 - 文件操作

// 打开文件
ipcMain.handle('file:open', async () => {
  const result = await dialog.showOpenDialog(mainWindow, {
    properties: ['openFile'],
    filters: [
      { name: 'XML Files', extensions: ['xml'] },
      { name: 'All Files', extensions: ['*'] }
    ],
    title: '选择配置文件',
  });
  
  if (result.canceled || result.filePaths.length === 0) {
    return null;
  }
  
  const filePath = result.filePaths[0];
  try {
    const content = await fsPromises.readFile(filePath, 'utf-8');
    return { filePath, content };
  } catch (error) {
    console.error('Failed to read file:', error);
    return null;
  }
});

// 保存文件
ipcMain.handle('file:save', async (event, defaultPath) => {
  const result = await dialog.showSaveDialog(mainWindow, {
    defaultPath: defaultPath || 'config.xml',
    filters: [
      { name: 'XML Files', extensions: ['xml'] },
      { name: 'All Files', extensions: ['*'] }
    ],
    title: '保存配置文件',
  });
  
  return result;
});

// 读取文件
ipcMain.handle('file:read', async (event, filePath) => {
  try {
    const content = await fsPromises.readFile(filePath, 'utf-8');
    return { success: true, content };
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    return { success: false, error: errorMessage };
  }
});

// 写入文件
ipcMain.handle('file:write', async (event, filePath, content) => {
  try {
    await fsPromises.writeFile(filePath, content, 'utf-8');
    return { success: true };
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    return { success: false, error: errorMessage };
  }
});

// 检查文件是否存在
ipcMain.handle('file:exists', async (event, filePath) => {
  try {
    await fsPromises.access(filePath);
    return true;
  } catch {
    return false;
  }
});

// 列出目录内容
ipcMain.handle('file:list-directory', async (event, dirPath) => {
  try {
    const files = await fsPromises.readdir(dirPath, { withFileTypes: true });
    const fileList = files.map(file => ({
      name: file.name,
      isDirectory: file.isDirectory(),
      size: file.isDirectory() ? 0 : -1 // 目录大小设为0，文件大小需要额外查询
    }));
    return { success: true, files: fileList };
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    return { success: false, error: errorMessage };
  }
});

// 获取文件统计信息
ipcMain.handle('file:get-stats', async (event, filePath) => {
  try {
    const stats = await fsPromises.stat(filePath);
    return { 
      success: true, 
      stats: { 
        mtime: stats.mtime, 
        size: stats.size 
      } 
    };
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    return { success: false, error: errorMessage };
  }
});

// 选择配置目录
ipcMain.handle('config:select-directory', async () => {
  const result = await dialog.showOpenDialog(mainWindow, {
    properties: ['openDirectory'],
    title: '选择配置文件目录',
  });
  
  if (result.canceled) {
    return { success: false, error: '用户取消选择' };
  }
  
  const selectedPath = result.filePaths[0];
  
  // 保存到 store
  if (store) {
    store.set('configDirectory', selectedPath);
  }
  
  return { success: true, path: selectedPath };
});

// 扫描配置文件
ipcMain.handle('config:scan-files', async () => {
  try {
    // 从 store 获取配置目录
    const configDir = store ? store.get('configDirectory') : null;
    
    if (!configDir) {
      return { success: false, error: '未设置配置目录，请先选择配置目录' };
    }
    
    // 递归扫描目录中的 .xml 文件
    const xmlFiles = [];
    
    async function scanDirectory(dirPath) {
      try {
        const entries = await fsPromises.readdir(dirPath, { withFileTypes: true });
        
        for (const entry of entries) {
          const fullPath = path.join(dirPath, entry.name);
          
          if (entry.isDirectory()) {
            // 递归扫描子目录
            await scanDirectory(fullPath);
          } else if (entry.isFile() && entry.name.endsWith('.xml')) {
            xmlFiles.push(fullPath);
          }
        }
      } catch (error) {
        console.warn(`无法扫描目录 ${dirPath}:`, error);
      }
    }
    
    await scanDirectory(configDir);
    
    return { success: true, files: xmlFiles };
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : 'Unknown error';
    return { success: false, error: errorMessage };
  }
});

// IPC 处理 - Store 相关操作

ipcMain.handle('store:get', (event, key, defaultValue) => {
  if (!store) return defaultValue;
  return store.get(key, defaultValue);
});

ipcMain.handle('store:set', (event, key, value) => {
  if (store) {
    store.set(key, value);
    return true;
  }
  return false;
});

ipcMain.handle('store:delete', (event, key) => {
  if (store) {
    store.delete(key);
    return true;
  }
  return false;
});

ipcMain.handle('store:has', (event, key) => {
  if (!store) return false;
  return store.has(key);
});

ipcMain.handle('store:clear', () => {
  if (store) {
    store.clear();
    return true;
  }
  return false;
});

ipcMain.handle('store:get-recent-files', () => {
  if (!store) return [];
  return store.get('recentFiles', []);
});

ipcMain.handle('store:add-to-recent-files', (event, file) => {
  if (!store) return false;
  
  try {
    let recentFiles = store.get('recentFiles', []);
    
    // 移除已存在的文件
    const existingIndex = recentFiles.findIndex(f => f.path === file.path);
    if (existingIndex >= 0) {
      recentFiles.splice(existingIndex, 1);
    }
    
    // 添加到开头
    recentFiles.unshift(file);
    
    // 限制数量
    if (recentFiles.length > 10) {
      recentFiles = recentFiles.slice(0, 10);
    }
    
    store.set('recentFiles', recentFiles);
    return true;
  } catch (error) {
    console.error('Failed to add to recent files:', error);
    return false;
  }
});

ipcMain.handle('store:clear-recent-files', () => {
  if (!store) return false;
  store.set('recentFiles', []);
  return true;
});

ipcMain.handle('store:get-statistics', () => {
  if (!store) {
    return {
      configFiles: 0,
      generationCount: 0,
      lastGenerated: '从未生成'
    };
  }
  return store.get('statistics', {
    configFiles: 0,
    generationCount: 0,
    lastGenerated: '从未生成'
  });
});

ipcMain.handle('store:save-statistics', (event, stats) => {
  if (store) {
    store.set('statistics', stats);
    return true;
  }
  return false;
});