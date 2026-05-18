package com.ureca.employee;

import com.ureca.employee.model.service.EmployeeService;
import com.ureca.employee.model.service.EmployeeServiceImp;
import com.ureca.employee.view.EmployeeUI;

public class Main {
	public static void main(String[] args) {
		
		EmployeeService service = new EmployeeServiceImp();
		EmployeeUI mainView = new EmployeeUI();
		mainView.setModel(service);
		
	}
}
