package com.stepx.stepx.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.time.LocalDate;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
@RequestMapping("/OrderItem")
public class OrderItemController {
    
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

    @PostMapping("/addItem")
    public ResponseEntity<String> addItemToCart(@RequestParam Long id_Shoe, @RequestParam String size, @RequestParam int cuantity,@RequestParam Long id_user) { 
        //obtenemos el usuario
        Optional<User> usergetted= userService.findUserById(id_user);
       
        if (!usergetted.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        User user=usergetted.get();

        //verify if the user has a cart or not
        Optional<OrderShoes> cart_Optional= orderShoesService.getCartById(id_user); //get the cart asosiated to the id
        OrderShoes cart;
        if(cart_Optional.isPresent()){
            cart = cart_Optional.get();
        }else{
            cart = orderShoesService.createCartForUser(user);
        }
        
        //search the shoe in the bbdd

        Optional<Shoe> shoe_optional=shoeService.getShoeById(id_Shoe);
        if(!shoe_optional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("shoes not found");
        }
        Shoe shoe=shoe_optional.get();

        //add the shoes into the cart
        //first verify if the shoe is in the cart, in that case just increment the quantity
        Optional<OrderItem> orderItemOptional=orderItemService.findByCartAndShoeAndSize(id_user, id_Shoe, size);
        OrderItem orderItem;
        if(orderItemOptional.isPresent()){//in case that exist in the cart, increase the cunatity
            orderItem=orderItemOptional.get();
            orderItem.setQuantity(orderItem.getQuantity()+1);
        }else{
            orderItem=new OrderItem(cart, shoe, cuantity, size);
        }
        orderItemService.save(orderItem);
        return ResponseEntity.ok("Shoe added to your cart");
    }
    
}
