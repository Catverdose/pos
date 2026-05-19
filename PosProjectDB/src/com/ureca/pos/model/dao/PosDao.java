package com.ureca.pos.model.dao;

import java.sql.SQLException;
import java.util.List;

import com.ureca.pos.model.dto.Customer;

public interface PosDao {
	void add(Customer emp)			throws SQLException;
	void update(Customer emp)		throws SQLException;
	void remove(String empno)		throws SQLException;
	void close()					throws SQLException;
	Customer search(String empno) 	throws SQLException;
	List<Customer> searchAll()		throws SQLException;
}
