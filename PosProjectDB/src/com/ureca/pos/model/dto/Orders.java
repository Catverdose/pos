package com.ureca.pos.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class Orders implements Cloneable, Comparable<Orders>, Serializable {
	private int orderid;      // 영수증 고유 트랜잭션 번호 (자동 증가)
	private int custid;       // 구매한 회원 고유 번호 (Customer FK)
	private int bookid;       // 구매한 상품 고유 번호 (Book FK)
	private int saleprice;    // 총 합산 결제 금액 (단가 * 수량)
	private int quantity;     // [ADD] 고객의 단일 상품 구매 수량
	private String orderdate; // 주문 일자 (DB의 DATE 타입을 자바에서 String으로 매핑)

	public Orders() {}

	public Orders(int orderid, int custid, int bookid, int saleprice, int quantity, String orderdate) {
		this.orderid = orderid;
		this.custid = custid;
		this.bookid = bookid;
		this.saleprice = saleprice;
		this.quantity = quantity;
		this.orderdate = orderdate;
	}

	@Override
	public int compareTo(Orders o) {
		// 최신 영수증 번호(orderid)가 앞으로 오도록 내림차순 정렬 (포스기 이력 조회 최적화)
		return Integer.compare(o.orderid, this.orderid);
	}

	/**
	 * clone() 
	 * - 객체의 내용이 똑같은 객체를 생성해서 리턴 
	 * - Object 클래스에서 protected로 선언했기 때문에 함수를 사용하기 위해서는 반드시 Override 해야 한다. 
	 * - Cloneable 인터페이스를 구현해야 한다. 
	 * ==> Cloneable 인터페이스를 구현하지 않으면 CloneNotSupportedException이 발생한다. 
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * equals(Object obj)
	 * - 객체의 내용을 비교하는 함수 
	 * - 영수증 고유 트랜잭션 번호(orderid)가 같으면 동일한 결제 이력으로 판정합니다.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Orders) { 
			Orders other = (Orders) obj;
			return Objects.equals(orderid, other.orderid);
		}
		return false;
	}

	// Getters and Setters
	public int getOrderid() {
		return orderid;
	}
	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}
	public int getCustid() {
		return custid;
	}
	public void setCustid(int custid) {
		this.custid = custid;
	}
	public int getBookid() {
		return bookid;
	}
	public void setBookid(int bookid) {
		this.bookid = bookid;
	}
	public int getSaleprice() {
		return saleprice;
	}
	public void setSaleprice(int saleprice) {
		this.saleprice = saleprice;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getOrderdate() {
		return orderdate;
	}
	public void setOrderdate(String orderdate) {
		this.orderdate = orderdate;
	}

	/**
	 * finalize()
	 * - 객체가 가비지 컬렉터에 의해 메모리에서 해제될 때 호출되는 함수 
	 */
	@Override
	protected void finalize() throws Throwable {
		System.out.println(this.hashCode() + " Orders 객체 수거 완료(finalize)...");
	}

	/**
	 * toString()
	 * - 객체의 내용을 문자열로 리턴 
	 */
	@Override
	public String toString() {
		return "Orders [orderid=" + orderid + ", custid=" + custid + ", bookid=" + bookid + ", saleprice=" + saleprice
				+ ", quantity=" + quantity + ", orderdate=" + orderdate + "]";
	}

	void notVirtualInvoke() {
		System.out.println("Orders의 notVirtualInvoke()");
	}
}