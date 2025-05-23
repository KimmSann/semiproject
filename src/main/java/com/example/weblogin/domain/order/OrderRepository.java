package com.example.weblogin.domain.order;

import com.example.weblogin.domain.cart.Cart;
import com.example.weblogin.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findOrdersByUserId(int id);
}
