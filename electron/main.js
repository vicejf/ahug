import { app, BrowserWindow, dialog, ipcMain } from 'electron';
import net from 'net';
import path from 'path';
import { fileURLToPath } from 'url';
import CodeGenerationService from '../src/core/CodeGenerationService.js';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

let mainWindow;
let codeGenerationService;

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
  codeGenerationService = CodeGenerationService.getInstance();
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
