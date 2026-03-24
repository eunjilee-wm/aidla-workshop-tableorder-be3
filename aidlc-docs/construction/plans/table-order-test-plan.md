# Test Plan for Table Order

## Unit Overview
- **Unit**: table-order
- **Stories**: US-1.1, US-1.2, US-2.1, US-4.1, US-4.2, US-5.1, US-5.2, US-5.3, US-6.1, US-6.2, US-6.3, US-7.1, US-7.2, US-7.3 (14개)
- **Excluded Stories**: US-3.1 (장바구니 - Frontend 로컬 기능, Backend 범위 외)
- **Requirements**: FR-1~FR-7, NFR-1~NFR-4

---

## Business Logic Layer Tests

### AuthService.login()
- **TC-001**: 유효한 자격증명으로 로그인 성공 → JWT 반환 + login_attempts 리셋
  - Given: DB에 관리자 계정 존재 (bcrypt 비밀번호, login_attempts=2)
  - When: 올바른 storeId, username, password로 login 호출
  - Then: JWT 토큰 반환 (storeId 포함), login_attempts가 0으로 리셋
  - Story: US-1.2 | Rule: BR-1.1, BR-1.3, BR-1.4
  - Status: ⬜ Not Started

- **TC-002**: 잘못된 비밀번호로 로그인 실패
  - Given: DB에 관리자 계정 존재 (login_attempts=0)
  - When: 잘못된 password로 login 호출
  - Then: InvalidCredentialsException 발생, login_attempts가 1로 증가
  - Story: US-1.2 | Rule: BR-1.2
  - Status: ⬜ Not Started

- **TC-003**: 5회 실패 후 계정 잠금
  - Given: login_attempts = 4인 관리자
  - When: 잘못된 password로 login 호출
  - Then: login_attempts=5, locked_until 설정 (현재+30분)
  - Story: US-1.2 | Rule: BR-1.2
  - Status: ⬜ Not Started

- **TC-004**: 잠금 상태에서 로그인 시도
  - Given: locked_until이 미래 시간인 관리자
  - When: 올바른 password로 login 호출
  - Then: AccountLockedException 발생
  - Story: US-1.2 | Rule: BR-1.2
  - Status: ⬜ Not Started

### StoreService.getStore()
- **TC-005**: 유효한 storeId로 매장 조회
  - Given: DB에 매장 존재
  - When: getStore(storeId) 호출
  - Then: Store 객체 반환
  - Story: US-1.1
  - Status: ⬜ Not Started

- **TC-006**: 존재하지 않는 storeId로 조회 실패
  - Given: DB에 해당 매장 없음
  - When: getStore(storeId) 호출
  - Then: StoreNotFoundException 발생
  - Story: US-1.1
  - Status: ⬜ Not Started

### TableService.validateTable()
- **TC-007**: 유효한 매장+테이블 조합 검증 성공
  - Given: DB에 매장과 테이블 존재
  - When: validateTable(storeId, tableNumber) 호출
  - Then: StoreTable 객체 반환
  - Story: US-1.1 | Rule: BR-1.5
  - Status: ⬜ Not Started

- **TC-008**: 존재하지 않는 테이블 검증 실패
  - Given: DB에 해당 테이블 없음
  - When: validateTable(storeId, tableNumber) 호출
  - Then: TableNotFoundException 발생
  - Story: US-1.1 | Rule: NFR-2 (QR코드 접근 보안)
  - Status: ⬜ Not Started

### MenuService
- **TC-009**: 매장 전체 메뉴 카테고리별 조회
  - Given: 매장에 카테고리 2개, 메뉴 각 2개
  - When: getMenusByStore(storeId) 호출
  - Then: 카테고리별 그룹화된 메뉴 목록 반환 (display_order 순)
  - Story: US-2.1
  - Status: ⬜ Not Started

- **TC-010**: 메뉴 생성 성공 (display_order 자동 부여)
  - Given: 유효한 카테고리 존재, 기존 메뉴 2개 (순서 1,2)
  - When: createMenu(storeId, request) 호출 (display_order 미지정)
  - Then: Menu 생성, display_order=3 자동 부여
  - Story: US-7.1 | Rule: BR-2.4
  - Status: ⬜ Not Started

- **TC-011**: 메뉴 수정 성공
  - Given: 기존 메뉴 존재
  - When: updateMenu(menuId, storeId, request) 호출
  - Then: 메뉴 정보 업데이트
  - Story: US-7.1
  - Status: ⬜ Not Started

- **TC-012**: 메뉴 삭제 시 순서 재정렬
  - Given: 카테고리에 메뉴 3개 (순서 1,2,3)
  - When: 순서 2 메뉴 deleteMenu 호출
  - Then: 메뉴 삭제, 남은 메뉴 순서 1,2로 재정렬
  - Story: US-7.1 | Rule: BR-2.5
  - Status: ⬜ Not Started

