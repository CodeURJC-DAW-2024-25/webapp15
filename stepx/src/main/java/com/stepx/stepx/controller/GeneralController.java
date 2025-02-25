
package com.stepx.stepx.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.stepx.stepx.model.Product;
import com.stepx.stepx.model.Shoe;

import com.stepx.stepx.service.CartService;
import com.stepx.stepx.service.ProductsService;
import com.stepx.stepx.service.ShoeService;

@Controller
public class GeneralController { // todas las solicitudes "/...." son con el controlador

    @Autowired
    private ProductsService productsService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ShoeService shoeService;



    @GetMapping("/index")
    public String showIndex(Model model) {
        return "index"; // nombre de la plantilla Mustache sin la extensión .html
    }
 
    @GetMapping("/shop")
    public String showShop(Model model) {

        List<Shoe> shoes = shoeService.getAllShoes();
        
        model.addAttribute("shoes", shoes);
        
        return "shop";
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

    @GetMapping("/single-product/{id}")
    public String showSingleProduct(Model model, @PathVariable Long id) {
        model.addAttribute("product", productsService.getProductById(id));
        return "single-product";
    }

    @GetMapping("/checkout")
    public String showCheckout(Model model) {
        return "checkout";

    }

    @GetMapping("/cart")
    public String getProductById(Model model) {

        List<Product> productList = new ArrayList<>(cartService.getCartContents().values()); //convertimos a una lista para que sea facil de iterar
        model.addAttribute("cartItems", productList); // Agregar el contenido del carrito
        return "partials/quick-view-cart-modal"; // 📌 Devolver el modal del carrito
    }

    

}
