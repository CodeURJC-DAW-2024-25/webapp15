package com.stepx.stepx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.stepx.stepx.model.Product;
import com.stepx.stepx.service.ProductsService;
import org.springframework.ui.Model;


@Controller
@RequestMapping("/shop")
public class ModalHandler {

    @Autowired 
    private ProductsService productsService;

    @GetMapping("/{id}")

    public String getProductById(Model model,@PathVariable Long id) {

        Product product = productsService.getProductById(id);

        if (product == null) {
            return "<p>Error: Product not found</p>";
        }

        model.addAttribute("product", product);

        return "partials/quick-view-modal";
        
    }   


 

}