- **TC-013**: 메뉴 순서 변경
  - Given: 카테고리에 메뉴 3개
  - When: updateMenuOrder 호출
  - Then: display_order 업데이트
  - Story: US-7.3 | Rule: BR-2.4
  - Status: ⬜ Not Started

- **TC-014**: 이미지 업로드 성공
  - Given: 기존 메뉴 존재, jpg 파일
  - When: uploadImage(menuId, storeId, file) 호출
  - Then: 파일 저장, image_url 업데이트
  - Story: US-7.2
  - Status: ⬜ Not Started

- **TC-028**: 메뉴 생성 시 필수 필드 누락 검증
  - Given: name이 빈 문자열인 요청
  - When: createMenu(storeId, request) 호출
  - Then: 검증 에러 발생 (name 필수)
  - Story: US-7.1 | Rule: BR-2.1
  - Status: ⬜ Not Started

- **TC-029**: 메뉴 생성 시 가격 0 이하 검증
  - Given: price=0인 요청
  - When: createMenu(storeId, request) 호출
  - Then: 검증 에러 발생 (price > 0)
  - Story: US-7.1 | Rule: BR-2.2
  - Status: ⬜ Not Started

- **TC-031**: 지원하지 않는 이미지 형식 업로드 실패
  - Given: 기존 메뉴 존재, .bmp 파일
  - When: uploadImage(menuId, storeId, file) 호출
  - Then: 검증 에러 발생 (허용 형식: jpg, jpeg, png, gif)
  - Story: US-7.2 | Rule: BR-2.3
  - Status: ⬜ Not Started

### OrderService
- **TC-015**: 주문 생성 성공 (새 세션 자동 생성)
  - Given: 유효한 매장/테이블, 활성 세션 없음, 메뉴 항목 2개
  - When: createOrder 호출
  - Then: 새 세션 생성, Order(PENDING) 생성, OrderItem에 menu_name/unit_price 스냅샷 저장, totalAmount 계산
  - Story: US-4.1 | Rule: BR-3.1, BR-3.2, BR-3.3, BR-4.2
  - Status: ⬜ Not Started

- **TC-016**: 주문 생성 성공 (기존 세션에 추가)
  - Given: 유효한 매장/테이블, 활성 세션 존재
  - When: createOrder 호출
  - Then: 기존 세션에 Order 연결
  - Story: US-4.1 | Rule: BR-4.1
  - Status: ⬜ Not Started

- **TC-030**: 빈 주문 항목으로 주문 생성 실패
  - Given: 유효한 매장/테이블, 주문 항목 0개
  - When: createOrder 호출
  - Then: 검증 에러 발생 (최소 1개 항목 필요)
  - Story: US-4.1 | Rule: BR-3.4
  - Status: ⬜ Not Started

- **TC-017**: 테이블별 현재 주문 조회 + 총 주문액
  - Given: 활성 세션에 ACCEPTED(10000원) 1건, PENDING(5000원) 1건
  - When: getOrdersByTable 호출
  - Then: 주문 2건 반환, 총 주문액=10000원 (ACCEPTED만 합산)
  - Story: US-4.2 | Rule: BR-5.1, BR-5.2
  - Status: ⬜ Not Started

- **TC-018**: 주문 승인 (PENDING → ACCEPTED)
  - Given: PENDING 상태 주문
  - When: approveOrder 호출
  - Then: 상태 ACCEPTED로 변경
  - Story: US-5.2 | Rule: BR-3.5
  - Status: ⬜ Not Started

- **TC-019**: 주문 거절 (PENDING → REJECTED) + 사유
  - Given: PENDING 상태 주문
  - When: rejectOrder(orderId, storeId, "재료 소진") 호출
  - Then: 상태 REJECTED, rejection_reason="재료 소진" 저장
  - Story: US-5.2 | Rule: BR-3.5, BR-3.6
  - Status: ⬜ Not Started

- **TC-020**: 잘못된 상태 전이 거부 (REJECTED → ACCEPTED)
  - Given: REJECTED 상태 주문
  - When: approveOrder 호출
  - Then: InvalidOrderStateException 발생
  - Story: US-5.2 | Rule: BR-3.5
  - Status: ⬜ Not Started

- **TC-021**: 주문 상태 변경 (ACCEPTED → PREPARING → COMPLETED)
  - Given: ACCEPTED 상태 주문
  - When: updateOrderStatus(orderId, storeId, "PREPARING") 호출
  - Then: 상태 PREPARING으로 변경
  - Story: US-5.3 | Rule: BR-3.5
  - Status: ⬜ Not Started

- **TC-022**: 주문 삭제 (관리자 직권, 모든 상태에서 가능)
  - Given: ACCEPTED 상태 주문
  - When: deleteOrder 호출
  - Then: 주문 및 OrderItem 삭제
  - Story: US-6.1 | Rule: BR-3.7
  - Status: ⬜ Not Started

