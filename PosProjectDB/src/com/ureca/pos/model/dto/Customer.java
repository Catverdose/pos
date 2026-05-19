package com.ureca.pos.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class Customer implements Cloneable, Comparable<Customer>, Serializable{
	private int custid;
	private String name;
	private String address;
	private String phone;
	private int point;
	
	public Customer() {}
	public Customer(int custid, String name, String address, String phone, int point) {
		this.custid = custid;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.point = point;
	}
	
	@Override
	public int compareTo(Customer o) {
//		return o.getEmpno().compareTo(empno);		//내림 차순
		return Integer.compare(this.custid, o.custid); // 회원번호 오름차순 정렬	
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
	 * * - 편의점 실무에서는 중복 가입을 막기 위해 '전화번호(phone)'가 같으면 같은 회원으로 판정합니다.
	 */
	public boolean booleanEquals(Object obj) {
		
		if (obj instanceof Customer) {  // instanceof가 null 검사도 한다. 
			Customer other = (Customer) obj;
			if (Objects.equals(phone, other.phone)) {
				return true;
			}
		}
		return false;
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
		if (obj instanceof Customer) {  // instanceof가  null 검사도 한다. 
			Customer other = (Customer) obj;
			return Objects.equals(phone, other.phone);
		}
		return false;
	}
	public int getCustid() {
		return custid;
	}
	public void setCustid(int custid) {
		this.custid = custid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	/**
	 * finalize()
	 *  - 객체가 가비지 컬렉터에 의해 메모리에서 해제될 때 호출되는 함수 
	 */
	protected void finalize() throws Throwable {
		System.out.println(this.hashCode() + " Customer 객체 수거 완료(finalize)...");	}
	
	/**
	 * toString()
	 * - 객체의 내용을 문자열로 리턴 
	 * - System.out으로 객체를 출력시  toString()를 호출해서 출력한다. 
	 * - 객체를 String 객체에 + 연산을 하면 toString()를 호출해서 문자열을 연결한다. 
	 */
	@Override
	public String toString() {
		return "Customer [custid=" + custid + ", name=" + name + ", address=" + address + ", phone=" + phone
				+ ", point=" + point + "]";
	}
	void notVirtualInvoke() {
		System.out.println("Employee의 notVirtualInvoke()");
	}
}
