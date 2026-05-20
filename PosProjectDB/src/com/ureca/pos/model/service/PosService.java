package com.ureca.pos.model.service;

import java.util.List;
import com.ureca.pos.model.dto.Customer;
import com.ureca.pos.model.dto.Book; 
public interface PosService {

    boolean checkExpiry(int bookId);
    boolean processPayment(int custId, int bookId, int quantity, int totalLinePrice); 

  
    Customer searchCustomerByPhone(String phone); 
    void addCustomer(Customer cust);  
    void updateProductStock(int bookId, int amount); 
    List<Book> getAllProducts();
    void addProduct(Book book);
    Book findProductById(int bookId);
    Book findProductByName(String bookName);
    int countProductOrders(int bookId);
    void deleteProductSafely(int bookId);
    

    void close(); 
}
