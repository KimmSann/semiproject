package com.example.weblogin.domain.user;

import com.example.weblogin.domain.cart.Cart;
import com.example.weblogin.domain.item.Item;
import com.example.weblogin.domain.order.Order;
import com.example.weblogin.domain.orderitem.OrderItem;
import com.example.weblogin.domain.sale.Sale;
import com.example.weblogin.domain.saleitem.SaleItem;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String username;

    private String password;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String role;

    private int coin;

    @OneToMany(mappedBy = "seller")
    private List<Item> items = new ArrayList<>();

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Order> userOrder = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<OrderItem> userOrderItem = new ArrayList<>();

    @OneToMany(mappedBy = "seller")
    private List<SaleItem> sellerSaleItem = new ArrayList<>();

    @OneToMany(mappedBy = "seller")
    private List<Sale> sellerSale = new ArrayList<>(); // ✅ 여기 수정

    @DateTimeFormat(pattern = "yyyy-MM-dd") // ✅ MM로 수정
    private LocalDate createDate;

    @PrePersist
    public void createDate() {
        this.createDate = LocalDate.now();
    }
}