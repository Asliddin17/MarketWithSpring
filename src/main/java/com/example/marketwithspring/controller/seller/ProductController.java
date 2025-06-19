package com.example.marketwithspring.controller.seller;

import com.example.marketwithspring.entity.Product;
import com.example.marketwithspring.entity.Shop;
import com.example.marketwithspring.entity.enums.ProductStatus;
import com.example.marketwithspring.repository.ProductDAO;
import com.example.marketwithspring.repository.SellerDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/product")
public class ProductController {
    private final ProductDAO productDAO;
    private final SellerDAO sellerDAO;

    @GetMapping("/products")
    public String getProducts(Model model) {
        List<Product> products = productDAO.getAllProducts();
        model.addAttribute("products", products);
        return "seller/crud-product/products";
    }



//    @GetMapping("/products")
//    public String getProducts(Model model) {
//        List<Product> products = productDAO.getAllProducts();
//
//        // Set avgRating manually for each product
//        for (Product product : products) {
//            double avg = productDAO.getAvgRatingForProduct(product.getId());
//        model.addAttribute("avg", avg);
//        }
//
//        model.addAttribute("products", products);
//        return "seller/crud-product/products";
//    }


    @GetMapping("/view/{id}")
    public String getProductById(@PathVariable("id") Long id, Model model) {
        Product product = productDAO.getProductById(id);
        model.addAttribute("product", product);
        return "seller/crud-product/product-details";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Product product = new Product();
        product.setProductStatus(ProductStatus.NEW);
        model.addAttribute("product", product);
        model.addAttribute("shops", sellerDAO.getAllShops());
        return "seller/crud-product/product-create";
    }

    @PostMapping("/create")
    public String createProduct(@ModelAttribute Product product, Model model) {
        System.out.println("Received Product: " + product); // Debug log
        if (product.getPrice() < 0) {
            model.addAttribute("error", "Price cannot be less than 0.");
            model.addAttribute("shops", sellerDAO.getAllShops());
            return "seller/crud-product/product-create";
        }

        product.setProductStatus(ProductStatus.NEW);
        product.setRatings(null);

        if (product.getShop() != null && product.getShop().getId() != null) {
            Shop shop = new Shop();
            shop.setId(product.getShop().getId());
            product.setShop(shop);
        }

        productDAO.createProduct(
                product.getName(),
                product.getPrice(),
                product.getShop(),
                product.getRatings(),
                product.getCount(),
                product.getProductStatus().name()
        );
        return "redirect:/product/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productDAO.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("shops", sellerDAO.getAllShops());
        return "seller/crud-product/product-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable("id") Long id, @ModelAttribute Product product, Model model) {
        if (product.getPrice() < 0) {
            model.addAttribute("error", "Price cannot be less than 0.");
            model.addAttribute("shops", sellerDAO.getAllShops());
            product.setId(id);
            model.addAttribute("product", product);
            return "seller/crud-product/product-edit";
        }

        product.setId(id);
        productDAO.updateProduct(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getShop(),
                product.getRatings(),
                product.getCount(),
                product.getProductStatus() != null ? product.getProductStatus().name() : ProductStatus.NEW.name()
        );
        return "redirect:/product/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productDAO.deleteProduct(id);
        return "redirect:/product/products";
    }
}