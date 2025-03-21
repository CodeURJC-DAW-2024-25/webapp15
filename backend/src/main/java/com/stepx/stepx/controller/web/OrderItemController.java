package com.stepx.stepx.controller.web;

import java.security.Principal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.stepx.stepx.model.User;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.service.OrderItemService;
import com.stepx.stepx.service.OrderShoesService;
import com.stepx.stepx.service.ShoeService;
import com.stepx.stepx.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import com.stepx.stepx.service.ShoeSizeStockService;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity<String> addItemToCart(@RequestParam Long id_Shoe, @RequestParam String size,
            @RequestParam int cuantity, HttpServletRequest request) {

        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuer not logged in");
        }

        Optional<User> usergetted = userService.findUserByUserName(principal.getName());
        if (!usergetted.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = usergetted.get();

        Optional<OrderShoes> cart_Optional = orderShoesService.getCartById(user.getId());

        OrderShoes cart;
        if (cart_Optional.isPresent()) {
            cart = cart_Optional.get();
        } else {
            cart = orderShoesService.createCartForUser(user);
        }

        Optional<Shoe> shoe_optional = shoeService.getShoeById(id_Shoe);
        if (!shoe_optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shoes not found");
        }
        Shoe shoe = shoe_optional.get();
        // obtain stock of that shoe
        Optional<Integer> stockShoe_Optional = shoeSizeStockService.getStockByShoeAndSize(id_Shoe, size);
        Integer stock = stockShoe_Optional.get();

        if (stock == null) {
            cuantity = 0;
        }

        Optional<OrderItem> orderItemOptional = orderItemService.findByCartAndShoeAndSize(user.getId(), id_Shoe, size);
        OrderItem orderItem;
        // confirmation if quantity is <1 or >stock
        if (cuantity < 1) {
            cuantity = 1;
        }
        if (stock!=null && cuantity >= stock) {
            cuantity = stock;
        }

        if (orderItemOptional.isPresent()) {
            orderItem = orderItemOptional.get();
            orderItem.setQuantity(orderItem.getQuantity() + 1);
        } else {
            orderItem = new OrderItem(cart, shoe, cuantity, size);
        }
        orderItemService.save(orderItem);

        return ResponseEntity.ok("Shoe added to your cart");
    }

}
