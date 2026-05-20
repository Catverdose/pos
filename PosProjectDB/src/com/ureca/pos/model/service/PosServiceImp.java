package com.ureca.pos.model.service;

import java.sql.SQLException;
import java.util.List;

import com.ureca.pos.model.dao.PosDao;
import com.ureca.pos.model.dao.PosDaoJang;
import com.ureca.pos.model.dto.Customer;
import com.ureca.pos.model.dto.PosException;
import com.ureca.pos.model.dto.Book;
import com.ureca.pos.util.PosFactory; // 💡 팩토리의 실제 경로인 .util.PosFactory로 임포트 지정!
public class PosServiceImp implements PosService {
    
	private PosDao dao = PosFactory.getPosDao();
    
    // =========================================================================
    // 👤 개발자 A (지원님 담당 서비스 로직)
    // =========================================================================
    
    @Override
    public boolean checkExpiry(int bookId) {
        try {
            // 지원이 만든 다오의 유통기한 체크 호출
        	return dao.checkExpiry(bookId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("유통기한 확인 중 오류 발생");
        }
    }

    @Override
    public boolean processPayment(int custId, int bookId, int quantity, int totalLinePrice) {
        try {
            // 지원이 만든 다오의 결제 트랜잭션 호출
        	return dao.processPayment(custId, bookId, quantity, totalLinePrice);
        	} catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("결제 처리(트랜잭션) 중 오류 발생");
        }
    }

    // =========================================================================
    // 👤 개발자 B (송님 담당 서비스 로직)
    // =========================================================================
    
    @Override
    public Customer searchCustomerByPhone(String phone) {
        try {
            // 송님이 만든 다오의 회원 번호 조회 호출
        	Customer cust = dao.searchCustomerByPhone(phone);
        	if (cust == null) {
                System.out.println("조회된 회원이 없습니다. 신규 가입이 필요합니다.");
            }
            return cust;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("회원 조회 중 오류 발생");
        }
    }

    @Override
    public void addCustomer(Customer cust) {
        try {
            // 송님이 만든 다오의 회원 등록 호출
        	dao.addCustomer(cust);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("신규 회원 등록 중 오류 발생");
        }
    }

    @Override
    public void updateProductStock(int bookId, int amount) {
        try {
            // 송님이 만든 다오의 재고 보충 호출
        	dao.updateProductStock(bookId, amount);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("물류 재고 보충 중 오류 발생");
        }
    }
    
    @Override
    public List<Book> getAllProducts() {
        try {
            return dao.getAllProducts();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("전체 상품 목록 조회 중 리소스 에러 발생");
        }
    }

    // =========================================================================
    // 공통 기능
    // =========================================================================
    
    @Override
    public void close() {
        System.exit(0);
    }
}