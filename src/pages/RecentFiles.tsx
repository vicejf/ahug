import {
  ClearOutlined,
  DeleteOutlined,
  FolderOpenOutlined
} from '@ant-design/icons';
import { Button, Card, Col, Divider, Empty, Flex, Row, Space, Tag, Tooltip, Typography } from 'antd';
import { useState } from 'react';

const { Title, Text } = Typography;

interface FileRecord {
  id: number;
  name: string;
  path: string;
  lastModified: string;
  size: string;
  type: 'json' | 'xml' | 'folder';
}

export default function RecentFiles() {
  const [recentFiles, setRecentFiles] = useState<FileRecord[]>([
    {
      id: 1,
      name: 'AUJX Configuration',
      path: 'config/aujx/AUJX.json',
      lastModified: '2026-02-08 14:30:22',
      size: '2.4 KB',
      type: 'json'
    },
    {
      id: 2,
      name: 'AUJY Configuration',
      path: 'config/aujy/AUJY.json',
      lastModified: '2026-02-07 09:15:45',
      size: '3.1 KB',
      type: 'json'
    },
    {
      id: 3,
      name: 'AUJZ Configuration',
      path: 'config/aujz/AUJZ.json',
      lastModified: '2026-02-06 16:42:18',
      size: '2.8 KB',
      type: 'json'
    },
    {
      id: 4,
      name: 'Test Configuration',
      path: 'config/test/TEST.json',
      lastModified: '2026-02-05 11:20:33',
      size: '1.9 KB',
      type: 'json'
    },
    {
      id: 5,
      name: 'Sample Configuration',
      path: 'config/sample/SAMPLE.xml',
      lastModified: '2026-02-04 13:55:12',
      size: '4.2 KB',
      type: 'xml'
    }
  ]);

  const [favoriteFiles, setFavoriteFiles] = useState<FileRecord[]>([
    {
      id: 6,
      name: 'Production Bills',
      path: 'config/prod/',
      lastModified: '2026-02-08 10:15:30',
      size: 'Folder',
      type: 'folder'
    }
  ]);

  const handleOpenFile = (filePath: string) => {
    console.log('Opening file:', filePath);
    window.location.assign('#/config');
  };

  const handleDeleteFile = (fileId: number, isFavorite: boolean = false) => {
    if (isFavorite) {
      setFavoriteFiles(favoriteFiles.filter(file => file.id !== fileId));
    } else {
      setRecentFiles(recentFiles.filter(file => file.id !== fileId));
    }
  };

  const handleClearAll = () => {
    setRecentFiles([]);
  };

  const getFileIcon = (type: string) => {
    switch(type) {
      case 'json': return '📄';
      case 'xml': return '📋';
      case 'folder': return '📁';
      default: return '📄';
    }
  };

  const getFileColor = (type: string) => {
    switch(type) {
      case 'json': return 'blue';
      case 'xml': return 'green';
      case 'folder': return 'orange';
      default: return 'default';
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0 }}>Recent Files</Title>
        <Button
          icon={<ClearOutlined />}
          onClick={handleClearAll}
          disabled={recentFiles.length === 0}
        >
          Clear All
        </Button>
      </div>

      <Row gutter={[16, 16]}>
        <Col xs={24} md={16}>
          <Card title="Recently Opened">
            {recentFiles.length === 0 ? (
              <Empty
                description={
                  <Flex vertical gap="small">
                    <Text type="secondary">No recent files</Text>
                    <Text type="secondary">Your recently opened files will appear here</Text>
                  </Flex>
                }
                style={{ padding: 48 }}
              />
            ) : (
              <Flex vertical gap="small">
                {recentFiles.map((file) => (
                  <Card key={file.id} size="small" variant="outlined">
                    <Flex justify="space-between" align="center">
                      <Flex gap="middle" align="center">
                        <span style={{ fontSize: '24px' }}>{getFileIcon(file.type)}</span>
                        <div>
                          <Space>
                            <Text strong>{file.name}</Text>
                            <Tag color={getFileColor(file.type)}>{file.type.toUpperCase()}</Tag>
                          </Space>
                          <br />
                          <Text type="secondary">{file.path}</Text>
                          <Divider type="vertical" />
                          <Tag>{file.size}</Tag>
                          <Divider type="vertical" />
                          <Tag>Modified: {formatDate(file.lastModified)}</Tag>
                        </div>
                      </Flex>
                      <Space>
                        <Tooltip title="Open">
                          <Button
                            type="text"
                            icon={<FolderOpenOutlined />}
                            onClick={() => handleOpenFile(file.path)}
                          />
                        </Tooltip>
                        <Tooltip title="Delete">
                          <Button
                            type="text"
                            danger
                            icon={<DeleteOutlined />}
                            onClick={() => handleDeleteFile(file.id)}
                          />
                        </Tooltip>
                      </Space>
                    </Flex>
                  </Card>
                ))}
              </Flex>
            )}
          </Card>
        </Col>

        <Col xs={24} md={8}>
          <Card title="Favorites">
            {favoriteFiles.length === 0 ? (
              <Empty
                description={
                  <Flex vertical gap="small">
                    <Text type="secondary">No favorite files</Text>
                    <Text type="secondary">Add frequently used files to favorites</Text>
                  </Flex>
                }
                style={{ padding: 48 }}
              />
            ) : (
              <Flex vertical gap="small">
                {favoriteFiles.map((file) => (
                  <Card key={file.id} size="small" variant="outlined">
                    <Flex justify="space-between" align="center">
                      <Flex gap="middle" align="center">
                        <span style={{ fontSize: '24px' }}>{getFileIcon(file.type)}</span>
                        <div>
                          <Text strong>{file.name}</Text>
                          <br />
                          <Text type="secondary">{file.path}</Text>
                          <br />
                          <Tag>{file.size}</Tag>
                        </div>
                      </Flex>
                      <Space>
                        <Tooltip title="Open">
                          <Button
                            type="text"
                            icon={<FolderOpenOutlined />}
                            onClick={() => handleOpenFile(file.path)}
                          />
                        </Tooltip>
                        <Tooltip title="Delete">
                          <Button
                            type="text"
                            danger
                            icon={<DeleteOutlined />}
                            onClick={() => handleDeleteFile(file.id, true)}
                          />
                        </Tooltip>
                      </Space>
                    </Flex>
                  </Card>
                ))}
              </Flex>
            )}

            <Divider />

            <Title level={5}>Tips:</Title>
            <ul style={{ paddingLeft: 20, margin: 0 }}>
              <li>Right-click files to add to favorites</li>
              <li>Quick access to frequently used configurations</li>
              <li>Recent files are automatically tracked</li>
              <li>Last 50 files are kept in history</li>
            </ul>
          </Card>
        </Col>
      </Row>
    </div>
  );
}
