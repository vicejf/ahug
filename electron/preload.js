const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
  selectDirectory: () => ipcRenderer.invoke('select-directory'),
  selectOutputDirectory: () => ipcRenderer.invoke('select-output-directory'),
  generateCode: (configPath, outputDir) => ipcRenderer.invoke('code-generation:generate', configPath, outputDir),
  validateConfig: (configPath) => ipcRenderer.invoke('code-generation:validate', configPath),
  // 文件操作方法
  openFile: () => ipcRenderer.invoke('file:open'),
  saveFile: (defaultPath) => ipcRenderer.invoke('file:save', defaultPath),
  readFile: (filePath) => ipcRenderer.invoke('file:read', filePath),
  writeFile: (filePath, content) => ipcRenderer.invoke('file:write', filePath, content),
  fileExists: (filePath) => ipcRenderer.invoke('file:exists', filePath),
  listDirectory: (dirPath) => ipcRenderer.invoke('file:list-directory', dirPath),
  // Store 相关方法
  storeGet: (key, defaultValue) => ipcRenderer.invoke('store:get', key, defaultValue),
  storeSet: (key, value) => ipcRenderer.invoke('store:set', key, value),
  storeDelete: (key) => ipcRenderer.invoke('store:delete', key),
  storeHas: (key) => ipcRenderer.invoke('store:has', key),
  storeClear: () => ipcRenderer.invoke('store:clear'),
  storeGetRecentFiles: () => ipcRenderer.invoke('store:get-recent-files'),
  storeAddToRecentFiles: (file) => ipcRenderer.invoke('store:add-to-recent-files', file),
  storeClearRecentFiles: () => ipcRenderer.invoke('store:clear-recent-files'),
  storeGetStatistics: () => ipcRenderer.invoke('store:get-statistics'),
  storeSaveStatistics: (stats) => ipcRenderer.invoke('store:save-statistics'),
  // 配置文件相关方法
  selectConfigDirectory: () => ipcRenderer.invoke('config:select-directory'),
  scanConfigFiles: () => ipcRenderer.invoke('config:scan-files'),
  getFileStats: (filePath) => ipcRenderer.invoke('file:get-stats', filePath),
});
