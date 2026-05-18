package com.ureca.employee.model.dto;

public class Engineer extends Employee {
	private String skill;

	public Engineer() {
	}

	public Engineer(String empno, String name, int salary, String skill) {
		super(empno, name, salary);
		this.skill = skill;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String toString() {
		return super.toString()+" skill=" + skill;
	}
	public void notVirtualInvoke() {
		System.out.println("Engineeer의 notVirtualInvoke()");
	}
}
