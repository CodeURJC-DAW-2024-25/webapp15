package com.stepx.stepx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Product;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.OrderShoesRepository;

@Service

public class OrderShoesService {

    private final OrderShoesRepository orderShoesRepository;

    public OrderShoesService(OrderShoesRepository orderShoesRepository){
        this.orderShoesRepository = orderShoesRepository;
    }

    public Optional<OrderShoes> getCartById(Long id_client){
        return orderShoesRepository.findCartById(id_client);
    }
    
    public OrderShoes createCartForUser(User user){
        OrderShoes ordershoe= new OrderShoes(user);
        ordershoe.setState("nosFinished");
        return orderShoesRepository.save(ordershoe);
    }
}
