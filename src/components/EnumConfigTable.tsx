import { DeleteOutlined, EditOutlined, PlusOutlined, SaveOutlined } from '@ant-design/icons';
import { Button, Card, Col, Input, Popconfirm, Row, Space, Table, Typography, message } from 'antd';
import { useState } from 'react';
import type { EnumConfig, EnumItem } from '../types';

const { Title, Text } = Typography;

interface EnumConfigProps {
  enumConfigs: EnumConfig[];
  onChange: (enumConfigs: EnumConfig[]) => void;
  disabled?: boolean;
}

export default function EnumConfigTable({ enumConfigs, onChange, disabled }: EnumConfigProps) {
  const [selectedEnumIndex, setSelectedEnumIndex] = useState<number>(-1);
  const [editingEnumIndex, setEditingEnumIndex] = useState<number>(-1);
  const [editingItemIndex, setEditingItemIndex] = useState<number>(-1);

  const selectedEnum = selectedEnumIndex >= 0 ? enumConfigs[selectedEnumIndex] : null;

  const handleAddEnum = () => {
    const newEnum: EnumConfig = {
      name: `enum${enumConfigs.length}`,
      displayName: '新枚举',
      className: 'NewEnum',
      items: []
    };

    onChange([...enumConfigs, newEnum]);
    setSelectedEnumIndex(enumConfigs.length);
    setEditingEnumIndex(enumConfigs.length);
  };

  const handleUpdateEnum = () => {
    if (editingEnumIndex < 0) return;

    const newEnums = [...enumConfigs];
    const enumConfig = newEnums[editingEnumIndex];

    if (!enumConfig.name || !enumConfig.displayName || !enumConfig.className) {
      message.warning('请填写完整的枚举信息');
      return;
    }

    const isDuplicate = newEnums.some((e, i) => i !== editingEnumIndex && e.name === enumConfig.name);
    if (isDuplicate) {
      message.warning(`枚举名称 '${enumConfig.name}' 已存在`);
      return;
    }

    setEditingEnumIndex(-1);
    message.success('枚举更新成功');
  };

  const handleDeleteEnum = (index: number) => {
    const newEnums = enumConfigs.filter((_, i) => i !== index);
    onChange(newEnums);
    if (selectedEnumIndex === index) {
      setSelectedEnumIndex(-1);
    } else if (selectedEnumIndex > index) {
      setSelectedEnumIndex(selectedEnumIndex - 1);
    }
  };

  const handleEnumFieldChange = (index: number, field: keyof EnumConfig, value: string) => {
    const newEnums = [...enumConfigs];
    newEnums[index] = { ...newEnums[index], [field]: value };
    onChange(newEnums);
  };

  const handleSelectEnum = (index: number) => {
    setSelectedEnumIndex(index);
    setEditingItemIndex(-1);
  };

  const handleAddItem = () => {
    if (!selectedEnum) {
      message.warning('请先选择一个枚举配置');
      return;
    }

    const newItem: EnumItem = {
      display: '新项',
      value: '0'
    };

    const newEnums = [...enumConfigs];
    newEnums[selectedEnumIndex] = {
      ...newEnums[selectedEnumIndex],
      items: [...newEnums[selectedEnumIndex].items, newItem]
    };
    onChange(newEnums);
    setEditingItemIndex(newEnums[selectedEnumIndex].items.length - 1);
  };

  const handleUpdateItem = () => {
    if (editingItemIndex < 0 || !selectedEnum) return;

    const newEnums = [...enumConfigs];
    const item = newEnums[selectedEnumIndex].items[editingItemIndex];

    if (!item.display || !item.value) {
      message.warning('请填写完整的枚举项信息');
      return;
    }

    setEditingItemIndex(-1);
    message.success('枚举项更新成功');
  };

  const handleDeleteItem = (itemIndex: number) => {
    if (!selectedEnum) return;

    const newEnums = [...enumConfigs];
    newEnums[selectedEnumIndex] = {
      ...newEnums[selectedEnumIndex],
      items: newEnums[selectedEnumIndex].items.filter((_, i) => i !== itemIndex)
    };
    onChange(newEnums);

    if (editingItemIndex === itemIndex) {
      setEditingItemIndex(-1);
    } else if (editingItemIndex > itemIndex) {
      setEditingItemIndex(editingItemIndex - 1);
    }
  };

  const handleItemFieldChange = (itemIndex: number, field: keyof EnumItem, value: string) => {
    if (!selectedEnum) return;

    const newEnums = [...enumConfigs];
    const newItems = [...newEnums[selectedEnumIndex].items];
    newItems[itemIndex] = { ...newItems[itemIndex], [field]: value };
    newEnums[selectedEnumIndex] = {
      ...newEnums[selectedEnumIndex],
      items: newItems
    };
    onChange(newEnums);
  };

  const enumColumns: ColumnType<EnumConfig>[] = [
    {
      title: '枚举名称',
      dataIndex: 'name',
      width: 120,
      render: (text: any, record: any, index: number) => {
        const isEditing = editingEnumIndex === index;
        return isEditing ? (
          <Input
            defaultValue={text}
            onPressEnter={handleUpdateEnum}
            onBlur={(e) => handleEnumFieldChange(index, 'name', e.target.value)}
            autoFocus
          />
        ) : (
          <span style={{ fontWeight: selectedEnumIndex === index ? 'bold' : 'normal' }}>{text}</span>
        );
      }
    },
    {
      title: '显示名称',
      dataIndex: 'displayName',
      width: 150,
      render: (text: any, _record: any, index: number) => {
        const isEditing = editingEnumIndex === index;
        return isEditing ? (
          <Input
            defaultValue={text}
            onPressEnter={handleUpdateEnum}
            onBlur={(e) => handleEnumFieldChange(index, 'displayName', e.target.value)}
          />
        ) : (
          text
        );
      }
    },
    {
      title: '类名',
      dataIndex: 'className',
      width: 150,
      render: (text: any, _record: any, index: number) => {
        const isEditing = editingEnumIndex === index;
        return isEditing ? (
          <Input
            defaultValue={text}
            onPressEnter={handleUpdateEnum}
            onBlur={(e) => handleEnumFieldChange(index, 'className', e.target.value)}
          />
        ) : (
          <span style={{ fontFamily: 'monospace' }}>{text}</span>
        );
      }
    },
    {
      title: '项数',
      dataIndex: 'items',
      width: 80,
      render: (items: EnumItem[]) => items.length
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      render: (_: any, record: EnumConfig, index: number) => (
        <Space size="small">
          {editingEnumIndex === index ? (
            <>
              <Button
                type="link"
                size="small"
                icon={<SaveOutlined />}
                onClick={handleUpdateEnum}
              >
                保存
              </Button>
              <Button
                type="link"
                size="small"
                onClick={() => setEditingEnumIndex(-1)}
              >
                取消
              </Button>
            </>
          ) : (
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => {
                setEditingEnumIndex(index);
                setSelectedEnumIndex(index);
              }}
              disabled={disabled}
            >
              编辑
            </Button>
          )}
          <Popconfirm
            title="确认删除"
            description="确定要删除这个枚举配置吗？"
            onConfirm={() => handleDeleteEnum(index)}
            okText="确定"
            cancelText="取消"
          >
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
              disabled={disabled}
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  const itemColumns: ColumnType<EnumItem>[] = [
    {
      title: '显示文本',
      dataIndex: 'display',
      width: 150,
      render: (text: any, _record: any, index: number) => {
        const isEditing = editingItemIndex === index;
        return isEditing ? (
          <Input
            defaultValue={text}
            onPressEnter={handleUpdateItem}
            onBlur={(e) => handleItemFieldChange(index, 'display', e.target.value)}
            autoFocus
          />
        ) : (
          text
        );
      }
    },
    {
      title: '值',
      dataIndex: 'value',
      width: 120,
      render: (text: any, _record: any, index: number) => {
        const isEditing = editingItemIndex === index;
        return isEditing ? (
          <Input
            defaultValue={text}
            onPressEnter={handleUpdateItem}
            onBlur={(e) => handleItemFieldChange(index, 'value', e.target.value)}
          />
        ) : (
          <span style={{ fontFamily: 'monospace' }}>{text}</span>
        );
      }
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      render: (_: any, record: EnumItem, index: number) => (
        <Space size="small">
          {editingItemIndex === index ? (
            <>
              <Button
                type="link"
                size="small"
                icon={<SaveOutlined />}
                onClick={handleUpdateItem}
              >
                保存
              </Button>
              <Button
                type="link"
                size="small"
                onClick={() => setEditingItemIndex(-1)}
              >
                取消
              </Button>
            </>
          ) : (
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => setEditingItemIndex(index)}
              disabled={disabled}
            >
              编辑
            </Button>
          )}
          <Popconfirm
            title="确认删除"
            description="确定要删除这个枚举项吗？"
            onConfirm={() => handleDeleteItem(index)}
            okText="确定"
            cancelText="取消"
          >
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
              disabled={disabled}
            >
              删除
            </Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <Row gutter={[16, 16]}>
      <Col xs={24} md={12}>
        <Card
          title={
            <Space>
              <Title level={5} style={{ margin: 0 }}>枚举配置</Title>
              <Text type="secondary">({enumConfigs.length})</Text>
            </Space>
          }
          extra={
            <Button
              type="primary"
              size="small"
              icon={<PlusOutlined />}
              onClick={handleAddEnum}
              disabled={disabled}
            >
              添加枚举
            </Button>
          }
        >
          <Table
            columns={enumColumns}
            dataSource={enumConfigs}
            rowKey={(_record, index) => index || 0}
            pagination={false}
            size="small"
            onRow={(_record, index) => ({
              onClick: () => index !== undefined && handleSelectEnum(index),
              style: {
                background: selectedEnumIndex === index ? '#e6f7ff' : undefined,
                cursor: 'pointer'
              }
            })}
          />
        </Card>
      </Col>

      <Col xs={24} md={12}>
        <Card
          title={
            <Space>
              <Title level={5} style={{ margin: 0 }}>枚举项</Title>
              {selectedEnum && (
                <Text type="secondary">({selectedEnum.displayName} - {selectedEnum.items.length} 项)</Text>
              )}
            </Space>
          }
          extra={
            <Button
              type="primary"
              size="small"
              icon={<PlusOutlined />}
              onClick={handleAddItem}
              disabled={disabled || !selectedEnum}
            >
              添加项
            </Button>
          }
        >
          {selectedEnum ? (
            <Table
              columns={itemColumns}
              dataSource={selectedEnum.items}
              rowKey={(_record, index) => index || 0}
              pagination={false}
              size="small"
            />
          ) : (
            <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>
              请从左侧选择一个枚举配置
            </div>
          )}
        </Card>
      </Col>
    </Row>
  );
}
