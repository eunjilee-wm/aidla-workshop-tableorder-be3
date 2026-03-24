import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCartStore = defineStore('cart', () => {
  const items = ref(JSON.parse(localStorage.getItem('cart') || '[]'))

  const totalAmount = computed(() => items.value.reduce((s, i) => s + i.price * i.quantity, 0))
  const totalCount = computed(() => items.value.reduce((s, i) => s + i.quantity, 0))

  function save() { localStorage.setItem('cart', JSON.stringify(items.value)) }

  function addItem(menu) {
    const existing = items.value.find(i => i.menuId === menu.id)
    if (existing) { existing.quantity++ } else {
      items.value.push({ menuId: menu.id, name: menu.name, price: menu.price, quantity: 1 })
    }
    save()
  }

  function updateQty(menuId, delta) {
    const item = items.value.find(i => i.menuId === menuId)
    if (!item) return
    item.quantity += delta
    if (item.quantity <= 0) items.value = items.value.filter(i => i.menuId !== menuId)
    save()
  }

  function removeItem(menuId) {
    items.value = items.value.filter(i => i.menuId !== menuId)
    save()
  }

  function clear() { items.value = []; save() }

  return { items, totalAmount, totalCount, addItem, updateQty, removeItem, clear }
})
