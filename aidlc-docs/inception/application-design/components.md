# Application Components

## 레이어 구조

```
Controller Layer → Service Layer → Repository Layer → Database
```

---

## 1. Auth Component
- **목적**: 관리자 인증 및 JWT 토큰 관리
- **책임**:
  - 관리자 로그인 처리
  - JWT 토큰 발급/검증
  - 로그인 시도 제한

## 2. Store Component
- **목적**: 매장 정보 관리
- **책임**:
  - 매장 정보 조회
  - 매장ID 유효성 검증

## 3. Table Component
- **목적**: 테이블 정보 및 QR코드 접근 관리
- **책임**:
  - 테이블 정보 조회
  - 매장ID+테이블번호 유효성 검증

## 4. Menu Component
- **목적**: 메뉴 CRUD 및 이미지 관리
- **책임**:
  - 메뉴 등록/조회/수정/삭제
  - 카테고리별 메뉴 조회
  - 메뉴 이미지 업로드/서빙
  - 메뉴 노출 순서 관리
  - 필수 필드/가격 범위 검증

## 5. Order Component
- **목적**: 주문 생성, 상태 관리, 승인/거절
- **책임**:
  - 주문 생성 (PENDING 상태)
  - 주문 승인/거절 (관리자)
  - 주문 상태 변경 (ACCEPTED→PREPARING→COMPLETED)
  - 주문 삭제 (관리자)
  - 테이블별 현재 세션 주문 조회
  - 매장별 주문 목록 조회 (관리자 모니터링)
  - 총 주문액 계산 (ACCEPTED/PREPARING/COMPLETED만 합산)

## 6. Session Component
- **목적**: 테이블 세션 라이프사이클 관리
- **책임**:
  - 세션 자동 생성 (첫 주문 시)
  - 세션 종료 (이용 완료)
  - 주문 이력 이동 (Order → OrderHistory)
  - 테이블 리셋

## 7. OrderHistory Component
- **목적**: 과거 주문 내역 관리
- **책임**:
  - 과거 주문 내역 저장
  - 테이블별 과거 주문 조회
  - 날짜 필터링
