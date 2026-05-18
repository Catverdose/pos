package com.ureca.employee.model.dao;

import java.sql.SQLException;
import java.util.List;

import com.ureca.employee.model.dto.Employee;

public interface EmployeeDao {
	void add(Employee emp)			throws SQLException;
	void update(Employee emp)		throws SQLException;
	void remove(String empno)		throws SQLException;
	void close()					throws SQLException;
	Employee search(String empno) 	throws SQLException;
	List<Employee> searchAll()		throws SQLException;
}
