# CloudPos


## 개요

CloudPos 는 웹 환경에서 간편하게 포스기와 키오스크 기능을 제공합니다.
기존 느리고 복잡한 UX를 개선하고 간편하게 매장 내 상품을 관리할 수 있습니다.
<br>
<img width="1455" height="864" alt="화면" src="https://github.com/user-attachments/assets/25966fa6-961a-40e5-8927-b8584ba912d9" />


<br><br>

## Team

| 이름 | 역할 | 주요 담당 |  
| --- | --- | --- | 
| 안지철 | BE, 인프라 설계, 팀 리더 | 권한·인증, 주문 프로세스 개발 |  
| 김유정 | BE, QA | 장바구니, 주문 프로세스 개발, QA |  
| 김에스더 | BE, 자료 정리 | 상품 도메인 시스템 개발, 발표 PPT 제작 |  
| 안희건 | BE, 인프라 관리 | 재고, 상품 등록 프로세스 개발, 인프라 관리 |  
| 신수호 | BE, 발표 | 쿠폰·할인·결제 시스템 개발, 발표 |  

<br><br>

## Tech Stack

- Backend: Spring Boot, Java 21, Spring Data JPA, Redis
- DB: MySQL
- Infra: AWS EC2, RDS, S3
- Auth: OAuth2 (Kakao), Session 기반 인증

<br><br>


## 아키텍처 & ERD

<img width="736" height="850" alt="Seed drawio" src="https://github.com/user-attachments/assets/a8fdb7ee-d417-4622-8db7-a2152c01c3fd" />

<br><br>

<img width="1578" height="775" alt="cloudpos_erd" src="https://github.com/user-attachments/assets/9b4bdbe7-b5e6-4744-b1f7-043656357885" />

<br><br>




## 작동 원리
CloudPos는 Modulith 구조를 따릅니다.
* 숙련도를 고려해 패키지 구조만 Modulith 구조를 따릅니다.
* 실제 모듈 분리 시 피해를 격리하기 위해 api, listener 계층을 구현하였습니다.
* api 계층에선 다른 모듈을 호출하며 응답 모듈은 listener 계층을 통해 응답합니다. 두 계층에서 다른 모듈을 의존 주입 받으며 서비스에 안정성을 책임집니다.
* product와 inventory 모듈은 api, listener 계층을 사용하지 않습니다. 해당 모듈은 도메인 성격상 분리하지 않는 것이 이득이라는 판단 때문입니다.
  
Inventory 모듈을 통해 product를 생성 수정등 관리합니다.
모든 결제 내역은 일차적으로 Order 모듈에서 명세를 작성합니다.
* 할인 등 최종 금액이 계산되며 OrderId를 리턴합니다. 이는 이중 계산을 피하고 Payment 모듈에서 내부적으로 값을 요청함으로 안정성을 확보하기 위함입니다.
* Order는 경제 성공 이후 이벤트 전파를 담당합니다.
  
키오스크는 Cart에서 Redis와 유한상태 머신을 통해 관리합니다.

<br><br>

## 🧱 패키지/모듈 구조

```text
com.yourapp.pos
  ├─ auth              // 인증/권한, 카카오 로그인, 점주-포스기 1:N 연동     //안지철
  │  ├─ domain
  │  └─ mvc
  ├─ order             // 주문 프로세스, 결제신청→완료신호→장바구니 비우기   //안지철
  │  ├─ domain
  │  ├─ api
  │  ├─ listener
  │  └─ mvc
  ├─ cart              // 장바구니 생성/추가/수량/삭제/결제시 비우기         //김유정
  │  ├─ domain
  │  ├─ api
  │  ├─ listener
  │  └─ mvc
  ├─ product           // 상품: id, 이름, 가격, 상태 / 생성·조회·수정        //김에스더
  │  ├─ domain
  │  └─ mvc
  ├─ inventory         // 재고/상품 관리: 등록, 품절, 수동추천               //김에스더
  │  ├─ domain
  │  ├─ listener
  │  └─ mvc
  ├─ payment           // 결제 인터페이스, 현금/카드, 외부 모듈 어댑터        //신수호
  │  ├─ domain
  │  ├─ api
  │  └─ mvc
  ├─ discount           // 할인 CRUD                                         //안희건
  │  ├─ domain
  │  ├─ listener
  │  └─ mvc
  └─ common            // 공통 예외, 공통 dto, 유틸
```

<br><br>

##  핵심 API 
[API PDF](docs/api.pdf)  

<br><br>
## 1) 매장/상품/할인 세팅

| 목적 | Method | Endpoint | 설명 |
| --- | --- | --- | --- |
| 매장 생성 | `POST` | `/api/inventories` | 매장 신규 생성, `inventoryId` 발급 |
| 매장 진열 목록 | `GET` | `/api/inventories/{inventoryId}/products` | 해당 매장 상품 리스트 |
| 매장에 상품 등록(이미지) | `POST` | `/api/inventories/{inventoryId}/products` | `multipart`(data JSON + image) |
| 매장 진열 해제 | `DELETE` | `/api/inventories/{inventoryId}/products/{productId}` | 매장-상품 매핑만 해제 |
| 할인 생성 | `POST` | `/api/discounts/owner/create` | 매장·상품 단위 할인 등록 |

<br><br>

## 2) 장바구니 → 주문

| 목적 | Method | Endpoint | 설명 |
| --- | --- | --- | --- |
| 장바구니 생성 | `POST` | `/api/carts` | 새 장바구니 발급 |
| 장바구니 조회 | `GET` | `/api/carts/{cartId}` | 아이템/금액 확인 |
| 아이템 추가 | `POST` | `/api/carts/{cartId}/items` | `{ productId, qty }` |
| 주문 생성 & 결제 시작 | `POST` | `/api/orders/start-payment/{cartId}` | `cartId` 기반 `CHECKOUT` 진입 |

<br><br>

## 3) 결제 (내부 결제 + Toss)

| 목적 | Method | Endpoint | 설명 |
| --- | --- | --- | --- |
| 활성 결제수단 | `GET` | `/payments/methods/active` | 노출 가능 결제수단만 |
| Payment 생성 | `POST` | `/payments` | `{ orderId, paymentMethodId }` |
| 주문별 최신 Payment | `GET` | `/payments/{orderId}` | 최근 결제 상태 조회 |
| Toss 승인 | `POST` | `/payments/toss/confirm` | `{ paymentKey, orderId, amount }` |
| Toss 취소 | `POST` | `/payments/toss/cancel/{paymentKey}` | `orderId`, `cancelReason` |

<br><br>
