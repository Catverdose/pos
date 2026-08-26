# 🏪 편의점 POS 시스템 (Backend Architecture)

> **데이터 정합성을 위한 ACID 트랜잭션 제어와 유통기한 실시간 검증 인터셉트 메커니즘을 구현한 자바 Swing & JDBC 기반의 포스기 미니 프로젝트입니다.**

---

## 🏗️ 시스템 아키텍처 및 핵심 설계 지향점

* **ACID 트랜잭션 제어**: 주문·재고 차감·포인트 적립을 단일 트랜잭션(`setAutoCommit(false)`)으로 묶어 결제 데이터 무결성 보장
* **데이터 레이어 검증**: 데이터 접근 레이어(DAO)에서 유통기한을 실시간 검증하여 만료 상품 인입을 원천 차단
* **유지보수성 확보**: `PosDao` 인터페이스 기반의 다형성 설계를 통해 비즈니스 로직과 데이터 접근 레이어 간 느슨한 결합 구현

---

## 🎯 `PosDaoJang` 통합 대표 함수 명세

`PosDaoJang`은 전체 시스템의 단일 진입점(Single Source of Truth)으로서, 상품 검증, 트랜잭션 결제, 회원 관리, 물류 시스템 전체를 관장합니다.

### 1. 주문-재고-포인트 일괄 결제 트랜잭션 (`processPayment`) ⭐
* **함수 명세**: `boolean processPayment(int custId, int bookId, int quantity, int totalLinePrice)`
* **역할 및 로직**: 영수증 추가(`INSERT`), 재고 차감(`UPDATE`), 포인트 적립(`UPDATE`)을 수동 커밋으로 처리합니다. 오류 발생 시 전체 롤백(`con.rollback()`)하여 데이터 꼬임을 방지합니다.

### 2. 상품 유통기한 만료 검증 인터셉터 (`checkExpiry`) 🛡️
* **함수 명세**: `boolean checkExpiry(int bookId)`
* **역할 및 로직**: DB의 `expire_date`와 현재 날짜를 실시간 비교합니다. 기한이 지난 상품은 판매 불가능(`false`)을 반환하여 장바구니 담기를 차단합니다.

### 3. 실시간 전체 상품 대시보드 관제 (`getAllProducts`) 🌟
* **함수 명세**: `List<Book> getAllProducts()`
* **역할 및 로직**: 매대 현황 시각화를 위해 전체 상품을 ID 오름차순 자동 조회하고, DTO에 매핑하여 UI `JTable`에 실시간 동기화합니다.

### 4. 연락처 기반 실시간 회원 조회 (`searchCustomerByPhone`)
* **함수 명세**: `Customer searchCustomerByPhone(String phone)`
* **역할 및 로직**: 연락처 기반 조회를 통해 기등록 회원의 포인트 정보를 반환하며, 미존재 시 `null`을 반환하여 즉석 회원가입 분기의 기준이 됩니다.

### 5. 신규 포인트 회원 즉석 가입 및 물류 보충 (`addCustomer` / `updateProductStock`)
* **역할**: 미등록 고객의 즉석 가입 쿼리(`INSERT`) 및 물류 입고 시 기존 재고량에 수량을 누적 합산(`UPDATE`)하는 파이프라인입니다.


---

## 🛠️ 프로젝트 구조 (Directory Tree)

인터페이스(`PosDao`) 하위에서 개별적으로 작업하던 소스코드를 최종 단계에서 **`PosDaoJang` 하나로 완전 통합**하여 아키텍처를 단순화했습니다.

```
com.ureca.pos
├── util
│   ├── DBUtil.java         # DB Connection 싱글톤 및 자원 해제 공통 모듈
│   └── PosFactory.java     # 통합된 PosDaoJang 객체를 서비스에 주입하는 팩토리 클래스
├── model
│   ├── dto                 # 데이터 모델 (Book, Customer, Orders, PosException)
│   ├── dao
│   │   ├── PosDao.java     # 추상화 인터페이스 명세 (공통 계약서)
│   │   └── PosDaoJang.java # [최종 통합본] 결제 트랜잭션, 유통기한, 회원·물류 전체 구현체
│   └── service
│       ├── PosService.java    # 비즈니스 로직 인터페이스
│       └── PosServiceImp.java # 팩토리로부터 DAO를 주입받아 제어하는 서비스 타워
└── view
│    └── PosUI.java          # 실시간 JTable 대시보드와 이벤트 계산기가 탑재된 Swing UI 레이어
└── Main.java           # 애플리케이션 실행 및 서비스-UI 연결 진입부

```

---

## 🗄️ 데이터베이스 스키마 구조 (Schema)

| 테이블명 | 핵심 컬럼 구성 (Key) | 비즈니스 역할 |
| :--- | :--- | :--- |
| **`book`** (상품) | `bookid` (PK), `bookname`, `price`, `stock`, `expire_date` | 물리 재고 및 유통기한 관제 |
| **`customer`** (회원) | `custid` (PK), `name`, `phone` (Unique), `point` | 연락처 식별 및 마일리지 적립 |
| **`orders`** (주문) | `orderid` (PK), `custid` (FK), `bookid` (FK), `saleprice`, `quantity`, `orderdate` | 결제 이력 및 트랜잭션 적재 |

---

## 🔄 핵심 연동 시나리오 (End-to-End Flow)

* **실시간 대시보드 갱신**: 프로그램 구동 시 `PosUI.setModel()` 단계에서 `refreshProductTable()`이 자동 트리거되어 최신 재고 정보를 `JTable`에 실시간 로딩합니다.
* **장바구니 담기 및 유효성 검증**: 상품 번호 조회 시 백엔드의 `checkExpiry()`가 연동되어, 유통기한 만료 상품이나 누락 상품의 결제 진입을 원천 차단합니다.
* **실시간 금액 계산기**: `PosUI` 내에 `DocumentListener`를 구현하여, '상품 아이디'와 '수량' 필드가 입력되는 변화를 실시간 감지해 결제 총 금액을 자동 연산합니다.
* **원자적 결제 및 동기화**: 결제 처리 시 `processPayment()`를 통해 연쇄 쿼리를 수행하고, 트랜잭션 성공 시 대시보드를 즉시 새로고침하여 차감된 재고 수량을 화면에 동기화합니다.
