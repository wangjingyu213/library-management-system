import axios from 'axios'

const api = axios.create({ baseURL: import.meta.env.VITE_API_BASE_URL || '/api' })

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('library_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export async function login(username, password) {
  const { data } = await api.post('/auth/login', { username, password })
  localStorage.setItem('library_token', data.token)
  localStorage.setItem('library_user', JSON.stringify(data))
  return data
}

export function logout() {
  localStorage.removeItem('library_token')
  localStorage.removeItem('library_user')
}

export function getStoredUser() {
  const raw = localStorage.getItem('library_user')
  return raw ? JSON.parse(raw) : null
}

export async function listBooks(params = {}) {
  const { data } = await api.get('/books', { params })
  return data
}

export async function createBook(book) {
  const { data } = await api.post('/books', book)
  return data
}

export async function updateBook(id, book) {
  const { data } = await api.put(`/books/${id}`, book)
  return data
}

export async function deleteBook(id) {
  await api.delete(`/books/${id}`)
}

export async function listCategories() {
  const { data } = await api.get('/categories')
  return data
}

export async function createCategory(category) {
  const { data } = await api.post('/categories', category)
  return data
}

export async function updateCategory(id, category) {
  const { data } = await api.put(`/categories/${id}`, category)
  return data
}

export async function deleteCategory(id) {
  await api.delete(`/categories/${id}`)
}

export async function listBorrowRecords(params = {}) {
  const { data } = await api.get('/borrow-records', { params })
  return data
}

export async function borrowBook(payload) {
  const { data } = await api.post('/borrow-records', payload)
  return data
}

export async function returnBook(id, payload = {}) {
  const { data } = await api.post(`/borrow-records/${id}/return`, payload)
  return data
}

export async function listUsers() {
  const { data } = await api.get('/admin/users')
  return data
}

export async function createUser(user) {
  const { data } = await api.post('/admin/users', user)
  return data
}

export async function updateUser(id, user) {
  const { data } = await api.put(`/admin/users/${id}`, user)
  return data
}

export async function deleteUser(id) {
  await api.delete(`/admin/users/${id}`)
}

export async function uploadCover(file) {
  const formData = new FormData()
  formData.append('file', file)
  const { data } = await api.post('/uploads/covers', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return data
}
