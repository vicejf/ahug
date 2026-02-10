import {
  AppstoreOutlined,
  DatabaseOutlined,
  ExclamationCircleOutlined,
  FileAddOutlined,
  FileTextOutlined,
  FolderOpenOutlined,
  SaveOutlined,
  SettingOutlined
} from '@ant-design/icons';
import type { TabsProps } from 'antd';
import {
  Badge,
  Button,
  Card,
  Col,
  ConfigProvider,
  Divider,
  Form,
  Input,
  Layout,
  message,
  Modal,
  Row,
  Select,
  Space,
  Tabs,
  Tooltip,
  Typography
} from 'antd';
import { useEffect, useState } from 'react';
import BodyFieldsTable from '../components/BodyFieldsTable';
import EnumConfigTable from '../components/EnumConfigTable';
import HeadFieldsTable from '../components/HeadFieldsTable';
import ConfigFileService from '../services/ConfigFileService';
import ConfigManager from '../services/ConfigManager';
import FileService from '../services/FileService';
import type { BillConfigData } from '../types';

const { TextArea } = Input;
const { Header, Content } = Layout;
const { Title, Text } = Typography;

const BILL_TYPES = [
  { label: '单表体单据', value: 'single' },
  { label: '多表体单据', value: 'multi' },
  { label: '归档单据', value: 'archive' }
];

