package com.ureca.pos.model.service;

import java.sql.SQLException;
import java.util.List;

import com.ureca.pos.model.dao.PosDao;
import com.ureca.pos.model.dao.PosDaoJang;
import com.ureca.pos.model.dto.Customer;
import com.ureca.pos.model.dto.PosException;
import com.ureca.pos.model.dto.Book;
import com.ureca.pos.util.PosFactory; 
public class PosServiceImp implements PosService {
    
	private PosDao dao = PosFactory.getPosDao();
    
    @Override
    public boolean checkExpiry(int bookId) {
        try {
            
        	return dao.checkExpiry(bookId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("유통기한 확인 중 오류 발생");
        }
    }

    @Override
    public boolean processPayment(int custId, int bookId, int quantity, int totalLinePrice) {
        try {
           
        	return dao.processPayment(custId, bookId, quantity, totalLinePrice);
        	} catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("결제 처리(트랜잭션) 중 오류 발생");
        }
    }

 
    @Override
    public Customer searchCustomerByPhone(String phone) {
        try {
        
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
         
        	dao.addCustomer(cust);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("신규 회원 등록 중 오류 발생");
        }
    }

    @Override
    public void updateProductStock(int bookId, int amount) {
        try {
          
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

    @Override
    public void addProduct(Book book) {
        try {
            dao.addProduct(book);
        } catch (SQLException e) {
            e.printStackTrace();
            String message = e.getMessage() == null || e.getMessage().trim().isEmpty()
                    ? "신규 상품 등록 중 오류 발생"
                    : "신규 상품 등록 중 오류 발생: " + e.getMessage();
            throw new PosException(message);
        }
    }

    @Override
    public Book findProductById(int bookId) {
        try {
            return dao.findProductById(bookId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("상품 조회 중 오류 발생");
        }
    }

    @Override
    public Book findProductByName(String bookName) {
        try {
            return dao.findProductByName(bookName);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("상품명 조회 중 오류 발생");
        }
    }

    @Override
    public int countProductOrders(int bookId) {
        try {
            return dao.countProductOrders(bookId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new PosException("상품 판매 이력 확인 중 오류 발생");
        }
    }

    @Override
    public void deleteProductSafely(int bookId) {
        try {
            Book book = dao.findProductById(bookId);
            if (book == null) {
                throw new PosException("상품을 찾을 수 없습니다.");
            }

            int orderCount = dao.countProductOrders(bookId);
            if (orderCount > 0) {
                throw new PosException("판매 이력 " + orderCount + "건이 있어 상품을 삭제할 수 없습니다. 재고를 0으로 조정해 주세요.");
            }

            dao.deleteProduct(bookId);
        } catch (PosException e) {
            throw e;
        } catch (SQLException e) {
            e.printStackTrace();
            String message = e.getMessage() == null || e.getMessage().trim().isEmpty()
                    ? "상품 삭제 중 오류 발생"
                    : e.getMessage();
            throw new PosException(message);
        }
    }

    @Override
    public void close() {
        System.exit(0);
    }
}
