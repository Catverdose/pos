package com.ureca.pos.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class Book implements Cloneable, Comparable<Book>, Serializable {
	private int bookid;          // 상품 고유 식별자 및 바코드 번호
	private String bookname;     // 편의점 상품명
	private String publisher;    // 제조사
	private int price;           // 상품 단가
	private int stock;           // 실시간 매대 잔여 재고량
	private String expireDate;   // 💡 변수명과 toString() 명칭을 expireDate로 깔끔하게 통일했습니다!

	public Book() {}
	
	public Book(int bookid, String bookname, String publisher, int price, int stock, String expireDate) {
		this.bookid = bookid;
		this.bookname = bookname;
		this.publisher = publisher;
		this.price = price;
		this.stock = stock;
		this.expireDate = expireDate;
	}
	
	@Override
	public int compareTo(Book o) {
		// 상품번호(bookid) 기준 오름차순 정렬
		return Integer.compare(this.bookid, o.bookid); 
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
	 * - 편의점 상품 바코드(bookid)가 일치하면 동일 상품으로 판정합니다.
	 * 💡 (가짜 중복 메서드인 booleanEquals는 깔끔하게 삭제하고 표준 equals로 일원화했습니다.)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Book) {  // instanceof가 null 검사도 한다. 
			Book other = (Book) obj;
			return Objects.equals(bookid, other.bookid);
		}
		return false;
	}
	
	// =========================================================================
	// Getters and Setters
	// =========================================================================
	public int getBookid() {
		return bookid;
	}
	public void setBookid(int bookid) {
		this.bookid = bookid;
	}
	public String getBookname() {
		return bookname;
	}
	public void setBookname(String bookname) {
		this.bookname = bookname;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public String getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	
	/**
	 * finalize()
	 * - 객체가 가비지 컬렉터에 의해 메모리에서 해제될 때 호출되는 함수 
	 */
	@Override
	protected void finalize() throws Throwable {
		System.out.println(this.hashCode() + " Book 객체 수거 완료(finalize)...");
	}
	
	/**
	 * toString()
	 * - 객체의 내용을 문자열로 리턴 
	 */
	@Override
	public String toString() {
		return "Book [bookid=" + bookid + ", bookname=" + bookname + ", publisher=" + publisher + ", price=" + price
				+ ", stock=" + stock + ", expireDate=" + expireDate + "]";
	}
	
	void notVirtualInvoke() {
		System.out.println("Employee의 notVirtualInvoke()");
	}
}