<template>
  <div class="container">
    <div class="header">
      <h1>{{ storeName || '메뉴' }}</h1>
      <span>테이블 {{ tableNumber }}</span>
    </div>

    <div class="tab-bar" v-if="categories.length">
      <button v-for="cat in categories" :key="cat.categoryId"
        :class="{ active: selectedCategory === cat.categoryId }"
        @click="selectedCategory = cat.categoryId">
        {{ cat.categoryName }}
      </button>
    </div>

    <div class="loading" v-if="loading">로딩 중...</div>
    <div class="error-msg" v-else-if="error" style="margin:20px">{{ error }}</div>

    <div class="menu-grid" v-else>
      <div v-if="!filteredMenus.length" class="empty-msg">등록된 메뉴가 없습니다.</div>
      <div class="menu-card" v-for="menu in filteredMenus" :key="menu.id">
        <img :src="menu.imageUrl || '/placeholder.png'" :alt="menu.name"
          @error="e => e.target.src='data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 width=%2280%22 height=%2280%22><rect fill=%22%23f0f0f0%22 width=%2280%22 height=%2280%22/><text x=%2240%22 y=%2244%22 text-anchor=%22middle%22 fill=%22%23ccc%22 font-size=%2212%22>No Image</text></svg>'">
        <div class="menu-info">
          <h4>{{ menu.name }}</h4>
          <div class="price">{{ menu.price.toLocaleString() }}원</div>
          <div class="desc" v-if="menu.description">{{ menu.description }}</div>
        </div>
        <button class="add-btn" @click="cart.addItem(menu)">+</button>
      </div>
    </div>

    <!-- 장바구니 바 -->
    <div class="cart-bar" v-if="cart.totalCount > 0" @click="goCart">
      <span>🛒 {{ cart.totalCount }}개</span>
      <span>{{ cart.totalAmount.toLocaleString() }}원 &gt;</span>
    </div>

    <!-- 하단 네비 -->
    <div class="bottom-nav" :style="{ marginBottom: cart.totalCount > 0 ? '50px' : '0' }">
      <router-link :to="menuPath" class="active"><span class="icon">🍽</span>메뉴</router-link>
      <router-link :to="cartPath"><span class="icon">🛒</span>장바구니</router-link>
      <router-link :to="ordersPath"><span class="icon">📋</span>주문내역</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { storeApi } from '../../api'
import { useCartStore } from '../../stores/cart'

const route = useRoute()
const router = useRouter()
const cart = useCartStore()

const storeId = route.params.storeId
const tableNumber = route.params.tableNumber

const storeName = ref('')
const categories = ref([])
const selectedCategory = ref(null)
const loading = ref(true)
const error = ref('')

const filteredMenus = computed(() => {
  const cat = categories.value.find(c => c.categoryId === selectedCategory.value)
  return cat ? cat.menus : []
})

const menuPath = computed(() => `/store/${storeId}/table/${tableNumber}`)
const cartPath = computed(() => `/store/${storeId}/table/${tableNumber}/cart`)
const ordersPath = computed(() => `/store/${storeId}/table/${tableNumber}/orders`)

function goCart() { router.push(cartPath.value) }

onMounted(async () => {
  try {
    const { data } = await storeApi.getTableWithMenus(storeId, tableNumber)
    storeName.value = data.store.name
    categories.value = data.menus
    if (categories.value.length) selectedCategory.value = categories.value[0].categoryId
  } catch (e) {
    error.value = e.response?.status === 404 ? '유효하지 않은 매장 또는 테이블입니다.' : '메뉴를 불러올 수 없습니다.'
  } finally { loading.value = false }
})
</script>
