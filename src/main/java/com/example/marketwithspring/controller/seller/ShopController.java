package com.example.marketwithspring.controller.seller;

import com.example.marketwithspring.entity.Shop;
import com.example.marketwithspring.repository.SellerDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/shop")
public class ShopController {
    private final SellerDAO sellerDAO;

    @GetMapping("/get")
    public String getShops(Model model) {
        List<Shop> shops = sellerDAO.getAllShops();
        if (shops == null) shops = new ArrayList<>();
        model.addAttribute("shops", shops.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        return "seller/crud-shop/shops";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("shop", new Shop());
        return "seller/crud-shop/shop-create";
    }

    @PostMapping("/create")
    public String createShop(@RequestParam("name") String name) {
        sellerDAO.createShop(name);
        return "redirect:/shop/get";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Shop shop = sellerDAO.getShopById(id);
        model.addAttribute("shop", shop);
        return "seller/crud-shop/shop-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateShop(@PathVariable("id") Long id, @RequestParam("name") String name) {
        sellerDAO.updateShop(id, name);
        return "redirect:/shop/get";
    }

    @PostMapping("/delete/{id}")
    public String deleteShop(@PathVariable("id") Long id) {
        sellerDAO.deleteShop(id);
        return "redirect:/shop/get";
    }

    @GetMapping("/view/{id}")
    public String viewShop(@PathVariable("id") Long id, Model model) {
        Shop shop = sellerDAO.getShopById(id);
        model.addAttribute("shop", shop);
        model.addAttribute("products", sellerDAO.getProductsByShopId(id));
        return "seller/crud-shop/shop-view";
    }
}
