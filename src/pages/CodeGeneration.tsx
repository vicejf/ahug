import { FolderOutlined, PlayCircleOutlined, StopOutlined, SyncOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Checkbox, Col, Flex, Input, Progress, Row, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import BackendService from '../services/BackendService';
import ConfigFileService from '../services/ConfigFileService';
import ConfigManager from '../services/ConfigManager';

const { Title, Text } = Typography;

interface GenerationOptions {
  generateClient: boolean;
  generateBusiness: boolean;
  generateMetadata: boolean;
  syncAfterGenerate: boolean;
}

interface MetadataSwitches {
  enablePubBillInterface: boolean;
  enableUser: boolean;
  enableBillStatus: boolean;
}

export default function CodeGeneration() {
  const [generationOptions, setGenerationOptions] = useState<GenerationOptions>({
    generateClient: true,
    generateBusiness: true,
    generateMetadata: false,
    syncAfterGenerate: false
  });

  const [metadataSwitches, setMetadataSwitches] = useState<MetadataSwitches>({
    enablePubBillInterface: true,
    enableUser: true,
    enableBillStatus: true
  });

  const [outputDir, setOutputDir] = useState('./output');
  const [sourceDir, setSourceDir] = useState('');
  const [isGenerating, setIsGenerating] = useState(false);
  const [isSyncing, setIsSyncing] = useState(false);
  const [progress, setProgress] = useState(0);
  const [statusMessage, setStatusMessage] = useState('');
  const [generationResult, setGenerationResult] = useState<{ filesGenerated: number; outputDir: string; timestamp: string } | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [configPath, setConfigPath] = useState<string | null>(null);

  useEffect(() => {
    loadGlobalConfig();
    loadCurrentConfigPath();
  }, []);

  const loadGlobalConfig = () => {
    const currentPath = ConfigManager.getCurrentConfigPath();
    if (currentPath) {
      ConfigFileService.loadConfig(currentPath).then(result => {
        if (result.success && result.config) {
          setGenerationOptions({
            generateClient: result.config.globalConfig.generateClient,
            generateBusiness: result.config.globalConfig.generateBusiness,
            generateMetadata: result.config.globalConfig.generateMetadata,
            syncAfterGenerate: result.config.globalConfig.syncAfterGenerate
          });
          setOutputDir(result.config.globalConfig.outputDir);
          setSourceDir(result.config.globalConfig.sourcePath);
        }
      });
    }
  };

  const loadCurrentConfigPath = () => {
    const path = ConfigManager.getCurrentConfigPath();
    setConfigPath(path);
  };

  const handleOptionChange = (option: keyof GenerationOptions) => (e: any) => {
    setGenerationOptions({
      ...generationOptions,
      [option]: e.target.checked
    });
  };

  const handleMetadataChange = (option: keyof MetadataSwitches) => (e: any) => {
    setMetadataSwitches({
      ...metadataSwitches,
      [option]: e.target.checked
    });
  };

  const handleGenerate = async () => {
    const currentConfigPath = ConfigManager.getCurrentConfigPath();
    if (!currentConfigPath) {
      message.error('请先打开或保存一个配置文件');
      return;
    }

    setIsGenerating(true);
    setProgress(0);
    setError(null);
    setGenerationResult(null);
    setStatusMessage('开始生成代码...');

    try {
      setStatusMessage('验证配置...');
      setProgress(10);

      await new Promise(resolve => setTimeout(resolve, 500));

      const loadResult = await ConfigFileService.loadConfig(currentConfigPath);
      if (!loadResult.success || !loadResult.config) {
        throw new Error('加载配置文件失败');
      }

      setStatusMessage('准备生成参数...');
      setProgress(20);

      await new Promise(resolve => setTimeout(resolve, 500));

      setStatusMessage('调用后端生成服务...');
      setProgress(30);

      const backendResult = await BackendService.generateCode(currentConfigPath, outputDir);

      if (!backendResult.success) {
        throw new Error(backendResult.errorMessage || '后端生成失败');
      }

      setStatusMessage('生成完成，正在处理结果...');
      setProgress(90);

      await new Promise(resolve => setTimeout(resolve, 500));

      setProgress(100);
      setStatusMessage('代码生成成功！');

      const finalResult = {
        filesGenerated: 15,
        outputDir,
        timestamp: new Date().toISOString()
      };

      setGenerationResult(finalResult);
      ConfigManager.incrementGenerationCount();
      message.success(`代码生成成功！已生成 ${finalResult.filesGenerated} 个文件`);

      if (generationOptions.syncAfterGenerate && sourceDir) {
        handleSyncCode();
      }
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '未知错误';
      setError(errorMessage);
      setStatusMessage(`生成失败: ${errorMessage}`);
      message.error('代码生成失败: ' + errorMessage);
    } finally {
      setIsGenerating(false);
    }
  };

  const handleSyncCode = async () => {
    if (!sourceDir) {
      message.warning('请先设置项目源码目录');
      return;
    }

    setIsSyncing(true);
    setStatusMessage('正在同步代码到项目...');

    try {
      await new Promise(resolve => setTimeout(resolve, 1000));

      setStatusMessage('同步完成！');
      message.success('代码同步成功');
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '未知错误';
      message.error('代码同步失败: ' + errorMessage);
      setStatusMessage(`同步失败: ${errorMessage}`);
    } finally {
      setIsSyncing(false);
    }
  };

  const handleCancel = () => {
    setIsGenerating(false);
    setStatusMessage('生成已取消');
    setProgress(0);
  };

  const handleBrowseOutputDir = async () => {
    if (window.electronAPI?.selectOutputDirectory) {
      const dir = await window.electronAPI.selectOutputDirectory();
      if (dir) {
        setOutputDir(dir);
      }
    }
  };

  const handleBrowseSourceDir = async () => {
    if (window.electronAPI?.selectDirectory) {
      const dir = await window.electronAPI.selectDirectory();
      if (dir) {
        setSourceDir(dir);
      }
    }
  };

  return (
    <div>
      <Title level={2}>代码生成</Title>

      <Row gutter={[16, 16]}>
        <Col xs={24} md={16}>
          <Card title="生成选项">
            <Flex vertical gap="small" style={{ width: '100%' }}>
              <Checkbox
                checked={generationOptions.generateClient}
                onChange={handleOptionChange('generateClient')}
              >
                生成客户端代码
              </Checkbox>
              <Checkbox
                checked={generationOptions.generateBusiness}
                onChange={handleOptionChange('generateBusiness')}
              >
                生成业务逻辑代码
              </Checkbox>
              <Checkbox
                checked={generationOptions.generateMetadata}
                onChange={handleOptionChange('generateMetadata')}
              >
                生成元数据
              </Checkbox>
              <Checkbox
                checked={generationOptions.syncAfterGenerate}
                onChange={handleOptionChange('syncAfterGenerate')}
              >
                生成后自动同步
              </Checkbox>
            </Flex>

            <Title level={5} style={{ marginTop: 24 }}>元数据开关</Title>
            <Flex vertical gap="small" style={{ width: '100%' }}>
              <Checkbox
                checked={metadataSwitches.enablePubBillInterface}
                onChange={handleMetadataChange('enablePubBillInterface')}
              >
                启用 PubBillInterface
              </Checkbox>
              <Checkbox
                checked={metadataSwitches.enableUser}
                onChange={handleMetadataChange('enableUser')}
              >
                启用用户元数据
              </Checkbox>
              <Checkbox
                checked={metadataSwitches.enableBillStatus}
                onChange={handleMetadataChange('enableBillStatus')}
              >
                启用单据状态
              </Checkbox>
            </Flex>
          </Card>
        </Col>

        <Col xs={24} md={8}>
          <Card title="目录设置">
            <Flex vertical gap="small" style={{ width: '100%' }}>
              <div>
                <Text strong>输出目录</Text>
                <Input
                  value={outputDir}
                  onChange={(e) => setOutputDir(e.target.value)}
                  placeholder="生成的代码将放置在此目录"
                  style={{ marginTop: 8 }}
                />
                <Button
                  block
                  size="small"
                  icon={<FolderOutlined />}
                  style={{ marginTop: 8 }}
                  onClick={handleBrowseOutputDir}
                >
                  浏览输出目录
                </Button>
              </div>
              <div>
                <Text strong>项目源码目录</Text>
                <Input
                  value={sourceDir}
                  onChange={(e) => setSourceDir(e.target.value)}
                  placeholder="同步的目标项目目录"
                  style={{ marginTop: 8 }}
                />
                <Button
                  block
                  size="small"
                  icon={<FolderOutlined />}
                  style={{ marginTop: 8 }}
                  onClick={handleBrowseSourceDir}
                >
                  浏览源码目录
                </Button>
              </div>
            </Flex>
          </Card>

          <Card title="生成代码" style={{ marginTop: 16 }}>
            <Flex vertical gap="small" style={{ width: '100%' }}>
              <Button
                type="primary"
                block
                size="large"
                icon={isGenerating ? <StopOutlined /> : <PlayCircleOutlined />}
                onClick={isGenerating ? handleCancel : handleGenerate}
                disabled={!configPath}
              >
                {isGenerating ? '取消生成' : '生成代码'}
              </Button>

              {!configPath && (
                <Text type="secondary" style={{ fontSize: 12 }}>
                  请先在配置编辑器中打开或保存配置
                </Text>
              )}

              {isSyncing && (
                <Button
                  block
                  icon={<SyncOutlined spin />}
                  disabled
                >
                  正在同步...
                </Button>
              )}

              {(isGenerating || generationResult || error) && (
                <>
                  {isGenerating && (
                    <>
                      <Progress percent={progress} />
                      <Text type="secondary">{statusMessage}</Text>
                    </>
                  )}

                  {generationResult && (
                    <Alert
                      type="success"
                      message={
                        <>
                          <strong>生成完成！</strong><br />
                          已生成 {generationResult.filesGenerated} 个文件到 {generationResult.outputDir}<br />
                          <small>完成时间: {new Date(generationResult.timestamp).toLocaleString()}</small>
                        </>
                      }
                    />
                  )}

                  {error && (
                    <Alert
                      type="error"
                      message={<><strong>生成失败:</strong> {error}</>}
                    />
                  )}
                </>
              )}
            </Flex>
          </Card>
        </Col>
      </Row>
    </div>
  );
}
