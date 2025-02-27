
package com.stepx.stepx.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.stepx.stepx.model.Product;
import com.stepx.stepx.service.ProductsService;

@Controller
public class GeneralController { // todas las solicitudes "/...." son con el controlador

    @Autowired
    private ProductsService productsService;

    @GetMapping("/index")
    public String showIndex(Model model) {
        return "index"; // nombre de la plantilla Mustache sin la extensión .html
    }
 

    @GetMapping("/register-user")
    public String showRegisterUser(Model model) {
        return "register-user";
    }

    @GetMapping("/admin-pannel")
    public String showAdminPanel(Model model) {
        return "admin-pannel";
    }

    @GetMapping("/edit-product/{id}")
    public String showEditProduct(Model model, @PathVariable Long id) {
        model.addAttribute("product", productsService.getProductById(id));
        return "edit-product";
    }

    @GetMapping("/create-product")
    public String showCreate(Model model) {
        return "create-product";

    }

    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        return "checkout";

    }

    

}
