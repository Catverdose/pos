package com.ureca.employee.model.service;

import java.util.List;

import com.ureca.employee.model.dto.Employee;

public interface EmployeeService {
	void add(Employee emp);
	void update(Employee emp);
	void remove(String empno);
	void close();
	Employee search(String empno);
	List<Employee> searchAll();
}
