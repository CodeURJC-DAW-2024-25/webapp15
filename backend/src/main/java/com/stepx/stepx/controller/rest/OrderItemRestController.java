package com.stepx.stepx.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.service.OrderItemService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/v1/OrderItem")
public class OrderItemRestController {
    private final OrderItemService orderItemService;

    public OrderItemRestController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    //get a order item by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        OrderItemDTO orderItem = orderItemService.findById(id);

        if (orderItem==null) {
            return ResponseEntity.status(404)
            .body(Collections.singletonMap("error", "OrderItem with ID " + id + " not found"));

        }
        return ResponseEntity.ok(orderItem);
    }

    //get all orders items
    @GetMapping("/All")
    public ResponseEntity<?> getAll(){
        List<OrderItemDTO> orderItems = orderItemService.findAll();
        if (orderItems.isEmpty()) {
            throw new NoSuchElementException("No OrderItems found");
        }
        return ResponseEntity.ok(orderItems);
    }

    @PostMapping
    public ResponseEntity<OrderItemDTO> createOrderItem(@Valid @RequestBody OrderItemDTO orderItemDTO) {
        OrderItemDTO savedDTO =orderItemService.save(orderItemDTO);
        if (savedDTO ==null) {
            return ResponseEntity.badRequest().build();
        }
        URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(savedDTO.id())
        .toUri(); 
    return ResponseEntity.created(location).body(savedDTO);
    }
    
    //update a orderitem
    @PutMapping("/{id}")
    public ResponseEntity<OrderItemDTO> updateOrderItem(@PathVariable Long id, @RequestBody OrderItemDTO orderItemDTO) {
        Optional<OrderItemDTO> orderItem = orderItemService.updateAllOrderItem(id, orderItemDTO);//get the orderitem
        if (orderItem.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(orderItem.get());
    }
        
    //delete a orderitem
    @DeleteMapping("/{id}")
    public ResponseEntity<OrderItemDTO> deleteOrderItem(@PathVariable Long id) {
        Optional<OrderItemDTO>deleted=orderItemService.deleteOrderItem(id);
        return deleted.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
