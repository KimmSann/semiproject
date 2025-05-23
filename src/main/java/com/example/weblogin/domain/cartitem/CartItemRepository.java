package com.example.weblogin.domain.cartitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    CartItem findByCartIdAndItemId(int cartId, int itemId);
    CartItem findCartItemById(int id);
    List<CartItem> findCartItemByItemId(int id);
}
