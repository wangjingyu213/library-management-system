# 实验二：前后端分离与 RESTful API

## 实验目标

在图书管理系统后端基础上，提供 RESTful JSON 接口，并使用 Vue + Axios 调用接口，实现前后端分离联调。

## 完成内容

- 后端使用 `@RestController` 暴露 `/api/books` 和 `/api/auth/login`。
- 前端位于 `frontend` 目录，使用 Vue 3、Vite、Axios。
- 前端新增仪表盘统计、图书状态筛选、库存预警、借出/归还、示例数据填充和权限提示。
- Axios 请求拦截器自动携带 JWT。
- 后端配置 CORS，允许 `http://localhost:5173` 调用。
- `docs/openapi.yml` 提供接口文档。

## 运行方式

后端：

```powershell
mvn spring-boot:run
```

前端：

```powershell
cd frontend
npm install
npm run dev
```

默认账号：

- 管理员：admin / admin123
- 读者：reader / reader123

## 验收截图

- 后端 `/api/books` 返回 JSON。
- 前端登录成功页面。
- 前端新增、编辑、删除图书，并演示借出/归还库存变化。
- 读者账号只能查询，不能写入。

## AI 工具使用记录

使用 AI 辅助生成 Vue 组件、Axios 封装和 OpenAPI 文档；人工检查接口字段、JWT 请求头、CORS 配置和错误边界，并通过后端测试验证。



