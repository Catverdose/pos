package com.ureca.employee.model.service;

import java.sql.SQLException;
import java.util.List;

import com.ureca.employee.model.dao.EmployeeDao;
import com.ureca.employee.model.dto.CanNotFindException;
import com.ureca.employee.model.dto.DuplicateException;
import com.ureca.employee.model.dto.Employee;
import com.ureca.employee.model.dto.EmployeeException;
import com.ureca.employee.util.EmployeeFactory;

public class EmployeeServiceImp implements EmployeeService {
	private EmployeeDao dao = EmployeeFactory.getEmployee();
	
	@Override
	public void add(Employee emp) {
		try {
			String empno = emp.getEmpno();
			Employee find= dao.search(empno);
			if(find != null) {
				throw new DuplicateException(empno);
			}else {
				dao.add(emp);
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw new EmployeeException("등록 중 오류 발생");
		}
	}
	public Employee search(String empno) {
		try {
			Employee emp = dao.search(empno);
			if(emp == null) {
				throw new CanNotFindException(empno);
			}
			return emp;
		}catch (SQLException e) {
			e.printStackTrace();
			throw new EmployeeException("사원 정보 조회 중 오류 발생");
		}
	}
	public void update(Employee emp) {
		try {
			search(emp.getEmpno());
			dao.update(emp);
		}catch (SQLException e) {
			e.printStackTrace();
			throw new EmployeeException("사원 정보 수정 중 오류 발생");
		}
	}
	public void remove(String empno) {
		try {
			search(empno);
			dao.remove(empno);
		}catch (SQLException e) {
			e.printStackTrace();
			throw new EmployeeException("사원 정보 삭제 중 오류 발생");
		}
	}
	public void close() {
		System.exit(0);
	}
	public List<Employee> searchAll() {
		try {
			return dao.searchAll();
		}catch (SQLException e) {
			e.printStackTrace();
			throw new EmployeeException("전체 사원 정보 조회 중 오류 발생");
		}
	}
}
