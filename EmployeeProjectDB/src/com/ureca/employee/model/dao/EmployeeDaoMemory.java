package com.ureca.employee.model.dao;

import java.util.ArrayList;
import java.util.List;

import com.ureca.employee.model.dto.Employee;

public class EmployeeDaoMemory implements EmployeeDao {
	private ArrayList<Employee> emps;
	
	public EmployeeDaoMemory() {
		emps = new ArrayList<>(10);
	}
	public ArrayList<Employee> getEmps() {
		return emps;
	}
	public void setEmps(ArrayList<Employee> emps) {
		this.emps = emps;
	}
	private int findIndex(String empno) {
		if(empno !=null) {
			for (int i = 0, size = emps.size(); i < size; i++) {
				if(empno.equals(emps.get(i).getEmpno())) {
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
	public void add(Employee emp) {
		emps.add(emp);
	}

	@Override
	public void update(Employee emp) {
		int index = findIndex(emp.getEmpno());
		emps.set(index, emp);
	}

	@Override
	public void remove(String empno) {
		emps.remove(findIndex(empno));
	}

	@Override
	public void close() {
		System.exit(0);  	//jvm을 정상 종료
	}

	@Override
	public Employee search(String empno) {
		int index = findIndex(empno);
		if(index >-1) {
			return  emps.get(index);
		}else {
			return null;
		}
	}

	@Override
	public List<Employee> searchAll() {
		return emps.subList(0, emps.size());
	}

}
