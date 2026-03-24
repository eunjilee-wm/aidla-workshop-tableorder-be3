<template>
  <div>
    <AdminNav />
    <div class="admin-container">
      <h2 style="margin-bottom:16px">주문 관리</h2>

      <div class="filter-bar">
        <button v-for="f in filters" :key="f.value" :class="{ active: currentFilter === f.value }" @click="loadOrders(f.value)">
          {{ f.label }}
        </button>
      </div>

      <div class="loading" v-if="loading">로딩 중...</div>
      <div class="error-msg" v-if="error">{{ error }}</div>

      <div v-if="!loading && !orders.length" class="empty-msg">주문이 없습니다.</div>

      <div v-for="order in orders" :key="order.id" class="card order-card" :class="order.status.toLowerCase()">
        <div class="order-header">
          <div>
            <strong>테이블 {{ order.tableNumber }}</strong>
            <span style="margin-left:8px;color:#999">#{{ order.id }}</span>
          </div>
          <span :class="'badge badge-' + order.status.toLowerCase()">{{ statusLabel(order.status) }}</span>
        </div>
        <div style="font-size:12px;color:#999;margin-bottom:8px">{{ formatDate(order.createdAt) }}</div>

        <div v-for="item in order.items" :key="item.id" style="font-size:14px;display:flex;justify-content:space-between">
          <span>{{ item.menuName }} × {{ item.quantity }}</span>
          <span>{{ (item.unitPrice * item.quantity).toLocaleString() }}원</span>
        </div>
        <div style="text-align:right;font-weight:700;margin-top:6px">{{ order.totalAmount.toLocaleString() }}원</div>

        <div v-if="order.rejectionReason" style="margin-top:6px;font-size:12px;color:#e74c3c">
          거절 사유: {{ order.rejectionReason }}
        </div>

        <div class="order-actions">
          <template v-if="order.status === 'PENDING'">
            <button class="btn-success btn-sm" @click="approve(order.id)">승인</button>
            <button class="btn-danger btn-sm" @click="openReject(order.id)">거절</button>
          </template>
          <template v-if="order.status === 'ACCEPTED'">
            <button class="btn-warning btn-sm" @click="changeStatus(order.id, 'PREPARING')">준비중</button>
          </template>
          <template v-if="order.status === 'PREPARING'">
            <button class="btn-success btn-sm" @click="changeStatus(order.id, 'COMPLETED')">완료</button>
          </template>
          <button class="btn-danger btn-sm" @click="confirmDelete(order.id)">삭제</button>
        </div>
      </div>
    </div>

    <!-- 거절 모달 -->
    <div class="modal-overlay" v-if="rejectModal.show">
      <div class="modal">
        <h3>주문 거절</h3>
        <div class="form-group">
          <label>거절 사유 (선택)</label>
          <input v-model="rejectModal.reason" placeholder="거절 사유를 입력하세요">
        </div>
        <div class="modal-actions">
          <button class="btn-secondary" @click="rejectModal.show = false">취소</button>
          <button class="btn-danger" @click="reject">거절</button>
        </div>
      </div>
    </div>

    <!-- 삭제 확인 모달 -->
    <div class="modal-overlay" v-if="deleteModal.show">
      <div class="modal">
        <h3>주문 삭제</h3>
        <p>이 주문을 삭제하시겠습니까?</p>
        <div class="modal-actions">
          <button class="btn-secondary" @click="deleteModal.show = false">취소</button>
          <button class="btn-danger" @click="doDelete">삭제</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import AdminNav from '../../components/AdminNav.vue'
import { adminOrderApi } from '../../api'

const filters = [
  { label: '전체', value: '' },
  { label: '대기중', value: 'PENDING' },
  { label: '승인', value: 'ACCEPTED' },
  { label: '준비중', value: 'PREPARING' },
  { label: '완료', value: 'COMPLETED' },
  { label: '거절', value: 'REJECTED' },
]

const orders = ref([])
const currentFilter = ref('')
const loading = ref(false)
const error = ref('')

const rejectModal = reactive({ show: false, orderId: null, reason: '' })
const deleteModal = reactive({ show: false, orderId: null })

const statusMap = { PENDING: '대기중', ACCEPTED: '승인', PREPARING: '준비중', COMPLETED: '완료', REJECTED: '거절' }
function statusLabel(s) { return statusMap[s] || s }
function formatDate(d) { return d ? new Date(d).toLocaleString('ko-KR') : '' }

async function loadOrders(status) {
  currentFilter.value = status
  loading.value = true; error.value = ''
  try {
    const { data } = await adminOrderApi.getAll(status || undefined)
    orders.value = data
  } catch (e) { error.value = '주문 목록을 불러올 수 없습니다.' }
  finally { loading.value = false }
}

async function approve(id) {
  try { await adminOrderApi.approve(id); loadOrders(currentFilter.value) }
  catch (e) { error.value = e.response?.data?.message || '승인 실패' }
}

function openReject(id) { rejectModal.show = true; rejectModal.orderId = id; rejectModal.reason = '' }
async function reject() {
  try { await adminOrderApi.reject(rejectModal.orderId, rejectModal.reason); rejectModal.show = false; loadOrders(currentFilter.value) }
  catch (e) { error.value = e.response?.data?.message || '거절 실패' }
}

async function changeStatus(id, status) {
  try { await adminOrderApi.updateStatus(id, status); loadOrders(currentFilter.value) }
  catch (e) { error.value = e.response?.data?.message || '상태 변경 실패' }
}

function confirmDelete(id) { deleteModal.show = true; deleteModal.orderId = id }
async function doDelete() {
  try { await adminOrderApi.remove(deleteModal.orderId); deleteModal.show = false; loadOrders(currentFilter.value) }
  catch (e) { error.value = e.response?.data?.message || '삭제 실패' }
}

onMounted(() => loadOrders(''))
</script>
