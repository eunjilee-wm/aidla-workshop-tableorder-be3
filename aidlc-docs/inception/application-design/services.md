# Services

### 1. AuthService
- **의존**: AdminRepository, JwtUtil, PasswordEncoder

### 2. StoreService
- **의존**: StoreRepository

### 3. TableService
- **의존**: TableRepository, StoreService

### 4. MenuService
- **의존**: MenuRepository, CategoryRepository, FileStorageService

### 5. OrderService
- **의존**: OrderRepository, SessionService, MenuService

### 6. SessionService
- **의존**: SessionRepository, OrderRepository, OrderHistoryRepository

### 7. OrderHistoryService
- **의존**: OrderHistoryRepository

### 8. FileStorageService
- **의존**: 로컬 파일 시스템
