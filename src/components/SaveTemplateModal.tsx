import React, { useState } from 'react';
import {
  Modal,
  Form,
  Input,
  Radio,
  message,
  Spin
} from 'antd';
import type { FieldConfig } from '../types';
import TemplateService from '../services/TemplateService';

interface SaveTemplateModalProps {
  open: boolean;
  onCancel: () => void;
  onSave: () => void;
  fields: FieldConfig[];
  category: 'head' | 'body';
}

const SaveTemplateModal: React.FC<SaveTemplateModalProps> = ({
  open,
  onCancel,
  onSave,
  fields,
  category
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);
      
      const templateType = values.templateType as 'basic' | 'custom';
      const templateName = values.templateName as string;
      const description = values.description as string;
      
      TemplateService.saveTemplate(
        templateName,
        category,
        fields,
        templateType,
        description
      );
      
      message.success(`Template "${templateName}" saved successfully as ${templateType} template`);
      form.resetFields();
      onSave();
      onCancel();
    } catch (error) {
      console.error('Save template failed:', error);
      if (error instanceof Error) {
        message.error(error.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  const basicTemplateExists = !!TemplateService.getBasicTemplate(category);

  return (
    <Modal
      title={`Save ${category === 'head' ? 'Header' : 'Body'} Fields as Template`}
      open={open}
      onOk={handleOk}
      onCancel={handleCancel}
      width={500}
      confirmLoading={loading}
      okText="Save"
    >
      <Spin spinning={loading}>
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            templateType: 'custom'
          }}
        >
          <Form.Item
            name="templateType"
            label="Template Type"
            rules={[{ required: true }]}
          >
            <Radio.Group>
              <Radio value="custom">Custom Template</Radio>
              <Radio 
                value="basic" 
                disabled={basicTemplateExists}
              >
                Basic Template {basicTemplateExists && '(already exists)'}
              </Radio>
            </Radio.Group>
          </Form.Item>
          
          <Form.Item
            name="templateName"
            label="Template Name"
            rules={[
              { required: true, message: 'Please enter template name' },
              { max: 50, message: 'Template name cannot exceed 50 characters' }
            ]}
          >
            <Input 
              placeholder="Enter template name" 
              maxLength={50}
            />
          </Form.Item>
          
          <Form.Item
            name="description"
            label="Description"
            rules={[{ max: 200, message: 'Description cannot exceed 200 characters' }]}
          >
            <Input.TextArea 
              placeholder="Enter template description (optional)"
              rows={3}
              maxLength={200}
              showCount
            />
          </Form.Item>
          
          <div style={{ 
            backgroundColor: '#f0f2f5', 
            padding: '12px', 
            borderRadius: '6px',
            marginTop: '16px'
          }}>
            <p><strong>Template Info:</strong></p>
            <p>• Fields to save: {fields.length}</p>
            <p>• Category: {category === 'head' ? 'Header' : 'Body'}</p>
            <p>• Storage: {form.getFieldValue('templateType') === 'basic' ? 'Overwrites existing basic template' : 'Creates new custom template'}</p>
          </div>
        </Form>
      </Spin>
    </Modal>
  );
};

export default SaveTemplateModal;