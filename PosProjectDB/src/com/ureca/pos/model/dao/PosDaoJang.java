package com.ureca.pos.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
			 String sql = "SELECT expire_date FROM pos_product WHERE book_id = ?";
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
			String orderSql = "INSERT INTO pos_orders(cust_id, book_id, quantity, total_price, order_date) VALUES (?, ?, ?, ?, NOW())";
			orderStmt = con.prepareStatement(orderSql);
			orderStmt.setInt(1, custId);
		    orderStmt.setInt(2, bookId);
		    orderStmt.setInt(3, quantity);
		    orderStmt.setInt(4, totalLinePrice);
		    
		    int orderResult = orderStmt.executeUpdate();

	        if (orderResult == 0) {
	            con.rollback();
	            return false;
	        }

		    
			// SQL 2: 재고 차감 (update pos_product set stock = stock - ? where book_id = ?...)
		    String stockSql = "UPDATE pos_product SET stock = stock - ? WHERE book_id = ? AND stock >= ?";
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
	        String pointSql = "UPDATE pos_customer SET point = point + ? WHERE cust_id = ?";
	        pointStmt = con.prepareStatement(pointSql);
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
			// 💡 다음 사람을 위해 AutoCommit을 다시 true로 돌려놓고 닫기
			if (con != null) {
	            con.setAutoCommit(true);
	        }

	        if (pointStmt != null) {
	            pointStmt.close();
	        }

	        if (stockStmt != null) {
	            stockStmt.close();
	        }

	        if (orderStmt != null) {
	            orderStmt.close();
	        }

	        if (con != null) {
	            con.close();
	        }
		}
	}

	// =========================================================================
	// 아래 메서드들은 파트너(송) 전용 기능이므로, 지원님 파일에서는 빈 껍데기로 유지합니다.
	// =========================================================================
	@Override
	public com.ureca.pos.model.dto.Customer searchCustomerByPhone(String phone) throws SQLException {
		return null;
	}

	@Override
	public void addCustomer(com.ureca.pos.model.dto.Customer cust) throws SQLException {
	}

	@Override
	public void updateProductStock(int bookId, int amount) throws SQLException {
	}
}