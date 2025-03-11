package com.stepx.stepx.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.OrderItemRepository;
import com.stepx.stepx.repository.OrderShoesRepository;
import com.stepx.stepx.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service

public class OrderShoesService {

    private final OrderShoesRepository orderShoesRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoeSizeStockService shoeSizeStockService;

    public OrderShoesService(OrderShoesRepository orderShoesRepository, UserRepository userRepository,
            OrderItemRepository orderItemRepository, ShoeSizeStockService shoeSizeStockService) {
        this.orderShoesRepository = orderShoesRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.shoeSizeStockService = shoeSizeStockService;
    }

    public Optional<OrderShoes> getCartById(Long id_client) {
        return orderShoesRepository.findCartById(id_client);
    }

    public OrderShoes createCartForUser(User user) {
        OrderShoes ordershoe = new OrderShoes(user);
        ordershoe.setState("notFinished");
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
            OrderShoes cart = item.getOrderShoes();

            if (cart == null || !cart.getUser().getId().equals(userId)) {
                return;
            }

            orderItemRepository.delete(item);

            cart.getOrderItems().remove(item);
        } else {
            throw new IllegalArgumentException("Not found item with the id " + itemId);
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
            return new ArrayList<>();
        }
        
        return lastOrder.getOrderItems().stream()
                        .map(orderItem -> orderItem.getShoe().getBrand())
                        .distinct()
                        .collect(Collectors.toList());
    }
    
    public void processOrder(OrderShoes order) {
        // Map with shoeId -> (size -> quantity to decrease)
        Map<Long, Map<String, Integer>> stockUpdates = new HashMap<>();
        for (OrderItem item : order.getOrderItems()) {
            Long shoeId = item.getShoe().getId();
            String size = item.getSize();
            int quantity = item.getQuantity();
            stockUpdates
                .computeIfAbsent(shoeId, k -> new HashMap<>())
                .merge(size, quantity, Integer::sum);
        }
        shoeSizeStockService.updateStock(stockUpdates);
    }
    
    public Optional<OrderShoes> getOrderById(Long orderId) {
        return orderShoesRepository.findById(orderId);
    }
    
}
