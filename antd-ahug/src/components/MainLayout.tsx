import { useState } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { Layout, Menu, theme, Avatar, Dropdown, Badge, Breadcrumb } from 'antd';
import type { MenuProps } from 'antd';
import {
  DashboardOutlined,
  SettingOutlined,
  CodeOutlined,
  EyeOutlined,
  HistoryOutlined,
  UserOutlined,
  BellOutlined,
  DownOutlined,
  LogoutOutlined,
  GithubOutlined,
  QuestionCircleOutlined
} from '@ant-design/icons';

const { Header, Sider, Content, Footer } = Layout;

const menuItems = [
  { key: '1', icon: <DashboardOutlined />, label: 'Dashboard', path: '/' },
  { key: '2', icon: <SettingOutlined />, label: 'Configuration', path: '/config' },
  { key: '3', icon: <CodeOutlined />, label: 'Generate Code', path: '/generate' },
  { key: '4', icon: <EyeOutlined />, label: 'Code Preview', path: '/preview' },
  { key: '5', icon: <HistoryOutlined />, label: 'Recent Files', path: '/recent' },
];

const userMenuItems: MenuProps['items'] = [
  { key: 'profile', icon: <UserOutlined />, label: '个人中心' },
  { key: 'settings', icon: <SettingOutlined />, label: '系统设置' },
  { type: 'divider' },
  { key: 'logout', icon: <LogoutOutlined />, label: '退出登录', danger: true },
];

// 转换菜单项为 Ant Design Menu 需要的格式
const getMenuItems = (): MenuProps['items'] => {
  return menuItems.map(item => ({
    key: item.key,
    icon: item.icon,
    label: item.label,
  }));
};

export default function MainLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const {
    token: { colorBgContainer, borderRadiusLG, colorPrimary },
  } = theme.useToken();

  // 根据当前路由计算选中的菜单项
  const selectedKey = menuItems.find(m => m.path === location.pathname)?.key || '1';

  const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
    const item = menuItems.find(m => m.key === key);
    if (item) {
      navigate(item.path);
    }
  };

  const getBreadcrumbItems = () => {
    const currentItem = menuItems.find(m => m.key === selectedKey);
    return [
      { title: '首页', href: '/' },
      currentItem ? { title: currentItem.label } : { title: 'Dashboard' },
    ];
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        style={{
          background: 'linear-gradient(180deg, #001529 0%, #002140 100%)',
          boxShadow: '2px 0 8px rgba(0,0,0,0.15)',
        }}
      >
        {/* Logo 区域 */}
        <div
          style={{
            height: 64,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            borderBottom: '1px solid rgba(255,255,255,0.1)',
            background: 'rgba(255,255,255,0.02)',
          }}
        >
          <div
            style={{
              width: collapsed ? 40 : '85%',
              height: 40,
              background: `linear-gradient(135deg, ${colorPrimary} 0%, #1890ff 100%)`,
              borderRadius: borderRadiusLG,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'white',
              fontWeight: 'bold',
              fontSize: collapsed ? '14px' : '16px',
              boxShadow: '0 4px 12px rgba(24,144,255,0.4)',
              transition: 'all 0.3s ease',
            }}
          >
            {collapsed ? 'AG' : 'Ahug Generator'}
          </div>
        </div>

        {/* 菜单区域 */}
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={getMenuItems()}
          onClick={handleMenuClick}
          style={{
            background: 'transparent',
            borderRight: 'none',
            padding: '8px',
          }}
        />
      </Sider>

      <Layout style={{ background: '#f5f7fa' }}>
        {/* 顶部 Header */}
        <Header
          style={{
            padding: '0 24px',
            background: colorBgContainer,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            boxShadow: '0 1px 4px rgba(0,0,0,0.05)',
            zIndex: 1,
          }}
        >
          {/* 左侧：面包屑 */}
          <Breadcrumb
            items={getBreadcrumbItems()}
            style={{ fontSize: 14 }}
          />

          {/* 右侧：操作区 */}
          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            {/* GitHub 链接 */}
            <a
              href="https://github.com"
              target="_blank"
              rel="noopener noreferrer"
              style={{
                color: 'rgba(0,0,0,0.45)',
                fontSize: 18,
                transition: 'color 0.3s',
              }}
            >
              <GithubOutlined />
            </a>

            {/* 帮助 */}
            <QuestionCircleOutlined
              style={{
                color: 'rgba(0,0,0,0.45)',
                fontSize: 18,
                cursor: 'pointer',
              }}
            />

            {/* 通知 */}
            <Badge count={5} size="small">
              <BellOutlined
                style={{
                  color: 'rgba(0,0,0,0.45)',
                  fontSize: 18,
                  cursor: 'pointer',
                }}
              />
            </Badge>

            {/* 用户下拉菜单 */}
            <Dropdown
              menu={{ items: userMenuItems }}
              placement="bottomRight"
              arrow
            >
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: 8,
                  cursor: 'pointer',
                  padding: '4px 8px',
                  borderRadius: borderRadiusLG,
                  transition: 'background 0.3s',
                }}
              >
                <Avatar
                  size="small"
                  icon={<UserOutlined />}
                  style={{ background: colorPrimary }}
                />
                <span style={{ color: 'rgba(0,0,0,0.85)' }}>管理员</span>
                <DownOutlined style={{ fontSize: 12, color: 'rgba(0,0,0,0.45)' }} />
              </div>
            </Dropdown>
          </div>
        </Header>

        {/* 页面标题栏 */}
        <div
          style={{
            padding: '16px 24px',
            background: colorBgContainer,
            borderBottom: '1px solid #f0f0f0',
          }}
        >
          <h1
            style={{
              margin: 0,
              fontSize: 20,
              fontWeight: 500,
              color: 'rgba(0,0,0,0.85)',
            }}
          >
            {menuItems.find(m => m.key === selectedKey)?.label || 'Dashboard'}
          </h1>
          <p
            style={{
              margin: '8px 0 0 0',
              fontSize: 14,
              color: 'rgba(0,0,0,0.45)',
            }}
          >
            NC5 代码生成器 - 高效、智能的代码生成工具
          </p>
        </div>

        {/* 内容区域 */}
        <Content style={{ margin: 24, overflow: 'initial' }}>
          <div
            style={{
              padding: 24,
              minHeight: 480,
              background: colorBgContainer,
              borderRadius: borderRadiusLG,
              boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
            }}
          >
            <Outlet />
          </div>
        </Content>

        {/* Footer */}
        <Footer
          style={{
            textAlign: 'center',
            padding: '16px 24px',
            color: 'rgba(0,0,0,0.45)',
            fontSize: 14,
            background: 'transparent',
          }}
        >
          Ahug Code Generator ©{new Date().getFullYear()} - Built with Ant Design
        </Footer>
      </Layout>
    </Layout>
  );
}
