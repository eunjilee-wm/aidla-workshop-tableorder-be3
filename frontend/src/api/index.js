import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

// 관리자 토큰 자동 첨부
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token && config.url.startsWith('/admin')) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(r => r, error => {
  if (error.response?.status === 401 && window.location.pathname.startsWith('/admin')) {
    localStorage.removeItem('token')
    window.location.href = '/admin/login'
  }
  return Promise.reject(error)
})

// 고객 API
export const storeApi = {
  getStore: (storeId) => api.get(`/stores/${storeId}`),
  getTableWithMenus: (storeId, tableNumber) => api.get(`/stores/${storeId}/tables/${tableNumber}`),
  getMenus: (storeId) => api.get(`/stores/${storeId}/menus`),
}

export const customerOrderApi = {
  create: (storeId, tableNumber, data) => api.post(`/stores/${storeId}/tables/${tableNumber}/orders`, data),
  getOrders: (storeId, tableNumber) => api.get(`/stores/${storeId}/tables/${tableNumber}/orders`),
}

// 관리자 API
export const authApi = {
  login: (data) => api.post('/auth/login', data),
}

export const adminMenuApi = {
  getAll: () => api.get('/admin/menus'),
  create: (data) => api.post('/admin/menus', data),
  update: (id, data) => api.put(`/admin/menus/${id}`, data),
  remove: (id) => api.delete(`/admin/menus/${id}`),
  updateOrder: (data) => api.put('/admin/menus/order', data),
  uploadImage: (id, file) => {
    const fd = new FormData()
    fd.append('file', file)
    return api.post(`/admin/menus/${id}/image`, fd, { headers: { 'Content-Type': 'multipart/form-data' } })
  },
}

export const adminOrderApi = {
  getAll: (status) => api.get('/admin/orders', { params: status ? { status } : {} }),
  approve: (id) => api.put(`/admin/orders/${id}/approve`),
  reject: (id, reason) => api.put(`/admin/orders/${id}/reject`, { reason }),
  updateStatus: (id, status) => api.put(`/admin/orders/${id}/status`, { status }),
  remove: (id) => api.delete(`/admin/orders/${id}`),
}

export const adminSessionApi = {
  complete: (tableNumber) => api.post(`/admin/tables/${tableNumber}/complete`),
}

export const adminHistoryApi = {
  get: (tableNumber, params) => api.get(`/admin/tables/${tableNumber}/history`, { params }),
}

export default api
