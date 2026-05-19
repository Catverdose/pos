# 🏪 마당DB 기반 편의점 POS 시스템

> **마당 DB(madangdb)를 확장하여 유통기한 검증 및 트랜잭션 제어를 구현한 자바 Swing & JDBC 미니 프로젝트입니다.**

---

## 🎯 3대 핵심 기능

* **유통기한 만료 상품 차단**: 상품 스캔 시 DB의 유통기한과 오늘 날짜를 비교해 만료된 상품은 장바구니 적재를 강제 차단합니다.
* **주문-재고-포인트 트랜잭션 (ACID)**: 결제 시 영수증 등록(`Insert`), 재고 차감(`Update`), 회원 포인트 적립(`Update`)을 하나의 수동 커밋 트랜잭션으로 안전하게 처리합니다.
* **포스기 즉석 회원 가입**: 미등록 연락처 조회 시 팝업창을 통해 즉석에서 포인트 회원 가입을 완료시킵니다.

---

## 🛠️ 프로젝트 구조 및 분업 (Directory Tree)

코드 충돌을 방지하기 위해 **동일 인터페이스(`PosDao`) 하위에 개발자별 전용 구현체(Jang / Song)를 분리**하여 개발합니다.

```
com.ureca.pos
├── util
│   └── DBUtil.java             # DB Connection 싱글톤 공통 모듈
├── model
│   ├── dto                     # 데이터 모델 (Book, Customer, Orders)
│   ├── dao
│   │   ├── PosDao.java         # 추상화 인터페이스 명세 (공통 계약서)
│   │   ├── PosDaoJang.java     # [장지원] 결제 트랜잭션 및 유통기한 검증 구현체
│   │   └── PosDaoSong.java     # [파트너] 회원 관리 및 물류 재고 보충 구현체
│   └── service
│       ├── PosService.java     # 비즈니스 로직 인터페이스
│       └── PosServiceImp.java  # 두 개의 DAO를 조립/제어하는 서비스 타워
└── view
    ├── PosMainFrame.java       # Swing GUI 메인 프레임 화면 (파트너 담당)
    └── Main.java               # 애플리케이션 실행 진입부

```

---

## 🗄️ DB 확장 명세 (madangdb)

* **Book**: `stock` (실시간 재고량), `expire_date` (유통기한) 컬럼 추가
* **Customer**: `phone` (조회용 전화번호), `point` (결제 금액 1% 적립 마일리지) 컬럼 추가
* **Orders**: `quantity` (구매 수량) 컬럼 추가, `saleprice` (총 합산 금액) 매핑

---

## 🔄 핵심 연동 시나리오

* **장바구니 담기**: 상품의 유통기한이 지났거나 재고가 0 이하이면 판매 차단 메시지를 팝업합니다.
* **회원 조회**: 핸드폰 번호 조회 시 회원이 없으면 즉석 가입 모달창으로 분기합니다.
* **최종 결제**: `con.setAutoCommit(false)` 세팅 후 주문·재고·포인트를 일괄 처리하며, 하나라도 실패 시 `rollback` 합니다.
