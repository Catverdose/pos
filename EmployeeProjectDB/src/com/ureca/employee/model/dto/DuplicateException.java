package com.ureca.employee.model.dto;

public class DuplicateException extends RuntimeException {
	public DuplicateException() {}
	public DuplicateException(String empno) {
		super(String.format("사원번호 %s번은 이미 등록된 사원번호 입니다.", empno));
	}
}
