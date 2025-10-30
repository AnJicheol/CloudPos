# CloudPos

## ⚙️프로세스

1. 이슈 생성 → 백로그(Backlog) 배치

2. 이슈 담당자 지정 후 In progress로 이동

3. 로컬에서 dev 최신 받기

4. dev에서 feature 브랜치 생성

5. 기능 개발 후 dev 대상으로 PR 생성

6. 코드 리뷰 → 통과 시 dev에 머지

7. 기능들이 dev에 일정량 모이면 release 브랜치 생성

## 📝이슈 템플릿

제목: [FEAT] 장바구니 아이템 추가 API

      [BUG] 결제 완료 후 장바구니 안 지워짐
      
      [REQ] 기능 요청

1. 목적
- 사용자가 장바구니에 상품을 담을 수 있게 한다.

2. 작업 내용
- POST /api/cart/items 생성
- Request/Response 스펙 정의
- 성공 시 총액 재계산

3. 완료 기준
- Swagger에서 호출 시 200
- Redis에 cart:{userId}에 아이템 추가됨



<br><br>

## 🧱 패키지/모듈 구조

```text
com.yourapp.pos
  ├─ auth              // 인증/권한, 카카오 로그인, 점주-포스기 1:N 연동
  │  ├─ domain
  │  └─ application
  ├─ order             // 주문 프로세스, 결제신청→완료신호→장바구니 비우기
  │  ├─ domain
  │  └─ application
  ├─ cart              // 장바구니 생성/추가/수량/삭제/결제시 비우기
  │  ├─ domain
  │  └─ application
  ├─ product           // 상품: id, 이름, 가격, 상태 / 생성·조회·수정
  │  ├─ domain
  │  └─ application
  ├─ inventory         // 재고/상품 관리: 등록, 품절, 수동추천
  │  ├─ domain
  │  └─ application
  ├─ payment           // 결제/할인 인터페이스, 현금/카드, 외부 모듈 어댑터
  │  ├─ domain
  │  └─ application
  └─ common            // 공통 예외, 공통 dto, 유틸
```

<br><br>

## 📏 네이밍 규칙

| 대상 | 적용 범위 | 규칙 |
| --- | --- | --- |
| 파일/클래스 | `class`, `interface`, `enum`, 스프링 컴포넌트 | **UpperCamelCase** *(= PascalCase, 파스칼)* |
| 메서드 | 모든 메서드/핸들러 | **lowerCamelCase** *(로워 카멜)* |
| 변수 | 필드/지역/파라미터 | **lowerCamelCase** |
| 상수 | `static final` | **UPPER_SNAKE_CASE** *(대문자 스네이크)* |
| 패키지 | 패키지 경로 | **모두 소문자 + 점(.)** |
| 엔티티 필드 | JPA 엔티티 속성 | **lowerCamelCase** |
| DB 테이블/컬럼 | 스키마/마이그레이션 | **snake_case** *(스네이크)* |
| 엔티티↔DB 매핑 | `@Table`, `@Column` | 엔티티=c**amel**, DB=**snake** |

<br><br>

## 🌀 Git Flow 전략

| 브랜치명 | 용도 | 비고 |
|---------|------|------|
| `master` | 실제 배포 브랜치 | 운영용, 코드 리뷰 후 병합 |
| `dev` | 개발 통합 브랜치 | 모든 feature 브랜치가 여기로 병합 |
| `feature/*` | 기능 개발 | `ex) feature/AUTH-SCRUM-20` 형식 |
| `fix/*` | 버그 수정 | |
| `hotfix/*` | 긴급 수정 | |
| `release/*` | 배포 준비 | 테스트 완료 후 master로 병합 |



<br><br>

## 💬 커밋 컨벤션

| 태그 | 설명 |
|------|------|
| `feat:` | 새로운 기능 추가 |
| `fix:` | 버그 수정 |
| `refactor:` | 리팩토링 (기능 변경 없음) |
| `docs:` | 문서 변경 |
| `style:` | 코드 스타일/포맷 변경 |
| `test:` | 테스트 코드 관련 |
| `chore:` | 설정, 빌드, 패키지 등 기타 변경 |

<br><br>

## 📦 API 응답 포맷


✅ 성공 응답:
```json
{
  "success": true,
  "status": 200,
  "data": {
    // 실제 DTO 값
  }
}
```
❌ 실패 응답:

```json
{
  "success": false,
  "status": 400,
  "data": null
}
```

<br>

## 🚦 HTTP 상태코드 통일

| 코드 | 의미                   | 사용 예                                |
|------|------------------------|----------------------------------------|
| 200  | OK                     | 일반 요청 성공 (GET, POST 요청 등)    |
| 201  | Created                | 자원 생성 완료 (POST 성공 시)         |
| 204  | No Content             | 응답 없음 (DELETE 요청 등)            |
| 400  | Bad Request            | 클라이언트 요청 오류 (유효성 실패 등) |
| 401  | Unauthorized           | 인증 실패 (로그인 필요)               |
| 403  | Forbidden              | 권한 없음 (비인가 요청)               |
| 404  | Not Found              | 리소스 없음 (잘못된 ID 등)           |
| 409  | Conflict               | 중복 충돌 (이메일 중복 등)            |
| 500  | Internal Server Error  | 서버 내부 에러 (처리 불가능한 예외)   |
