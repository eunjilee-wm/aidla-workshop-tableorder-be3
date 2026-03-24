# Component Methods

## 1. AuthController / AuthService

| Method | Input | Output | 설명 |
|--------|-------|--------|------|
| `login` | storeId, username, password | JWT token | 관리자 로그인 |

## 2. StoreController / StoreService

| Method | Input | Output | 설명 |
|--------|-------|--------|------|
| `getStore` | storeId | Store | 매장 정보 조회 |
| `validateStore` | storeId | boolean | 매장 존재 여부 검증 |

## 3. TableController / TableService

| Method | Input | Output | 설명 |
|--------|-------|--------|------|
| `validateTable` | storeId, tableNumber | boolean | 테이블 유효성 검증 |
| `getTableInfo` | storeId, tableNumber | Table | 테이블 정보 조회 |

## 4. MenuController / MenuService

| Method | Input | Output | 설명 |
|--------|-------|--------|------|
| `getMenusByCategory` | storeId, category? | List\<Menu\> | 카테고리별 메뉴 조회 |
| `getMenu` | menuId | Menu | 메뉴 상세 조회 |
| `createMenu` | storeId, MenuRequest | Menu | 메뉴 등록 |
| `updateMenu` | menuId, MenuRequest | Menu | 메뉴 수정 |
| `deleteMenu` | menuId | void | 메뉴 삭제 |
| `updateMenuOrder` | storeId, List\<MenuOrder\> | void | 메뉴 노출 순서 변경 |
| `uploadImage` | menuId, MultipartFile | String (imageUrl) | 이미지 업로드 |

## 5. OrderController / OrderService

| Method | Input | Output | 설명 |
|--------|-------|--------|------|
| `createOrder` | storeId, tableNumber, List\<OrderItem\> | Order | 주문 생성 (PENDING) |
| `getOrdersByTable` | storeId, tableNumber | List\<Order\> | 테이블 현재 세션 주문 조회 |
| `getOrdersByStore` | storeId, status? | List\<Order\> | 매장 주문 목록 조회 (관리자) |
| `approveOrder` | orderId | Order | 주문 승인 (ACCEPTED) |
| `rejectOrder` | orderId, reason? | Order | 주문 거절 (REJECTED) |
| `updateOrderStatus` | orderId, status | Order | 주문 상태 변경 |
| `deleteOrder` | orderId | void | 주문 삭제 |

## 6. SessionService (내부 서비스, Controller 없음)

| Method | Input | Output | 설명 |
|--------|-------|--------|------|
| `getOrCreateSession` | storeId, tableNumber | Session | 활성 세션 조회 또는 생성 |
| `completeSession` | storeId, tableNumber | void | 세션 종료 (이용 완료) |

## 7. OrderHistoryController / OrderHistoryService

| Method | Input | Output | 설명 |
|--------|-------|--------|------|
| `getHistory` | storeId, tableNumber, dateFrom?, dateTo? | List\<OrderHistory\> | 과거 주문 내역 조회 |
