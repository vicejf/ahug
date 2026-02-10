import {
  CodeOutlined,
  FileOutlined,
  FolderOpenOutlined,
  HistoryOutlined,
  PlayCircleOutlined,
  PlusOutlined
} from '@ant-design/icons';
import { Button, Card, Col, Dropdown, Flex, type MenuProps, Row, Space, Statistic, Tag, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ConfigManager from '../services/ConfigManager';

const { Title, Text, Paragraph } = Typography;

interface RecentFile {
  name: string;
  path: string;
  lastModified: string;
}

interface QuickAction {
  title: string;
  description: string;
  icon: React.ReactNode;
  color: string;
  action: string;
}

interface Statistics {
  configFiles: number;
  generationCount: number;
  lastGenerated: string;
}

export default function Dashboard() {
  const navigate = useNavigate();
  const [recentFiles, setRecentFiles] = useState<RecentFile[]>([]);
  const [statistics, setStatistics] = useState<Statistics>({
    configFiles: 0,
    generationCount: 0,
    lastGenerated: '从未生成'
  });

  useEffect(() => {
    loadRecentFiles();
    loadStatistics();
  }, []);

  const loadRecentFiles = () => {
    const files = ConfigManager.getRecentFiles();
    setRecentFiles(files.slice(0, 5));
  };

  const loadStatistics = () => {
    const stats = ConfigManager.loadStatistics();
    setStatistics(stats);
  };

  const quickActions: QuickAction[] = [
    {
      title: '新建配置',
      description: '创建新的单据配置',
      icon: <PlusOutlined />,
      color: '#1677ff',
      action: '/config'
    },
    {
      title: '打开配置',
      description: '打开现有配置文件',
      icon: <FolderOpenOutlined />,
      color: '#52c41a',
      action: '/config'
    },
    {
      title: '生成代码',
      description: '从当前配置生成代码',
      icon: <PlayCircleOutlined />,
      color: '#722ed1',
      action: '/generate'
    },
    {
      title: '代码预览',
      description: '预览生成的代码文件',
      icon: <CodeOutlined />,
      color: '#fa8c16',
      action: '/preview'
    }
  ];

  const recentFilesMenuItems: MenuProps['items'] = recentFiles.map((file, index) => ({
    key: index.toString(),
    label: (
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <Text strong>{file.name}</Text>
          <div style={{ marginTop: 4 }}>
            <Text type="secondary" style={{ fontSize: 12 }}>{file.path}</Text>
          </div>
        </div>
        <Tag color="blue" style={{ fontSize: 12 }}>
          {file.lastModified}
        </Tag>
      </div>
    ),
    onClick: () => handleOpenRecentFile(file)
  }));

  const handleOpenRecentFile = (file: RecentFile) => {
    console.log('Opening recent file:', file.path);
    navigate('/config');
  };

  const handleViewAllRecentFiles = () => {
    navigate('/recent');
  };

  return (
    <div>
      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        {quickActions.map((action, index) => (
          <Col xs={24} sm={12} md={6} key={index}>
            <Card
              hoverable
              onClick={() => navigate(action.action)}
              styles={{ body: { padding: 24 } }}
            >
              <div style={{ textAlign: 'center' }}>
                <div style={{
                  width: 64,
                  height: 64,
                  borderRadius: '50%',
                  backgroundColor: `${action.color}15`,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  margin: '0 auto 16px',
                  color: action.color,
                  fontSize: 28,
                  transition: 'all 0.3s ease',
                }}>
                  {action.icon}
                </div>
                <Title level={5} style={{ margin: '0 0 8px' }}>{action.title}</Title>
                <Text type="secondary" style={{ fontSize: 13 }}>
                  {action.description}
                </Text>
              </div>
            </Card>
          </Col>
        ))}
      </Row>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="配置文件"
              value={statistics.configFiles}
              prefix={<FileOutlined style={{ color: '#1677ff' }} />}
              suffix="个"
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="生成次数"
              value={statistics.generationCount}
              prefix={<CodeOutlined style={{ color: '#52c41a' }} />}
              suffix="次"
            />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card>
            <Statistic
              title="最近生成"
              value={statistics.lastGenerated}
              prefix={<PlayCircleOutlined style={{ color: '#722ed1' }} />}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} md={16}>
          <Card title="欢迎使用 Ahug 代码生成器">
            <Paragraph>
              这是一个用于生成 NC5 单据代码的工具。通过配置文件定义单据结构、字段和生成选项，
              然后生成完整的代码包，可直接用于 NC5 项目。
            </Paragraph>
            <Title level={5}>核心特性</Title>
            <ul style={{ paddingLeft: 20, margin: 0 }}>
              <li><Text>可视化配置编辑器 - 通过直观的界面轻松配置单据信息、字段和枚举</Text></li>
              <li><Text>基于模板生成 - 使用 Velocity 模板生成一致且结构良好的代码</Text></li>
              <li><Text>多目标支持 - 生成 VO 层、客户端 UI、业务逻辑和元数据代码</Text></li>
              <li><Text>GBK 编码 - 生成的文件使用 GBK 编码，完美兼容 NC5</Text></li>
              <li><Text>多表体支持 - 支持单表体和多表体单据类型</Text></li>
              <li><Text>枚举配置 - 灵活的枚举类型配置，支持自定义枚举项</Text></li>
            </ul>
          </Card>
        </Col>

        <Col xs={24} md={8}>
          <Card
            title={
              <Space>
                <HistoryOutlined />
                <span>最近文件</span>
              </Space>
            }
            extra={
              <Button
                type="link"
                size="small"
                onClick={handleViewAllRecentFiles}
              >
                查看全部
              </Button>
            }
          >
            {recentFiles.length > 0 ? (
              <Dropdown menu={{ items: recentFilesMenuItems }} trigger={['click']} placement="bottomLeft">
                <div style={{ cursor: 'pointer' }}>
                  {recentFiles.map((file, index) => (
                    <div
                      key={index}
                      style={{
                        padding: 12,
                        border: '1px solid #f0f0f0',
                        borderRadius: 8,
                        cursor: 'pointer',
                        transition: 'all 0.3s',
                        marginBottom: index < recentFiles.length - 1 ? 8 : 0
                      }}
                      onClick={() => handleOpenRecentFile(file)}
                    >
                      <Flex justify="space-between" align="center">
                        <div>
                          <Text strong>{file.name}</Text>
                          <div style={{ marginTop: 4 }}>
                            <Text type="secondary" style={{ fontSize: 12 }}>{file.path}</Text>
                          </div>
                          <Tag color="blue" style={{ marginTop: 8, fontSize: 12 }}>
                            {file.lastModified}
                          </Tag>
                        </div>
                        <Button
                          type="text"
                          icon={<FolderOpenOutlined />}
                          onClick={(e) => {
                            e.stopPropagation();
                            handleOpenRecentFile(file);
                          }}
                        />
                      </Flex>
                    </div>
                  ))}
                </div>
              </Dropdown>
            ) : (
              <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>
                <FileOutlined style={{ fontSize: 48, marginBottom: 16 }} />
                <div>暂无最近文件</div>
                <Button
                  type="primary"
                  size="small"
                  style={{ marginTop: 16 }}
                  onClick={() => navigate('/config')}
                >
                  创建配置
                </Button>
              </div>
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
}
