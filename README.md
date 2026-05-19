

# 🏪 편의점 POS 시스템 (Backend Architecture)

> **유통기한 검증 및 안정적인 결제 트랜잭션 제어를 구현한 자바 Swing & JDBC 기반의 포스기 미니 프로젝트입니다.**

---

## 🎯 `PosDaoJang` 통합 대표 함수 명세

`PosDaoJang`은 전체 시스템의 단일 진입점(Single Source of Truth)으로서, 상품 검증, 트랜잭션 결제, 회원 관리, 물류 시스템 전체를 관장합니다.

### 1. 상품 유통기한 만료 검증 (`checkExpiry`)

* **함수 명세**: `boolean checkExpiry(int bookId)`
* **역할**: DB에서 상품(`Book`)의 유통기한을 조회하여 오늘 날짜와 실시간으로 비교합니다. 기한이 지난 상품은 화면단에서 장바구니에 담기지 않도록 원천 차단하는 유효성 검증을 수행합니다.

### 2. 주문-재고-포인트 일괄 결제 트랜잭션 (`processPayment`)

* **함수 명세**: `boolean processPayment(int custId, int bookId, int quantity, int totalLinePrice)`
* **역할**: 영수증 추가(`INSERT`), 실시간 재고 차감(`UPDATE`), 회원 포인트 적립(`UPDATE`)이라는 3가지 DB 작업을 하나의 수동 커밋 트랜잭션(`setAutoCommit(false)`)으로 묶어 처리합니다. 하나라도 실패하면 전체 롤백하여 데이터가 꼬이는 현상을 방지합니다.

### 3. 연락처 기반 실시간 회원 조회 (`searchCustomerByPhone`)

* **함수 명세**: `Customer searchCustomerByPhone(String phone)`
* **역할**: 고객이 포스기 패드에 입력한 핸드폰 번호를 매개변수로 받아 DB에서 회원 정보를 실시간 조회합니다. 조회 성공 시 이름과 누적 포인트를 반환하며, 미존재 시 `null`을 반환하여 즉석 가입 폼으로 분기하는 기준이 됩니다.

### 4. 신규 포인트 회원 즉석 가입 (`addCustomer`)

* **함수 명세**: `void addCustomer(Customer cust)`
* **역할**: 미등록 고객이 결제 중 즉석 가입을 요청할 때 사용됩니다. 입력받은 이름, 주소, 연락처, 초기 포인트(0점)를 DB `Customer` 테이블에 즉시 삽입(`INSERT`)하여 회원 계정을 생성합니다.

### 5. 물류 보충 및 상품 재고 관리 (`updateProductStock`)

* **함수 명세**: `void updateProductStock(int bookId, int amount)`
* **역할**: 물류 창고에서 새로운 상품이 입고되었을 때 사용하는 입고 관리 기능입니다. 특정 상품(`bookId`)을 지정해 추가할 수량(`amount`)을 던지면, DB에서 기존 물리 재고량에 누적 합산(`UPDATE`) 처리를 수행합니다.

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
    └── Main.java           # 애플리케이션 실행 및 서비스-UI 연결 진입부

```

---

## 🗄️ 시스템 DB 테이블 구조

실무 환경에 맞추어 유통기한, 실시간 재고 및 포인트 적립 메커니즘을 반영한 데이터 구조입니다.

* **Book (상품)**: `bookid` (PK), `title`, `publisher`, `price`, `stock` (실시간 물리 재고량), `expire_date` (유통기한 날짜)
* **Customer (회원)**: `custid` (PK), `name`, `address`, `phone` (조회용 연락처), `point` (결제 금액 1% 적립 마일리지)
* **Orders (주문 영수증)**: `orderid` (PK), `custid` (FK), `bookid` (FK), `saleprice` (총 금액), `quantity` (구매 수량), `orderdate` (주문일자)

---

## 🔄 핵심 연동 시나리오

* **장바구니 담기**: 상품 번호 스캔 시 `checkExpiry()`가 실행되어 유통기한이 지났거나 재고가 없으면 판매 차단 메시지 팝업을 발생시킵니다.
* **회원 조회**: 핸드폰 번호 조회 시 회원이 없으면 즉석 가입 모달창(`addCustomer`) 단계로 연결됩니다.
* **최종 결제**: `processPayment()`를 통해 주문·재고·포인트를 일괄 안전하게 처리하며, 에러 발생 시 완전 롤백을 수행합니다.
