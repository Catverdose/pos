-- 1. 기존에 혹시 있을지 모를 포스기 DB를 날리고 완전 새 장부 개설
DROP DATABASE IF EXISTS pos_db;
CREATE DATABASE pos_db;
USE pos_db;

-- 2. 편의점 상품 마스터 테이블 생성
CREATE TABLE Book (
    bookid      INTEGER PRIMARY KEY,      -- 상품 바코드 번호
    bookname    VARCHAR(40) NOT NULL,     -- 상품명
    publisher   VARCHAR(40),              -- 제조사
    price       INTEGER NOT NULL,         -- 상품 단가
    stock       INTEGER DEFAULT 0,        -- 실시간 매대 잔여 재고량
    expire_date DATE NOT NULL             -- 유통기한
);

-- 3. 포인트 회원 테이블 생성 (전화번호 식별자)
CREATE TABLE Customer (
    custid      INTEGER PRIMARY KEY AUTO_INCREMENT, -- 회원 고유 시스템 번호
    name        VARCHAR(40) NOT NULL,               -- 고객 이름
    address     VARCHAR(50),                        -- 고객 주소
    phone       VARCHAR(20) UNIQUE NOT NULL,        -- 포스기 입력 전화번호 (조회 Key)
    point       INTEGER DEFAULT 0                   -- 누적 포인트 (1% 적립)
);

-- 4. 판매 영수증 매출 이력 테이블 생성
CREATE TABLE Orders (
    orderid   INTEGER PRIMARY KEY AUTO_INCREMENT, -- 영수증 일련번호
    custid    INTEGER,                            -- 구매 회원 번호
    bookid    INTEGER,                            -- 구매 상품 번호
    saleprice INTEGER NOT NULL,                   -- 최종 결제 합산 금액 (단가 * 수량)
    quantity  INTEGER DEFAULT 1,                  -- 단일 상품 구매 수량
    orderdate DATE DEFAULT (CURRENT_DATE),        -- 결제 일자 (오늘 날짜)
    FOREIGN KEY (custid) REFERENCES Customer(custid),
    FOREIGN KEY (bookid) REFERENCES Book(bookid)
);

-- =========================================================================
-- 5. 비즈니스 시나리오 테스트용 초기 실무 데이터 세팅 (2026년 현시점 기준)
-- =========================================================================
INSERT INTO Book VALUES 
(1001, '참치마요삼각김밥', 'GS푸드', 1200, 15, '2026-05-30'),
(1002, '바나나맛우유', '빙그레', 1700, 25, '2026-06-15'),
(1003, '혜자도시락', 'GS푸드', 4500, 0, '2026-05-28'),      -- [품절 테스트용]
(1004, '폐기대상샌드위치', '프레시푸드', 2500, 5, '2026-05-10'); -- [유통기한 만료 테스트용]

INSERT INTO Customer (name, address, phone, point) VALUES 
('장지원', '대한민국 안양', '010-1234-5678', 500),
('전송흔', '대한민국 서울', '010-9876-5432', 0);

INSERT INTO Orders (custid, bookid, saleprice, quantity, orderdate) VALUES 
(1, 1001, 2400, 2, '2026-05-19'); -- 오늘 첫 결제 데이터 샘플

COMMIT;