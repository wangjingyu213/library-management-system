<template>
  <main class="app">
    <section v-if="!user" class="login-shell">
      <div class="login-hero">
        <div class="login-brand"><span class="brand-mark">藏</span><span>LIBRARY OPERATIONS</span></div>
        <div class="hero-copy">
          <p class="eyebrow">Enterprise Library Console</p>
          <h1>图书管理系统</h1>
          <p>统一管理馆藏、借阅、分类和用户权限，让日常流转清晰可追踪。</p>
        </div>
        <div class="hero-grid"><span><b>01</b> 书库维护</span><span><b>02</b> 借阅流转</span><span><b>03</b> 逾期跟进</span><span><b>04</b> 权限管理</span></div>
        <div class="hero-footer"><span class="status-dot"></span> 实训项目控制台 <span>Spring Boot 3 · Vue 3 · MySQL</span></div>
      </div>
      <section class="panel login-panel">
        <div class="panel-heading compact"><div><p class="section-kicker">Welcome back</p><h2>登录系统</h2></div><span class="secure-label">安全登录</span></div>
        <form @submit.prevent="handleLogin" class="stack">
          <label>用户名<input v-model="loginForm.username" autocomplete="username" /></label>
          <label>密码<input v-model="loginForm.password" type="password" autocomplete="current-password" /></label>
          <button class="primary-action" :disabled="loading"><span>{{ loading ? '正在验证...' : '进入系统' }}</span><span aria-hidden="true">→</span></button>
        </form>
        <div class="quick-login mt"><span>快速填充账号</span><div class="row"><button class="ghost" @click="useAccount('admin','admin123')">管理员</button><button class="ghost" @click="useAccount('reader','reader123')">读者</button></div></div>
        <p class="muted mt login-hint">演示账号：<b>admin / admin123</b> · <b>reader / reader123</b></p>
      </section>
    </section>

    <template v-else>
      <aside class="sidebar">
        <div class="brand"><div class="brand-lockup"><span class="brand-mark">藏</span><span><strong>图书管理系统</strong><small>LIBRARY CONSOLE</small></span></div><span class="env-label">实训环境</span></div>
        <div class="nav-section"><span class="nav-section-title">工作空间</span><button v-for="tab in visibleTabs" :key="tab.key" :class="['nav', {active: activeTab===tab.key}]" @click="activeTab=tab.key"><span class="nav-icon">{{ tab.icon }}</span><span>{{ tab.label }}</span><span v-if="tab.key==='dashboard'" class="nav-arrow">⌂</span></button></div>
        <div class="sidebar-footer"><div class="profile"><span class="avatar">{{ (user.displayName || user.username || 'U').slice(0, 1) }}</span><span class="profile-main"><strong>{{ user.displayName }}</strong><small>{{ user.roles?.join(' / ') }}</small></span><span class="online-dot"></span></div><button class="logout-button" @click="handleLogout"><span>↪</span> 退出登录</button></div>
      </aside>

      <section class="content">
        <div class="content-inner">
          <header class="topbar">
            <div class="page-heading"><div class="breadcrumbs"><span>工作空间</span><span>/</span><b>{{ currentTab.label }}</b></div><h1>{{ pageTitle }}</h1><p>{{ pageDescription }}</p></div>
            <div class="topbar-actions"><span class="sync-status"><i></i> 数据已同步</span><button class="refresh-button" @click="refreshAll" :disabled="loading"><span :class="{spinning: loading}">↻</span> 刷新数据</button></div>
          </header>

          <section class="stats-grid">
            <article class="stat"><div class="stat-top"><span class="stat-icon">册</span><span class="stat-trend neutral">书目</span></div><span class="stat-label">图书种类</span><strong>{{ stats.totalTitles }}</strong><p>当前系统收录的书目</p></article>
            <article class="stat blue"><div class="stat-top"><span class="stat-icon">借</span><span class="stat-trend positive">可流通</span></div><span class="stat-label">可借库存</span><strong>{{ stats.availableCopies }}</strong><p>当前可被借出的馆藏</p></article>
            <article class="stat orange"><div class="stat-top"><span class="stat-icon">催</span><span class="stat-trend warning">需跟进</span></div><span class="stat-label">逾期记录</span><strong>{{ stats.overdue }}</strong><p>需要提醒归还的记录</p></article>
            <article class="stat green"><div class="stat-top"><span class="stat-icon">人</span><span class="stat-trend positive">账号</span></div><span class="stat-label">用户数量</span><strong>{{ users.length || '-' }}</strong><p>{{ canManageUsers ? '系统内的全部账号' : '管理员可查看账号总数' }}</p></article>
          </section>

          <section v-if="activeTab==='dashboard'" class="dashboard-grid">
            <div class="panel dashboard-main"><div class="panel-heading"><div><p class="section-kicker">Daily overview</p><h2>今日运营概览</h2></div><span class="panel-caption">重点事项</span></div><div class="action-grid"><button class="action-card" @click="activeTab='books'" :disabled="!canWrite"><span class="action-icon blue-bg">+</span><span><b>维护图书</b><small>新增或更新馆藏信息</small></span><i>→</i></button><button class="action-card" @click="openBorrowPanel(); activeTab='borrows'"><span class="action-icon green-bg">↗</span><span><b>办理借阅</b><small>登记新的借阅记录</small></span><i>→</i></button><button class="action-card" @click="activeTab='borrows'"><span class="action-icon orange-bg">!</span><span><b>处理逾期</b><small>{{ stats.overdue ? `当前 ${stats.overdue} 条待跟进` : '当前暂无逾期记录' }}</small></span><i>→</i></button></div><div class="dashboard-note"><span class="note-icon">i</span><span>日常建议：先处理逾期记录，再检查库存紧张的图书，保持馆藏流转顺畅。</span></div></div>
            <div class="panel"><div class="panel-heading"><div><p class="section-kicker">Stock alert</p><h2>库存预警</h2></div><button class="text-button" @click="activeTab='books'">查看书库 →</button></div><div class="list"><div v-for="b in lowStockBooks" :key="b.id" class="list-item"><span><b>{{ b.title }}</b><small>{{ b.categoryName || '未分类' }}</small></span><strong class="stock-warning">{{ b.availableCopies }}/{{ b.totalCopies }}</strong></div><p v-if="!lowStockBooks.length" class="empty">暂无库存预警</p></div></div>
            <div class="panel"><div class="panel-heading"><div><p class="section-kicker">Overdue follow-up</p><h2>逾期提醒</h2></div><button class="text-button" @click="activeTab='borrows'">查看记录 →</button></div><div class="list"><div v-for="r in overdueRecords" :key="r.id" class="list-item danger-text"><span><b>{{ r.displayName }}：{{ r.bookTitle }}</b><small>应还日期 {{ r.dueDate }}</small></span><strong>{{ r.overdueDays }} 天</strong></div><p v-if="!overdueRecords.length" class="empty">暂无逾期记录</p></div></div>
            <div class="panel recent-panel"><div class="panel-heading"><div><p class="section-kicker">Recent activity</p><h2>最近借阅动态</h2></div><button class="text-button" @click="activeTab='borrows'">全部记录 →</button></div><div class="activity-list"><div v-for="r in records.slice(0, 5)" :key="r.id" class="activity-item"><span class="activity-avatar">{{ (r.displayName || '读').slice(0, 1) }}</span><span><b>{{ r.displayName }} 借阅了《{{ r.bookTitle }}》</b><small>{{ r.borrowDate }} · {{ recordText(r) }}</small></span><span :class="['badge', recordClass(r)]">{{ recordText(r) }}</span></div><p v-if="!records.length" class="empty">暂无借阅动态</p></div></div>
          </section>

          <section v-if="activeTab==='ai'" class="ai-layout">
            <div class="panel ai-shell">
              <div class="panel-heading"><div><p class="section-kicker">AI assistant</p><h2>AI 学习搭子</h2></div><span class="ai-status"><i></i>馆藏问答</span></div>
              <div class="ai-notice"><span class="note-icon">i</span><span>回答会参考当前馆藏资料，适合查询图书、库存和阅读建议。请勿输入密码、密钥等敏感信息，馆藏结果请以书库页面为准。</span></div>
              <div class="chat-history" aria-live="polite">
                <div v-for="(item, index) in aiMessages" :key="`${item.role}-${index}`" :class="['chat-message', item.role]">
                  <span class="chat-avatar">{{ item.role === 'user' ? (user.displayName || user.username || '我').slice(0, 1) : 'AI' }}</span>
                  <div class="chat-bubble"><small>{{ item.role === 'user' ? '我' : 'AI 学习搭子' }}</small><p>{{ item.content }}</p></div>
                </div>
                <div v-if="aiLoading" class="chat-message assistant"><span class="chat-avatar">AI</span><div class="chat-bubble typing"><small>AI 学习搭子</small><p>正在整理馆藏资料并生成回答<span class="typing-dots">...</span></p></div></div>
              </div>
              <div class="quick-prompts"><span>试试这样问：</span><button v-for="prompt in aiQuickPrompts" :key="prompt" class="ghost" type="button" @click="submitAiQuestion(prompt)" :disabled="aiLoading">{{ prompt }}</button></div>
              <form class="ai-composer" @submit.prevent="submitAiQuestion()">
                <textarea v-model="aiQuestion" maxlength="1000" rows="3" placeholder="例如：帮我推荐适合入门学习 Java 的馆藏图书" @keydown.enter.exact.prevent="submitAiQuestion()"></textarea>
                <div class="ai-composer-footer"><small>{{ aiQuestion.length }}/1000 · Enter 发送，Shift + Enter 换行</small><button :disabled="aiLoading || !aiQuestion.trim()">{{ aiLoading ? '生成中...' : '发送问题 →' }}</button></div>
              </form>
            </div>
            <aside class="panel ai-guide"><div class="panel-heading"><div><p class="section-kicker">How to use</p><h2>使用建议</h2></div></div><div class="ai-guide-list"><div><b>查馆藏</b><p>询问某类图书、作者或当前可借库存。</p></div><div><b>做推荐</b><p>说明学习目标和难度，获取基于馆藏的阅读建议。</p></div><div><b>问学习</b><p>让 AI 解释概念、整理学习计划或总结重点。</p></div></div><p class="ai-footnote">当前会话不会保存到数据库。模型回答可能存在偏差，请人工核对关键结论。</p></aside>
          </section>

          <section v-if="activeTab==='books'" class="grid books-layout">
            <div class="panel"><div class="panel-heading"><div><p class="section-kicker">Collection</p><h2>馆藏目录 <span class="count-label">{{ books.length }} 本</span></h2></div><span class="panel-caption">支持封面和库存维护</span></div><div class="tools"><label class="search-field"><span>⌕</span><input v-model="bookQuery.keyword" placeholder="搜索书名、作者、ISBN、出版社或位置" @keyup.enter="loadBooks" /></label><select v-model="bookQuery.categoryId" @change="loadBooks"><option value="">全部分类</option><option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option></select><select v-model="bookQuery.stockStatus" @change="loadBooks"><option value="">全部库存</option><option value="available">可借</option><option value="low">库存紧张</option><option value="empty">已借完</option></select><button @click="loadBooks">查询</button></div><div class="book-grid"><article v-for="book in books" :key="book.id" class="book-card"><img :src="coverOf(book)" alt="图书封面" /><div class="book-body"><div class="book-meta-top"><span :class="['badge', statusOf(book).className]">{{ statusOf(book).text }}</span><span class="book-id">#{{ book.id }}</span></div><h3>{{ book.title }}</h3><p class="book-author">{{ book.author }}</p><p class="book-location">{{ book.categoryName || '未分类' }} <span>·</span> {{ book.shelfLocation || '未设置位置' }}</p><div class="stock"><b>{{ book.availableCopies }}</b><span>/ {{ book.totalCopies }} 可借</span></div><p class="summary">{{ book.summary || '暂无简介' }}</p><div class="row wrap book-actions"><button class="ghost" @click="editBook(book)" :disabled="!canWrite">编辑</button><button @click="quickBorrow(book)" :disabled="book.availableCopies<=0">借阅</button><button class="danger" @click="removeBook(book)" :disabled="!canDelete">删除</button></div></div></article><p v-if="!books.length" class="empty empty-card">没有符合条件的图书</p></div></div>
            <div class="panel sticky form-panel"><div class="panel-heading"><div><p class="section-kicker">Book editor</p><h2>{{ bookForm.id ? '编辑图书' : '新增图书' }}</h2></div><span class="panel-caption">{{ canWrite ? '可编辑' : '只读模式' }}</span></div><div v-if="!canWrite" class="permission-note">当前账号只有查看和借阅权限。</div><form class="stack" @submit.prevent="saveBook"><label>书名<input v-model="bookForm.title" required :disabled="!canWrite" /></label><label>作者<input v-model="bookForm.author" required :disabled="!canWrite" /></label><div class="form-grid"><label>ISBN<input v-model="bookForm.isbn" :disabled="!canWrite" /></label><label>出版年份<input v-model.number="bookForm.publishYear" type="number" :disabled="!canWrite" /></label></div><label>出版社<input v-model="bookForm.publisher" :disabled="!canWrite" /></label><div class="form-grid"><label>分类<select v-model="bookForm.categoryId" :disabled="!canWrite"><option :value="null">未分类</option><option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option></select></label><label>馆藏位置<input v-model="bookForm.shelfLocation" :disabled="!canWrite" /></label></div><div class="form-grid"><label>馆藏<input v-model.number="bookForm.totalCopies" type="number" min="0" :disabled="!canWrite" /></label><label>可借<input v-model.number="bookForm.availableCopies" type="number" min="0" :disabled="!canWrite" /></label></div><label>简介<textarea v-model="bookForm.summary" rows="3" :disabled="!canWrite"></textarea></label><label>封面上传<input type="file" accept="image/*" @change="handleCoverUpload" :disabled="!canWrite" /></label><img v-if="bookForm.coverUrl" class="cover-preview" :src="bookForm.coverUrl" alt="封面预览" /><div class="row form-actions"><button :disabled="!canWrite">保存图书</button><button type="button" class="ghost" @click="resetBookForm">清空</button></div></form></div>
          </section>

          <section v-if="activeTab==='borrows'" class="panel"><div class="panel-heading"><div><p class="section-kicker">Circulation</p><h2>借阅记录 <span class="count-label">{{ records.length }} 条</span></h2></div><span class="panel-caption">借出、归还和逾期跟进</span></div><div class="tools record-tools"><label class="search-field"><span>⌕</span><input v-model="recordQuery.keyword" placeholder="搜索图书或借阅人" @keyup.enter="loadRecords" /></label><select v-model="recordQuery.status" @change="loadRecords"><option value="">全部状态</option><option value="BORROWED">借阅中</option><option value="OVERDUE">已逾期</option><option value="RETURNED">已归还</option></select><button @click="loadRecords">查询</button><button class="ghost" @click="openBorrowPanel">{{ borrowPanel ? '收起办理' : '新建借阅' }}</button></div><div v-if="borrowPanel" class="inline-form"><select v-model="borrowForm.bookId"><option :value="null">选择图书</option><option v-for="b in books" :key="b.id" :value="b.id" :disabled="b.availableCopies<=0">{{ b.title }}（可借 {{ b.availableCopies }}）</option></select><select v-if="canManageUsers" v-model="borrowForm.userId"><option :value="null">选择读者</option><option v-for="u in users" :key="u.id" :value="u.id">{{ u.displayName }} / {{ u.username }}</option></select><input v-model="borrowForm.dueDate" type="date" /><input v-model="borrowForm.remark" placeholder="备注" /><button @click="saveBorrow">确认借阅</button></div><div class="table-scroll"><table><thead><tr><th>图书</th><th>借阅人</th><th>借出日期</th><th>应还日期</th><th>归还日期</th><th>状态</th><th>操作</th></tr></thead><tbody><tr v-for="r in records" :key="r.id"><td><b>{{ r.bookTitle }}</b></td><td><span class="table-person"><span class="mini-avatar">{{ (r.displayName || '读').slice(0, 1) }}</span><span>{{ r.displayName }}<small>{{ r.username }}</small></span></span></td><td>{{ r.borrowDate }}</td><td :class="{'date-overdue': r.status==='OVERDUE'}">{{ r.dueDate }}</td><td>{{ r.returnDate || '-' }}</td><td><span :class="['badge', recordClass(r)]">{{ recordText(r) }}</span></td><td><button class="success small-button" @click="returnRecord(r)" :disabled="r.returnDate">{{ r.returnDate ? '已归还' : '办理归还' }}</button></td></tr><tr v-if="!records.length"><td colspan="7" class="empty">暂无借阅记录</td></tr></tbody></table></div></section>

          <section v-if="activeTab==='categories'" class="grid two"><div class="panel"><div class="panel-heading"><div><p class="section-kicker">Taxonomy</p><h2>分类列表 <span class="count-label">{{ categories.length }} 个</span></h2></div><span class="panel-caption">统一书目标签</span></div><div class="list"><div v-for="c in categories" :key="c.id" class="list-item"><span class="category-name"><span class="category-mark">{{ c.name.slice(0, 1) }}</span><span><b>{{ c.name }}</b><small>{{ c.description || '暂无说明' }}</small></span></span><span class="row"><button class="ghost" @click="editCategory(c)" :disabled="!canWrite">编辑</button><button class="danger" @click="removeCategory(c)" :disabled="!canDelete">删除</button></span></div><p v-if="!categories.length" class="empty">暂无分类</p></div></div><div class="panel sticky form-panel"><div class="panel-heading"><div><p class="section-kicker">Category editor</p><h2>{{ categoryForm.id ? '编辑分类' : '新增分类' }}</h2></div><span class="panel-caption">{{ canWrite ? '可编辑' : '只读模式' }}</span></div><form class="stack" @submit.prevent="saveCategory"><label>名称<input v-model="categoryForm.name" required :disabled="!canWrite" /></label><label>说明<textarea v-model="categoryForm.description" rows="4" :disabled="!canWrite"></textarea></label><div class="row form-actions"><button :disabled="!canWrite">保存分类</button><button type="button" class="ghost" @click="resetCategoryForm">清空</button></div></form></div></section>

          <section v-if="activeTab==='users'" class="grid users-layout"><div class="panel"><div class="panel-heading"><div><p class="section-kicker">Identity & access</p><h2>用户管理 <span class="count-label">{{ users.length }} 个</span></h2></div><span class="panel-caption">账号与角色权限</span></div><div class="table-scroll"><table><thead><tr><th>用户</th><th>角色</th><th>状态</th><th>操作</th></tr></thead><tbody><tr v-for="u in users" :key="u.id"><td><span class="table-person"><span class="mini-avatar">{{ (u.displayName || '用').slice(0, 1) }}</span><span><b>{{ u.displayName }}</b><small>{{ u.username }}</small></span></span></td><td><span class="role-text">{{ u.roles?.join(' / ') }}</span></td><td><span :class="['badge', u.enabled ? 'ok' : 'empty']">{{ u.enabled ? '启用' : '禁用' }}</span></td><td><button class="ghost small-button" @click="editUser(u)">编辑</button><button class="danger small-button" @click="removeUser(u)" :disabled="u.username==='admin'">删除</button></td></tr><tr v-if="!users.length"><td colspan="4" class="empty">暂无用户数据</td></tr></tbody></table></div></div><div class="panel sticky form-panel"><div class="panel-heading"><div><p class="section-kicker">User editor</p><h2>{{ userForm.id ? '编辑用户' : '新增用户' }}</h2></div><span class="panel-caption">管理员权限</span></div><form class="stack" @submit.prevent="saveUser"><label>用户名<input v-model="userForm.username" required /></label><label>显示名称<input v-model="userForm.displayName" required /></label><label>密码<input v-model="userForm.password" type="password" :placeholder="userForm.id ? '留空表示不修改' : '新用户必填'" /></label><label>角色<select v-model="userForm.roles" multiple><option value="ADMIN">ADMIN</option><option value="LIBRARIAN">LIBRARIAN</option><option value="READER">READER</option></select></label><label class="check"><input v-model="userForm.enabled" type="checkbox" /> 启用账号</label><div class="row form-actions"><button>保存用户</button><button type="button" class="ghost" @click="resetUserForm">清空</button></div></form></div></section>
        </div>
      </section>
    </template>

    <p v-if="message" :class="['toast', messageType]" role="status"><span>{{ messageType === 'error' ? '!' : '✓' }}</span>{{ message }}</p>
  </main>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { askAi, borrowBook, createBook, createCategory, createUser, deleteBook, deleteCategory, deleteUser, getStoredUser, listBooks, listBorrowRecords, listCategories, listUsers, login, logout, returnBook, updateBook, updateCategory, updateUser, uploadCover } from './api'

