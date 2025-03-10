package com.stepx.stepx.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Product;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.ShoeSizeStock;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.OrderItemRepository;
import com.stepx.stepx.repository.OrderShoesRepository;
import com.stepx.stepx.repository.ShoeSizeStockRepository;
import com.stepx.stepx.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service

public class OrderShoesService {

    private final OrderShoesRepository orderShoesRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoeSizeStockService shoeSizeStockService;
    private final ShoeSizeStockRepository shoeSizeStockRepository;

    public OrderShoesService(OrderShoesRepository orderShoesRepository, UserRepository userRepository,
            OrderItemRepository orderItemRepository, ShoeSizeStockService shoeSizeStockService,
            ShoeSizeStockRepository shoeSizeStockRepository) {
        this.orderShoesRepository = orderShoesRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.shoeSizeStockService = shoeSizeStockService;
        this.shoeSizeStockRepository = shoeSizeStockRepository;
    }

    public Optional<OrderShoes> getCartById(Long id_client) {
        return orderShoesRepository.findCartById(id_client);
    }

    public OrderShoes createCartForUser(User user) {
        OrderShoes ordershoe = new OrderShoes(user);
        ordershoe.setState("notFinished");
        // Asegurar que el carrito también se agrega a la lista de órdenes del usuario
        user.addOrderShoe(ordershoe);
        orderShoesRepository.save(ordershoe);
        userRepository.save(user);
        return ordershoe;
    }

    @Transactional
    public void deleteOrderItems(Long userId, Long itemId) {
        Optional<OrderItem> itemOptional = orderItemRepository.findById(itemId);

        if (itemOptional.isPresent()) {
            OrderItem item = itemOptional.get();
            OrderShoes cart = item.getOrderShoes(); // Obtener el carrito desde el item

            if (cart == null || !cart.getUser().getId().equals(userId)) {
                System.out.println("❌ Error: El item no pertenece al usuario.");
                return;
            }

            orderItemRepository.delete(item);

            cart.getOrderItems().remove(item);
        } else {
            System.out.println("No se encontró el carrito.");
        }
    }
    @Transactional
    public void saveOrderShoes(OrderShoes orderShoes) {
        orderShoesRepository.save(orderShoes);
    }

   
    public BigDecimal getTotalPriceExcludingOutOfStock(Long id_cart) {
        BigDecimal totalPrice = orderShoesRepository.getTotalPriceExcludingOutOfStock(id_cart);

        if (totalPrice == null) {
            return BigDecimal.ZERO;
        }
    
        return totalPrice;
    }

    

    public List<OrderShoes> getOrderShoesFinishedByUserId(Long userId){
        return orderShoesRepository.getOrderShoesFinishedByUserId(userId);
    }

    public List<OrderShoes>getPagedOrdersByUserId(int pageStart,Long userId){
        return orderShoesRepository.getPagedOrdersByUserId(userId,PageRequest.of(pageStart, 5));
    }
    public OrderShoes getLastOrder(Long userId) {
        return orderShoesRepository.findTopByUserIdOrderByIdDesc(userId);
    }

    public List<Shoe.Brand> getBrandsFromLastOrder(Long userId) {
        OrderShoes lastOrder = getLastOrder(userId);
        
        if (lastOrder == null) {
            return new ArrayList<>(); // No hay compras previas
        }
        
        return lastOrder.getOrderItems().stream()
                        .map(orderItem -> orderItem.getShoe().getBrand())
                        .distinct()
                        .collect(Collectors.toList());
    }
    
    public void processOrder(OrderShoes order) {
        // Mapa con shoeId -> (size -> cantidad a descontar)
        Map<Long, Map<String, Integer>> stockUpdates = new HashMap<>();

        for (OrderItem item : order.getOrderItems()) {
            Long shoeId = item.getShoe().getId();
            String size = item.getSize();
            int quantity = item.getQuantity();

            stockUpdates
                .computeIfAbsent(shoeId, k -> new HashMap<>())
                .merge(size, quantity, Integer::sum);
        }

        // Llamamos al servicio para actualizar el stock en lote
        shoeSizeStockService.updateStock(stockUpdates);
    }
    //debvuelve la orden por id
    public Optional<OrderShoes> getOrderById(Long orderId) {
        return orderShoesRepository.findById(orderId);
    }
    
}
