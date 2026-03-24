import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const storeId = ref(localStorage.getItem('adminStoreId') || '')
  const storeName = ref(localStorage.getItem('adminStoreName') || '')

  const isLoggedIn = computed(() => !!token.value)

  function setAuth(data) {
    token.value = data.token
    storeId.value = data.storeId
    storeName.value = data.storeName
    localStorage.setItem('token', data.token)
    localStorage.setItem('adminStoreId', data.storeId)
    localStorage.setItem('adminStoreName', data.storeName)
  }

  function logout() {
    token.value = ''; storeId.value = ''; storeName.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('adminStoreId')
    localStorage.removeItem('adminStoreName')
  }

  return { token, storeId, storeName, isLoggedIn, setAuth, logout }
})
