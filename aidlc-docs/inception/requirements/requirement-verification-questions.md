# Requirements Verification Questions

요구사항 문서를 분석한 결과, 다음 사항들에 대한 명확화가 필요합니다.
각 질문의 [Answer]: 태그 뒤에 선택지 알파벳을 입력해주세요.

---

## Question 1
Backend 기술 스택으로 어떤 것을 사용하시겠습니까?

A) Java + Spring Boot
B) Node.js + Express/NestJS
C) Python + FastAPI/Django
D) Go + Gin/Echo
E) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 2
Frontend 기술 스택으로 어떤 것을 사용하시겠습니까?

A) React (JavaScript/TypeScript)
B) Vue.js
C) Next.js (React 기반 SSR)
D) Angular
E) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 3
데이터베이스로 어떤 것을 사용하시겠습니까?

A) PostgreSQL
B) MySQL
C) Amazon DynamoDB
D) MongoDB
E) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 4
배포 환경은 어떻게 구성하시겠습니까?

A) AWS (EC2, ECS, Lambda 등)
B) Docker 컨테이너 (로컬/온프레미스)
C) 로컬 개발 환경만 (배포 미고려)
D) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 5
메뉴 이미지 저장 방식은 어떻게 하시겠습니까?

A) 외부 URL 링크만 저장 (이미지 업로드 기능 없음)
B) AWS S3 등 클라우드 스토리지에 업로드
C) 서버 로컬 파일 시스템에 저장
D) Other (please describe after [Answer]: tag below)

[Answer]: C

## Question 6
매장(Store) 관리 범위는 어떻게 하시겠습니까? (멀티 매장 vs 단일 매장)

A) 단일 매장만 지원 (하나의 매장 데이터만 관리)
B) 멀티 매장 지원 (여러 매장을 독립적으로 관리)
C) Other (please describe after [Answer]: tag below)

[Answer]: B

## Question 7
관리자 계정 관리 방식은 어떻게 하시겠습니까?

A) 사전 등록된 관리자 계정 사용 (DB seed 또는 초기 설정)
B) 관리자 회원가입 기능 포함
C) Other (please describe after [Answer]: tag below)

[Answer]: A

## Question 8
이 프로젝트의 범위는 Backend만 해당합니까, Frontend도 포함합니까?

A) Backend API만 구현 (Frontend는 별도 프로젝트)
B) Backend + Frontend 모두 이 프로젝트에서 구현 (모노레포)
C) Backend만 구현하되, API 문서(Swagger 등) 제공
D) Other (please describe after [Answer]: tag below)

[Answer]: C
