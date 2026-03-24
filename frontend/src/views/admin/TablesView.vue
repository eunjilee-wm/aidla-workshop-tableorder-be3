<template>
  <div>
    <AdminNav />
    <div class="admin-container">
      <h2 style="margin-bottom:16px">테이블 관리</h2>

      <div class="form-group" style="max-width:300px;margin-bottom:20px">
        <label>테이블 번호 선택</label>
        <div style="display:flex;gap:8px">
          <input v-model.number="selectedTable" type="number" min="1" placeholder="테이블 번호">
          <button class="btn-primary" @click="loadTable">조회</button>
        </div>
      </div>

      <div class="error-msg" v-if="error">{{ error }}</div>
      <div class="success-msg" v-if="success">{{ success }}</div>

      <div v-if="tableData" style="margin-top:16px">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
          <h3>테이블 {{ selectedTable }} - 현재 주문</h3>
          <div style="display:flex;gap:8px">
            <button class="btn-secondary" @click="showHistory = !showHistory">
              {{ showHistory ? '현재 주문' : '과거 내역' }}
            </button>
            <button class="btn-danger" @click="confirmComplete = true" v-if="tableData.orders.length">이용 완료</button>
          </div>
        </div>

        <!-- 현재 주문 -->
        <div v-if="!showHistory">
          <div v-if="!tableData.orders.length" class="empty-msg">현재 주문이 없습니다.</div>
          <div v-else>
            <div style="text-align:right;margin-bottom:12px;font-size:14px">
              총 주문액: <strong style="color:#4a90d9">{{ tableData.totalAmount.toLocaleString() }}원</strong>
            </div>
            <div class="card order-card" :class="order.status.toLowerCase()" v-for="order in tableData.orders" :key="order.id">
              <div class="order-header">
                <span style="font-weight:600">#{{ order.id }}</span>
                <span :class="'badge badge-' + order.status.toLowerCase()">{{ statusLabel(order.status) }}</span>
              </div>
              <div style="font-size:12px;color:#999;margin-bottom:6px">{{ formatDate(order.createdAt) }}</div>
              <div v-for="item in order.items" :key="item.id" style="font-size:14px;display:flex;justify-content:space-between">
                <span>{{ item.menuName }} × {{ item.quantity }}</span>
                <span>{{ (item.unitPrice * item.quantity).toLocaleString() }}원</span>
              </div>
              <div style="text-align:right;font-weight:600;margin-top:4px">{{ order.totalAmount.toLocaleString() }}원</div>
            </div>
          </div>
        </div>

        <!-- 과거 내역 -->
        <div v-else>
          <div style="display:flex;gap:8px;margin-bottom:12px;align-items:flex-end">
            <div class="form-group" style="margin:0"><label>시작일</label><input type="date" v-model="dateFrom"></div>
            <div class="form-group" style="margin:0"><label>종료일</label><input type="date" v-model="dateTo"></div>
            <button class="btn-primary btn-sm" @click="loadHistory">검색</button>
          </div>
          <div class="loading" v-if="historyLoading">로딩 중...</div>
          <div v-else-if="!history.length" class="empty-msg">과거 주문 내역이 없습니다.</div>
          <div v-else>
            <div class="card" v-for="h in history" :key="h.id">
              <div style="display:flex;justify-content:space-between;margin-bottom:6px">
                <span style="font-weight:600">주문 #{{ h.originalOrderId }}</span>
                <span :class="'badge badge-' + h.status.toLowerCase()">{{ statusLabel(h.status) }}</span>
              </div>
              <div style="font-size:12px;color:#999">주문: {{ formatDate(h.orderedAt) }} | 완료: {{ formatDate(h.sessionCompletedAt) }}</div>
              <div v-for="item in h.items" :key="item.menuName" style="font-size:14px;display:flex;justify-content:space-between;margin-top:4px">
                <span>{{ item.menuName }} × {{ item.quantity }}</span>
                <span>{{ (item.unitPrice * item.quantity).toLocaleString() }}원</span>
              </div>
              <div style="text-align:right;font-weight:600;margin-top:4px">{{ h.totalAmount.toLocaleString() }}원</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 이용 완료 확인 모달 -->
    <div class="modal-overlay" v-if="confirmComplete">
      <div class="modal">
        <h3>이용 완료</h3>
        <p>테이블 {{ selectedTable }}의 세션을 종료하시겠습니까?<br>주문 내역이 과거 이력으로 이동됩니다.</p>
        <div class="modal-actions">
          <button class="btn-secondary" @click="confirmComplete = false">취소</button>
          <button class="btn-danger" @click="completeSession">확인</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AdminNav from '../../components/AdminNav.vue'
import { customerOrderApi, adminSessionApi, adminHistoryApi } from '../../api'
import { useAuthStore } from '../../stores/auth'

const auth = useAuthStore()
const selectedTable = ref(1)
const tableData = ref(null)
const showHistory = ref(false)
const history = ref([])
const historyLoading = ref(false)
const dateFrom = ref('')
const dateTo = ref('')
const confirmComplete = ref(false)
const error = ref('')
const success = ref('')

const statusMap = { PENDING: '대기중', ACCEPTED: '승인', PREPARING: '준비중', COMPLETED: '완료', REJECTED: '거절' }
function statusLabel(s) { return statusMap[s] || s }
function formatDate(d) { return d ? new Date(d).toLocaleString('ko-KR') : '' }

async function loadTable() {
  error.value = ''; showHistory.value = false
  try {
    const { data } = await customerOrderApi.getOrders(auth.storeId, selectedTable.value)
    tableData.value = data
  } catch (e) { error.value = '테이블 정보를 불러올 수 없습니다.' }
}

async function loadHistory() {
  historyLoading.value = true
  try {
    const params = {}
    if (dateFrom.value) params.dateFrom = dateFrom.value
    if (dateTo.value) params.dateTo = dateTo.value
    const { data } = await adminHistoryApi.get(selectedTable.value, params)
    history.value = data
  } catch { history.value = [] }
  finally { historyLoading.value = false }
}

async function completeSession() {
  try {
    await adminSessionApi.complete(selectedTable.value)
    confirmComplete.value = false
    success.value = '이용 완료 처리되었습니다.'
    setTimeout(() => success.value = '', 3000)
    loadTable()
  } catch (e) {
    error.value = e.response?.data?.message || '이용 완료 처리에 실패했습니다.'
    confirmComplete.value = false
  }
}

onMounted(() => { loadTable(); loadHistory() })
</script>
