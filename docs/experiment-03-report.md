# 实验三：Git 协作开发与持续集成

## 实验目标

掌握 Git 分支开发、Pull Request、Code Review 和基础 CI。

## 完成内容

- 新增 `.github/pull_request_template.md`。
- 新增 `.github/workflows/ci.yml`。
- CI 包含后端 `mvn test` 和前端 `npm run build`。

## 建议操作流程

```powershell
git init
git checkout -b feature/library-rest-auth
git add .
git commit -m "feat: complete library system experiments 2-4"
```

推送到 GitHub 后创建 Pull Request，并使用 PR 模板填写变更内容、验证方式和评审重点。

## AI 工具使用记录

使用 AI 辅助生成 PR 模板、GitHub Actions 配置和代码评审关注点；人工确认 CI 命令与项目目录一致。

