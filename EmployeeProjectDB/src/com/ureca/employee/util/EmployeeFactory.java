package com.ureca.employee.util;

import com.ureca.employee.model.dao.EmployeeDao;
import com.ureca.employee.model.dao.EmployeeDaoFile;
import com.ureca.employee.model.dao.EmployeeDaoImp;

public class EmployeeFactory {
//	private static final EmployeeDao dao = new EmployeeDaoMemory();
//	private static final EmployeeDao dao = new EmployeeDaoFile();
	private static final EmployeeDao dao = new EmployeeDaoImp();
	public static EmployeeDao getEmployee() {
		return dao;
	}
}
