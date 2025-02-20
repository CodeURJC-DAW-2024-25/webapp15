package com.stepx.stepx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.stepx.stepx.model.Product;
import com.stepx.stepx.service.ProductsService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/shop") 
public class ModalHandler {

    @Autowired 
    private ProductsService productsService;

    @GetMapping("/{id}")
    public String getProductById(Model model, @PathVariable Long id, 
                                 @RequestParam(required = false) String action) {
        
        Product product = productsService.getProductById(id);

        if (product == null) {
            model.addAttribute("error", "Product not found");
            return "partials/error-modal"; 
        }

        model.addAttribute("product", product);

        
        if ("quick".equals(action)) {
            return "partials/quick-view-modal";  
        } else if ("confirmation".equals(action)) {
            return "partials/cart-confirmation-view"; 
        } else {
            return "partials/error-modal";
        }
    }
}
