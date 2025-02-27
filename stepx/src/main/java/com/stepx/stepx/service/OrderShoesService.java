package com.stepx.stepx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.stepx.stepx.model.Product;
import com.stepx.stepx.repository.OrderShoesRepository;

@Service

public class OrderShoesService {

    private final OrderShoesRepository orderShoesRepository;

    public OrderShoesService(OrderShoesRepository orderShoesRepository){
        this.orderShoesRepository = orderShoesRepository;
    }
    
}
