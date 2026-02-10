import { ArrowDownOutlined, ArrowUpOutlined, DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import { Button, Checkbox, Input, InputNumber, message, Popconfirm, Select, Space, Table, Tag } from 'antd';
import type { ColumnType } from 'antd/es/table';
import { useEffect, useState } from 'react';
import type { FieldConfig } from '../types';

const { Option } = Select;

const FIELD_TYPES = ['', 'UFID', 'String', 'Integer', 'UFDouble', 'UFDate', 'UFDateTime', 'UFBoolean', 'CUSTOM'];

interface HeadFieldsTableProps {
  fields: FieldConfig[];
  enumConfigs: Array<{ name: string; displayName: string; className: string }>;
  onChange: (fields: FieldConfig[]) => void;
  disabled?: boolean;
}

export default function HeadFieldsTable({ fields, enumConfigs, onChange, disabled }: HeadFieldsTableProps) {
  const [editingKey, setEditingKey] = useState<string>('');
  const [fieldCount, setFieldCount] = useState(fields.length);

  useEffect(() => {
    setFieldCount(fields.length);
  }, [fields]);

  const isEditing = (record: FieldConfig) => record.name === editingKey;

  const getFieldTypeOptions = () => {
    const options = [...FIELD_TYPES];
    enumConfigs.forEach(enumConfig => {
      options.push(`enum:${enumConfig.name}`);
    });
    return options;
  };

  const updateDbTypeWithLength = (field: FieldConfig, newLength: number) => {
    const currentDbType = field.dbType;
    if (!currentDbType) return;

    const upperDbType = currentDbType.toUpperCase();

    if (upperDbType.startsWith('VARCHAR2(')) {
      return `VARCHAR2(${newLength})`;
    } else if (upperDbType.startsWith('VARCHAR(')) {
      return `VARCHAR(${newLength})`;
    } else if (upperDbType.startsWith('CHAR(')) {
      return `CHAR(${newLength})`;
    } else if (upperDbType.startsWith('NVARCHAR2(')) {
      return `NVARCHAR2(${newLength})`;
    }
    return currentDbType;
  };

  const handleAdd = () => {
    const newField: FieldConfig = {
      name: `newField${fields.length}`,
      label: '新字段',
      type: 'String',
      dbType: 'VARCHAR2(50)',
      length: 50,
      required: false,
      editable: true,
      primaryKey: false,
      uiType: 'Text'
    };

    const isDuplicate = fields.some(f => f.name === newField.name);
    if (isDuplicate) {
      message.warning('字段名已存在，请使用不同的名称');
      return;
    }

    onChange([...fields, newField]);
    setEditingKey(newField.name);
  };

  const handleDelete = (record: FieldConfig) => {
    onChange(fields.filter(item => item.name !== record.name));
  };

  const handleMoveUp = (index: number) => {
    if (index > 0) {
      const newFields = [...fields];
      [newFields[index - 1], newFields[index]] = [newFields[index], newFields[index - 1]];
      onChange(newFields);
    }
  };

  const handleMoveDown = (index: number) => {
    if (index < fields.length - 1) {
      const newFields = [...fields];
      [newFields[index], newFields[index + 1]] = [newFields[index + 1], newFields[index]];
      onChange(newFields);
    }
  };

  const handleEdit = (record: FieldConfig) => {
    setEditingKey(record.name);
  };

  const handleSave = () => {
    setEditingKey('');
  };

  const handleCancel = () => {
    setEditingKey('');
  };

  const handleFieldChange = (index: number, field: keyof FieldConfig, value: unknown) => {
    const newFields = [...fields];
    const updatedField = { ...newFields[index], [field]: value };

    if (field === 'length' && typeof value === 'number') {
      updatedField.dbType = updateDbTypeWithLength(updatedField, value) || '';
    }

    newFields[index] = updatedField;
    onChange(newFields);
  };

  const handleNameChange = (index: number, newName: string) => {
    const newFields = [...fields];

    if (!newName || newName.trim() === '') {
      message.warning('字段名不能为空');
      return;
    }

    const isDuplicate = newFields.some((f, i) => i !== index && f.name === newName);
    if (isDuplicate) {
      message.warning(`字段名 '${newName}' 已存在，请使用不同的名称`);
      return;
    }

    newFields[index].name = newName;
    onChange(newFields);
  };

  const columns: ColumnType<FieldConfig>[] = [
    {
      title: '字段名',
      dataIndex: 'name',
      width: 150,
      render: (text: string, record: FieldConfig, index: number) => {
        const editable = isEditing(record);
        return editable ? (
          <Input
            defaultValue={text}
            onPressEnter={() => handleSave()}
            onBlur={(e) => handleNameChange(index, e.target.value)}
            autoFocus
          />
        ) : (
          <span style={{ fontWeight: record.primaryKey ? 'bold' : 'normal' }}>
            {record.primaryKey && <Tag color="blue" style={{ marginRight: 4 }}>PK</Tag>}
            {text}
          </span>
        );
      }
    },
    {
      title: '中文名',
      dataIndex: 'label',
      width: 120,
      render: (text: string, record: FieldConfig, index: number) => {
        const editable = isEditing(record);
        return editable ? (
          <Input
            defaultValue={text}
            onPressEnter={() => handleSave()}
            onBlur={(e) => handleFieldChange(index, 'label', e.target.value)}
          />
        ) : (
          text
        );
      }
    },
    {
      title: '字段类型',
      dataIndex: 'type',
      width: 130,
      render: (text: string, record: FieldConfig, index: number) => {
        const editable = isEditing(record);
        return editable ? (
          <Select
            defaultValue={text}
            style={{ width: '100%' }}
            onChange={(value) => handleFieldChange(index, 'type', value)}
          >
            {getFieldTypeOptions().map(type => (
              <Option key={type} value={type}>{type}</Option>
            ))}
          </Select>
        ) : (
          <Tag color={text.startsWith('enum:') ? 'purple' : 'default'}>{text}</Tag>
        );
      }
    },
    {
      title: '数据库类型',
      dataIndex: 'dbType',
      width: 150,
      render: (text: string, record: FieldConfig, index: number) => {
        const editable = isEditing(record);
        return editable ? (
          <Input
            defaultValue={text}
            onPressEnter={() => handleSave()}
            onBlur={(e) => handleFieldChange(index, 'dbType', e.target.value)}
          />
        ) : (
          <span style={{ fontFamily: 'monospace' }}>{text}</span>
        );
      }
    },
    {
      title: '长度',
      dataIndex: 'length',
      width: 100,
      render: (text: number, record: FieldConfig, index: number) => {
        const editable = isEditing(record);
        return editable ? (
          <InputNumber
            defaultValue={text}
            min={0}
            style={{ width: '100%' }}
            onChange={(value) => handleFieldChange(index, 'length', value)}
          />
        ) : (
          text
        );
      }
    },
    {
      title: '必填',
      dataIndex: 'required',
      width: 80,
      render: (text: boolean, _record: FieldConfig, index: number) => (
        <Checkbox
          checked={text}
          onChange={(e) => handleFieldChange(index, 'required', e.target.checked)}
          disabled={disabled}
        />
      )
    },
    {
      title: '可编辑',
      dataIndex: 'editable',
      width: 80,
      render: (text: boolean, _record: FieldConfig, index: number) => (
        <Checkbox
          checked={text}
          onChange={(e) => handleFieldChange(index, 'editable', e.target.checked)}
          disabled={disabled}
        />
      )
    },
    {
      title: '主键',
      dataIndex: 'primaryKey',
      width: 80,
      render: (text: boolean, _record: FieldConfig, index: number) => (
        <Checkbox
          checked={text}
          onChange={(e) => handleFieldChange(index, 'primaryKey', e.target.checked)}
          disabled={disabled}
        />
      )
    },
    {
      title: 'UI类型',
      dataIndex: 'uiType',
      width: 120,
      render: (text: string, record: FieldConfig, index: number) => {
        const editable = isEditing(record);
        return editable ? (
          <Select
            defaultValue={text}
            style={{ width: '100%' }}
            onChange={(value) => handleFieldChange(index, 'uiType', value)}
          >
            <Option value="Text">Text</Option>
            <Option value="Number">Number</Option>
            <Option value="Date">Date</Option>
            <Option value="DateTime">DateTime</Option>
            <Option value="Boolean">Boolean</Option>
            <Option value="ComboBox">ComboBox</Option>
            <Option value="Reference">Reference</Option>
          </Select>
        ) : (
          <Tag>{text}</Tag>
        );
      }
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      fixed: 'right',
      render: (_: unknown, record: FieldConfig, index: number) => (
        <Space size="small">
          {isEditing(record) ? (
            <>
              <Button
                type="link"
                size="small"
                icon={<EditOutlined />}
                onClick={() => handleSave()}
              >
                保存
              </Button>
              <Button
                type="link"
                size="small"
                onClick={handleCancel}
              >
                取消
              </Button>
            </>
          ) : (
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
              disabled={disabled}
            >
              编辑
            </Button>
          )}
          <Button
            type="link"
            size="small"
            icon={<ArrowUpOutlined />}
            onClick={() => handleMoveUp(index)}
            disabled={index === 0 || disabled}
          />
          <Button
            type="link"
            size="small"
            icon={<ArrowDownOutlined />}
            onClick={() => handleMoveDown(index)}
            disabled={index === fields.length - 1 || disabled}
          />
          <Popconfirm
            title="确认删除"
            description="确定要删除这个字段吗？"
            onConfirm={() => handleDelete(record)}
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
    <div>
      <div style={{ marginBottom: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Space>
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleAdd}
            disabled={disabled}
          >
            添加字段
          </Button>
          <span style={{ color: '#999' }}>
            共 {fieldCount} 个字段
          </span>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={fields}
        rowKey="name"
        pagination={false}
        scroll={{ x: 1200 }}
        size="small"
        bordered
      />
    </div>
  );
}
