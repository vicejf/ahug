import { useState, useEffect } from 'react';
import { Card, Row, Col, Tree, Button, Typography, Space, Input, Empty, message, Popconfirm, Modal, Tag } from 'antd';
import {
  ReloadOutlined,
  CodeOutlined,
  FolderOutlined,
  FileOutlined,
  DeleteOutlined,
  ExpandOutlined,
  CompressOutlined,
  CopyOutlined,
  FolderOpenOutlined
} from '@ant-design/icons';
import FileService from '../services/FileService';
import ConfigManager from '../services/ConfigManager';

const { Title, Text } = Typography;

interface FileNode {
  key: string;
  title: string;
  icon?: React.ReactNode;
  children?: FileNode[];
  isLeaf?: boolean;
  relativePath?: string;
}

export default function CodePreview() {
  const [selectedFile, setSelectedFile] = useState<string | null>(null);
  const [expandedKeys, setExpandedKeys] = useState<React.Key[]>([]);
  const [fileTree, setFileTree] = useState<FileNode[]>([]);
  const [fileContent, setFileContent] = useState<string>('');
  const [outputDir, setOutputDir] = useState<string>('./output');
  const [loading, setLoading] = useState(false);
  const [deleteModalVisible, setDeleteModalVisible] = useState(false);
  const [fileToDelete, setFileToDelete] = useState<string | null>(null);

  useEffect(() => {
    loadOutputDirFromConfig();
    refreshFileTree();
  }, []);

  const loadOutputDirFromConfig = () => {
    const globalConfig = ConfigManager.getCurrentConfigPath();
    if (globalConfig) {
      setOutputDir('./output');
    }
  };

  const refreshFileTree = async () => {
    setLoading(true);
    try {
      const result = await FileService.listDirectory(outputDir);
      if (result.success && result.files) {
        const tree = buildFileTree(result.files, '');
        setFileTree(tree);
        if (tree.length > 0 && expandedKeys.length === 0) {
          const firstLevelKeys = tree.map(node => node.key);
          setExpandedKeys(firstLevelKeys);
        }
      } else {
        message.error('读取文件列表失败: ' + (result.error || '未知错误'));
      }
    } catch (error) {
      message.error('读取文件列表失败');
    } finally {
      setLoading(false);
    }
  };

  const buildFileTree = (files: Array<{ name: string; isDirectory: boolean; size: number }>, parentPath: string): FileNode[] => {
    const tree: FileNode[] = [];
    const directories: { [key: string]: FileNode } = {};
    const fileNodes: FileNode[] = [];

    files.forEach(file => {
      const relativePath = parentPath ? `${parentPath}/${file.name}` : file.name;
      const key = relativePath;

      if (file.isDirectory) {
        const node: FileNode = {
          key,
          title: file.name,
          icon: <FolderOutlined />,
          children: [],
          isLeaf: false
        };
        directories[key] = node;
        tree.push(node);
      } else if (file.name.endsWith('.java')) {
        const node: FileNode = {
          key,
          title: file.name,
          icon: <FileOutlined />,
          isLeaf: true,
          relativePath
        };
        fileNodes.push(node);
      }
    });

    fileNodes.forEach(fileNode => {
      const pathParts = fileNode.relativePath!.split('/');
      let currentLevel = tree;
      let currentPath = '';

      for (let i = 0; i < pathParts.length - 1; i++) {
        currentPath = currentPath ? `${currentPath}/${pathParts[i]}` : pathParts[i];
        const dirNode = currentLevel.find(node => node.key === currentPath);
        if (dirNode && dirNode.children) {
          currentLevel = dirNode.children;
        }
      }

      currentLevel.push(fileNode);
    });

    return tree;
  };

  const handleSelect = (_selectedKeys: React.Key[], info: any) => {
    const node = info.node;
    if (node.isLeaf && node.relativePath) {
      setSelectedFile(node.title as string);
      loadFileContent(node.relativePath);
    }
  };

  const loadFileContent = async (relativePath: string) => {
    setLoading(true);
    try {
      const fullPath = `${outputDir}/${relativePath}`;
      const result = await FileService.readFile(fullPath);
      if (result.success && result.content) {
        setFileContent(result.content);
      } else {
        message.error('读取文件失败: ' + (result.error || '未知错误'));
        setFileContent('');
      }
    } catch (error) {
      message.error('读取文件失败');
      setFileContent('');
    } finally {
      setLoading(false);
    }
  };

  const handleExpand = (expandedKeys: React.Key[]) => {
    setExpandedKeys(expandedKeys);
  };

  const handleExpandAll = () => {
    const allKeys: React.Key[] = [];
    const collectKeys = (nodes: FileNode[]) => {
      nodes.forEach(node => {
        allKeys.push(node.key);
        if (node.children) {
          collectKeys(node.children);
        }
      });
    };
    collectKeys(fileTree);
    setExpandedKeys(allKeys);
  };

  const handleCollapseAll = () => {
    setExpandedKeys([]);
  };

  const handleCopyCode = () => {
    if (fileContent) {
      navigator.clipboard.writeText(fileContent).then(() => {
        message.success('代码已复制到剪贴板');
      }).catch(() => {
        message.error('复制失败');
      });
    }
  };

  const handleOpenDir = () => {
    if (!selectedFile) {
      message.warning('请先选择一个文件');
      return;
    }

    const selectedNode = findNodeByTitle(fileTree, selectedFile);
    if (selectedNode && selectedNode.relativePath) {
      const parts = selectedNode.relativePath.split('/');
      parts.pop();
      const dirPath = parts.length > 0 ? `${outputDir}/${parts.join('/')}` : outputDir;

      message.info('打开目录: ' + dirPath);
    }
  };

  const findNodeByTitle = (nodes: FileNode[], title: string): FileNode | null => {
    for (const node of nodes) {
      if (node.title === title) {
        return node;
      }
      if (node.children) {
        const found = findNodeByTitle(node.children, title);
        if (found) return found;
      }
    }
    return null;
  };

  const handleDeleteFile = () => {
    if (!fileToDelete) return;

    const fullPath = `${outputDir}/${fileToDelete}`;
    FileService.writeFile(fullPath, '').then(() => {
      message.success('文件已删除');
      setFileContent('');
      setSelectedFile(null);
      setDeleteModalVisible(false);
      setFileToDelete(null);
      refreshFileTree();
    }).catch(() => {
      message.error('删除文件失败');
    });
  };

  const handleDeleteClick = () => {
    if (!selectedFile) {
      message.warning('请先选择一个文件');
      return;
    }

    const selectedNode = findNodeByTitle(fileTree, selectedFile);
    if (selectedNode && selectedNode.relativePath) {
      setFileToDelete(selectedNode.relativePath);
      setDeleteModalVisible(true);
    }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <Title level={2} style={{ margin: 0 }}>代码预览</Title>
        <Space>
          <Text type="secondary">输出目录: {outputDir}</Text>
          <Button icon={<ReloadOutlined />} onClick={refreshFileTree} loading={loading}>
            刷新
          </Button>
        </Space>
      </div>

      <Row gutter={[16, 16]}>
        <Col xs={24} md={8}>
          <Card
            title={
              <Space>
                <FolderOutlined />
                <span>文件结构</span>
              </Space>
            }
            extra={
              <Space>
                <Button size="small" icon={<ExpandOutlined />} onClick={handleExpandAll}>
                  展开
                </Button>
                <Button size="small" icon={<CompressOutlined />} onClick={handleCollapseAll}>
                  收起
                </Button>
              </Space>
            }
          >
            <div style={{ marginBottom: 12 }}>
              <Input.Search
                placeholder="搜索文件..."
                size="small"
                onSearch={(value) => {
                  if (value) {
                    const allKeys: React.Key[] = [];
                    const searchNodes = (nodes: FileNode[]) => {
                      nodes.forEach(node => {
                        if (node.title.toLowerCase().includes(value.toLowerCase())) {
                          allKeys.push(node.key);
                        }
                        if (node.children) {
                          searchNodes(node.children);
                        }
                      });
                    };
                    searchNodes(fileTree);
                    setExpandedKeys(allKeys);
                  }
                }}
              />
            </div>
            <div style={{ maxHeight: 600, overflow: 'auto' }}>
              {fileTree.length > 0 ? (
                <Tree
                  showIcon
                  expandedKeys={expandedKeys}
                  selectedKeys={selectedFile ? [selectedFile] : []}
                  treeData={fileTree}
                  onSelect={handleSelect}
                  onExpand={handleExpand}
                />
              ) : (
                <Empty
                  description="暂无生成的文件"
                  style={{ padding: 40 }}
                />
              )}
            </div>
          </Card>
        </Col>

        <Col xs={24} md={16}>
          <Card
            title={
              <Space>
                <CodeOutlined />
                <span>{selectedFile || '选择文件以预览'}</span>
                {selectedFile && <Tag color="blue">.java</Tag>}
              </Space>
            }
            extra={
              <Space>
                <Button
                  size="small"
                  icon={<CopyOutlined />}
                  onClick={handleCopyCode}
                  disabled={!selectedFile}
                >
                  复制
                </Button>
                <Button
                  size="small"
                  icon={<FolderOpenOutlined />}
                  onClick={handleOpenDir}
                  disabled={!selectedFile}
                >
                  打开目录
                </Button>
                <Popconfirm
                  title="确认删除"
                  description="确定要删除这个文件吗？"
                  onConfirm={handleDeleteClick}
                  okText="确定"
                  cancelText="取消"
                  disabled={!selectedFile}
                >
                  <Button
                    size="small"
                    danger
                    icon={<DeleteOutlined />}
                    disabled={!selectedFile}
                  >
                    删除
                  </Button>
                </Popconfirm>
              </Space>
            }
          >
            {selectedFile ? (
              <>
                <div style={{
                  backgroundColor: '#1e1e1e',
                  color: '#d4d4d4',
                  padding: 16,
                  borderRadius: 4,
                  fontFamily: 'Consolas, Monaco, "Courier New", monospace',
                  fontSize: '13px',
                  maxHeight: 500,
                  overflow: 'auto',
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-all'
                }}>
                  {fileContent || <Text type="secondary">文件内容为空</Text>}
                </div>
                <div style={{ marginTop: 12, fontSize: 12, color: '#999' }}>
                  <Space>
                    <span>文件: {selectedFile}</span>
                    <span>编码: GBK</span>
                    <span>大小: {fileContent.length} 字节</span>
                  </Space>
                </div>
              </>
            ) : (
              <Empty
                description="从左侧文件树选择一个文件以查看内容"
                style={{ padding: 48 }}
              />
            )}
          </Card>
        </Col>
      </Row>

      <Modal
        title="确认删除"
        open={deleteModalVisible}
        onOk={handleDeleteFile}
        onCancel={() => {
          setDeleteModalVisible(false);
          setFileToDelete(null);
        }}
        okText="确定"
        cancelText="取消"
        okButtonProps={{ danger: true }}
      >
        <p>确定要删除文件 <strong>{fileToDelete}</strong> 吗？</p>
        <p style={{ color: '#ff4d4f' }}>此操作不可恢复！</p>
      </Modal>
    </div>
  );
}
