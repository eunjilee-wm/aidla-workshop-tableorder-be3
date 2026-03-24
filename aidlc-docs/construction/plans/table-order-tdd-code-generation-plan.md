# TDD Code Generation Plan for Table Order

## Unit Context
- **Workspace Root**: /Users/ieunji/my/aws/aidla-workshop-tableorder-be3
- **Project Type**: Greenfield
- **Stories**: US-1.1, US-1.2, US-2.1, US-4.1, US-4.2, US-5.1, US-5.2, US-5.3, US-6.1, US-6.2, US-6.3, US-7.1, US-7.2, US-7.3 (14개)
- **Excluded Stories**: US-3.1 (장바구니 - Frontend 로컬 기능)
- **Package Base**: com.tableorder

---

## Plan Step 0: Project Setup & Contract Skeleton
- [x] Spring Boot 프로젝트 초기화 (build.gradle, application.yml)
- [x] 패키지 구조 생성
- [x] Entity 클래스 스켈레톤 (Store, Admin, StoreTable, Category, Menu, Session, OrderEntity, OrderItem, OrderHistory, OrderHistoryItem)
- [x] Repository 인터페이스 스켈레톤
- [x] Service 클래스 스켈레톤 (UnsupportedOperationException)
- [x] DTO/Request/Response 클래스
- [x] Exception 클래스
- [x] schema.sql, data.sql
- [x] 컴파일 확인 ✓

## Plan Step 1: Auth Layer (TDD) - US-1.2
- [x] JwtUtil 구현 (토큰 생성/검증)
- [x] AuthService.login() - RED-GREEN-REFACTOR
  - [x] RED: TC-001 (로그인 성공 + login_attempts 리셋)
  - [x] GREEN: 최소 구현
  - [x] RED: TC-002 (잘못된 비밀번호 + login_attempts 증가)
  - [x] GREEN: 비밀번호 검증 추가
  - [x] RED: TC-003 (5회 실패 잠금)
  - [x] GREEN: 잠금 로직 추가
  - [x] RED: TC-004 (잠금 상태 로그인)
  - [x] GREEN: 잠금 확인 추가
  - [x] REFACTOR: 코드 정리
  - [x] VERIFY: 전체 테스트 통과 ✓
- [x] AuthController + JwtAuthenticationFilter + SecurityConfig
  - Story: US-1.2 | Rule: BR-1.1~BR-1.5

## Plan Step 2: Store & Table Layer (TDD) - US-1.1
- [x] StoreService - RED-GREEN-REFACTOR
  - [x] RED: TC-005 (매장 조회 성공)
  - [x] GREEN: 최소 구현
  - [x] RED: TC-006 (매장 없음)
  - [x] GREEN: 예외 처리
  - [x] VERIFY: 테스트 통과 ✓
- [x] TableService - RED-GREEN-REFACTOR
  - [x] RED: TC-007 (테이블 검증 성공)
  - [x] GREEN: 최소 구현
  - [x] RED: TC-008 (테이블 없음)
  - [x] GREEN: 예외 처리
  - [x] VERIFY: 테스트 통과 ✓
- [x] StoreController, TableController
  - Story: US-1.1

## Plan Step 3: Menu Layer (TDD) - US-2.1, US-7.1, US-7.2, US-7.3
- [x] FileStorageService 구현
- [x] MenuService - RED-GREEN-REFACTOR
  - [x] RED: TC-009 (카테고리별 조회)
  - [x] GREEN: 최소 구현
  - [x] RED: TC-010 (메뉴 생성 + display_order 자동 부여)
  - [x] GREEN: 생성 구현
  - [x] RED: TC-028 (필수 필드 누락 검증)
  - [x] GREEN: 검증 추가
  - [x] RED: TC-029 (가격 0 이하 검증)
  - [x] GREEN: 가격 검증 추가
  - [x] RED: TC-011 (메뉴 수정)
  - [x] GREEN: 수정 구현
  - [x] RED: TC-012 (메뉴 삭제 + 재정렬)
  - [x] GREEN: 삭제 + 재정렬 구현
  - [x] RED: TC-013 (순서 변경)
  - [x] GREEN: 순서 변경 구현
  - [x] RED: TC-014 (이미지 업로드)
  - [x] GREEN: 이미지 업로드 구현
  - [x] RED: TC-031 (지원하지 않는 이미지 형식)
  - [x] GREEN: 형식 검증 추가
  - [x] REFACTOR: 코드 정리
  - [x] VERIFY: 전체 테스트 통과 ✓
