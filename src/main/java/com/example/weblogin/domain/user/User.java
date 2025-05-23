package com.example.weblogin.domain.user;

import com.example.weblogin.domain.cart.Cart;
import com.example.weblogin.domain.item.Item;
import com.example.weblogin.domain.order.Order;
import com.example.weblogin.domain.orderitem.OrderItem;
import com.example.weblogin.domain.sale.Sale;
import com.example.weblogin.domain.saleitem.SaleItem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;


import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Builder
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자를 만들어줌
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 만들어줌
@Getter
@Setter
@Entity // DB에 테이블 자동 생성
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true) // 닉네임 중복 안됨
    private String username;

    private String password;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String role; // 권한 (회원 / 관리자)

    private int coin; // 구매자 - 충전한 돈 / 판매자 - 수익

    // 판매자가 가지고 있는 상품들
    @OneToMany(mappedBy = "seller")
    private List<Item> items = new ArrayList<>();

    // 구매자의 장바구니
    @OneToOne(mappedBy = "user")
    private Cart cart;

    // 구매자의 주문
    @OneToMany(mappedBy = "user")
    private List<Order> userOrder = new ArrayList<>();

    // 구매자의 주문상품들
    @OneToMany(mappedBy = "user")
    private List<OrderItem> userOrderItem = new ArrayList<>();

    // 판매자의 판매상품들
    @OneToMany(mappedBy = "seller")
    private List<SaleItem> sellerSaleItem = new ArrayList<>();

    // 판매자의 판매
    @OneToMany(mappedBy = "seller")
    private List<Sale> sellerSale;

    @DateTimeFormat(pattern = "yyyy-mm-dd")
    private LocalDate createDate; // 날짜

    @PrePersist // DB에 INSERT 되기 직전에 실행. 즉 DB에 값을 넣으면 자동으로 실행됨
    public void createDate() {
        this.createDate = LocalDate.now();
    }
}
