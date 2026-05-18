package com.ureca.employee.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class Employee implements Cloneable, Comparable<Employee>, Serializable{
	private String empno;
	private String name;
	private int salary;
	public Employee() {}
	public Employee(String empno, String name, int salary) {
		this.empno = empno;
		this.name = name;
		this.salary = salary;
	}
	
	@Override
	public int compareTo(Employee o) {
//		return o.getEmpno().compareTo(empno);		//내림 차순
		return empno.compareTo(o.empno);  //오름 차순
	}
	
	
	/**
	 * clone() 
	 *  - 객체의 내용이 똑같은 객체를 생성해서 리턴 
	 *  - Object 클래스에서 protected로 선언했기 때문에 함수를 사용하기 위해서는 반드시 Override 해야 한다. 
	 *  - Cloneable 인터페이스를 구현해야 한다. 
	 *   ==> Cloneable 인터페이스를 구현하지 않으면 CloneNotSupportedException이 발생한다. 
	 */
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/**
	 * equals(Object o)
	 * - 객체의 내용을 비교하는 함수 
	 * - 반드시 Override 해야 객체 내용을 비교할 수 있다.  
	 */
	public String getEmpno() {
		return empno;
	}
	
	
	
	/**
	 * hashCode 
	 *  - 객체의 참조 값을 리턴하는 기능 
	 *  - 필요시 Override한다. 
	 *    ==> hashCode() 함수를 Override 해도 객체의 실제 hashcode는 변하지 않는다. 
	 */
//	@Override
//	public int hashCode() {
//		return Objects.hash(empno, name, salary);
//	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Employee) {  // instanceof가  null 검사도 한다. 
			Employee emp = (Employee) obj;
			if (Objects.equals(empno, emp.empno)) {
				return true;
			}
		}
		return false;
	}
	public void setEmpno(String empno) {
		this.empno = empno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSalary() {
		return salary;
	}
	public void setSalary(int salary) {
		this.salary = salary;
	}
	
	/**
	 * finalize()
	 *  - 객체가 가비지 컬렉터에 의해 메모리에서 해제될 때 호출되는 함수 
	 */
	protected void finalize() throws Throwable {
		System.out.println(this.hashCode()+" finalize........");
	}
	
	/**
	 * toString()
	 * - 객체의 내용을 문자열로 리턴 
	 * - System.out으로 객체를 출력시  toString()를 호출해서 출력한다. 
	 * - 객체를 String 객체에 + 연산을 하면 toString()를 호출해서 문자열을 연결한다. 
	 */
	public String toString() {
		return "empno=" + empno + ", name=" + name + ", salary=" + salary;
	}
	
	void notVirtualInvoke() {
		System.out.println("Employee의 notVirtualInvoke()");
	}
}
