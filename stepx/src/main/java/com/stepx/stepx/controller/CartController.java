package com.stepx.stepx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.stepx.stepx.model.Product;
import com.stepx.stepx.service.CartService;
import com.stepx.stepx.service.ProductsService;

@RestController
@RequestMapping("/single-product/{id}")
public class CartController {

    @Autowired 
    private ProductsService productsService;

    @Autowired 
    private CartService cartService; 

    @GetMapping("/add")//solo quiero que me añada este producto al carrito
    public String getProductById(Model model, @PathVariable Long id) {
        
        Product product = productsService.getProductById(id);

        if (product == null) {
            model.addAttribute("error", "Product not found");
            return "partials/error-modal"; 
        }

        cartService.addProductToCart(id, product);

        return "Producto añadido"; 

    }

    
}
