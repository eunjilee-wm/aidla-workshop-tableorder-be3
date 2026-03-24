<template>
  <div class="container">
    <div class="header">
      <h1>주문 내역</h1>
      <span>테이블 {{ tableNumber }}</span>
    </div>

    <div class="loading" v-if="loading">로딩 중...</div>
    <div class="error-msg" v-else-if="error" style="margin:20px">{{ error }}</div>

    <div v-else style="padding:16px;padding-bottom:80px">
      <div v-if="!orders.length" class="empty-msg">주문 내역이 없습니다.</div>

      <div v-else>
        <div style="text-align:right;font-size:14px;margin-bottom:12px;color:#555">
          총 주문액: <strong style="color:#4a90d9">{{ totalAmount.toLocaleString() }}원</strong>
        </div>

        <div class="card" :class="'order-card ' + order.status.toLowerCase()" v-for="order in orders" :key="order.id">
          <div class="order-header">
            <span style="font-weight:600">#{{ order.id }}</span>
            <span :class="'badge badge-' + order.status.toLowerCase()">{{ statusLabel(order.status) }}</span>
          </div>
          <div style="font-size:12px;color:#999;margin-bottom:8px">{{ formatDate(order.createdAt) }}</div>
          <div v-for="item in order.items" :key="item.id" style="font-size:14px;display:flex;justify-content:space-between">
            <span>{{ item.menuName }} × {{ item.quantity }}</span>
            <span>{{ (item.unitPrice * item.quantity).toLocaleString() }}원</span>
          </div>
          <div style="text-align:right;font-weight:600;margin-top:6px">{{ order.totalAmount.toLocaleString() }}원</div>
          <div v-if="order.rejectionReason" style="margin-top:6px;font-size:12px;color:#e74c3c">
            거절 사유: {{ order.rejectionReason }}
          </div>
        </div>
      </div>
    </div>

    <div class="bottom-nav">
      <router-link :to="`/store/${storeId}/table/${tableNumber}`"><span class="icon">🍽</span>메뉴</router-link>
      <router-link :to="`/store/${storeId}/table/${tableNumber}/cart`"><span class="icon">🛒</span>장바구니</router-link>
      <router-link :to="`/store/${storeId}/table/${tableNumber}/orders`" class="active"><span class="icon">📋</span>주문내역</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { customerOrderApi } from '../../api'

const route = useRoute()
const storeId = route.params.storeId
const tableNumber = route.params.tableNumber

const orders = ref([])
const totalAmount = ref(0)
const loading = ref(true)
const error = ref('')

const statusMap = { PENDING: '대기중', ACCEPTED: '승인', PREPARING: '준비중', COMPLETED: '완료', REJECTED: '거절' }
function statusLabel(s) { return statusMap[s] || s }
function formatDate(d) { return d ? new Date(d).toLocaleString('ko-KR') : '' }

onMounted(async () => {
  try {
    const { data } = await customerOrderApi.getOrders(storeId, tableNumber)
    orders.value = data.orders || []
    totalAmount.value = data.totalAmount || 0
  } catch (e) {
    error.value = '주문 내역을 불러올 수 없습니다.'
  } finally { loading.value = false }
})
</script>
