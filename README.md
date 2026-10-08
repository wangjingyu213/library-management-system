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
- Vue + Axios 前后端分离页面。
- GitHub Actions CI 和 PR 模板。

## 默认账号

- 管理员：`admin / admin123`
- 读者：`reader / reader123`

MySQL 默认连接配置：`root / 123456`，数据库名：`library_management`。

## 初始化或升级数据库

推荐在 MySQL 客户端中执行：

```sql
source D:/个人项目/library-management-system/src/main/resources/schema.sql;
```

如果在 PowerShell 中执行，注意使用 UTF-8，避免中文乱码：

```powershell
Copy-Item D:/个人项目/library-management-system/src/main/resources/schema.sql D:/library_schema.sql -Force
cmd.exe /d /c "mysql.exe --default-character-set=utf8mb4 -uroot -p123456 < D:\library_schema.sql"
```

## 运行后端

```powershell
cd D:/个人项目/library-management-system
mvn spring-boot:run
```

后端地址：`http://localhost:8080`

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
