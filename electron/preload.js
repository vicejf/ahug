const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
  selectDirectory: () => ipcRenderer.invoke('select-directory'),
  selectOutputDirectory: () => ipcRenderer.invoke('select-output-directory'),
  generateCode: (configPath, outputDir) => ipcRenderer.invoke('code-generation:generate', configPath, outputDir),
  validateConfig: (configPath) => ipcRenderer.invoke('code-generation:validate', configPath),
});