const user = ref(getStoredUser())
const activeTab = ref('dashboard')
const loading = ref(false)
const message = ref('')
const messageType = ref('info')
const books = ref([]), categories = ref([]), records = ref([]), users = ref([])
const loginForm = reactive({ username: 'admin', password: 'admin123' })
const bookQuery = reactive({ keyword: '', categoryId: '', stockStatus: '' })
const recordQuery = reactive({ keyword: '', status: '' })
const borrowPanel = ref(false)
const borrowForm = reactive({ bookId: null, userId: null, dueDate: dateAfter(30), remark: '' })
const bookForm = reactive(emptyBook())
const categoryForm = reactive({ id: null, name: '', description: '' })
const userForm = reactive(emptyUser())
const aiQuestion = ref('')
const aiLoading = ref(false)
const aiMessages = ref([{ role: 'assistant', content: '你好，我是 AI 学习搭子。你可以问我馆藏查询、阅读推荐、借阅规则或学习规划。' }])
const aiQuickPrompts = ['推荐适合 Java 入门的图书', '当前有哪些库存紧张的书？', '帮我制定一周阅读计划']
const tabs = [{key:'dashboard',label:'工作台',icon:'总'}, {key:'books',label:'书库管理',icon:'书'}, {key:'borrows',label:'借阅记录',icon:'借'}, {key:'categories',label:'图书分类',icon:'类'}, {key:'ai',label:'AI 学习搭子',icon:'AI'}, {key:'users',label:'用户管理',icon:'人', admin:true}]
const visibleTabs = computed(() => tabs.filter(t => !t.admin || canManageUsers.value))
const currentTab = computed(() => tabs.find(t => t.key === activeTab.value) || tabs[0])
const pageTitle = computed(() => ({dashboard:'运营总览',books:'书库与封面维护',borrows:'借阅流转与逾期提醒',categories:'分类体系维护',ai:'AI 智能问答',users:'账号与角色管理'}[activeTab.value]))
const pageDescription = computed(() => ({dashboard:'掌握馆藏状态、借阅动态和需要跟进的重点事项。',books:'维护书目、库存位置与图书封面，保持馆藏信息准确。',borrows:'登记借阅和归还，集中处理逾期记录。',categories:'建立统一的图书分类体系，提升检索与管理效率。',ai:'结合当前馆藏资料，辅助完成图书查询与学习规划。',users:'管理系统账号、角色和访问状态。'}[activeTab.value]))
const canWrite = computed(() => user.value?.roles?.some(r => r === 'ADMIN' || r === 'LIBRARIAN'))
const canDelete = computed(() => user.value?.roles?.includes('ADMIN'))
const canManageUsers = computed(() => user.value?.roles?.includes('ADMIN'))
const stats = computed(() => ({ totalTitles: books.value.length, availableCopies: books.value.reduce((s,b)=>s+Number(b.availableCopies||0),0), overdue: records.value.filter(r=>r.status==='OVERDUE').length }))
const lowStockBooks = computed(() => books.value.filter(b => b.availableCopies <= 1).slice(0, 8))
const overdueRecords = computed(() => records.value.filter(r => r.status === 'OVERDUE').slice(0, 8))

