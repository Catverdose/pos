package com.ureca.pos.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.ureca.pos.model.dto.Customer;
import com.ureca.pos.util.DBUtil;

public class PosDaoSong implements PosDao {
	////////////////////////TODO 01. DBUtil 객체 생성하기  
	private DBUtil dbutil = DBUtil.getInstance();

	@Override
	public Customer searchCustomerByPhone(String phone) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
	////////////////////////TODO 02. 핸드폰 번호로 회원 조회하기 (송 담당)
			con = dbutil.getConnection();
			// SQL 예시: select cust_id, name, phone, point from pos_customer where phone = ?
			
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
			// SQL 예시: update pos_product set stock = stock + ? where book_id = ?
			
		} finally {
			dbutil.close(stmt, con);
		}
	}

	// =========================================================================
	// 아래 메서드들은 개발자 A(지원) 전용 기능이므로, 송님 파일에서는 빈 껍데기로 유지합니다.
	// =========================================================================
	@Override
	public boolean checkExpiry(int bookId) throws SQLException {
		return false;
	}

	@Override
	public boolean processPayment(int custId, int bookId, int quantity, int totalLinePrice) throws SQLException {
		return false;
	}
}