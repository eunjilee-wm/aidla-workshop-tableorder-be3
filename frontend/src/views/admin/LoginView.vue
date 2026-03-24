<template>
  <div style="min-height:100vh;display:flex;align-items:center;justify-content:center;background:#f5f5f5">
    <div style="background:#fff;padding:40px;border-radius:12px;width:360px;box-shadow:0 4px 20px rgba(0,0,0,0.1)">
      <h2 style="text-align:center;margin-bottom:24px">🏪 관리자 로그인</h2>
      <div class="form-group">
        <label>매장 식별자</label>
        <input v-model="form.storeId" placeholder="매장 ID" @keyup.enter="login">
      </div>
      <div class="form-group">
        <label>사용자명</label>
        <input v-model="form.username" placeholder="Username" @keyup.enter="login">
      </div>
      <div class="form-group">
        <label>비밀번호</label>
        <input v-model="form.password" type="password" placeholder="Password" @keyup.enter="login">
      </div>
      <div class="error-msg" v-if="error">{{ error }}</div>
      <button class="btn-primary" style="width:100%;padding:12px;font-size:16px;margin-top:8px" @click="login" :disabled="loading">
        {{ loading ? '로그인 중...' : '로그인' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../../api'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({ storeId: '', username: '', password: '' })
const loading = ref(false)
const error = ref('')

async function login() {
  if (!form.storeId || !form.username || !form.password) { error.value = '모든 필드를 입력해주세요.'; return }
  loading.value = true; error.value = ''
  try {
    const { data } = await authApi.login(form)
    auth.setAuth(data)
    await router.push('/admin/orders')
    return
  } catch (e) {
    console.error('Login error:', e)
    error.value = e.response?.status === 403 ? '계정이 잠겼습니다. 잠시 후 다시 시도해주세요.'
      : e.response?.status === 401 ? '인증 정보가 올바르지 않습니다.' : '로그인에 실패했습니다.'
  } finally { loading.value = false }
}
</script>
