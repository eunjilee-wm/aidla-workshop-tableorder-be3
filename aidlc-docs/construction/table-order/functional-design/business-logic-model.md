# Business Logic Model

## 1. 주문 상태 머신 (Order State Machine)

```
PENDING ──→ ACCEPTED ──→ PREPARING ──→ COMPLETED
   │
   └──→ REJECTED
```

### 허용되는 상태 전이
| From | To | 트리거 |
|------|----|--------|
| PENDING | ACCEPTED | 관리자 승인 |
| PENDING | REJECTED | 관리자 거절 |
| ACCEPTED | PREPARING | 관리자 상태 변경 |
| PREPARING | COMPLETED | 관리자 상태 변경 |

### 금지되는 상태 전이
- REJECTED → 어떤 상태로도 전이 불가
- COMPLETED → 어떤 상태로도 전이 불가
- 역방향 전이 불가 (PREPARING → ACCEPTED 등)

---

## 2. 세션 라이프사이클

```
[테이블 비활성] ──첫 주문──→ [ACTIVE] ──이용 완료──→ [COMPLETED]
                                                         │
                                                    다음 고객 첫 주문
                                                         │
                                                         v
                                                    [새 ACTIVE]
```

### 세션 생성 로직
1. 주문 생성 요청 수신
2. storeId + tableNumber로 status=ACTIVE 세션 조회
3. 활성 세션 없음 → 새 Session(status=ACTIVE) 생성
4. 활성 세션 있음 → 기존 세션에 주문 연결

### 세션 종료 로직 (트랜잭션)
1. 해당 세션의 모든 Order → OrderHistory로 복사
2. 해당 세션의 모든 OrderItem → OrderHistoryItem으로 복사
3. 해당 세션의 Order, OrderItem 삭제
4. Session.status = COMPLETED, completedAt = now()

---

## 3. 주문 생성 플로우

1. storeId, tableNumber 유효성 검증
2. 메뉴 항목 검증 (존재 여부, 해당 매장 소속 여부)
3. 활성 세션 조회 또는 생성
4. Order 생성 (status=PENDING, totalAmount 계산)
5. OrderItem 생성 (menu_name, unit_price 스냅샷 저장)

---

## 4. 총 주문액 계산

- 대상: 해당 테이블 활성 세션의 주문 중 ACCEPTED, PREPARING, COMPLETED 상태만
- PENDING, REJECTED 주문은 제외
- 계산: SUM(order.total_amount) WHERE status IN (ACCEPTED, PREPARING, COMPLETED)

---

## 5. 관리자 인증 플로우

1. storeId + username으로 Admin 조회
2. locked_until 확인 → 잠금 상태면 거부
3. 비밀번호 bcrypt 검증
4. 실패 시: login_attempts 증가, 임계값 초과 시 locked_until 설정
5. 성공 시: login_attempts 리셋, JWT 토큰 발급 (16시간 만료, storeId 포함)

---

## 6. QR코드 접근 플로우

1. URL에서 storeId, tableNumber 추출
2. Store 존재 여부 검증
3. StoreTable 존재 여부 검증 (storeId + tableNumber)
4. 유효하면 해당 매장 메뉴 데이터 반환
5. 무효하면 에러 응답
