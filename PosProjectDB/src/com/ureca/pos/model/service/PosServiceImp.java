package com.ureca.pos.model.service;

import java.sql.SQLException;
import java.util.List;

import com.ureca.pos.model.dao.PosDao;
import com.ureca.pos.model.dto.CanNotFindException;
import com.ureca.pos.model.dto.DuplicateException;
import com.ureca.pos.model.dto.Customer;
import com.ureca.pos.model.dto.PosException;
import com.ureca.pos.util.PosFactory;

public class PosServiceImp implements PosService {
	private PosDao dao = PosFactory.getEmployee();
	
	@Override
	public void add(Customer emp) {
		try {
			String empno = emp.getEmpno();
			Customer find= dao.search(empno);
			if(find != null) {
				throw new DuplicateException(empno);
			}else {
				dao.add(emp);
			}
		}catch (SQLException e) {
			e.printStackTrace();
			throw new PosException("등록 중 오류 발생");
		}
	}
	public Customer search(String empno) {
		try {
			Customer emp = dao.search(empno);
			if(emp == null) {
				throw new CanNotFindException(empno);
			}
			return emp;
		}catch (SQLException e) {
			e.printStackTrace();
			throw new PosException("사원 정보 조회 중 오류 발생");
		}
	}
	public void update(Customer emp) {
		try {
			search(emp.getEmpno());
			dao.update(emp);
		}catch (SQLException e) {
			e.printStackTrace();
			throw new PosException("사원 정보 수정 중 오류 발생");
		}
	}
	public void remove(String empno) {
		try {
			search(empno);
			dao.remove(empno);
		}catch (SQLException e) {
			e.printStackTrace();
			throw new PosException("사원 정보 삭제 중 오류 발생");
		}
	}
	public void close() {
		System.exit(0);
	}
	public List<Customer> searchAll() {
		try {
			return dao.searchAll();
		}catch (SQLException e) {
			e.printStackTrace();
			throw new PosException("전체 사원 정보 조회 중 오류 발생");
		}
	}
}
