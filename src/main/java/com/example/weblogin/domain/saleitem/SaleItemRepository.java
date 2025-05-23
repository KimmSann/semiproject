package com.example.weblogin.domain.saleitem;

import com.example.weblogin.domain.cartitem.CartItem;
import com.example.weblogin.domain.orderitem.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Integer> {
    List<SaleItem> findSaleItemsBySellerId(int sellerId);
    List<SaleItem> findAll();
}
