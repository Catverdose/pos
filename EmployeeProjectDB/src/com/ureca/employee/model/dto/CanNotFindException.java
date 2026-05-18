package com.ureca.employee.model.dto;

public class CanNotFindException extends RuntimeException {
	public CanNotFindException() {}
	public CanNotFindException(String empno) {
		super(String.format("사원번호 %s번에 해당하는 사원 정보를 찾을 수 없습니다.", empno));
	}
}
