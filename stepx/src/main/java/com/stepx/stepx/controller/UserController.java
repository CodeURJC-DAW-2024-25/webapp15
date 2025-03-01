package com.stepx.stepx.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.time.LocalDate;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.stepx.stepx.repository.ShoeSizeStockRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;

import com.stepx.stepx.model.User;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.Shoe.Brand;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Review;
import com.stepx.stepx.model.ShoeSizeStock;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.OrderShoesService;
import com.stepx.stepx.service.ProductsService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.ReviewService;
import com.stepx.stepx.service.UserService;
import com.stepx.stepx.service.ShoeSizeStockService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private ShoeService shoeService;
    
    @Autowired
    private ShoeSizeStockService shoeSizeStockService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderShoesService orderShoesService;

    @Autowired
    private UserService userService;

    @GetMapping("/cart")
    public String getMethodName(@RequestParam Long id_user,Model model) {

        //primero comrpibamos el usuario
        Optional<User> usergetted= userService.findUserById(id_user);
        if (!usergetted.isPresent()) {
            System.out.println("el usuario buscado no existe");
        }
        User user=usergetted.get();

        //verify if the user has a cart or not
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
            cart = orderShoesService.createCartForUser(user);//put in cart modal not objects jet
            model.addAttribute("setSubtotal", false);
        }
        //comprobamos el carrito o lo cargamos

        return "partials/quick-view-cart-modal";
    }
    

}