onMounted(() => { if (user.value) refreshAll() })
function useAccount(username, password){ loginForm.username=username; loginForm.password=password }
async function handleLogin(){ await run(async()=>{ user.value=await login(loginForm.username, loginForm.password); await refreshAll() }, '登录成功') }
function handleLogout(){ logout(); user.value=null; activeTab.value='dashboard' }
async function refreshAll(){ await Promise.all([loadCategories(), loadBooks(), loadRecords(), canManageUsers.value ? loadUsers() : Promise.resolve()]) }
async function loadBooks(){ books.value = await listBooks({ ...bookQuery, categoryId: bookQuery.categoryId || undefined, stockStatus: bookQuery.stockStatus || undefined }) }
async function loadCategories(){ categories.value = await listCategories() }
async function loadRecords(){ records.value = await listBorrowRecords({ ...recordQuery, status: recordQuery.status || undefined }) }
async function loadUsers(){ if (canManageUsers.value) users.value = await listUsers() }
async function saveBook(){ await run(async()=>{ const payload={...bookForm, categoryId: bookForm.categoryId || null}; delete payload.id; bookForm.id ? await updateBook(bookForm.id,payload) : await createBook(payload); resetBookForm(); await loadBooks() }, '图书已保存') }
function editBook(b){ Object.assign(bookForm, b) }
function resetBookForm(){ Object.assign(bookForm, emptyBook()) }
async function removeBook(b){ if(confirm(`删除《${b.title}》？`)) await run(async()=>{ await deleteBook(b.id); await loadBooks() }, '图书已删除') }
async function handleCoverUpload(e){ const file=e.target.files?.[0]; if(!file)return; await run(async()=>{ const res=await uploadCover(file); bookForm.coverUrl=res.url }, '封面上传成功') }
async function quickBorrow(book){ Object.assign(borrowForm,{bookId:book.id,userId:canManageUsers.value ? (users.value[0]?.id || null) : null,dueDate:dateAfter(30),remark:''}); activeTab.value='borrows'; borrowPanel.value=true }
function openBorrowPanel(){ borrowPanel.value=!borrowPanel.value }
async function submitAiQuestion(question = aiQuestion.value){
  const content = String(question || '').trim()
  if(!content || aiLoading.value) return
  aiMessages.value.push({ role: 'user', content })
  aiQuestion.value = ''
  aiLoading.value = true
  try {
    const response = await askAi(content)
    aiMessages.value.push({ role: 'assistant', content: response.answer })
  } catch (e) {
    const error = e.response?.data?.message || e.message || 'AI 服务暂时不可用'
    aiMessages.value.push({ role: 'assistant', content: `暂时无法回答：${error}` })
  } finally {
    aiLoading.value = false
  }
}
async function saveBorrow(){ await run(async()=>{ await borrowBook({...borrowForm}); borrowPanel.value=false; await Promise.all([loadBooks(),loadRecords()]) }, '借阅成功') }
async function returnRecord(r){ await run(async()=>{ await returnBook(r.id,{remark:'正常归还'}); await Promise.all([loadBooks(),loadRecords()]) }, '归还成功') }
async function saveCategory(){ await run(async()=>{ categoryForm.id ? await updateCategory(categoryForm.id,categoryForm) : await createCategory(categoryForm); resetCategoryForm(); await loadCategories() }, '分类已保存') }
function editCategory(c){ Object.assign(categoryForm,c) }
function resetCategoryForm(){ Object.assign(categoryForm,{id:null,name:'',description:''}) }
async function removeCategory(c){ if(confirm(`删除分类 ${c.name}？`)) await run(async()=>{ await deleteCategory(c.id); await loadCategories() }, '分类已删除') }
async function saveUser(){ await run(async()=>{ const payload={...userForm}; if(!payload.password) delete payload.password; userForm.id ? await updateUser(userForm.id,payload) : await createUser(payload); resetUserForm(); await loadUsers() }, '用户已保存') }
function editUser(u){ Object.assign(userForm,{id:u.id,username:u.username,displayName:u.displayName,password:'',enabled:u.enabled,roles:[...(u.roles||[])]}) }
function resetUserForm(){ Object.assign(userForm, emptyUser()) }
async function removeUser(u){ if(confirm(`删除用户 ${u.username}？`)) await run(async()=>{ await deleteUser(u.id); await loadUsers() }, '用户已删除') }
function emptyBook(){ return { id:null,title:'',author:'',isbn:'',publisher:'',publishYear:new Date().getFullYear(),totalCopies:1,availableCopies:1,categoryId:null,shelfLocation:'',summary:'',coverUrl:'' } }
function emptyUser(){ return { id:null,username:'',displayName:'',password:'',enabled:true,roles:['READER'] } }
function statusOf(b){ if(b.availableCopies===0)return {text:'已借完',className:'empty'}; if(b.availableCopies<=1)return {text:'库存紧张',className:'low'}; return {text:'可借',className:'ok'} }
function recordText(r){ return r.status==='OVERDUE'?`逾期 ${r.overdueDays} 天`:r.status==='RETURNED'?'已归还':'借阅中' }
function recordClass(r){ return r.status==='OVERDUE'?'empty':r.status==='RETURNED'?'ok':'low' }
function coverOf(b){ return b.coverUrl || 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="160" height="220"><rect width="100%" height="100%" rx="16" fill="%23dbeafe"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%231e3a8a" font-size="20">BOOK</text></svg>' }
function dateAfter(days){ const d=new Date(); d.setDate(d.getDate()+days); return d.toISOString().slice(0,10) }
async function run(task, ok){ loading.value=true; try{ await task(); if(ok) show(ok,'info') }catch(e){ show(e.response?.data?.message || e.message || '操作失败','error') }finally{ loading.value=false } }
function show(text,type='info'){ message.value=text; messageType.value=type; setTimeout(()=>{ if(message.value===text) message.value='' }, 3000) }
</script>
