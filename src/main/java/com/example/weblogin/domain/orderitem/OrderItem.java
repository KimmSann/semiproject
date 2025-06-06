package com.example.weblogin.domain.orderitem;

import com.example.weblogin.domain.cartitem.CartItem;
import com.example.weblogin.domain.item.Item;
import com.example.weblogin.domain.order.Order;
import com.example.weblogin.domain.saleitem.SaleItem;
import com.example.weblogin.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

import jakarta.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user; // 구매자

    //@ManyToOne(fetch = FetchType.EAGER)
    //@JoinColumn(name="item_id")
    //private Item item;

    private int itemId; // 주문 상품 번호
    private String itemName; // 주문 상품 이름
    private int itemPrice; // 주문 상품 가격
    private int itemCount; // 주문 상품 수량
    private int itemTotalPrice; // 가격*수량

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="saleItem_id")
    private SaleItem saleItem; // 주문상품에 매핑되는 판매상품

    private int isCancel; // 주문 취소 여부 (0:주문완료 / 1:주문취소)

    // 장바구니 전체 주문
    public static OrderItem createOrderItem(int itemId, User user, CartItem cartItem, SaleItem saleItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(itemId);
        orderItem.setUser(user);
        orderItem.setItemName(cartItem.getItem().getName());
        orderItem.setItemPrice(cartItem.getItem().getPrice());
        orderItem.setItemCount(cartItem.getCount());
        orderItem.setItemTotalPrice(cartItem.getItem().getPrice()*cartItem.getCount());
        orderItem.setSaleItem(saleItem);
        return orderItem;
    }

    // 상품 개별 주문
    public static OrderItem createOrderItem(int itemId, User user, Item item, int count, Order order, SaleItem saleItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItemId(itemId);
        orderItem.setUser(user);
        orderItem.setOrder(order);
        orderItem.setItemName(item.getName());
        orderItem.setItemPrice(item.getPrice());
        orderItem.setItemCount(count);
        orderItem.setItemTotalPrice(item.getPrice()*count);
        orderItem.setSaleItem(saleItem);
        return orderItem;
    }

}
