# vue-ems-admin

基于 Vue 3 + Vite 开发的企业级后台管理系统模板，集成了最新的前端技术栈，提供了丰富的功能组件和完善的权限管理系统。

## 项目简介

vue-ems-admin 是一个现代化的企业管理系统框架，采用前后端分离架构，专注于提供灵活、高效的管理系统解决方案。系统集成了用户管理、请假管理、考勤管理、权限控制、数据统计等核心功能，并提供了丰富的UI组件和业务模板。

## 核心特性

- **权限管理**
  - 基于 RBAC 的权限控制
  - 动态路由配置
  - 细粒度的按钮级权限
  - 可配置的菜单权限

- **用户管理**
  - 用户信息管理
  - 部门管理
  - 角色管理
  - 账号安全设置

- **系统功能**
  - 多主题切换
  - 国际化支持（中文、英文）
  - 全局错误处理
  - 请求/响应拦截
  - 数据 Mock 支持

- **组件功能**
  - 高级表格
  - 动态表单
  - 图表统计
  - 文件上传
  - Excel 导入导出

## 技术栈

- **核心框架：** Vue 3.x
- **构建工具：** Vite 4.x
- **状态管理：** Pinia 2.x
- **路由工具：** Vue Router 4.x
- **UI 框架：** Element Plus 2.x
- **HTTP 工具：** Axios
- **工具库：**
  - TypeScript
  - Vue Use
  - ECharts 5.x
  - Wangditor
  - File-Saver
  - JSZip

## 开发环境

- Node.js >= 16
- pnpm >= 8
- [VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar)

## 项目设置

```bash
# 推荐使用 pnpm 包管理器
pnpm install

# 启动开发服务器
pnpm dev

# 类型检查与构建
pnpm type-check
pnpm build

# 代码格式化
pnpm lint
pnpm format
```

## 项目结构

```
├── public/                 # 静态资源
├── src/
│   ├── api/               # API 接口定义
│   ├── assets/            # 项目资源文件
│   ├── components/        # 公共组件
│   │   ├── Basic/        # 基础组件
│   │   └── Business/     # 业务组件
│   ├── composables/       # 组合式函数
│   ├── config/           # 全局配置
│   ├── directives/       # 自定义指令
│   ├── hooks/            # 钩子函数
│   ├── layouts/          # 布局组件
│   ├── router/           # 路由配置
│   ├── store/            # Pinia 状态管理
│   ├── styles/           # 全局样式
│   ├── utils/            # 工具函数
│   └── views/            # 页面组件
├── types/                 # 类型定义
├── .env                   # 环境变量
├── tsconfig.json         # TypeScript 配置
└── vite.config.ts        # Vite 配置
```

## 功能模块

1. **登录认证**
   - 账号密码登录
   - 手机验证码登录
   - 第三方登录集成
   - 登录状态保持

2. **系统管理**
   - 用户管理
   - 角色管理
   - 菜单管理
   - 部门管理
   - 岗位管理

3. **系统监控**
   - 操作日志
   - 登录日志
   - 服务监控
   - 在线用户

4. **系统工具**
   - 数据字典
   - 参数设置
   - 代码生成
   - 系统接口

## 浏览器支持

推荐使用下列主流浏览器的最新版本:

- Chrome >= 64
- Firefox >= 78
- Safari >= 12
- Edge >= 79
