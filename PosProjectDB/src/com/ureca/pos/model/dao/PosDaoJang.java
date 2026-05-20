package com.ureca.pos.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ureca.pos.model.dto.Book;
import com.ureca.pos.model.dto.Customer;
import com.ureca.pos.util.DBUtil;

public class PosDaoJang implements PosDao {
	////////////////////////TODO 01. DBUtil 객체 생성하기  
	private DBUtil dbutil = DBUtil.getInstance();

	@Override
	public boolean checkExpiry(int bookId) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
	////////////////////////TODO 02. [지원 담당] 상품 유통기한과 오늘 날짜 비교하기
			con = dbutil.getConnection();
			// SQL 예시: select expire_date from pos_product where book_id = ?
			// 💡 팁: 가져온 유통기한 날짜와 오늘 날짜를 비교해서 판매 가능하면 true, 지났으면 false 리턴!
			String sql = "SELECT expire_date FROM Book WHERE bookid = ?";
		     stmt = con.prepareStatement(sql);
		     stmt.setInt(1, bookId);
		     rs = stmt.executeQuery();
		     if (rs.next()) {
		            java.sql.Date expireDate = rs.getDate("expire_date");
		            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

		            return !expireDate.before(today);
		        }
			
		} finally {
			dbutil.close(rs, stmt, con);
		}
		return false;
	}

	@Override
	public boolean processPayment(int custId, int bookId, int quantity, int totalLinePrice) throws SQLException {
		Connection con = null;
		PreparedStatement orderStmt = null;
	    PreparedStatement stockStmt = null;
	    PreparedStatement pointStmt = null;

		try {
	////////////////////////TODO 03. [지원 담당] 결제 트랜잭션 처리하기 (포스기 핵심 ⭐)
			con = dbutil.getConnection();
			
			// 💡 트랜잭션 필수 세팅: 수동 커밋으로 전환!
			con.setAutoCommit(false); 
			
			// SQL 1: 영수증 추가 (insert into pos_orders...)
			String orderSql = "INSERT INTO Orders(custid, bookid, saleprice, quantity, orderdate) VALUES (?, ?, ?, ?, CURRENT_DATE)";			
			orderStmt = con.prepareStatement(orderSql);
			orderStmt.setInt(1, custId);
		    orderStmt.setInt(2, bookId);
		    orderStmt.setInt(3, totalLinePrice); // 기존 코드의 quantity 위치를 saleprice로 교정
			orderStmt.setInt(4, quantity);       // 기존 코드의 totalLinePrice 위치를 quantity로 교정
		    
		    int orderResult = orderStmt.executeUpdate();

	        if (orderResult == 0) {
	            con.rollback();
	            return false;
	        }

		    
			// SQL 2: 재고 차감 (update pos_product set stock = stock - ? where book_id = ?...)
	        String stockSql = "UPDATE Book SET stock = stock - ? WHERE bookid = ? AND stock >= ?";
	        stockStmt = con.prepareStatement(stockSql);
	        stockStmt.setInt(1, quantity);
	        stockStmt.setInt(2, bookId);
	        stockStmt.setInt(3, quantity);
	        
	        int stockResult = stockStmt.executeUpdate();

	        if (stockResult == 0) {
	            con.rollback();
	            return false;
	        }

			// SQL 3: 포인트 적립 (update pos_customer set point = point + ? where cust_id = ?...)
	        int savedPoint = totalLinePrice / 100;
	        String pointSql = "UPDATE Customer SET point = point + ? WHERE custid = ?";	        pointStmt = con.prepareStatement(pointSql);
	        pointStmt.setInt(1, savedPoint);
	        pointStmt.setInt(2, custId);

	        int pointResult = pointStmt.executeUpdate();

	        if (pointResult == 0) {
	            con.rollback();
	            return false;
	        }

	        
			// 모든 SQL이 에러 없이 잘 실행되면 수동 커밋!
			con.commit(); 
			return true;
			
		} catch (SQLException e) {
			// 하나라도 뻑나면 안전하게 전부 롤백!
			if (con != null) con.rollback(); 
			throw e;
		} finally {
			// 💡 [보완] 커넥션을 반환하기 전에 오토커밋을 true로 복구하여 다음 작업의 장애를 막습니다.
			if (con != null) {
				try { con.setAutoCommit(true); } catch (Exception e) {}
			}
			dbutil.close(orderStmt, stockStmt, pointStmt, con);
		}
	}

	// =========================================================================
	// 아래 메서드들은 파트너(송) 전용 기능이므로, 지원님 파일에서는 빈 껍데기로 유지합니다.
	// =========================================================================
	@Override
	public Customer searchCustomerByPhone(String phone) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
	////////////////////////TODO 02. 핸드폰 번호로 회원 조회하기 (송 담당)
			con = dbutil.getConnection();
			// SQL 예시: select cust_id, name, phone, point from pos_customer where phone = ?
			String sql = "SELECT custid, name, address, phone, point FROM Customer WHERE phone = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, phone);
			rs = stmt.executeQuery();
			if (rs.next()) {
				int custId = rs.getInt("custid");
	            String name = rs.getString("name");
	            String customerPhone = rs.getString("phone");
	            String address = rs.getString("address");
	            int point = rs.getInt("point");

	            return new Customer(custId, name, address, customerPhone, point);
	        }
			
		} finally {
			dbutil.close(rs, stmt, con);
		}
		return null;
	}
	
	

	@Override
	public void addCustomer(Customer cust) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
	////////////////////////TODO 03. 신규 포인트 회원 즉석 등록하기 (송 담당)
			con = dbutil.getConnection();
			// SQL 예시: insert into pos_customer(name, phone, point) values(?,?,?)
			String sql = "INSERT INTO Customer(name, address, phone, point) VALUES (?, ?, ?, ?)"; // 테이블명, 컬럼수 수정		        
			stmt = con.prepareStatement(sql);

		        stmt.setString(1, cust.getName());
		        stmt.setString(2, cust.getAddress()); // 주소 세팅 추가
		        stmt.setString(3, cust.getPhone());
		        stmt.setInt(4, cust.getPoint());
		        int result = stmt.executeUpdate();
		        
		        if (result == 0) {
		            throw new CanNotSaveException();
		        }
		} catch (SQLException e) {
	        throw new CanNotSaveException();

			
		} finally {
			dbutil.close(stmt, con);
		}
	}
	
	@Override
	public List<Book> getAllProducts() throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Book> list = new ArrayList<>();
		try {
			con = dbutil.getConnection();
			
			// 💡 1. 쿼리문에서 title 대신 실제 컬럼명인 bookname으로 수정!
			String sql = "SELECT bookid, bookname, publisher, price, stock, expire_date FROM Book ORDER BY bookid ASC";
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				Book book = new Book();
				book.setBookid(rs.getInt("bookid"));
				
				// 💡 2. DB에서 꺼내올 때도 bookname 컬럼 명칭으로 정확하게 매핑!
				book.setBookname(rs.getString("bookname"));      
				book.setPublisher(rs.getString("publisher"));
				book.setPrice(rs.getInt("price"));
				book.setStock(rs.getInt("stock"));
				
				java.sql.Date date = rs.getDate("expire_date");
				book.setExpireDate(date != null ? date.toString() : "");
				
				list.add(book);
			}
		} finally {
			dbutil.close(rs, stmt, con);
		}
		return list;
	}

	@Override
	public void updateProductStock(int bookId, int amount) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
	////////////////////////TODO 04. 물류 입고 - 상품 재고 더하기 (송 담당)
			con = dbutil.getConnection();
			// SQL 예시: update pos_product set stock = stock + ? where book_id = ?
			String sql = "UPDATE Book SET stock = stock + ? WHERE bookid = ?"; // 테이블명, 컬럼명 매칭
	        stmt = con.prepareStatement(sql);

	        stmt.setInt(1, amount);
	        stmt.setInt(2, bookId);

	        int result = stmt.executeUpdate();

	        if (result == 0) {
	            throw new CanNotSaveException();
	        }
		 } catch (SQLException e) {
		        throw new CanNotSaveException();

			
		} finally {
			dbutil.close(stmt, con);
		}
	}

}