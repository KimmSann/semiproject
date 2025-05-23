package com.example.weblogin.controller;

import com.example.weblogin.config.auth.PrincipalDetails;
import com.example.weblogin.domain.cart.Cart;
import com.example.weblogin.domain.cartitem.CartItem;
import com.example.weblogin.domain.item.Item;
import com.example.weblogin.domain.user.User;
import com.example.weblogin.service.CartService;
import com.example.weblogin.service.ItemService;
import com.example.weblogin.service.UserPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserPageService userPageService;
    private final CartService cartService;

    @GetMapping("/")
    public String mainPageNoneLogin(Model model) {
        List<Item> items = itemService.allItemView();
        model.addAttribute("items", items);
        return "main";
    }

    @GetMapping("/main")
    public String mainPage(Model model,
                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<Item> items = itemService.allItemView();
        model.addAttribute("items", items);
        model.addAttribute("user",
            userPageService.findUser(principalDetails.getUser().getId()));
        return "/main";
    }

    @GetMapping("/item/new")
    public String itemSaveForm(@AuthenticationPrincipal PrincipalDetails principalDetails,
                               Model model) {
        if (!principalDetails.getUser().getRole().equals("ROLE_SELLER")) {
            return "redirect:/main";
        }
        model.addAttribute("user", principalDetails.getUser());
        return "/seller/itemForm";
    }

    @PostMapping("/item/new/pro")
    public String itemSave(Item item,
                           @AuthenticationPrincipal PrincipalDetails principalDetails,
                           MultipartFile imgFile) throws Exception {
        if (!principalDetails.getUser().getRole().equals("ROLE_SELLER")) {
            return "redirect:/main";
        }
        item.setSeller(principalDetails.getUser());
        itemService.saveItem(item, imgFile);
        return "redirect:/main";
    }

    @GetMapping("/item/modify/{id}")
    public String itemModifyForm(@PathVariable("id") Integer id,
                                 Model model,
                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (!principalDetails.getUser().getRole().equals("ROLE_SELLER")) {
            return "redirect:/main";
        }
        User seller = itemService.itemView(id).getSeller();
        if (seller.getId() != principalDetails.getUser().getId()) {
            return "redirect:/main";
        }
        model.addAttribute("item", itemService.itemView(id));
        model.addAttribute("user", principalDetails.getUser());
        return "/seller/itemModify";
    }

    @PostMapping("/item/modify/pro/{id}")
    public String itemModify(Item item,
                             @PathVariable("id") Integer id,
                             @AuthenticationPrincipal PrincipalDetails principalDetails,
                             MultipartFile imgFile) throws Exception {
        if (!principalDetails.getUser().getRole().equals("ROLE_SELLER")) {
            return "redirect:/main";
        }
        User seller = itemService.itemView(id).getSeller();
        if (seller.getId() != principalDetails.getUser().getId()) {
            return "redirect:/main";
        }
        itemService.itemModify(item, id, imgFile);
        return "redirect:/main";
    }

    @GetMapping("/item/view/{itemId}")
    public String ItemView(Model model,
                           @PathVariable("itemId") Integer id,
                           @AuthenticationPrincipal PrincipalDetails principalDetails) {
        User user = principalDetails.getUser();
        model.addAttribute("item", itemService.itemView(id));
        model.addAttribute("user", user);

        if (!user.getRole().equals("ROLE_SELLER")) {
            Cart userCart = cartService.findUserCart(user.getId());
            int cartCount = cartService.allUserCartView(userCart)
                                .stream()
                                .mapToInt(CartItem::getCount)
                                .sum();
            model.addAttribute("cartCount", cartCount);
        }
        return "itemView";
    }

    @GetMapping("/item/view/nonlogin/{id}")
    public String nonLoginItemView(Model model,
                                   @PathVariable("id") Integer id) {
        model.addAttribute("item", itemService.itemView(id));
        return "itemView";
    }

    @GetMapping("/item/delete/{id}")
    public String itemDelete(@PathVariable("id") Integer id,
                             @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (!principalDetails.getUser().getRole().equals("ROLE_SELLER")) {
            return "redirect:/main";
        }
        User seller = itemService.itemView(id).getSeller();
        if (seller.getId() != principalDetails.getUser().getId()) {
            return "redirect:/main";
        }
        itemService.itemDelete(id);
        return "redirect:/main";
    }

    // ========== 수정된 검색+페이징 처리 메서드 ==========

    @GetMapping("/item/list")
    public String itemList(Model model,
                           @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC)
                           Pageable pageable,
                           @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                           @AuthenticationPrincipal PrincipalDetails principalDetails) {

        User user = userPageService.findUser(principalDetails.getUser().getId());
        Page<Item> items = (searchKeyword == null || searchKeyword.isBlank())
                ? itemService.allItemViewPage(pageable)
                : itemService.itemSearchList(searchKeyword, pageable);

        int nowPage = items.getNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, items.getTotalPages());

        model.addAttribute("items", items);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("user", user);

        return "itemList";
    }

    @GetMapping("/nonlogin/item/list")
    public String nonloginItemList(Model model,
                                   @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC)
                                   Pageable pageable,
                                   @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

        Page<Item> items = (searchKeyword == null || searchKeyword.isBlank())
                ? itemService.allItemViewPage(pageable)
                : itemService.itemSearchList(searchKeyword, pageable);

        int nowPage = items.getNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, items.getTotalPages());

        model.addAttribute("items", items);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "itemList";
    }
}