package com.ureca.employee.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ureca.employee.model.dto.Employee;
import com.ureca.employee.util.DBUtil;

public class EmployeeDaoImp implements EmployeeDao {
	////////////////////////TODO 01. DBUtil 객체 생성하기  
	private DBUtil dbutil = DBUtil.getInstance();
	
	@Override
	public void add(Employee emp) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			////////////////////////TODO 02. 사원 정보 등록하기 
			con = dbutil.getConnection();
			String sql =" insert into emp(empno, ename, sal) values(?,?,?) ";
			stmt = con.prepareStatement(sql);
			int idx = 1;
			stmt.setString(idx++, emp.getEmpno());
			stmt.setString(idx++, emp.getName());
			stmt.setInt(idx++, emp.getSalary());
			stmt.executeUpdate();
		} finally {
			dbutil.close(stmt, con);
		}
	}

	public void update(Employee emp) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
	////////////////////////TODO 03. 사원 정보 수정하기 
			
		} finally {
			dbutil.close(stmt, con);
		}
	}

	public void remove(String empno) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
	////////////////////////TODO 04. 사원 정보 삭제하기 
			
		} finally {
			dbutil.close(stmt, con);
		}

	}

	@Override
	public void close() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public Employee search(String empno) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			////////////////////////TODO 05. 사원 정보 조회하기
			con = dbutil.getConnection();
			String sql = " select empno, ename, sal from emp where empno = ? ";
			stmt = con.prepareStatement(sql);
			stmt.setString(1, empno);
			rs = stmt.executeQuery();
			if(rs.next()) {
				Employee emp = new Employee();
				emp.setEmpno(rs.getString("empno"));
				emp.setName(rs.getString("ename"));
				emp.setSalary(rs.getInt("sal"));
				return emp;
			}
		} finally {
			dbutil.close(rs, stmt, con);
		}
		return null;
	}

	@Override
	public List<Employee> searchAll() throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Employee> emps = new ArrayList<>(20);
		try {
		////////////////////////TODO 06. 사원 정보 전체 조회하기
			con = dbutil.getConnection();
			String sql = " select empno, ename, sal from emp ";
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			while(rs.next()) {
				Employee emp = new Employee();
				emp.setEmpno(rs.getString("empno"));
				emp.setName(rs.getString("ename"));
				emp.setSalary(rs.getInt("sal"));
				emps.add(emp);
			}
		} finally {
			dbutil.close(rs, stmt, con);
		}
		return emps;
	}
}


