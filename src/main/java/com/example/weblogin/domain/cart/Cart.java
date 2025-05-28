package com.example.weblogin.domain.cart;

import com.example.weblogin.domain.cartitem.CartItem;
import com.example.weblogin.domain.user.User;
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
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user; // 구매자

    private int count; // 카트에 담긴 총 상품 개수

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Builder.Default
    private LocalDate createDate = LocalDate.now();

    @PrePersist
    public void onCreate() {
        this.createDate = LocalDate.now();
    }

    public static Cart createCart(User user) {
        return Cart.builder()
                .user(user)
                .count(0)
                .build();
    }
}