package com.ureca.employee.model.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.ureca.employee.model.dto.Employee;

public class EmployeeDaoFile extends EmployeeDaoMemory {
	private String fileName="emps.dat";
	
	public EmployeeDaoFile() {
		load();
	}
	private void load() {
		File file = new File(fileName);
		if(file.exists() && file.canRead()) {
			try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
				ArrayList<Employee> emps = (ArrayList<Employee>) ois.readObject();
				setEmps(emps);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	//등록 , 수정, 삭제 시 마다 저장을 한다면 
//	@Override
//	public void add(Employee emp) {
//		super.add(emp);
//		save();
//	}
	
	@Override
	public void close() {
		save();
		System.exit(0);
	}
	
	private void save() {
		try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
			oos.writeObject(getEmps());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CanNotSaveException();
		}
	}
}





