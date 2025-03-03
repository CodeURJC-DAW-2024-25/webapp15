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
import java.util.stream.Collectors;
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
    private ShoeSizeStockService shoeSizeStockService;

    @Autowired
    private PdfService pdfService;

    @PostMapping("/downloadTicket")
    public void downloadTicket(
            @RequestParam Long orderId,
            @RequestParam String country,
            @RequestParam(required = false) String coupon,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String address,
            @RequestParam String phone,
            HttpServletResponse response
    ) throws IOException {
        System.out.println("🔹 Recibiendo solicitud para descargar ticket con ID: " + orderId);

        // Getting order from service
        Optional<OrderShoes> orderOptional = orderShoesService.getCartById(1L);
        if (!orderOptional.isPresent()) {
            System.out.println("❌ Error: Orden no encontrada con ID " + orderId);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        OrderShoes order = orderOptional.get();
        order.setCountry(country);
        order.setCuponUsed(coupon);
        order.setFirstName(firstName);
        order.setSecondName(lastName);
        order.setEmail(email);
        order.setAddress(address);
        order.setNumerPhone(phone);
        order.setState("Processed");
        order.setActualDate();
        orderShoesService.saveOrderShoes(order);
        
        //Prearing data to send the template
        Map<String, Object> data = new HashMap<>();
        data.put("customerName", firstName + " " + lastName);
        data.put("email", email);
        data.put("address", address);
        data.put("phone", phone);
        data.put("country", country);
        data.put("coupon", coupon != null ? coupon : "No coupon applied");
        data.put("date", order.getDate());
        data.put("products", order.getOrderItems());
        data.put("total", order.getTotalPrice());

        System.out.println("🔹 Generando PDF...");
        byte[] pdfBytes = pdfService.generatePdfFromOrder(data);

        if (pdfBytes == null || pdfBytes.length == 0) {
            System.out.println("❌ Error: El PDF está vacío o no se generó correctamente.");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating PDF");
            return;
        }

        System.out.println("✅ PDF generado correctamente. Enviando respuesta...");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=ticket.pdf");
        response.getOutputStream().write(pdfBytes);
    }


    @GetMapping("/{id_user}")
    public String showCheckout(@PathVariable Long id_user, Model model) {

        Optional<User> usergetted = userService.findUserById(id_user);
        if (!usergetted.isPresent()) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("cartItems", false);
            return "checkout";
        }

        Optional<OrderShoes> cart_Optional = orderShoesService.getCartById(id_user); // get the cart asosiated to the id
        

        if(cart_Optional.isEmpty()){
            model.addAttribute("setSubtotal", false);
            model.addAttribute("cartItems", false);
            
            return "checkout";
        }

        OrderShoes cart=cart_Optional.get();

        if(cart.getLenghtOrderShoes()==0){
            model.addAttribute("setSubtotal", false);
            model.addAttribute("cartItems", false);
            model.addAttribute("id_orderShoe", cart.getId());
            return "checkout";
        }

        //ids of all of orderitems from cart
        List<Long> shoeIds = cart.getOrderItems().stream()
            .map(orderItem -> orderItem.getShoe().getId())
            .distinct()
            .collect(Collectors.toList());

        //all of stocks in a single consult
        Map<String, Integer> stockMap = shoeSizeStockService.getAllStocksForShoes(shoeIds);

        //process the information
        List<Map<String,Object>> cartItems=new ArrayList<>();
        for (OrderItem orderItem:cart.getOrderItems()){
            Shoe shoe=orderItem.getShoe();
            String stockKey=shoe.getId()+"_"+orderItem.getSize();
            boolean stockAvailable=stockMap.getOrDefault(stockKey,0)>0;///if the stock is grater than 0
                
            Map<String, Object> item = new HashMap<>();
            item.put("id", shoe.getId());
            item.put("name", shoe.getName());
            item.put("price", shoe.getPrice());
            item.put("quantity", orderItem.getQuantity());
            item.put("size", orderItem.getSize());
            item.put("id_orderItem", orderItem.getId());
            item.put("stock", stockAvailable);
            cartItems.add(item);
        }

        BigDecimal total=orderShoesService.getTotalPriceExcludingOutOfStock(cart.getId());
        model.addAttribute("setSubtotal", true);
        model.addAttribute("total", total);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("id_orderShoe", cart.getId());
        return "checkout";

    }

    @GetMapping("/deleteItem/{id}/{id_user}")
    public String DeleteItemCheckout(@PathVariable Long id, @PathVariable Long id_user, Model model) {

        Optional<User> usergetted = userService.findUserById(id_user);
        if (!usergetted.isPresent()) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        Optional<OrderShoes> cart_Optional = orderShoesService.getCartById(id_user); // get the cart asosiated to the id

        OrderShoes cart=cart_Optional.get();
        if (cart.getLenghtOrderShoes()==0) {
            model.addAttribute("setSubtotal", false);
            model.addAttribute("cartItems", false);
            return "partials/checkout-itemsList";
        }

        orderShoesService.deleteOrderItems(id_user, id);//delete the orderitem from the cart
        orderShoesService.saveOrderShoes(cart);

        List<OrderItem> remainingItems= cart.getOrderItems();
        if (remainingItems.isEmpty()){
            model.addAttribute("setSubtotal", false);
            model.addAttribute("cartItems", false);
        }

        List<Long> shoeIds = remainingItems.stream()
            .map(orderItem -> orderItem.getShoe().getId())
            .distinct()
            .collect(Collectors.toList());

        Map<String, Integer> stockMap = shoeSizeStockService.getAllStocksForShoes(shoeIds);
    
        List<Map<String, Object>> cartItems = new ArrayList<>();
        for (OrderItem orderItem : remainingItems) {
            Shoe shoe = orderItem.getShoe();
            String stockKey = shoe.getId() + "_" + orderItem.getSize();
            boolean stockAvailable = stockMap.getOrDefault(stockKey, 0) > 0;
    
            Map<String, Object> item = new HashMap<>();
            item.put("id", shoe.getId());
            item.put("name", shoe.getName());
            item.put("price", shoe.getPrice());
            item.put("quantity", orderItem.getQuantity());
            item.put("size", orderItem.getSize());
            item.put("id_orderItem", orderItem.getId());
            item.put("stock", stockAvailable);
            cartItems.add(item);
        }    

        BigDecimal total=orderShoesService.getTotalPriceExcludingOutOfStock(cart.getId());
        if (total == null) {
            total = BigDecimal.ZERO;
        }
        model.addAttribute("setSubtotal", true);
        model.addAttribute("total", total);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("id_orderShoe", cart.getId());

        return "partials/checkout-itemsList";
    }

    @PostMapping("/recalculate")
    public String Recalculate(@RequestParam List<Long> ids,
            @RequestParam List<Integer> quantities,
            @RequestParam Long id_user,
            Model model) {

        if (ids.isEmpty() || quantities.isEmpty()) {
            model.addAttribute("setSubtotal", false);
            return "partials/checkout-itemsList";
        }
        //Making sure the quantify is at least 1
        for (int i = 0; i < quantities.size(); i++) {
            if (quantities.get(i) < 1) {
                quantities.set(i, 1);
            }
        }

        orderItemService.updateOrderItemsBatch(ids, quantities);

        Optional<OrderShoes> cartOptional = orderShoesService.getCartById(id_user);
        if (!cartOptional.isPresent()) {
            model.addAttribute("setSubtotal", false);
            return "partials/checkout-itemsList";
        }

        OrderShoes cart = cartOptional.get();
        if (cart.getLenghtOrderShoes() == 0) {
            model.addAttribute("setSubtotal", false);
            return "partials/checkout-itemsList";
        }

        //Getting ID's List for every product in cart
        List<Long> shoeIds = cart.getOrderItems().stream()
                .map(orderItem -> orderItem.getShoe().getId())
                .distinct()
                .collect(Collectors.toList());

        //getting all stocks in one optimized request
        Map<String, Integer> stockMap = shoeSizeStockService.getAllStocksForShoes(shoeIds);

        // Procesing all products in cart
        // updated
        List<Map<String, Object>> cartItems = new ArrayList<>();
        for (OrderItem orderItem : cart.getOrderItems()) {
            Shoe shoe = orderItem.getShoe();
            String stockKey = shoe.getId() + "_" + orderItem.getSize();
            boolean stockAvailable = stockMap.getOrDefault(stockKey, 0) > 0;

            Map<String, Object> item = new HashMap<>();
            item.put("id", shoe.getId());
            item.put("name", shoe.getName());
            item.put("price", shoe.getPrice());
            item.put("quantity", orderItem.getQuantity()); // Ya está actualizado
            item.put("size", orderItem.getSize());
            item.put("id_orderItem", orderItem.getId());
            item.put("stock", stockAvailable);
            cartItems.add(item);
        }

        // Calculating new total
        BigDecimal total = orderShoesService.getTotalPriceExcludingOutOfStock(cart.getId());

        // Send data to view
        model.addAttribute("setSubtotal", true);
        model.addAttribute("total", total);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("id_orderShoe", cart.getId());

        return "partials/checkout-itemsList";
    }

}
