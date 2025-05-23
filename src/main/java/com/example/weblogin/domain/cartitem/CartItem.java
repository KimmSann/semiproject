package com.example.weblogin.domain.cartitem;

import com.example.weblogin.domain.cart.Cart;
import com.example.weblogin.domain.item.Item;

import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class CartItem {

    @jakarta.persistence.Id // ✅ 변경된 부분: 정확한 JPA용 @Id 사용
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count; // 상품 개수

    public static CartItem createCartItem(Cart cart, Item item, int amount) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(amount);
        return cartItem;
    }

    // 이미 담겨있는 물건 또 담을 경우 수량 증가
    public void addCount(int count) {
        this.count += count;
    }
}