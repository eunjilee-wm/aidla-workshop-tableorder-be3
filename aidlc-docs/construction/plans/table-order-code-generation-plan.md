# Code Generation Plan - Table Order

## 접근 방식: TDD (Test-Driven Development)
## 프로젝트 구조: Spring Boot (Gradle) + MySQL

## Unit Context
- **Stories**: US-1.1~US-7.3 (15개 스토리)
- **기술 스택**: Java 17, Spring Boot 3.x, MySQL, JPA, JWT, Swagger
- **코드 위치**: Workspace root (`/Users/ieunji/my/aws/aidla-workshop-tableorder-be3/`)

---

## Execution Steps

### Phase 1: Project Setup
- [ ] Step 1: Spring Boot 프로젝트 초기화 (build.gradle, application.yml, 패키지 구조)
- [ ] Step 2: DB 스키마 생성 (schema.sql, data.sql seed 데이터)

### Phase 2: Domain & Repository Layer
- [ ] Step 3: Entity 클래스 생성 (Store, Admin, StoreTable, Category, Menu, Session, Order, OrderItem, OrderHistory, OrderHistoryItem)
- [ ] Step 4: Repository 인터페이스 생성
- [ ] Step 5: Repository 테스트 작성 및 구현

### Phase 3: Auth (US-1.2)
- [ ] Step 6: AuthService 테스트 작성 (로그인, JWT, 시도 제한)
- [ ] Step 7: AuthService 구현
- [ ] Step 8: AuthController + JWT 필터 구현
- [ ] Step 9: Security 설정 (고객 API 인증 제외, 관리자 API JWT 필수)

### Phase 4: Store & Table (US-1.1)
- [ ] Step 10: StoreService, TableService 테스트 작성 (QR 접근 검증)
- [ ] Step 11: StoreService, TableService 구현
- [ ] Step 12: StoreController, TableController 구현

### Phase 5: Menu (US-2.1, US-7.1, US-7.2, US-7.3)
- [ ] Step 13: MenuService 테스트 작성 (CRUD, 이미지, 순서)
- [ ] Step 14: MenuService, FileStorageService 구현
- [ ] Step 15: MenuController 구현

### Phase 6: Order (US-4.1, US-4.2, US-5.1, US-5.2, US-5.3, US-6.1)
- [ ] Step 16: OrderService 테스트 작성 (생성, 승인/거절, 상태 변경, 삭제, 상태 머신)
- [ ] Step 17: OrderService 구현
- [ ] Step 18: OrderController 구현

### Phase 7: Session (US-6.2)
- [ ] Step 19: SessionService 테스트 작성 (생성, 종료, 이력 이동)
- [ ] Step 20: SessionService 구현
- [ ] Step 21: SessionController (이용 완료 API)

### Phase 8: Order History (US-6.3)
- [ ] Step 22: OrderHistoryService 테스트 작성 (조회, 날짜 필터)
- [ ] Step 23: OrderHistoryService 구현
- [ ] Step 24: OrderHistoryController 구현

### Phase 9: Documentation & Config
- [ ] Step 25: Swagger 설정 및 API 문서화
- [ ] Step 26: README.md 작성
