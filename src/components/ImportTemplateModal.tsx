import React, { useState } from 'react';
import {
  Modal,
  Button,
  Tabs,
  Form,
  Input,
  Upload,
  Radio,
  message,
  Spin
} from 'antd';
import type { RcFile, UploadChangeParam, UploadFile } from 'antd/es/upload';
import { UploadOutlined } from '@ant-design/icons';
import type { TemplateImportOptions, FieldConfig } from '../types';
import TemplateService from '../services/TemplateService';

interface ImportTemplateModalProps {
  open: boolean;
  onCancel: () => void;
  onImport: (fields: FieldConfig[]) => void;
  category: 'head' | 'body';
}

const ImportTemplateModal: React.FC<ImportTemplateModalProps> = ({
  open,
  onCancel,
  onImport,
  category
}) => {
  const [form] = Form.useForm();
  const [activeTab, setActiveTab] = useState('basic');
  const [loading, setLoading] = useState(false);
  const [pasteContent, setPasteContent] = useState('');

  const handleOk = async () => {
    setLoading(true);
    try {
      let importOptions: TemplateImportOptions;
      
      if (activeTab === 'basic') {
        importOptions = {
          source: 'basic',
          category
        };
      } else if (activeTab === 'file') {
        const fileType = form.getFieldValue('fileType');
        const fileList = form.getFieldValue('fileList');
        
        if (!fileList || fileList.length === 0) {
          message.error('Please select a file');
          setLoading(false);
          return;
        }
        
        importOptions = {
          source: 'file',
          fileType,
          fileName: fileList[0].name
        };
      } else {
        // Paste tab
        const fileType = form.getFieldValue('pasteFileType');
        
        if (!pasteContent.trim()) {
          message.error('Please enter content to import');
          setLoading(false);
          return;
        }
        
        importOptions = {
          source: 'paste',
          fileType,
          content: pasteContent
        };
      }

      const importData = await TemplateService.importFields(importOptions);
      onImport(importData.fields);
      message.success('Fields imported successfully');
      handleCancel();
    } catch (error) {
      console.error('Import failed:', error);
      message.error(error instanceof Error ? error.message : 'Import failed');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    setPasteContent('');
    setActiveTab('basic');
    onCancel();
  };

  const handleFileChange = (info: UploadChangeParam<UploadFile>) => {
    if (info.file.status === 'done') {
      message.success(`${info.file.name} file uploaded successfully`);
    } else if (info.file.status === 'error') {
      message.error(`${info.file.name} file upload failed.`);
    }
  };

  const beforeUpload = (file: RcFile) => {
    const isJsonOrSql = file.type === 'application/json' || 
                       file.name.endsWith('.sql') ||
                       file.name.endsWith('.json');
    
    if (!isJsonOrSql) {
      message.error('You can only upload JSON or SQL files!');
      return false;
    }
    
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isLt2M) {
      message.error('File must smaller than 2MB!');
      return false;
    }
    
    return false; // Prevent automatic upload
  };

  const normFile = (e: unknown) => {
    if (Array.isArray(e)) {
      return e;
    }
    return (e as { fileList?: UploadFile[] })?.fileList;
  };

  const basicTemplate = TemplateService.getBasicTemplate(category);

  return (
    <Modal
      title={`Import ${category === 'head' ? 'Header' : 'Body'} Fields`}
      open={open}
      onOk={handleOk}
      onCancel={handleCancel}
      width={600}
      confirmLoading={loading}
      okText="Import"
    >
      <Spin spinning={loading}>
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={[
            {
              key: 'basic',
              label: 'Import Basic Template',
              children: (
                <div style={{ padding: '20px 0' }}>
                  <p>Import the basic field template for {category} fields.</p>
                  {basicTemplate ? (
                    <div style={{ 
                      backgroundColor: '#f0f2f5', 
                      padding: '12px', 
                      borderRadius: '6px',
                      marginTop: '16px'
                    }}>
                      <h4>Basic Template: {basicTemplate.name}</h4>
                      <p>Contains {basicTemplate.fields.length} fields</p>
                      {basicTemplate.description && <p>{basicTemplate.description}</p>}
                    </div>
                  ) : (
                    <p style={{ color: '#ff4d4f' }}>No basic template found for {category} fields</p>
                  )}
                </div>
              )
            },
            {
              key: 'file',
              label: 'Import from File',
              children: (
                <Form
                  form={form}
                  layout="vertical"
                  style={{ paddingTop: '20px' }}
                >
                  <Form.Item
                    name="fileType"
                    label="File Type"
                    initialValue="json"
                    rules={[{ required: true }]}
                  >
                    <Radio.Group>
                      <Radio value="json">JSON</Radio>
                      <Radio value="sql">SQL</Radio>
                    </Radio.Group>
                  </Form.Item>
                  
                  <Form.Item
                    name="fileList"
                    label="Select File"
                    valuePropName="fileList"
                    getValueFromEvent={normFile}
                    rules={[{ required: true, message: 'Please select a file' }]}
                  >
                    <Upload
                      beforeUpload={beforeUpload}
                      onChange={handleFileChange}
                      maxCount={1}
                      accept=".json,.sql"
                    >
                      <Button icon={<UploadOutlined />}>Click to Upload</Button>
                    </Upload>
                    <div style={{ marginTop: '8px', fontSize: '12px', color: '#666' }}>
                      Support .json and .sql files, max size 2MB
                    </div>
                  </Form.Item>
                </Form>
              )
            },
            {
              key: 'paste',
              label: 'Paste Content',
              children: (
                <Form
                  form={form}
                  layout="vertical"
                  style={{ paddingTop: '20px' }}
                >
                  <Form.Item
                    name="pasteFileType"
                    label="Content Type"
                    initialValue="json"
                    rules={[{ required: true }]}
                  >
                    <Radio.Group>
                      <Radio value="json">JSON</Radio>
                      <Radio value="sql">SQL</Radio>
                    </Radio.Group>
                  </Form.Item>
                  
                  <Form.Item
                    label="Paste Content"
                    required
                  >
                    <Input.TextArea
                      rows={8}
                      placeholder={
                        form.getFieldValue('pasteFileType') === 'json' 
                          ? 'Paste JSON content here...\nExample:\n{\n  "fields": [\n    {\n      "name": "fieldName",\n      "label": "Field Label",\n      "type": "String",\n      "dbType": "VARCHAR",\n      "length": 50,\n      "required": false,\n      "editable": true\n    }\n  ]\n}'
                          : 'Paste SQL CREATE TABLE statement here...\nExample:\nCREATE TABLE table_name (\n  `column_name` VARCHAR(50) COMMENT \'Column Description\',\n  `another_column` INT NOT NULL DEFAULT 0\n);'
                      }
                      value={pasteContent}
                      onChange={(e) => setPasteContent(e.target.value)}
                    />
                  </Form.Item>
                </Form>
              )
            }
          ]}
        />
      </Spin>
    </Modal>
  );
};

export default ImportTemplateModal;