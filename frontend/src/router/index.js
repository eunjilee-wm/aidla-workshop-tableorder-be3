import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 고객
  { path: '/store/:storeId/table/:tableNumber', name: 'CustomerMenu', component: () => import('../views/customer/MenuView.vue') },
  { path: '/store/:storeId/table/:tableNumber/cart', name: 'CustomerCart', component: () => import('../views/customer/CartView.vue') },
  { path: '/store/:storeId/table/:tableNumber/orders', name: 'CustomerOrders', component: () => import('../views/customer/OrdersView.vue') },

  // 관리자
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/admin/LoginView.vue') },
  { path: '/admin/orders', name: 'AdminOrders', component: () => import('../views/admin/OrdersView.vue'), meta: { auth: true } },
  { path: '/admin/menus', name: 'AdminMenus', component: () => import('../views/admin/MenusView.vue'), meta: { auth: true } },
  { path: '/admin/tables', name: 'AdminTables', component: () => import('../views/admin/TablesView.vue'), meta: { auth: true } },

  { path: '/', redirect: '/admin/login' },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to) => {
  if (to.meta.auth && !localStorage.getItem('token')) {
    return { name: 'AdminLogin' }
  }
})

export default router
