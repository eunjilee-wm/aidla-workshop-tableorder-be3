# Database Schema & ERD

## ERD

```mermaid
erDiagram
    Store ||--o{ Admin : has
    Store ||--o{ StoreTable : has
    Store ||--o{ Category : has
    Category ||--o{ Menu : contains
    Store ||--o{ Session : has
    StoreTable ||--o{ Session : has
    Session ||--o{ OrderEntity : contains
    OrderEntity ||--o{ OrderItem : contains
    Menu ||--o{ OrderItem : referenced
    Session ||--o{ OrderHistory : archived
    OrderHistory ||--o{ OrderHistoryItem : contains

    Store {
        bigint id PK
        varchar store_id UK "매장 식별자"
        varchar name
        timestamp created_at
    }

    Admin {
        bigint id PK
        bigint store_id FK
        varchar username
        varchar password "bcrypt"
        int login_attempts
        timestamp locked_until
        timestamp created_at
    }

    StoreTable {
        bigint id PK
        bigint store_id FK
        int table_number
        timestamp created_at
    }

    Category {
        bigint id PK
        bigint store_id FK
        varchar name
        int display_order
    }

    Menu {
        bigint id PK
        bigint category_id FK
        bigint store_id FK
        varchar name
        int price
        varchar description
        varchar image_url
        int display_order
        timestamp created_at
        timestamp updated_at
    }

    Session {
        bigint id PK
        bigint store_id FK
        int table_number
        varchar status "ACTIVE/COMPLETED"
        timestamp created_at
        timestamp completed_at
    }

    OrderEntity {
        bigint id PK
        bigint session_id FK
        bigint store_id FK
        int table_number
        varchar status "PENDING/ACCEPTED/REJECTED/PREPARING/COMPLETED"
        int total_amount
        varchar rejection_reason
        timestamp created_at
        timestamp updated_at
    }

    OrderItem {
        bigint id PK
        bigint order_id FK
        bigint menu_id FK
        varchar menu_name
        int quantity
        int unit_price
    }

    OrderHistory {
        bigint id PK
        bigint store_id FK
        int table_number
        bigint original_order_id
        varchar status
        int total_amount
        varchar rejection_reason
        timestamp ordered_at
        timestamp session_completed_at
    }

    OrderHistoryItem {
        bigint id PK
        bigint order_history_id FK
        varchar menu_name
        int quantity
        int unit_price
    }
```

## 테이블 설명

| 테이블 | 설명 |
|--------|------|
| Store | 매장 정보 |
| Admin | 관리자 계정 (매장당 N명) |
| StoreTable | 테이블 정보 (매장당 N개) |
| Category | 메뉴 카테고리 |
| Menu | 메뉴 항목 |
| Session | 테이블 세션 (ACTIVE/COMPLETED) |
| OrderEntity | 주문 (현재 활성 세션) |
| OrderItem | 주문 항목 (메뉴별 수량/단가) |
| OrderHistory | 과거 주문 (세션 종료 시 이동) |
| OrderHistoryItem | 과거 주문 항목 |

## 주요 설계 결정

- OrderItem에 menu_name, unit_price를 스냅샷으로 저장 (메뉴 변경 시 기존 주문 영향 방지)
- Session은 store_id + table_number + status=ACTIVE 조합으로 활성 세션 조회
- OrderHistory는 Order와 별도 테이블로 분리 (세션 종료 시 데이터 이동)
