<template>
  <div class="container">
    <div class="header">
      <button @click="router.back()" style="background:none;color:#fff;font-size:18px;padding:0">← 장바구니</button>
    </div>

    <div v-if="!cart.items.length" class="empty-msg">장바구니가 비어있습니다.</div>

    <div v-else style="padding:16px;padding-bottom:120px">
      <div class="cart-item" v-for="item in cart.items" :key="item.menuId">
        <div>
          <div style="font-weight:600">{{ item.name }}</div>
          <div style="color:#4a90d9;font-size:14px">{{ (item.price * item.quantity).toLocaleString() }}원</div>
        </div>
        <div class="qty-control">
          <button @click="cart.updateQty(item.menuId, -1)">−</button>
          <span>{{ item.quantity }}</span>
          <button @click="cart.updateQty(item.menuId, 1)">+</button>
        </div>
      </div>

      <div style="margin-top:20px;padding-top:16px;border-top:2px solid #333;display:flex;justify-content:space-between;font-size:18px;font-weight:700">
        <span>총 금액</span>
        <span>{{ cart.totalAmount.toLocaleString() }}원</span>
      </div>

      <div style="display:flex;gap:8px;margin-top:20px">
        <button class="btn-secondary" style="flex:1" @click="cart.clear()">비우기</button>
        <button class="btn-primary" style="flex:2;font-size:16px;padding:14px" @click="placeOrder" :disabled="ordering">
          {{ ordering ? '주문 중...' : '주문하기' }}
        </button>
      </div>

      <div class="error-msg" v-if="error">{{ error }}</div>
    </div>

    <!-- 주문 완료 모달 -->
    <div class="modal-overlay" v-if="orderComplete">
      <div class="modal" style="text-align:center">
        <div style="font-size:48px;margin-bottom:12px">✅</div>
        <h3>주문이 완료되었습니다!</h3>
        <p style="color:#999;margin-top:8px">{{ countdown }}초 후 메뉴 화면으로 이동합니다.</p>
      </div>
    </div>

    <div class="bottom-nav">
      <router-link :to="`/store/${storeId}/table/${tableNumber}`"><span class="icon">🍽</span>메뉴</router-link>
      <router-link :to="`/store/${storeId}/table/${tableNumber}/cart`" class="active"><span class="icon">🛒</span>장바구니</router-link>
      <router-link :to="`/store/${storeId}/table/${tableNumber}/orders`"><span class="icon">📋</span>주문내역</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { customerOrderApi } from '../../api'
import { useCartStore } from '../../stores/cart'

const route = useRoute()
const router = useRouter()
const cart = useCartStore()

const storeId = route.params.storeId
const tableNumber = route.params.tableNumber
const ordering = ref(false)
const error = ref('')
const orderComplete = ref(false)
const countdown = ref(5)

async function placeOrder() {
  if (!cart.items.length) return
  ordering.value = true
  error.value = ''
  try {
    await customerOrderApi.create(storeId, tableNumber, {
      items: cart.items.map(i => ({ menuId: i.menuId, quantity: i.quantity }))
    })
    cart.clear()
    orderComplete.value = true
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
        router.push(`/store/${storeId}/table/${tableNumber}`)
      }
    }, 1000)
  } catch (e) {
    error.value = e.response?.data?.message || '주문에 실패했습니다. 다시 시도해주세요.'
  } finally { ordering.value = false }
}
</script>
