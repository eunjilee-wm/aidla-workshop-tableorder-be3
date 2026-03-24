# Business Rules

## BR-1: 인증

| Rule | 설명 |
|------|------|
| BR-1.1 | 관리자 비밀번호는 bcrypt로 해싱하여 저장 |
| BR-1.2 | 로그인 실패 5회 시 30분간 계정 잠금 |
| BR-1.3 | JWT 토큰 만료 시간: 16시간 |
| BR-1.4 | JWT payload에 storeId, adminId, username 포함 |
| BR-1.5 | 고객 API는 인증 불필요 (storeId + tableNumber만 검증) |

## BR-2: 메뉴

| Rule | 설명 |
|------|------|
| BR-2.1 | 필수 필드: name, price, categoryId |
| BR-2.2 | price > 0 (양수만 허용) |
| BR-2.3 | 이미지 허용 형식: jpg, jpeg, png, gif |
| BR-2.4 | display_order 미지정 시 해당 카테고리 마지막 순서 자동 부여 |
| BR-2.5 | 메뉴 삭제 시 해당 카테고리 내 순서 자동 재정렬 |
| BR-2.6 | 메뉴는 반드시 하나의 매장(storeId)에 소속 |

## BR-3: 주문

| Rule | 설명 |
|------|------|
| BR-3.1 | 주문 생성 시 상태는 항상 PENDING |
| BR-3.2 | 주문 항목의 menu_name, unit_price는 생성 시점 스냅샷 |
| BR-3.3 | totalAmount = SUM(quantity * unit_price) for all items |
| BR-3.4 | 주문 항목은 최소 1개 이상 |
| BR-3.5 | 주문 상태 전이는 정의된 상태 머신만 허용 |
| BR-3.6 | 거절 사유(rejection_reason)는 선택 입력 |
| BR-3.7 | 주문 삭제는 모든 상태에서 가능 (관리자 직권) |

## BR-4: 세션

| Rule | 설명 |
|------|------|
| BR-4.1 | 테이블당 활성 세션은 최대 1개 |
| BR-4.2 | 첫 주문 시 활성 세션 없으면 자동 생성 |
| BR-4.3 | 세션 종료 시 모든 주문을 OrderHistory로 이동 (트랜잭션) |
| BR-4.4 | 세션 종료 후 테이블 주문 목록/총 주문액 리셋 |

## BR-5: 총 주문액

| Rule | 설명 |
|------|------|
| BR-5.1 | ACCEPTED, PREPARING, COMPLETED 상태 주문만 합산 |
| BR-5.2 | PENDING, REJECTED 주문은 총 주문액에서 제외 |

## BR-6: 데이터 격리

| Rule | 설명 |
|------|------|
| BR-6.1 | 모든 데이터 조회/변경은 storeId 기반 필터링 필수 |
| BR-6.2 | 관리자는 자신의 매장 데이터만 접근 가능 (JWT의 storeId 검증) |