- **TC-023**: 매장별 주문 목록 조회 (상태 필터)
  - Given: 매장에 PENDING 2건, ACCEPTED 1건 존재
  - When: getOrdersByStore(storeId, "PENDING") 호출
  - Then: PENDING 주문 2건만 반환
  - Story: US-5.1
  - Status: ⬜ Not Started

### SessionService
- **TC-024**: 활성 세션 없을 때 새 세션 생성
  - Given: 해당 테이블에 활성 세션 없음
  - When: getOrCreateSession 호출
  - Then: 새 Session(status=ACTIVE) 생성
  - Story: US-4.1 | Rule: BR-4.1, BR-4.2
  - Status: ⬜ Not Started

- **TC-025**: 세션 종료 (이용 완료) - 주문 이력 이동
  - Given: 활성 세션에 주문 2건 (OrderItem 각 2개)
  - When: completeSession 호출
  - Then: OrderHistory 2건 + OrderHistoryItem 4개 생성, Order/OrderItem 삭제, Session.status=COMPLETED, completedAt 설정
  - Story: US-6.2 | Rule: BR-4.3, BR-4.4
  - Status: ⬜ Not Started

### OrderHistoryService
- **TC-026**: 과거 주문 내역 조회
  - Given: OrderHistory 데이터 3건 존재
  - When: getHistory(storeId, tableNumber) 호출
  - Then: 시간 역순 목록 3건 반환 (주문번호, 시각, 메뉴 목록, 총 금액, 이용 완료 시각 포함)
  - Story: US-6.3
  - Status: ⬜ Not Started

- **TC-027**: 날짜 필터링 조회
  - Given: 3/18, 3/19, 3/20 각 1건의 OrderHistory 존재
  - When: getHistory(storeId, tableNumber, 3/19, 3/20) 호출
  - Then: 3/19, 3/20 주문 2건만 반환
  - Story: US-6.3
  - Status: ⬜ Not Started

---

## Requirements Coverage

| Requirement | Test Cases | Status |
|-------------|------------|--------|
| FR-1 (매장 관리) | TC-005, TC-006 | ⬜ Pending |
| FR-2.1 (관리자 인증) | TC-001~TC-004 | ⬜ Pending |
| FR-2.2 (고객 접근) | TC-007, TC-008 | ⬜ Pending |
| FR-3.1~3.4 (메뉴 CRUD) | TC-009~TC-012, TC-028, TC-029 | ⬜ Pending |
| FR-3.5 (이미지) | TC-014, TC-031 | ⬜ Pending |
| FR-3.6 (검증) | TC-028, TC-029, TC-031 | ⬜ Pending |
| FR-4.1 (주문 생성) | TC-015, TC-016, TC-030 | ⬜ Pending |
| FR-4.2 (주문 상태) | TC-018~TC-021 | ⬜ Pending |
| FR-4.3 (승인/거절) | TC-018, TC-019, TC-020 | ⬜ Pending |
| FR-4.4 (상태 변경) | TC-021 | ⬜ Pending |
| FR-4.5 (주문 삭제) | TC-022 | ⬜ Pending |
| FR-4.6~4.8 (조회/총액) | TC-017 | ⬜ Pending |
| FR-5 (세션 관리) | TC-024, TC-025 | ⬜ Pending |
| FR-6 (주문 모니터링) | TC-023 | ⬜ Pending |
| FR-7 (주문 내역) | TC-026, TC-027 | ⬜ Pending |
| NFR-2 (보안) | TC-001~TC-004, TC-007, TC-008 | ⬜ Pending |
| NFR-3 (데이터 무결성) | TC-015, TC-025, TC-030 | ⬜ Pending |

## Story Coverage

| Story | Test Cases | Status |
|-------|------------|--------|
| US-1.1 QR코드 접근 | TC-005~TC-008 | ⬜ Pending |
| US-1.2 관리자 로그인 | TC-001~TC-004 | ⬜ Pending |
| US-2.1 메뉴 조회 | TC-009 | ⬜ Pending |
| US-4.1 주문 생성 | TC-015, TC-016, TC-024, TC-030 | ⬜ Pending |
| US-4.2 주문 내역 조회 | TC-017 | ⬜ Pending |
| US-5.1 주문 모니터링 | TC-023 | ⬜ Pending |
| US-5.2 주문 승인/거절 | TC-018~TC-020 | ⬜ Pending |
| US-5.3 주문 상태 변경 | TC-021 | ⬜ Pending |
| US-6.1 주문 삭제 | TC-022 | ⬜ Pending |
| US-6.2 세션 종료 | TC-025 | ⬜ Pending |
| US-6.3 과거 주문 내역 | TC-026, TC-027 | ⬜ Pending |
| US-7.1 메뉴 CRUD | TC-010~TC-012, TC-028, TC-029 | ⬜ Pending |
| US-7.2 이미지 업로드 | TC-014, TC-031 | ⬜ Pending |
| US-7.3 메뉴 순서 조정 | TC-013 | ⬜ Pending |
