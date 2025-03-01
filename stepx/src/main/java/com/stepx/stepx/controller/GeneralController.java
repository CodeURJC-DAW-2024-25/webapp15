
package com.stepx.stepx.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Product;
import com.stepx.stepx.model.User;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.OrderShoesService;
import com.stepx.stepx.service.ProductsService;
import com.stepx.stepx.service.UserService;

@Controller
public class GeneralController { // todas las solicitudes "/...." son con el controlador

    @Autowired
    private ProductsService productsService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderShoesService orderShoesService;


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

    @GetMapping("/checkout/{id_user}")
public String showCheckout(@PathVariable Long id_user,Model model) {
    // Obtener el usuariio
    Optional<User> usergetted= userService.findUserById(id_user);
        if (!usergetted.isPresent()) {
            System.out.println("el usuario buscado no existe");
        }

    Optional<OrderShoes> cart_Optional= orderShoesService.getCartById(id_user); //get the cart asosiated to the id
        OrderShoes cart;
        if(cart_Optional.isPresent()){//in case that has a cart
            cart = cart_Optional.get();
            if(cart.getLenghtOrderShoes()==0){ //if exists but its empty
                model.addAttribute("setSubtotal", false);
            }else{//exists but has orderItems
                List<Map<String,Object>> cartItems=new ArrayList<>();
                for(OrderItem orderItem:cart.getOrderItems()){
                    Map<String,Object> item=new HashMap<>();
                    item.put("id", orderItem.getShoe().getId());//id of the shoe
                    item.put("name", orderItem.getShoe().getName());
                    item.put("price",orderItem.getShoe().getPrice());
                    item.put("quantity", orderItem.getQuantity());
                    item.put("size", orderItem.getSize());
                    cartItems.add(item);
                }
                model.addAttribute("setSubtotal",true);
                model.addAttribute("total",cart.getTotalPrice());
                model.addAttribute("cartItems", cartItems);
            }
        }else{
            model.addAttribute("setSubtotal", false);
        }
        //comprobamos el carrito o lo cargamos

    return "checkout";
}

    @GetMapping("/profile")
    public String showProfileString(Model model) {
        return "profile";

    }


}
