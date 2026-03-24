# Contract/Interface Definition for Table Order

## Unit Context
- **Stories**: US-1.1, US-1.2, US-2.1, US-4.1, US-4.2, US-5.1, US-5.2, US-5.3, US-6.1, US-6.2, US-6.3, US-7.1, US-7.2, US-7.3
- **Excluded Stories**: US-3.1 (장바구니 - Frontend 로컬 기능, Backend 범위 외)
- **Dependencies**: None (단일 유닛)
- **Database Entities**: Store, Admin, StoreTable, Category, Menu, Session, OrderEntity, OrderItem, OrderHistory, OrderHistoryItem
- **Tech Stack**: Java 17, Spring Boot 3.x, MySQL, JPA, JWT, Swagger

---

## Repository Layer

### StoreRepository
- `findByStoreId(storeId: String) -> Optional<Store>`: 매장 식별자로 조회

### AdminRepository
- `findByStoreIdAndUsername(storeId: Long, username: String) -> Optional<Admin>`: 매장+사용자명으로 관리자 조회

### StoreTableRepository
- `findByStoreIdAndTableNumber(storeId: Long, tableNumber: int) -> Optional<StoreTable>`: 매장+테이블번호로 조회

### CategoryRepository
- `findByStoreIdOrderByDisplayOrder(storeId: Long) -> List<Category>`: 매장별 카테고리 목록

### MenuRepository
- `findByCategoryIdOrderByDisplayOrder(categoryId: Long) -> List<Menu>`: 카테고리별 메뉴 목록
- `findByStoreIdOrderByDisplayOrder(storeId: Long) -> List<Menu>`: 매장별 전체 메뉴
- `findMaxDisplayOrderByCategoryId(categoryId: Long) -> Optional<Integer>`: 카테고리 내 최대 순서

### SessionRepository
- `findByStoreIdAndTableNumberAndStatus(storeId: Long, tableNumber: int, status: String) -> Optional<Session>`: 활성 세션 조회

### OrderRepository
- `findBySessionIdOrderByCreatedAtAsc(sessionId: Long) -> List<OrderEntity>`: 세션별 주문 목록
- `findByStoreIdOrderByCreatedAtDesc(storeId: Long) -> List<OrderEntity>`: 매장별 주문 목록
- `findByStoreIdAndStatusOrderByCreatedAtDesc(storeId: Long, status: String) -> List<OrderEntity>`: 매장별+상태별 주문

### OrderHistoryRepository
- `findByStoreIdAndTableNumberOrderByOrderedAtDesc(storeId: Long, tableNumber: int) -> List<OrderHistory>`: 과거 주문 조회
- `findByStoreIdAndTableNumberAndOrderedAtBetweenOrderByOrderedAtDesc(storeId: Long, tableNumber: int, from: LocalDateTime, to: LocalDateTime) -> List<OrderHistory>`: 날짜 필터 과거 주문 조회

---

## Business Logic Layer

### AuthService
- `login(storeId: String, username: String, password: String) -> LoginResponse`
  - Returns: JWT token, 매장 정보
  - Raises: InvalidCredentialsException, AccountLockedException
  - Rules: BR-1.1~BR-1.4 (bcrypt 검증, 5회 실패 잠금 30분, JWT 16시간, 성공 시 login_attempts 리셋)

### StoreService
- `getStore(storeId: String) -> Store`
  - Raises: StoreNotFoundException
- `validateStore(storeId: String) -> Store`
  - Raises: StoreNotFoundException

### TableService
- `validateTable(storeId: Long, tableNumber: int) -> StoreTable`
  - Raises: TableNotFoundException

### MenuService
- `getMenusByStore(storeId: Long) -> List<CategoryMenuResponse>`: 매장 전체 메뉴 (카테고리별 그룹)
- `getMenu(menuId: Long) -> Menu`
  - Raises: MenuNotFoundException
- `createMenu(storeId: Long, request: MenuCreateRequest) -> Menu`
  - Validation: name 필수, price > 0, categoryId 필수 (BR-2.1, BR-2.2)
  - Rules: display_order 미지정 시 카테고리 마지막 순서 자동 부여 (BR-2.4)
- `updateMenu(menuId: Long, storeId: Long, request: MenuUpdateRequest) -> Menu`
- `deleteMenu(menuId: Long, storeId: Long) -> void`
  - Rules: 삭제 시 카테고리 내 순서 자동 재정렬 (BR-2.5)
