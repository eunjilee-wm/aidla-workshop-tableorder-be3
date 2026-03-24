<template>
  <div>
    <AdminNav />
    <div class="admin-container">
      <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:16px">
        <h2>메뉴 관리</h2>
        <button class="btn-primary" @click="openCreate">+ 메뉴 등록</button>
      </div>

      <div class="loading" v-if="loading">로딩 중...</div>
      <div class="error-msg" v-if="error">{{ error }}</div>
      <div class="success-msg" v-if="success">{{ success }}</div>

      <div v-for="cat in categories" :key="cat.categoryId" style="margin-bottom:24px">
        <h3 style="margin-bottom:8px;color:#555">{{ cat.categoryName }}</h3>
        <table>
          <thead><tr><th>메뉴명</th><th>가격</th><th>설명</th><th>순서</th><th>작업</th></tr></thead>
          <tbody>
            <tr v-for="menu in cat.menus" :key="menu.id">
              <td style="display:flex;align-items:center;gap:8px">
                <img v-if="menu.imageUrl" :src="menu.imageUrl" style="width:36px;height:36px;border-radius:4px;object-fit:cover">
                {{ menu.name }}
              </td>
              <td>{{ menu.price.toLocaleString() }}원</td>
              <td style="color:#999;font-size:13px">{{ menu.description || '-' }}</td>
              <td>{{ menu.displayOrder }}</td>
              <td>
                <div style="display:flex;gap:4px">
                  <button class="btn-primary btn-sm" @click="openEdit(menu, cat.categoryId)">수정</button>
                  <button class="btn-secondary btn-sm" @click="openImage(menu.id)">이미지</button>
                  <button class="btn-danger btn-sm" @click="confirmDelete(menu.id)">삭제</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="!loading && !categories.length" class="empty-msg">등록된 메뉴가 없습니다.</div>
    </div>

    <!-- 메뉴 등록/수정 모달 -->
    <div class="modal-overlay" v-if="formModal.show">
      <div class="modal">
        <h3>{{ formModal.isEdit ? '메뉴 수정' : '메뉴 등록' }}</h3>
        <div class="form-group"><label>메뉴명 *</label><input v-model="formModal.data.name"></div>
        <div class="form-group"><label>가격 *</label><input v-model.number="formModal.data.price" type="number"></div>
        <div class="form-group"><label>설명</label><input v-model="formModal.data.description"></div>
        <div class="form-group">
          <label>카테고리 *</label>
          <select v-model="formModal.data.categoryId">
            <option v-for="c in categories" :key="c.categoryId" :value="c.categoryId">{{ c.categoryName }}</option>
          </select>
        </div>
        <div class="error-msg" v-if="formModal.error">{{ formModal.error }}</div>
        <div class="modal-actions">
          <button class="btn-secondary" @click="formModal.show = false">취소</button>
          <button class="btn-primary" @click="saveMenu">저장</button>
        </div>
      </div>
    </div>

    <!-- 이미지 업로드 모달 -->
    <div class="modal-overlay" v-if="imageModal.show">
      <div class="modal">
        <h3>이미지 업로드</h3>
        <div class="form-group">
          <input type="file" accept="image/jpeg,image/png,image/gif" @change="e => imageModal.file = e.target.files[0]">
        </div>
        <div class="error-msg" v-if="imageModal.error">{{ imageModal.error }}</div>
        <div class="modal-actions">
          <button class="btn-secondary" @click="imageModal.show = false">취소</button>
          <button class="btn-primary" @click="uploadImage">업로드</button>
        </div>
      </div>
    </div>

    <!-- 삭제 확인 모달 -->
    <div class="modal-overlay" v-if="deleteId">
      <div class="modal">
        <h3>메뉴 삭제</h3>
        <p>이 메뉴를 삭제하시겠습니까?</p>
        <div class="modal-actions">
          <button class="btn-secondary" @click="deleteId = null">취소</button>
          <button class="btn-danger" @click="doDelete">삭제</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import AdminNav from '../../components/AdminNav.vue'
import { adminMenuApi } from '../../api'

const categories = ref([])
const loading = ref(false)
const error = ref('')
const success = ref('')
const deleteId = ref(null)

const formModal = reactive({ show: false, isEdit: false, editId: null, error: '', data: { name: '', price: null, description: '', categoryId: null } })
const imageModal = reactive({ show: false, menuId: null, file: null, error: '' })

async function load() {
  loading.value = true; error.value = ''
  try { const { data } = await adminMenuApi.getAll(); categories.value = data }
  catch { error.value = '메뉴를 불러올 수 없습니다.' }
  finally { loading.value = false }
}

function openCreate() {
  formModal.isEdit = false; formModal.editId = null; formModal.error = ''
  formModal.data = { name: '', price: null, description: '', categoryId: categories.value[0]?.categoryId || null }
  formModal.show = true
}

function openEdit(menu, categoryId) {
  formModal.isEdit = true; formModal.editId = menu.id; formModal.error = ''
  formModal.data = { name: menu.name, price: menu.price, description: menu.description || '', categoryId }
  formModal.show = true
}

async function saveMenu() {
  const d = formModal.data
  if (!d.name || !d.price || !d.categoryId) { formModal.error = '필수 필드를 입력해주세요.'; return }
  if (d.price <= 0) { formModal.error = '가격은 0보다 커야 합니다.'; return }
  try {
    if (formModal.isEdit) { await adminMenuApi.update(formModal.editId, d) }
    else { await adminMenuApi.create(d) }
    formModal.show = false; showSuccess(formModal.isEdit ? '메뉴가 수정되었습니다.' : '메뉴가 등록되었습니다.'); load()
  } catch (e) { formModal.error = e.response?.data?.message || '저장 실패' }
}

function confirmDelete(id) { deleteId.value = id }
async function doDelete() {
  try { await adminMenuApi.remove(deleteId.value); deleteId.value = null; showSuccess('메뉴가 삭제되었습니다.'); load() }
  catch (e) { error.value = e.response?.data?.message || '삭제 실패'; deleteId.value = null }
}

function openImage(id) { imageModal.show = true; imageModal.menuId = id; imageModal.file = null; imageModal.error = '' }
async function uploadImage() {
  if (!imageModal.file) { imageModal.error = '파일을 선택해주세요.'; return }
  try { await adminMenuApi.uploadImage(imageModal.menuId, imageModal.file); imageModal.show = false; showSuccess('이미지가 업로드되었습니다.'); load() }
  catch (e) { imageModal.error = e.response?.data?.message || '업로드 실패' }
}

function showSuccess(msg) { success.value = msg; setTimeout(() => success.value = '', 3000) }

onMounted(load)
</script>
