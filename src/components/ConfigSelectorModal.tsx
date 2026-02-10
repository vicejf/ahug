import { FileOutlined, FolderOpenOutlined } from '@ant-design/icons';
import type { TableProps } from 'antd';
import {
  Button,
  Empty,
  Modal,
  Space,
  Table,
  Tag,
  Typography,
  message
} from 'antd';
import { useEffect, useState } from 'react';
import ConfigFileService from '../services/ConfigFileService';
import ConfigManager from '../services/ConfigManager';
import FileService from '../services/FileService';
import type { BillConfigData } from '../types';

const { Text } = Typography;

interface ConfigFileInfo {
  name: string;
  path: string;
  billCode: string;
  billName: string;
  billType: string;
  lastModified: string;
  size: string;
}

interface ConfigSelectorModalProps {
  open: boolean;
  onCancel: () => void;
  onConfigSelected: (config: BillConfigData, filePath: string) => void;
}

export default function ConfigSelectorModal({
  open,
  onCancel,
  onConfigSelected
}: ConfigSelectorModalProps) {
  const [messageApi, contextHolder] = message.useMessage();
  const [configs, setConfigs] = useState<ConfigFileInfo[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);

  const loadConfigList = async () => {
    setLoading(true);
    try {
      // 获取最近使用的文件
      const recentFiles = ConfigManager.getRecentFiles();
      
      // 扫描配置目录
      const scanResult = await FileService.scanConfigFiles();
      
      let allFiles: string[] = [];
      
      if (scanResult.success && scanResult.files) {
        allFiles = [...new Set([...recentFiles.map(f => f.path), ...scanResult.files])];
      } else {
        allFiles = recentFiles.map(f => f.path);
      }
      
      // 并行加载所有配置的基本信息
      const loadPromises = allFiles.map(async (filePath) => {
        try {
          const loadResult = await ConfigFileService.loadBasicConfigInfo(filePath);
          if (loadResult.success && loadResult.basicInfo) {
            const fileInfo = await FileService.getFileStats(filePath);
            return {
              name: filePath.split(/[/\\]/).pop() || '',
              path: filePath,
              billCode: loadResult.basicInfo.billCode,
              billName: loadResult.basicInfo.billName,
              billType: loadResult.basicInfo.billType,
              lastModified: fileInfo.success && fileInfo.stats ? new Date(fileInfo.stats.mtime).toLocaleString() : '未知',
              size: fileInfo.success && fileInfo.stats ? formatFileSize(fileInfo.stats.size) : '未知'
            };
          }
          return null;
        } catch (error) {
          console.error(`Failed to load config info from ${filePath}:`, error);
          return null;
        }
      });

      const results = await Promise.all(loadPromises);
      const validConfigs = results.filter((info): info is ConfigFileInfo => info !== null);
      
      setConfigs(validConfigs);
    } catch (error) {
      console.error('Failed to load config list:', error);
      messageApi.error('加载配置列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (open) {
      loadConfigList();
    }
  }, [open]);

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const handleRefresh = () => {
    loadConfigList();
  };

  const handleOpenFolder = async () => {
    try {
      const result = await FileService.selectConfigDirectory();
      if (result.success) {
        messageApi.success('配置目录已更新');
        loadConfigList();
      }
    } catch (error) {
      console.error('打开目录失败:', error);
      messageApi.error('打开目录失败');
    }
  };

  const handleRowSelection: TableProps<ConfigFileInfo>['rowSelection'] = {
    type: 'radio',
    selectedRowKeys,
    onChange: (selectedKeys) => {
      setSelectedRowKeys(selectedKeys);
    },
  };

  const columns: TableProps<ConfigFileInfo>['columns'] = [
    {
      title: '单据编码',
      dataIndex: 'billCode',
      key: 'billCode',
      width: 120,
      render: (text: string) => (
        <Text strong style={{ color: '#1890ff' }}>
          {text}
        </Text>
      )
    },
    {
      title: '单据名称',
      dataIndex: 'billName',
      key: 'billName',
      ellipsis: true,
    },
    {
      title: '类型',
      dataIndex: 'billType',
      key: 'billType',
      width: 100,
      render: (type: string) => (
        <Tag color={type === 'single' ? 'blue' : type === 'multi' ? 'green' : 'orange'}>
          {type === 'single' ? '单表体' : type === 'multi' ? '多表体' : '归档'}
        </Tag>
      )
    },
    {
      title: '文件名',
      dataIndex: 'name',
      key: 'name',
      ellipsis: true,
      render: (text: string) => (
        <Space>
          <FileOutlined />
          <Text>{text}</Text>
        </Space>
      )
    },
    {
      title: '大小',
      dataIndex: 'size',
      key: 'size',
      width: 100,
    },
    {
      title: '最后修改',
      dataIndex: 'lastModified',
      key: 'lastModified',
      width: 180,
    }
  ];

  const handleOk = async () => {
    if (selectedRowKeys.length === 0) {
      messageApi.warning('请先选择一个配置文件');
      return;
    }

    const selectedPath = selectedRowKeys[0] as string;
    const selectedConfig = configs.find(c => c.path === selectedPath);

    if (!selectedConfig) {
      messageApi.error('找不到选中的配置文件');
      return;
    }

    try {
      setLoading(true);
      const loadResult = await ConfigFileService.loadFullConfig(selectedPath);
      
      if (loadResult.success && loadResult.config) {
        onConfigSelected(loadResult.config, selectedPath);
        setSelectedRowKeys([]);
        messageApi.success(`已加载配置: ${selectedConfig.billCode}`);
      } else {
        messageApi.error('加载配置失败: ' + (loadResult.error || '未知错误'));
      }
    } catch (error) {
      messageApi.error('加载配置时发生错误');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    setSelectedRowKeys([]);
    onCancel();
  };

  return (
    <>
      {contextHolder}
      <Modal
        title={
          <Space>
            <FolderOpenOutlined style={{ color: '#1890ff' }} />
            <span>加载配置</span>
          </Space>
        }
        open={open}
        onOk={handleOk}
        onCancel={handleCancel}
        width={800}
        okText="加载选中配置"
        cancelText="取消"
        okButtonProps={{
          loading,
          disabled: selectedRowKeys.length === 0
        }}
        footer={[
          <Button key="refresh" onClick={handleRefresh} loading={loading}>
            刷新列表
          </Button>,
          <Button key="folder" onClick={handleOpenFolder} icon={<FolderOpenOutlined />}>
            打开目录
          </Button>,
          <Button key="cancel" onClick={handleCancel}>
            取消
          </Button>,
          <Button 
            key="ok" 
            type="primary" 
            onClick={handleOk}
            loading={loading}
            disabled={selectedRowKeys.length === 0}
          >
            加载选中配置
          </Button>
        ]}
      >
        <div style={{ marginBottom: 16 }}>
          <Text type="secondary">
            从以下列表中选择一个配置文件进行加载，系统会自动关联加载所有相关的配置文件。
          </Text>
        </div>
        
        {configs.length === 0 && !loading ? (
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description="暂无配置文件"
            style={{ padding: '40px 0' }}
          >
            <Button type="primary" onClick={handleOpenFolder} icon={<FolderOpenOutlined />}>
              选择配置目录
            </Button>
          </Empty>
        ) : (
          <Table
            rowSelection={handleRowSelection}
            columns={columns}
            dataSource={configs.map(config => ({ ...config, key: config.path }))}
            pagination={false}
            loading={loading}
            scroll={{ y: 400 }}
            size="middle"
            onRow={(record) => ({
              onClick: () => {
                setSelectedRowKeys([record.path]);
              }
            })}
          />
        )}
      </Modal>
    </>
  );
}