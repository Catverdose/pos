package com.ureca.pos.model.service;

import java.util.List;

import com.ureca.pos.model.dto.Customer;

public interface PosService {
	void add(Customer emp);
	void update(Customer emp);
	void remove(String empno);
	void close();
	Customer search(String empno);
	List<Customer> searchAll();
}
