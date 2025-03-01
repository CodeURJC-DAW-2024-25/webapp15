package com.stepx.stepx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Product;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.OrderItemRepository;
import com.stepx.stepx.repository.OrderShoesRepository;
import com.stepx.stepx.repository.UserRepository;

@Service

public class OrderShoesService {

    private final OrderShoesRepository orderShoesRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderShoesService(OrderShoesRepository orderShoesRepository,UserRepository userRepository, OrderItemRepository orderItemRepository){
        this.orderShoesRepository = orderShoesRepository;
        this.userRepository=userRepository;
        this.orderItemRepository=orderItemRepository;
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
    
    public void deleteOrderItems(Long userId, Long itemId) {
    Optional<OrderShoes> cartOptional = orderShoesRepository.findCartById(userId);

    if (cartOptional.isPresent()) {
        OrderShoes cart = cartOptional.get();
        
        Optional<OrderItem> itemOptional = orderItemRepository.findById(itemId);
        
        if (itemOptional.isPresent()) {
            OrderItem item = itemOptional.get();
            
            // Eliminar el item de la lista del carrito
            cart.getOrderItems().remove(item);
            
            // Eliminar el item de la base de datos
            orderItemRepository.delete(item);

            // Guardar cambios en el carrito
            orderShoesRepository.save(cart);

            System.out.println("Item eliminado y carrito actualizado.");
        } else {
            System.out.println("El item a eliminar no existe.");
        }
    } else {
        System.out.println("No se encontró el carrito.");
    }
}

    public void saveOrderShoes(OrderShoes orderShoes) {
        orderShoesRepository.save(orderShoes);
    }

}
