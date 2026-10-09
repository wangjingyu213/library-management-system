# 图书管理系统

这是一个用于个人技能实训的图书管理系统项目，已覆盖实验一到实验四，并扩展了更贴近企业日常应用的业务功能。

## 已完成功能

- 图书 CRUD、搜索、库存状态筛选。
- 图书分类管理。
- 图书封面上传，本地保存并通过 `/uploads/**` 访问。
- 借阅记录：新建借阅、归还、借阅历史查询。
- 逾期提醒：根据应还日期自动计算逾期状态和逾期天数。
- 用户管理：管理员新增、编辑、删除用户，分配 `ADMIN / LIBRARIAN / READER` 角色。
- JWT 登录认证和 RBAC 权限控制。
- AI 学习搭子：基于当前馆藏资料进行图书问答、阅读推荐和学习规划，支持 OpenAI 兼容接口及 Ollama。
- Vue + Axios 前后端分离页面。
- GitHub Actions CI 和 PR 模板。

## 默认账号

- 管理员：`admin / admin123`
- 读者：`reader / reader123`

MySQL 数据库名为 `library_management`。数据库用户名和密码通过环境变量配置，提交代码中不保存真实密码。

启动后端前，请在当前 PowerShell 窗口设置本机 MySQL 凭据；未设置时后端可能启动，但登录和数据接口无法连接数据库：

```powershell
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = '你的MySQL密码'
```

## 初始化或升级数据库

推荐在 MySQL 客户端中执行：

```sql
source D:/个人项目/library-management-system/src/main/resources/schema.sql;
```

如果在 PowerShell 中执行，注意使用 UTF-8，避免中文乱码。请将下面的密码占位符替换为本机 MySQL 密码：

```powershell
Copy-Item D:/个人项目/library-management-system/src/main/resources/schema.sql D:/library_schema.sql -Force
cmd.exe /d /c "mysql.exe --default-character-set=utf8mb4 -uroot -p你的MySQL密码 < D:\library_schema.sql"
```

## 运行后端

```powershell
cd D:/个人项目/library-management-system
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

## 配置 AI 学习搭子（实验五）

项目默认连接本机 Ollama，使用模型 `qwen2.5:7b`。请先确认 Ollama 正在运行，并且模型已安装：

```powershell
ollama pull qwen2.5:7b
```

如果使用本机 Ollama，通常无需额外配置。也可以通过环境变量覆盖默认设置：

```powershell
$env:AI_BASE_URL = 'http://localhost:11434/v1'
$env:AI_API_KEY = 'ollama'
$env:AI_MODEL = 'qwen2.5:7b'
```

如果改用兼容 OpenAI Chat Completions 协议的远程模型服务：

```powershell
$env:AI_BASE_URL = 'https://api.openai.com/v1'
$env:AI_API_KEY = '你的模型 API Key'
$env:AI_MODEL = 'gpt-4o-mini'
```

使用本地 Ollama 时可以改为：

```powershell
$env:AI_BASE_URL = 'http://localhost:11434/v1'
$env:AI_API_KEY = 'ollama'
$env:AI_MODEL = 'qwen2.5:7b'
```

重启后端后，登录系统并打开左侧“AI 学习搭子”。接口只允许已登录用户访问，问题限制为 1000 字，模型上下文只包含图书公开字段，不包含密码、JWT 密钥或 API Key。

## 运行前端

PowerShell 可能会拦截 `npm.ps1`，建议使用 `npm.cmd`：

```powershell
cd D:/个人项目/library-management-system/frontend
npm.cmd install
npm.cmd run dev
```

前端地址：`http://localhost:5173`

## 主要接口

```text
POST   /api/auth/login
GET    /api/books
POST   /api/books
PUT    /api/books/{id}
DELETE /api/books/{id}
GET    /api/categories
POST   /api/categories
GET    /api/borrow-records
POST   /api/borrow-records
POST   /api/borrow-records/{id}/return
GET    /api/admin/users
POST   /api/admin/users
POST   /api/uploads/covers
POST   /api/ai/chat
```

## 验证命令

```powershell
mvn test
```

```powershell
cd frontend
npm.cmd run build
```

本机已验证：后端测试通过，前端生产构建通过，登录、分类、图书、用户、借阅、归还接口冒烟测试通过。

## 提交材料

- `环境配置说明.md`：JDK、Maven、MySQL、Node.js、数据库初始化和启动步骤。
- `src/main/resources/application.example.yml`：不包含真实密码的后端配置模板。
- `frontend/.env.example`：前端 API 地址配置模板。
- `.github/workflows/ci.yml`：GitHub Actions 构建与测试流程。
- `.github/pull_request_template.md`：Pull Request 模板。

提交前请不要上传 `frontend/node_modules/`、`target/`、`uploads/` 和包含个人密码的本地配置文件。
