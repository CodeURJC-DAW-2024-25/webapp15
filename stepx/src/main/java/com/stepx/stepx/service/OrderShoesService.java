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
import com.stepx.stepx.repository.UserRepository;

@Service

public class OrderShoesService {

    private final OrderShoesRepository orderShoesRepository;
    private final UserRepository userRepository;

    public OrderShoesService(OrderShoesRepository orderShoesRepository,UserRepository userRepository){
        this.orderShoesRepository = orderShoesRepository;
        this.userRepository=userRepository;
    }

    public Optional<OrderShoes> getCartById(Long id_client){
        return orderShoesRepository.findCartById(id_client);
    }
    
    public OrderShoes createCartForUser(User user){
        OrderShoes ordershoe= new OrderShoes(user);
        ordershoe.setState("notFinished");
        // Asegurar que el carrito también se agrega a la lista de órdenes del usuario
        user.addOrderShoe(ordershoe); 
        orderShoesRepository.save(ordershoe);
        userRepository.save(user);
        return ordershoe;
    }
}