- `updateMenuOrder(storeId: Long, request: List<MenuOrderRequest>) -> void`
- `uploadImage(menuId: Long, storeId: Long, file: MultipartFile) -> String`
  - Validation: 허용 형식 jpg, jpeg, png, gif만 (BR-2.3)

### OrderService
- `createOrder(storeId: String, tableNumber: int, request: OrderCreateRequest) -> OrderResponse`
  - Validation: 주문 항목 최소 1개 (BR-3.4)
  - Rules: status=PENDING, menu_name/unit_price 스냅샷 (BR-3.1, BR-3.2, BR-3.3)
  - Raises: StoreNotFoundException, TableNotFoundException, MenuNotFoundException, InvalidOrderException
- `getOrdersByTable(storeId: Long, tableNumber: int) -> TableOrdersResponse`
  - Returns: 주문 목록 + 총 주문액 (ACCEPTED/PREPARING/COMPLETED만 합산, BR-5.1, BR-5.2)
- `getOrdersByStore(storeId: Long, status: String?) -> List<OrderResponse>`
- `approveOrder(orderId: Long, storeId: Long) -> OrderResponse`
  - Rules: PENDING → ACCEPTED만 허용 (BR-3.5)
  - Raises: OrderNotFoundException, InvalidOrderStateException
- `rejectOrder(orderId: Long, storeId: Long, reason: String?) -> OrderResponse`
  - Rules: PENDING → REJECTED만 허용, 거절 사유 선택 입력 (BR-3.5, BR-3.6)
  - Raises: OrderNotFoundException, InvalidOrderStateException
- `updateOrderStatus(orderId: Long, storeId: Long, status: String) -> OrderResponse`
  - Rules: ACCEPTED→PREPARING, PREPARING→COMPLETED만 허용 (BR-3.5)
  - Raises: OrderNotFoundException, InvalidOrderStateException
- `deleteOrder(orderId: Long, storeId: Long) -> void`
  - Rules: 모든 상태에서 삭제 가능 (BR-3.7)
  - Raises: OrderNotFoundException

### SessionService
- `getOrCreateSession(storeId: Long, tableNumber: int) -> Session`
  - Rules: 테이블당 활성 세션 최대 1개 (BR-4.1, BR-4.2)
- `completeSession(storeId: Long, tableNumber: int) -> void`
  - Rules: 트랜잭션으로 Order→OrderHistory 이동, 리셋 (BR-4.3, BR-4.4)
  - Raises: SessionNotFoundException

### OrderHistoryService
- `getHistory(storeId: Long, tableNumber: int, dateFrom: LocalDate?, dateTo: LocalDate?) -> List<OrderHistoryResponse>`

### FileStorageService
- `store(file: MultipartFile) -> String`: 파일 저장, URL 반환
- `delete(filePath: String) -> void`: 파일 삭제

---

## API Layer

### AuthController
- `POST /api/auth/login`: 관리자 로그인 → LoginResponse

### StoreController
- `GET /api/stores/{storeId}`: 매장 정보 조회

### TableController
- `GET /api/stores/{storeId}/tables/{tableNumber}`: 테이블 유효성 검증 + 매장 메뉴 반환

### MenuController (관리자)
- `GET /api/admin/menus`: 매장 전체 메뉴 조회
- `POST /api/admin/menus`: 메뉴 등록
- `PUT /api/admin/menus/{menuId}`: 메뉴 수정
- `DELETE /api/admin/menus/{menuId}`: 메뉴 삭제
- `PUT /api/admin/menus/order`: 메뉴 순서 변경
- `POST /api/admin/menus/{menuId}/image`: 이미지 업로드

### MenuController (고객)
- `GET /api/stores/{storeId}/menus`: 매장 메뉴 조회 (카테고리별)

### OrderController (고객)
- `POST /api/stores/{storeId}/tables/{tableNumber}/orders`: 주문 생성
- `GET /api/stores/{storeId}/tables/{tableNumber}/orders`: 테이블 현재 주문 조회

### OrderController (관리자)
- `GET /api/admin/orders`: 매장 주문 목록 (status 필터 옵션)
- `PUT /api/admin/orders/{orderId}/approve`: 주문 승인
- `PUT /api/admin/orders/{orderId}/reject`: 주문 거절
- `PUT /api/admin/orders/{orderId}/status`: 주문 상태 변경
- `DELETE /api/admin/orders/{orderId}`: 주문 삭제

### SessionController (관리자)
- `POST /api/admin/tables/{tableNumber}/complete`: 이용 완료 (세션 종료)

### OrderHistoryController (관리자)
- `GET /api/admin/tables/{tableNumber}/history`: 과거 주문 내역 조회
