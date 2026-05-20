package com.ureca.pos.model.dao;

import java.sql.Connection;
import java.sql.Date;
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
			
			con.setAutoCommit(false); 

		    
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


	        int savedPoint = totalLinePrice / 100;
	        String pointSql = "UPDATE Customer SET point = point + ? WHERE custid = ?";	        pointStmt = con.prepareStatement(pointSql);
	        pointStmt.setInt(1, savedPoint);
	        pointStmt.setInt(2, custId);

	        int pointResult = pointStmt.executeUpdate();

	        if (pointResult == 0) {
	            con.rollback();
	            return false;
	        }

			int orderId = nextOrderId(con);
			String orderSql = "INSERT INTO Orders(orderid, custid, bookid, saleprice, quantity, orderdate) VALUES (?, ?, ?, ?, ?, CURRENT_DATE)";
			orderStmt = con.prepareStatement(orderSql);
			orderStmt.setInt(1, orderId);
			orderStmt.setInt(2, custId);
		    orderStmt.setInt(3, bookId);
		    orderStmt.setInt(4, totalLinePrice);
			orderStmt.setInt(5, quantity);

		    int orderResult = orderStmt.executeUpdate();

	        if (orderResult == 0) {
	            con.rollback();
	            return false;
	        }

	        

			con.commit(); 
			return true;
			
		} catch (SQLException e) {

			if (con != null) con.rollback(); 
			throw e;
		} finally {

			if (con != null) {
				try { con.setAutoCommit(true); } catch (Exception e) {}
			}
			dbutil.close(orderStmt, stockStmt, pointStmt, con);
		}
	}


	@Override
	public Customer searchCustomerByPhone(String phone) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
	////////////////////////TODO 02. 핸드폰 번호로 회원 조회하기 (송 담당)
			con = dbutil.getConnection();

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
			

			String sql = "SELECT bookid, bookname, publisher, price, stock, expire_date FROM Book ORDER BY bookid ASC";
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				list.add(rowToBook(rs));
			}
		} finally {
			dbutil.close(rs, stmt, con);
		}
		return list;
	}

	@Override
	public void addProduct(Book book) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dbutil.getConnection();
			int bookId = book.getBookid() > 0 ? book.getBookid() : nextBookId(con);
			String sql = "INSERT INTO Book(bookid, bookname, publisher, price, stock, expire_date) VALUES (?, ?, ?, ?, ?, ?)";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, bookId);
			stmt.setString(2, book.getBookname());
			stmt.setString(3, book.getPublisher());
			stmt.setInt(4, book.getPrice());
			stmt.setInt(5, book.getStock());
			stmt.setDate(6, Date.valueOf(book.getExpireDate()));

			int result = stmt.executeUpdate();
			if (result == 0) {
				throw new CanNotSaveException();
			}
			book.setBookid(bookId);
		} finally {
			dbutil.close(stmt, con);
		}
	}

	@Override
	public Book findProductById(int bookId) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dbutil.getConnection();
			String sql = "SELECT bookid, bookname, publisher, price, stock, expire_date FROM Book WHERE bookid = ?";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, bookId);
			rs = stmt.executeQuery();

			if (rs.next()) {
				return rowToBook(rs);
			}
		} finally {
			dbutil.close(rs, stmt, con);
		}
		return null;
	}

	@Override
	public Book findProductByName(String bookName) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dbutil.getConnection();
			String sql = "SELECT bookid, bookname, publisher, price, stock, expire_date FROM Book WHERE bookname = ?";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, bookName);
			rs = stmt.executeQuery();

			if (rs.next()) {
				return rowToBook(rs);
			}
		} finally {
			dbutil.close(rs, stmt, con);
		}
		return null;
	}

	@Override
	public int countProductOrders(int bookId) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = dbutil.getConnection();
			String sql = "SELECT COUNT(*) FROM Orders WHERE bookid = ?";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, bookId);
			rs = stmt.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}
		} finally {
			dbutil.close(rs, stmt, con);
		}
		return 0;
	}

	@Override
	public void deleteProduct(int bookId) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = dbutil.getConnection();
			String sql = "DELETE FROM Book WHERE bookid = ? AND NOT EXISTS (SELECT 1 FROM Orders WHERE bookid = ?)";
			stmt = con.prepareStatement(sql);
			stmt.setInt(1, bookId);
			stmt.setInt(2, bookId);

			int result = stmt.executeUpdate();
			if (result == 0) {
				throw new SQLException("상품을 찾을 수 없거나 판매 이력이 있어 삭제할 수 없습니다.");
			}
		} finally {
			dbutil.close(stmt, con);
		}
	}

	@Override
	public void updateProductStock(int bookId, int amount) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
	////////////////////////TODO 04. 물류 입고 - 상품 재고 더하기 (송 담당)
			con = dbutil.getConnection();
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

	private Book rowToBook(ResultSet rs) throws SQLException {
		Book book = new Book();
		book.setBookid(rs.getInt("bookid"));
		book.setBookname(rs.getString("bookname"));
		book.setPublisher(rs.getString("publisher"));
		book.setPrice(rs.getInt("price"));
		book.setStock(rs.getInt("stock"));

		Date date = rs.getDate("expire_date");
		book.setExpireDate(date != null ? date.toString() : "");
		return book;
	}

	private int nextBookId(Connection con) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT COALESCE(MAX(bookid), 0) + 1 FROM Book";
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 1;
		} finally {
			dbutil.close(rs, stmt);
		}
	}

	private int nextOrderId(Connection con) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT COALESCE(MAX(orderid), 0) + 1 FROM Orders";
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 1;
		} finally {
			dbutil.close(rs, stmt);
		}
	}

}