export default function ConfigurationEditor() {
  const [messageApi, messageContextHolder] = message.useMessage();
  const [activeTab, setActiveTab] = useState('1');
  const [form] = Form.useForm();
  const [config, setConfig] = useState<BillConfigData>(ConfigManager.clearConfig());
  const [isModified, setIsModified] = useState(false);
  const [currentBodyCode, setCurrentBodyCode] = useState<string>('');
  const [unsavedChangesModalVisible, setUnsavedChangesModalVisible] = useState(false);
  const [pendingAction, setPendingAction] = useState<(() => void) | null>(null);

  useEffect(() => {
    form.setFieldsValue(config.basicInfo);
  }, [config.basicInfo, form]);

  useEffect(() => {
    if (config.bodyCodeList.length > 0 && !currentBodyCode) {
      // 使用 setTimeout 避免在 effect 中直接调用 setState
      setTimeout(() => {
        setCurrentBodyCode(config.bodyCodeList[0]);
      }, 0);
    }
  }, [config.bodyCodeList, currentBodyCode]);

  const handleBasicInfoChange = (field: keyof BillConfigData['basicInfo'], value: string | number | boolean | undefined) => {
    const newConfig = { ...config };
    newConfig.basicInfo = { ...newConfig.basicInfo, [field]: value };

    if (field === 'billCode') {
      const upperValue = typeof value === 'string' ? value.toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 4) : '';
      newConfig.basicInfo.billCode = upperValue;
      newConfig.basicInfo.headCode = upperValue ? `${upperValue}HVO` : '';

      if (newConfig.basicInfo.billType === 'multi') {
        newConfig.basicInfo.bodyCode = upperValue ? `${upperValue}BVO` : '';
      }
    }

    if (field === 'billType' && value === 'single') {
      newConfig.basicInfo.bodyCode = '';
      newConfig.bodyFields = [];
    }

    if (field === 'billType' && value === 'multi' && !newConfig.basicInfo.bodyCode && newConfig.basicInfo.billCode) {
      newConfig.basicInfo.bodyCode = `${newConfig.basicInfo.billCode}BVO`;
    }

    setConfig(newConfig);
    setIsModified(true);
  };

  const handleHeadFieldsChange = (fields: BillConfigData['headFields']) => {
    setConfig({ ...config, headFields: fields });
    setIsModified(true);
  };

  const handleBodyFieldsChange = (fields: BillConfigData['bodyFields']) => {
    setConfig({ ...config, bodyFields: fields });
    setIsModified(true);
  };

  const handleBodyCodeChange = (bodyCode: string) => {
    setCurrentBodyCode(bodyCode);
  };

  const handleBodyCodeListChange = (bodyCodeList: string[]) => {
    setConfig({ ...config, bodyCodeList });
    setIsModified(true);
  };

  const handleEnumConfigsChange = (enumConfigs: BillConfigData['enumConfigs']) => {
    setConfig({ ...config, enumConfigs });
    setIsModified(true);
  };

  const validateBasicInfo = (): boolean => {
    const errors: string[] = [];

    if (!config.basicInfo.billCode) {
      errors.push('单据编码不能为空');
    }
    if (!config.basicInfo.billName) {
      errors.push('单据名称不能为空');
    }
    if (config.basicInfo.billType === 'multi' && !config.basicInfo.bodyCode) {
      errors.push('多表体类型需要设置表体编码');
    }

    if (errors.length > 0) {
      messageApi.error('验证失败：\n' + errors.join('\n'));
      return false;
    }

    return true;
  };

  const checkUnsavedChanges = (action: () => void) => {
    if (isModified) {
      setPendingAction(() => action);
      setUnsavedChangesModalVisible(true);
    } else {
      action();
    }
  };

  const handleNewConfig = () => {
    checkUnsavedChanges(() => {
      const newConfig = ConfigManager.clearConfig();
      setConfig(newConfig);
      form.setFieldsValue(newConfig.basicInfo);
      setCurrentBodyCode('');
      ConfigManager.setCurrentConfigPath(null);
      setIsModified(false);
      messageApi.success('已新建配置');
    });
  };

  const handleOpenConfig = async () => {
    console.log('handleOpenConfig called');
    checkUnsavedChanges(() => {
      console.log('Inside checkUnsavedChanges callback');
      // 将异步操作包装在一个立即执行的异步函数中
      (async () => {
        try {
          console.log('Calling FileService.openFile()');
          const result = await FileService.openFile();
          console.log('FileService.openFile() result:', result);
          
          if (result.success && result.filePath) {
            console.log('Loading config from:', result.filePath);
            const loadResult = await ConfigFileService.loadConfig(result.filePath);
            console.log('ConfigFileService.loadConfig result:', loadResult);
            
            if (loadResult.success && loadResult.config) {
              setConfig(loadResult.config);
              form.setFieldsValue(loadResult.config.basicInfo);
              setCurrentBodyCode(loadResult.config.bodyCodeList[0] || '');
              ConfigManager.setCurrentConfigPath(result.filePath);
              ConfigManager.addToRecentFiles(result.filePath);
              setIsModified(false);
              messageApi.success(`已加载: ${result.filePath}`);
            } else {
              messageApi.error('加载配置失败: ' + (loadResult.error || '未知错误'));
            }
          } else {
            console.log('File selection cancelled or failed');
            if (result.error) {
              messageApi.error('打开文件失败: ' + result.error);
            }
          }
        } catch (error) {
          console.error('Error in handleOpenConfig:', error);
          messageApi.error('操作失败: ' + (error instanceof Error ? error.message : '未知错误'));
        }
      })();
    });
  };

  const handleSaveConfig = async () => {
    if (!validateBasicInfo()) {
      return;
    }

    const currentPath = ConfigManager.getCurrentConfigPath();
    const filePath = currentPath || ConfigManager.generateDefaultFileName(config.basicInfo.billCode);

    const saveResult = await ConfigFileService.saveConfig(config, filePath);

    if (saveResult.success && saveResult.filePath) {
      ConfigManager.setCurrentConfigPath(saveResult.filePath);
      ConfigManager.addToRecentFiles(saveResult.filePath);
      ConfigManager.incrementConfigCount();
      setIsModified(false);
      messageApi.success(`已保存: ${saveResult.filePath}`);
    } else {
      messageApi.error('保存失败: ' + (saveResult.error || '未知错误'));
    }
  };

  const handleSaveAsConfig = async () => {
    if (!validateBasicInfo()) {
      return;
    }

    const defaultFileName = ConfigManager.generateDefaultFileName(config.basicInfo.billCode);
    const result = await FileService.saveFile('', defaultFileName);

    if (result.success && result.filePath) {
      const saveResult = await ConfigFileService.saveConfig(config, result.filePath);

      if (saveResult.success && saveResult.filePath) {
        ConfigManager.setCurrentConfigPath(saveResult.filePath);
        ConfigManager.addToRecentFiles(saveResult.filePath);
        ConfigManager.incrementConfigCount();
        setIsModified(false);
        messageApi.success(`已保存: ${saveResult.filePath}`);
      } else {
        messageApi.error('保存失败: ' + (saveResult.error || '未知错误'));
      }
    }
  };

  const handleTabChange = (key: string) => {
    if (key !== '1' && !validateBasicInfo()) {
      return;
    }
    setActiveTab(key);
  };

  const tabItems: TabsProps['items'] = [
    {
      key: '1',
      label: (
        <span>
          <SettingOutlined />
          基本信息
        </span>
      ),
      children: (
        <Card
          className="config-card"
          styles={{ body: { padding: '24px 32px' } }}
        >
          <div style={{ marginBottom: 24 }}>
            <Title level={4} style={{ margin: 0, color: '#1890ff' }}>
              <SettingOutlined style={{ marginRight: 8 }} />
              基本信息配置
            </Title>
            <Text type="secondary" style={{ marginTop: 8, display: 'block' }}>
              配置单据的基本属性，包括编码、名称、类型等核心信息
            </Text>
          </div>
          <Divider style={{ margin: '16px 0 24px' }} />
          <Form
            form={form}
            layout="vertical"
            initialValues={config.basicInfo}
            className="config-form"
          >
            <Row gutter={[24, 16]}>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label={<span style={{ fontWeight: 500 }}>单据编码</span>}
                  name="billCode"
                  rules={[{ required: true, message: '请输入单据编码!' }]}
                  tooltip="只能输入字母和数字，自动大写，限制4个字符"
                >
                  <Input
                    placeholder="例如：AUJX"
                    onChange={(e) => handleBasicInfoChange('billCode', e.target.value)}
                    maxLength={4}
                    style={{ textTransform: 'uppercase' }}
                    size="large"
                    prefix={<DatabaseOutlined style={{ color: '#bfbfbf' }} />}
                  />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label={<span style={{ fontWeight: 500 }}>单据名称</span>}
                  name="billName"
                  rules={[{ required: true, message: '请输入单据名称!' }]}
                >
                  <Input
                    placeholder="单据显示名称"
                    onChange={(e) => handleBasicInfoChange('billName', e.target.value)}
                    size="large"
                    prefix={<FileTextOutlined style={{ color: '#bfbfbf' }} />}
                  />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label={<span style={{ fontWeight: 500 }}>单据类型</span>}
                  name="billType"
                >
                  <Select
                    onChange={(value) => handleBasicInfoChange('billType', value)}
                    size="large"
                    placeholder="请选择单据类型"
                  >
                    {BILL_TYPES.map(type => (
                      <Select.Option key={type.value} value={type.value}>{type.label}</Select.Option>
                    ))}
                  </Select>
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label={<span style={{ fontWeight: 500 }}>模块</span>}
                  name="module"
                >
                  <Input
                    placeholder="例如：mymodule"
                    onChange={(e) => handleBasicInfoChange('module', e.target.value)}
                    size="large"
                    prefix={<AppstoreOutlined style={{ color: '#bfbfbf' }} />}
                  />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label={<span style={{ fontWeight: 500 }}>包名</span>}
                  name="packageName"
                  rules={[{ required: true, message: '请输入包名!' }]}
                >
                  <Input
                    placeholder="例如：com.nc5.mymodule"
                    onChange={(e) => handleBasicInfoChange('packageName', e.target.value)}
                    size="large"
                  />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label={<span style={{ fontWeight: 500 }}>表体编码</span>}
                  name="bodyCode"
                  tooltip={config.basicInfo.billType === 'single' ? '单表体类型无需设置' : '多表体类型需要设置'}
                >
                  <Input
                    placeholder="例如：AUJXBVO"
                    onChange={(e) => handleBasicInfoChange('bodyCode', e.target.value)}
                    disabled={config.basicInfo.billType === 'single'}
                    size="large"
                  />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} lg={8}>
                <Form.Item
                  label={<span style={{ fontWeight: 500 }}>作者</span>}
                  name="author"
                >
                  <Input
                    placeholder="代码作者"
                    onChange={(e) => handleBasicInfoChange('author', e.target.value)}
                    size="large"
                  />
                </Form.Item>
              </Col>
              <Col xs={24}>
                <Form.Item
                  label={<span style={{ fontWeight: 500 }}>描述</span>}
                  name="description"
                >
                  <TextArea
                    rows={4}
                    placeholder="请输入单据描述信息..."
                    onChange={(e) => handleBasicInfoChange('description', e.target.value)}
                    showCount
                    maxLength={500}
                  />
                </Form.Item>
              </Col>
            </Row>
          </Form>
        </Card>
      )
    },
    {
      key: '2',
      label: (
        <span>
          <DatabaseOutlined />
          表头字段
          {config.headFields.length > 0 && (
            <Badge count={config.headFields.length} style={{ marginLeft: 4 }} size="small" />
          )}
        </span>
      ),
      children: (
        <Card
          className="config-card"
          styles={{ body: { padding: '24px 32px' } }}
        >
          <div style={{ marginBottom: 24 }}>
            <Title level={4} style={{ margin: 0, color: '#1890ff' }}>
              <DatabaseOutlined style={{ marginRight: 8 }} />
              表头字段配置
            </Title>
            <Text type="secondary" style={{ marginTop: 8, display: 'block' }}>
              配置单据的表头字段，这些字段显示在主表单中
            </Text>
          </div>
          <Divider style={{ margin: '16px 0 24px' }} />
          <HeadFieldsTable
            fields={config.headFields}
            enumConfigs={config.enumConfigs}
            onChange={handleHeadFieldsChange}
            disabled={false}
          />
        </Card>
      )
    },
    {
      key: '3',
      label: (
        <span>
          <AppstoreOutlined />
          表体字段
          {config.bodyFields.length > 0 && (
            <Badge count={config.bodyFields.length} style={{ marginLeft: 4 }} size="small" />
          )}
        </span>
      ),
      children: (
        <Card
          className="config-card"
          styles={{ body: { padding: '24px 32px' } }}
        >
          <div style={{ marginBottom: 24 }}>
            <Title level={4} style={{ margin: 0, color: '#1890ff' }}>
              <AppstoreOutlined style={{ marginRight: 8 }} />
              表体字段配置
            </Title>
            <Text type="secondary" style={{ marginTop: 8, display: 'block' }}>
              配置多表体单据的表体字段，这些字段显示在明细表格中
            </Text>
          </div>
          <Divider style={{ margin: '16px 0 24px' }} />
          <BodyFieldsTable
            fields={config.bodyFields}
            bodyCodeList={config.bodyCodeList}
            currentBodyCode={currentBodyCode}
            enumConfigs={config.enumConfigs}
            onChange={handleBodyFieldsChange}
            onBodyCodeChange={handleBodyCodeChange}
            onBodyCodeListChange={handleBodyCodeListChange}
            showBodyCodeSelector={config.basicInfo.billType === 'multi'}
            disabled={false}
          />
        </Card>
      )
    },
    {
      key: '4',
      label: (
        <span>
          <FileTextOutlined />
          枚举配置
          {config.enumConfigs.length > 0 && (
            <Badge count={config.enumConfigs.length} style={{ marginLeft: 4 }} size="small" />
          )}
        </span>
      ),
      children: (
        <Card
          className="config-card"
          styles={{ body: { padding: '24px 32px' } }}
        >
          <div style={{ marginBottom: 24 }}>
            <Title level={4} style={{ margin: 0, color: '#1890ff' }}>
              <FileTextOutlined style={{ marginRight: 8 }} />
              枚举配置
            </Title>
            <Text type="secondary" style={{ marginTop: 8, display: 'block' }}>
              定义在单据字段中使用的枚举类型
            </Text>
          </div>
          <Divider style={{ margin: '16px 0 24px' }} />
          <EnumConfigTable
            enumConfigs={config.enumConfigs}
            onChange={handleEnumConfigsChange}
            disabled={false}
          />
        </Card>
      )
    }
  ];

  return (
    <ConfigProvider
      theme={{
        components: {
          Card: {
            borderRadius: 8,
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.06)'
          },
          Tabs: {
            cardBg: '#fafafa'
          }
        }
      }}
    >
      {messageContextHolder}
      <Layout style={{ minHeight: '100vh', background: '#f5f7fa' }}>
        {/* Header */}
        <Header
          style={{
            background: '#fff',
            padding: '0 32px',
            height: 64,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            borderBottom: '1px solid #e8e8e8',
            boxShadow: '0 2px 8px rgba(0, 0, 0, 0.06)',
            position: 'sticky',
            top: 0,
            zIndex: 100
          }}
        >
          {/* Left: Title and File Path */}
          <Space size="middle" align="center">
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <div
                style={{
                  width: 40,
                  height: 40,
                  background: 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)',
                  borderRadius: 8,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  boxShadow: '0 2px 8px rgba(24, 144, 255, 0.3)'
                }}
              >
                <SettingOutlined style={{ fontSize: 20, color: '#fff' }} />
              </div>
              <div>
                <Title level={4} style={{ margin: 0, fontSize: 18, fontWeight: 600 }}>
                  单据配置
                  {isModified && (
                    <Badge
                      status="processing"
                      color="#faad14"
                      style={{ marginLeft: 8 }}
                    />
                  )}
                </Title>
                {ConfigManager.getCurrentConfigPath() && (
                  <Text type="secondary" style={{ fontSize: 12, display: 'block' }}>
                    {ConfigManager.getCurrentConfigPath()}
                  </Text>
                )}
              </div>
            </div>
          </Space>

          {/* Right: Action Buttons */}
          <Space size="small">
            <Tooltip title="打开配置文件">
              <Button
                icon={<FolderOpenOutlined />}
                onClick={handleOpenConfig}
                size="middle"
              >
                打开
              </Button>
            </Tooltip>
            <Tooltip title="新建配置">
              <Button
                icon={<FileAddOutlined />}
                onClick={handleNewConfig}
                size="middle"
              >
                新建
              </Button>
            </Tooltip>
            <Tooltip title="另存为">
              <Button
                icon={<SaveOutlined />}
                onClick={handleSaveAsConfig}
                size="middle"
              >
                另存为
              </Button>
            </Tooltip>
            <Button
              type="primary"
              icon={<SaveOutlined />}
              onClick={handleSaveConfig}
              size="middle"
              style={{
                background: 'linear-gradient(135deg, #1890ff 0%, #096dd9 100%)',
                boxShadow: '0 2px 8px rgba(24, 144, 255, 0.3)'
              }}
            >
              保存
            </Button>
          </Space>
        </Header>

        {/* Content */}
        <Content style={{ padding: '24px 32px', maxWidth: 1400, margin: '0 auto', width: '100%' }}>
          <Tabs
            activeKey={activeTab}
            onChange={handleTabChange}
            items={tabItems}
            type="card"
            size="large"
            style={{
              background: '#fff',
              borderRadius: 8,
              padding: '0 0 24px',
              boxShadow: '0 2px 8px rgba(0, 0, 0, 0.06)'
            }}
          />
        </Content>

        {/* Unsaved Changes Modal */}
        <Modal
          title={
            <Space>
              <ExclamationCircleOutlined style={{ color: '#faad14', fontSize: 20 }} />
              <span>未保存的更改</span>
            </Space>
          }
          open={unsavedChangesModalVisible}
          onOk={() => {
            setUnsavedChangesModalVisible(false);
            pendingAction?.();
          }}
          onCancel={() => {
            setUnsavedChangesModalVisible(false);
            setPendingAction(null);
          }}
          okText="放弃更改"
          cancelText="取消"
          okButtonProps={{ danger: true }}
          centered
        >
          <p style={{ fontSize: 14, color: '#595959' }}>
            当前配置有未保存的更改，是否放弃这些更改？
          </p>
        </Modal>
      </Layout>
    </ConfigProvider>
  );
}
