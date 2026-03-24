# Component Dependencies

```
AuthService ──→ AdminRepository
StoreService ──→ StoreRepository
TableService ──→ TableRepository, StoreService
MenuService ──→ MenuRepository, CategoryRepository, FileStorageService
OrderService ──→ OrderRepository, SessionService, MenuService
SessionService ──→ SessionRepository, OrderRepository, OrderHistoryRepository
OrderHistoryService ──→ OrderHistoryRepository
```

## 의존 방향 규칙
- Controller → Service → Repository
- Service 간 의존은 단방향만 허용
- 순환 의존 없음
