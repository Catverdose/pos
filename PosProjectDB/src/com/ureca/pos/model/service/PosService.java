package com.ureca.pos.model.service;

import java.util.List;
import com.ureca.pos.model.dto.Customer;
import com.ureca.pos.model.dto.Book; // 🌟 Book 임포트 추가
public interface PosService {
    // 👤 지원님 담당 파트 서비스
    boolean checkExpiry(int bookId); // 유통기한 검증 후 판매 가능 여부 반환
    boolean processPayment(int custId, int bookId, int quantity, int totalLinePrice); // 결제 트랜잭션 총괄

    // 👤 송님 담당 파트 서비스
    Customer searchCustomerByPhone(String phone); // 회원 조회 (없으면 예외 처리 가능)
    void addCustomer(Customer cust); // 신규 회원 가입 
    void updateProductStock(int bookId, int amount); // 물류 재고 보충
    List<Book> getAllProducts();
    
    // 공통 기능
    void close(); // 프로그램 종료
}