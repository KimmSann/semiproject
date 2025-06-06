package com.example.weblogin.controller;

import com.example.weblogin.config.auth.PrincipalDetails;
import com.example.weblogin.domain.cart.Cart;
import com.example.weblogin.domain.cartitem.CartItem;
import com.example.weblogin.domain.item.Item;
import com.example.weblogin.domain.orderitem.OrderItem;
import com.example.weblogin.domain.saleitem.SaleItem;
import com.example.weblogin.domain.user.User;
import com.example.weblogin.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserPageController {

    private final UserPageService userPageService;
    private final CartService cartService;
    private final ItemService itemService;
    private final OrderService orderService;
    private final SaleService saleService;

    @GetMapping("/user/{id}")
    public String userPage(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails.getUser().getId() != id) {
            return "redirect:/main";
        }
        model.addAttribute("user", userPageService.findUser(id));
        return "/user/userPage";
    }

    @GetMapping("/user/modify/{id}")
    public String userModify(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails.getUser().getId() != id) {
            return "redirect:/main";
        }
        model.addAttribute("user", userPageService.findUser(id));
        return "/userModify";
    }

    @PostMapping("/user/update/{id}")
    public String userUpdate(@PathVariable("id") Integer id, User user) {
        userPageService.userModify(user);
        return "redirect:/user/{id}";
    }

    @GetMapping("/user/cart/{id}")
    public String userCartPage(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails.getUser().getId() != id) {
            return "redirect:/main";
        }
        User user = userPageService.findUser(id);
        Cart userCart = user.getCart();
        List<CartItem> cartItemList = cartService.allUserCartView(userCart);

        int totalPrice = 0;
        for (CartItem cartitem : cartItemList) {
            totalPrice += cartitem.getCount() * cartitem.getItem().getPrice();
        }

        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalCount", userCart.getCount());
        model.addAttribute("cartItems", cartItemList);
        model.addAttribute("user", user);

        return "/user/userCart";
    }

    @PostMapping("/user/cart/{id}/{itemId}")
    public String addCartItem(
            @PathVariable("id") Integer id,
            @PathVariable("itemId") Integer itemId,
            @RequestParam(name = "amount", defaultValue = "1") int amount) {
        
        User user = userPageService.findUser(id);
        Item item = itemService.itemView(itemId);
        cartService.addCart(user, item, amount);
        return "redirect:/item/view/" + itemId;
    }
    @GetMapping("/user/cart/{id}/{cartItemId}/delete")
    public String deleteCartItem(@PathVariable("id") Integer id, @PathVariable("cartItemId") Integer itemId, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails.getUser().getId() != id) {
            return "redirect:/main";
        }
        CartItem cartItem = cartService.findCartItemById(itemId);
        Cart userCart = cartService.findUserCart(id);
        userCart.setCount(userCart.getCount() - cartItem.getCount());
        cartService.cartItemDelete(itemId);

        List<CartItem> cartItemList = cartService.allUserCartView(userCart);
        int totalPrice = 0;
        for (CartItem cartitem : cartItemList) {
            totalPrice += cartitem.getCount() * cartitem.getItem().getPrice();
        }

        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("totalCount", userCart.getCount());
        model.addAttribute("cartItems", cartItemList);
        model.addAttribute("user", userPageService.findUser(id));

        return "/user/userCart";
    }

    @GetMapping("/user/orderHist/{id}")
    public String orderList(@PathVariable("id") Integer id, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        if (principalDetails.getUser().getId() != id) {
            return "redirect:/main";
        }
        List<OrderItem> orderItemList = orderService.findUserOrderItems(id);
        int totalCount = 0;
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.getIsCancel() != 1)
                totalCount += orderItem.getItemCount();
        }
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("orderItems", orderItemList);
        model.addAttribute("user", userPageService.findUser(id));
        return "user/userOrderList";
    }

    @Transactional
    @PostMapping("/user/cart/checkout/{id}")
    public String cartCheckout(@PathVariable("id") Integer id, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        if (principalDetails.getUser().getId() != id) {
            return "redirect:/main";
        }
        User user = userPageService.findUser(id);
        Cart userCart = cartService.findUserCart(user.getId());
        List<CartItem> userCartItems = cartService.allUserCartView(userCart);

        int totalPrice = 0;
        for (CartItem cartItem : userCartItems) {
            if (cartItem.getItem().getStock() == 0 || cartItem.getItem().getStock() < cartItem.getCount()) {
                return "redirect:/main";
            }
            totalPrice += cartItem.getCount() * cartItem.getItem().getPrice();
        }

        int userCoin = user.getCoin();
        if (userCoin < totalPrice) {
            return "redirect:/main";
        } else {
            user.setCoin(user.getCoin() - totalPrice);

            List<OrderItem> orderItemList = new ArrayList<>();
            for (CartItem cartItem : userCartItems) {
                User seller = cartItem.getItem().getSeller();
                seller.setCoin(seller.getCoin() + (cartItem.getCount() * cartItem.getItem().getPrice()));
                cartItem.getItem().setStock(cartItem.getItem().getStock() - cartItem.getCount());
                cartItem.getItem().setCount(cartItem.getItem().getCount() + cartItem.getCount());

                SaleItem saleItem = saleService.addSale(cartItem.getItem().getId(), seller.getId(), cartItem);
                OrderItem orderItem = orderService.addCartOrder(cartItem.getItem().getId(), user.getId(), cartItem, saleItem);
                orderItemList.add(orderItem);
            }

            orderService.addOrder(user, orderItemList);
            cartService.allCartItemDelete(id);
        }

        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("cartItems", userCartItems);
        model.addAttribute("user", userPageService.findUser(id));

        return "redirect:/user/cart/{id}";
    }

    @Transactional
    @PostMapping("/user/{id}/checkout/{itemId}")
    public String checkout(@PathVariable("id") Integer id, @PathVariable("itemId") Integer itemId, @AuthenticationPrincipal PrincipalDetails principalDetails, Model model, int count) {
        if (principalDetails.getUser().getId() != id) {
            return "redirect:/main";
        }
        User user = userPageService.findUser(id);
        Item item = itemService.itemView(itemId);

        if (item.getStock() == 0 || item.getStock() < count) {
            return "redirect:/main";
        }

        int totalPrice = item.getPrice() * count;
        int userCoin = user.getCoin();
        if (userCoin < totalPrice) {
            return "redirect:/main";
        } else {
            user.setCoin(user.getCoin() - totalPrice);
            item.getSeller().setCoin(item.getSeller().getCoin() + totalPrice);
            item.setStock(item.getStock() - count);
            item.setCount(item.getCount() + count);

            SaleItem saleItem = saleService.addSale(item.getSeller().getId(), item, count);
            orderService.addOneItemOrder(user.getId(), item, count, saleItem);
        }

        return "redirect:/user/orderHist/{id}";
    }

    @PostMapping("/user/{id}/checkout/cancel/{orderItemId}")
    public String cancelOrder(@PathVariable("id") Integer id, @PathVariable("orderItemId") Integer orderItemId, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails.getUser().getId() != id) {
            return "redirect:/main";
        }
        OrderItem cancelItem = orderService.findOrderitem(orderItemId);
        User user = userPageService.findUser(id);

        List<OrderItem> orderItemList = orderService.findUserOrderItems(id);
        int totalCount = 0;
        for (OrderItem orderItem : orderItemList) {
            totalCount += orderItem.getItemCount();
        }
        totalCount = totalCount - cancelItem.getItemCount();

        orderService.orderCancel(user, cancelItem);

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("orderItems", orderItemList);
        model.addAttribute("user", user);

        return "redirect:/user/orderHist/{id}";
    }

    @Transactional
    @GetMapping("/user/cash/{id}")
    public String charge(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails.getUser().getId() != id) {
            return "redirect:/main";
        }
        User user = userPageService.findUser(id);
        model.addAttribute("user", user);
        return "/user/cash";
    }

    @GetMapping("/user/charge/pro")
    public String chargePro(int amount, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = userPageService.findUser(principalDetails.getUser().getId());
        userPageService.chargePoint(user.getId(), amount);
        return "redirect:/main";
    }
}
