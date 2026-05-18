package com.ureca.employee.model.dao;

public class CanNotSaveException extends RuntimeException {
	public CanNotSaveException() {
		super("저장 중 오류 발생!!!");
	}
}
