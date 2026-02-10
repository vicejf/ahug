// 测试 electron-store 功能的简单脚本
// 可以在 Electron 开发者工具控制台中运行

console.log('=== 测试 electron-store 功能 ===');

// 测试添加最近文件
const testFile = {
  name: 'test-config.xml',
  path: 'D:/test/test-config.xml',
  lastModified: new Date().toISOString()
};

// 测试统计信息
const testStats = {
  configFiles: 5,
  generationCount: 12,
  lastGenerated: new Date().toLocaleString()
};

// 如果在 Electron 环境中
if (typeof window !== 'undefined' && window.electronAPI) {
  console.log('检测到 Electron 环境');
  
  // 测试存储功能
  window.electronAPI.storeSet('test-key', 'test-value')
    .then(() => console.log('✓ 基本存储功能正常'))
    .catch(err => console.error('✗ 基本存储功能失败:', err));
  
  // 测试获取功能
  window.electronAPI.storeGet('test-key', 'default')
    .then(value => console.log('✓ 获取功能正常:', value))
    .catch(err => console.error('✗ 获取功能失败:', err));
  
  // 测试最近文件功能
  window.electronAPI.storeAddToRecentFiles(testFile)
    .then(() => console.log('✓ 添加最近文件功能正常'))
    .catch(err => console.error('✗ 添加最近文件功能失败:', err));
  
  // 测试获取最近文件
  window.electronAPI.storeGetRecentFiles()
    .then(files => console.log('✓ 获取最近文件功能正常:', files))
    .catch(err => console.error('✗ 获取最近文件功能失败:', err));
  
  // 测试统计信息功能
  window.electronAPI.storeSaveStatistics(testStats)
    .then(() => console.log('✓ 保存统计信息功能正常'))
    .catch(err => console.error('✗ 保存统计信息功能失败:', err));
  
  // 测试获取统计信息
  window.electronAPI.storeGetStatistics()
    .then(stats => console.log('✓ 获取统计信息功能正常:', stats))
    .catch(err => console.error('✗ 获取统计信息功能失败:', err));
  
} else {
  console.log('未检测到 Electron 环境，跳过测试');
}

console.log('=== 测试完成 ===');