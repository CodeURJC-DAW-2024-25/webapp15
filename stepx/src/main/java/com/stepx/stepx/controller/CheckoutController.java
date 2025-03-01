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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.Resource;

import com.stepx.stepx.model.User;
import com.stepx.stepx.model.Shoe;
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
import com.stepx.stepx.service.PdfService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderShoesService orderShoesService;

    @Autowired
    private OrderItemService orderItemService;


    @Autowired
    private PdfService pdfService;

    @GetMapping("/downloadTicket{orderId}")
    public void downloadTicket(@RequestParam Long orderId, HttpServletResponse response) throws IOException {
        Optional<OrderShoes> orderOptional = orderShoesService.getCartById(orderId);
        if (orderOptional.isPresent()) {
            OrderShoes order = orderOptional.get();

            Map<String, Object> data = new HashMap<>();
            data.put("customerName", order.getUser().getUsername());
            data.put("email", order.getEmail());
            data.put("address", order.getAddress());
            data.put("date", order.getDate());

            byte[] pdfBytes = pdfService.generatePdfFromOrder(data);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=ticket.pdf");
            response.getOutputStream().write(pdfBytes);
        }
    }

    


    @Autowired
    private PdfService pdfService;

    @GetMapping("/downloadTicket{orderId}")
    public void downloadTicket(@RequestParam Long orderId, HttpServletResponse response) throws IOException {
        Optional<OrderShoes> orderOptional = orderShoesService.getCartById(orderId);
        if (orderOptional.isPresent()) {
            OrderShoes order = orderOptional.get();

            Map<String, Object> data = new HashMap<>();
            data.put("customerName", order.getUser().getUsername());
            data.put("email", order.getEmail());
            data.put("address", order.getAddress());
            data.put("date", order.getDate());

            byte[] pdfBytes = pdfService.generatePdfFromOrder(data);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=ticket.pdf");
            response.getOutputStream().write(pdfBytes);
        }
    }

    

    @GetMapping("/{id_user}")
    public String showCheckout(@PathVariable Long id_user, Model model) {
        // Obtener el usuariio
        Optional<User> usergetted = userService.findUserById(id_user);
        if (!usergetted.isPresent()) {
            System.out.println("el usuario buscado no existe");
        }

        Optional<OrderShoes> cart_Optional = orderShoesService.getCartById(id_user); // get the cart asosiated to the id
        OrderShoes cart;
        if (cart_Optional.isPresent()) {// in case that has a cart
            cart = cart_Optional.get();
            if (cart.getLenghtOrderShoes() == 0) { // if exists but its empty
                model.addAttribute("setSubtotal", false);
            } else {// exists but has orderItems
                List<Map<String, Object>> cartItems = new ArrayList<>();
                for (OrderItem orderItem : cart.getOrderItems()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", orderItem.getShoe().getId());// id of the shoe
                    item.put("name", orderItem.getShoe().getName());
                    item.put("price", orderItem.getShoe().getPrice());
                    item.put("quantity", orderItem.getQuantity());
                    item.put("size", orderItem.getSize());
                    item.put("id_orderItem", orderItem.getId());
                    cartItems.add(item);
                }
                model.addAttribute("setSubtotal", true);
                model.addAttribute("total", cart.getTotalPrice());
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("id_orderShoe", cart.getId());
               
            }
        } else {
            model.addAttribute("setSubtotal", false);
        }
        // comprobamos el carrito o lo cargamos

        return "checkout";

    }

    @GetMapping("/deleteItem/{id}/{id_user}")
    public String DeleteItemCheckout(@PathVariable Long id, @PathVariable Long id_user, Model model) {

        Optional<User> usergetted = userService.findUserById(id_user);
        if (!usergetted.isPresent()) {
            System.out.println("el usuario buscado no existe");
        }

        Optional<OrderShoes> cart_Optional = orderShoesService.getCartById(id_user); // get the cart asosiated to the id
        OrderShoes cart;
        if (cart_Optional.isPresent()) {// in case that has a cart
            cart = cart_Optional.get();
            System.out.println("existe el carrito");
            if (cart.getLenghtOrderShoes() != 0) { // if has Items in the cart
                orderShoesService.deleteOrderItems(id_user,id);
                orderShoesService.saveOrderShoes(cart);
                
                List<Map<String,Object>> cartItems = new ArrayList<>();
                for(OrderItem orderItem : cart.getOrderItems()) {
                    Map<String,Object> item = new HashMap<>();
                    item.put("id", orderItem.getShoe().getId());
                    item.put("name", orderItem.getShoe().getName());
                    item.put("price", orderItem.getShoe().getPrice());
                    item.put("quantity", orderItem.getQuantity());
                    item.put("size", orderItem.getSize());
                    item.put("id_orderItem", orderItem.getId());
                    cartItems.add(item);
                }
                model.addAttribute("setSubtotal", true);
                model.addAttribute("total", cart.getTotalPrice());
                model.addAttribute("cartItems", cartItems);
            }
        } else {
            model.addAttribute("setSubtotal", false);
        }

        System.out.println("No existe el carrito");

        return "partials/checkout-itemsList";
}


    @PostMapping("/recalculate")
    public String Recalculate(@RequestParam List<Long> ids,@RequestParam List<Integer> quantities,@RequestParam Long id_user,Model model) {
        for (int i=0;i<ids.size();i++){
            orderItemService.updateOrderItem(ids.get(i),quantities.get(i));
        }

        Optional<OrderShoes> cart_Optional = orderShoesService.getCartById(id_user); // get the cart asosiated to the id
        OrderShoes cart;
        if (cart_Optional.isPresent()) {// in case that has a cart
            cart = cart_Optional.get();
            if (cart.getLenghtOrderShoes() == 0) { // if exists but its empty
                model.addAttribute("setSubtotal", false);
            } else {// exists but has orderItems
                List<Map<String, Object>> cartItems = new ArrayList<>();
                for (OrderItem orderItem : cart.getOrderItems()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", orderItem.getShoe().getId());// id of the shoe
                    item.put("name", orderItem.getShoe().getName());
                    item.put("price", orderItem.getShoe().getPrice());
                    item.put("quantity", orderItem.getQuantity());
                    item.put("size", orderItem.getSize());
                    item.put("id_orderItem", orderItem.getId());
                    cartItems.add(item);
                }
                model.addAttribute("setSubtotal", true);
                model.addAttribute("total", cart.getTotalPrice());
                model.addAttribute("cartItems", cartItems);
            }
        } else {
            model.addAttribute("setSubtotal", false);
        }

        return "partials/checkout-itemsList";
    }
    
}
