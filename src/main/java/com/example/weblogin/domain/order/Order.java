package com.example.weblogin.domain.order;

import com.example.weblogin.domain.orderitem.OrderItem;
import com.example.weblogin.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user; // 구매자

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItems = new ArrayList<>();

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate createDate; // 구매 날짜

    @PrePersist
    public void createDate() {
        this.createDate = LocalDate.now();
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(User user, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setUser(user);
        for (OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        order.setCreateDate(order.createDate);
        return order;
    }

    public static Order createOrder(User user) {
        Order order = new Order();
        order.setUser(user);
        order.setCreateDate(order.createDate);
        return order;
    }

}
