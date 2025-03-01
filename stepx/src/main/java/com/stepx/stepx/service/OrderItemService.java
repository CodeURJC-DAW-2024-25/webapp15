package com.stepx.stepx.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.Product;
import com.stepx.stepx.repository.OrderItemRepository;

@Service
public class OrderItemService {

    private OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository){
        this.orderItemRepository=orderItemRepository;
    }
    
    public Optional<OrderItem> findByCartAndShoeAndSize(Long userId,Long shoeId,String size){
        return orderItemRepository.findByCartAndShoeAndSize(userId,shoeId,size);//this return the shoe that the cliente is triying to add to the cart
        //if exist or not, that will be managed by the controller
    }

    public void save(OrderItem orderItem){//save the order item in the bbdd
        orderItemRepository.save(orderItem);
    }

    public void updateOrderItem(Long id,int quantity){
        Optional<OrderItem> item_Optional = orderItemRepository.findById(id);
        OrderItem item=item_Optional.get();
        item.setQuantity(quantity);
        orderItemRepository.save(item);

    }
}
