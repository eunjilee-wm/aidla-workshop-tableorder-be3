<template>
  <nav class="admin-nav">
    <span class="logo">🏪 {{ auth.storeName }}</span>
    <router-link to="/admin/orders" :class="{ active: $route.path === '/admin/orders' }">주문관리</router-link>
    <router-link to="/admin/menus" :class="{ active: $route.path === '/admin/menus' }">메뉴관리</router-link>
    <router-link to="/admin/tables" :class="{ active: $route.path === '/admin/tables' }">테이블관리</router-link>
    <span class="spacer"></span>
    <button class="logout" @click="doLogout">로그아웃</button>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const auth = useAuthStore()

function doLogout() {
  auth.logout()
  router.push('/admin/login')
}
</script>
