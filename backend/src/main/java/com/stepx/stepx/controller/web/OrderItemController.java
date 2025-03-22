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
import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.dto.OrderShoesDTO;
import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.dto.UserDTO;
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

        Optional<UserDTO> usergettedDto = userService.findUserByUserName(principal.getName());
        if (!usergettedDto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        UserDTO user = usergettedDto.get();

        Optional<OrderShoesDTO> cart_Optional = orderShoesService.getCartById(user.id());

        OrderShoesDTO cart;
        if (cart_Optional.isPresent()) {
            cart = cart_Optional.get();
        } else {
            cart = orderShoesService.createCartForUser(user);
        }

        Optional<ShoeDTO> shoe_optional = shoeService.getShoeById(id_Shoe);
        if (!shoe_optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shoes not found");
        }
        ShoeDTO shoe = shoe_optional.get();

        // obtain stock of that shoe
        Optional<Integer> stockShoe_Optional = shoeSizeStockService.getStockByShoeAndSize(id_Shoe, size);
        Integer stock = stockShoe_Optional.get();

        if (stock == null) {
            cuantity = 0;
        }

        OrderItemDTO orderItemDTO = orderItemService.findByCartAndShoeAndSize(user.id(), id_Shoe, size);
        // confirmation if quantity is <1 or >stock
        if (cuantity < 1) {
            cuantity = 1;
        }
        if (stock!=null && cuantity >= stock) {
            cuantity = stock;
        }

        if (orderItemDTO!=null) {//if the shoe is already in the cart
            orderItemService.addOrUpdateItem(orderItemDTO, orderItemDTO.quantity() + cuantity);
        } else {
            OrderItemDTO itemToAdd = new OrderItemDTO(
                null, // id
                cart.id(),
                shoe.id(),
                shoe.name(),
                cuantity,
                size,
                shoe.price()
            );
            orderItemService.addOrUpdateItem(itemToAdd, cuantity);
        }
        return ResponseEntity.ok("Shoe added to your cart");
    }

}