- [x] MenuController (관리자 + 고객)
  - Story: US-2.1, US-7.1, US-7.2, US-7.3 | Rule: BR-2.1~BR-2.6

## Plan Step 4: Session Layer (TDD) - US-4.1, US-6.2
- [x] SessionService - RED-GREEN-REFACTOR
  - [x] RED: TC-024 (새 세션 생성)
  - [x] GREEN: 최소 구현
  - [x] RED: TC-025 (세션 종료 + 이력 이동 트랜잭션)
  - [x] GREEN: 트랜잭션 구현
  - [x] REFACTOR: 코드 정리
  - [x] VERIFY: 전체 테스트 통과 ✓
- [x] SessionController
  - Story: US-4.1, US-6.2 | Rule: BR-4.1~BR-4.4

## Plan Step 5: Order Layer (TDD) - US-4.1, US-4.2, US-5.1, US-5.2, US-5.3, US-6.1
- [x] OrderService - RED-GREEN-REFACTOR
  - [x] RED: TC-015 (주문 생성 + 새 세션 + 스냅샷)
  - [x] GREEN: 최소 구현
  - [x] RED: TC-016 (주문 생성 + 기존 세션)
  - [x] GREEN: 기존 세션 연결
  - [x] RED: TC-030 (빈 주문 항목 검증)
  - [x] GREEN: 항목 수 검증 추가
  - [x] RED: TC-017 (테이블 주문 조회 + 총 주문액 계산)
  - [x] GREEN: 조회 + 총 주문액 계산 (ACCEPTED/PREPARING/COMPLETED만)
  - [x] RED: TC-018 (주문 승인 PENDING→ACCEPTED)
  - [x] GREEN: 상태 전이 구현
  - [x] RED: TC-019 (주문 거절 + 사유)
  - [x] GREEN: 거절 + 사유 저장
  - [x] RED: TC-020 (잘못된 상태 전이 거부)
  - [x] GREEN: 상태 머신 검증
  - [x] RED: TC-021 (상태 변경 ACCEPTED→PREPARING)
  - [x] GREEN: 상태 변경 구현
  - [x] RED: TC-022 (주문 삭제 - 모든 상태)
  - [x] GREEN: 삭제 구현
  - [x] RED: TC-023 (매장 주문 목록 + 상태 필터)
  - [x] GREEN: 필터 조회 구현
  - [x] REFACTOR: 코드 정리
  - [x] VERIFY: 전체 테스트 통과 ✓
- [x] OrderController (고객 + 관리자)
  - Story: US-4.1, US-4.2, US-5.1, US-5.2, US-5.3, US-6.1 | Rule: BR-3.1~BR-3.7, BR-5.1~BR-5.2

## Plan Step 6: OrderHistory Layer (TDD) - US-6.3
- [x] OrderHistoryService - RED-GREEN-REFACTOR
  - [x] RED: TC-026 (과거 주문 조회 - 시간 역순)
  - [x] GREEN: 최소 구현
  - [x] RED: TC-027 (날짜 필터링)
  - [x] GREEN: 날짜 필터 구현
  - [x] REFACTOR: 코드 정리
  - [x] VERIFY: 전체 테스트 통과 ✓
- [x] OrderHistoryController
  - Story: US-6.3

## Plan Step 7: Documentation & Config
- [x] Swagger 설정 (OpenAPI 3.0)
- [x] GlobalExceptionHandler (통합 예외 처리)
- [x] README.md 업데이트

---

## Summary
- **Total Plan Steps**: 8 (Step 0~7) - ALL COMPLETE ✓
- **Total Test Cases**: 31 (TC-001~TC-031) - ALL PASSED ✓
- **Total Stories Covered**: 14 (US-3.1 제외)
- **Business Rules Covered**: BR-1.1~BR-6.2 전체
