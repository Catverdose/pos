package com.ureca.pos.model.dao;

import java.sql.SQLException;
import java.util.List;

import com.ureca.pos.model.dto.Customer;
import com.ureca.pos.model.dto.Book;
import com.ureca.pos.model.dto.Customer;
public interface PosDao {
	
	// 👤 개발자 A (지원님 담당 핵심 기능)
	boolean checkExpiry(int bookId) throws SQLException; // 유통기한 검증
	boolean processPayment(int custId, int bookId, int quantity, int totalLinePrice) throws SQLException; // 결제 트랜잭션 처리

	// 👤 개발자 B (송님 담당 핵심 기능)
	Customer searchCustomerByPhone(String phone) throws SQLException; // 핸드폰 번호로 회원 조회
	void addCustomer(Customer cust) throws SQLException; // 신규 회원 등록
	void updateProductStock(int bookId, int amount) throws SQLException; // 물류 재고 보충
	
	List<Book> getAllProducts() throws SQLException;
}