package com.stepx.stepx.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.stepx.stepx.dto.*;
import com.stepx.stepx.mapper.OrderShoesMapper;
import com.stepx.stepx.mapper.UserMapper;
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
    @Autowired
    private OrderShoesMapper orderShoesMapper;
    @Autowired
    private UserMapper userMapper;

    public OrderShoesService(OrderShoesRepository orderShoesRepository, UserRepository userRepository,
            OrderItemRepository orderItemRepository, ShoeSizeStockService shoeSizeStockService) {
        this.orderShoesRepository = orderShoesRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.shoeSizeStockService = shoeSizeStockService;
    }

    
    public Optional<OrderShoesDTO> getCartById(Long id_client) {
        return orderShoesRepository.findCartById(id_client)
                .map(orderShoes -> Optional.ofNullable(orderShoesMapper.toDTO(orderShoes)))
                .orElse(Optional.empty());
    }

    public OrderShoesDTO createCartForUser(UserDTO userdto) {
        Optional<User> userOptional = userRepository.findById(userdto.id());
        if (userOptional.isPresent()){
            User user = userOptional.get();
            OrderShoes ordershoe = new OrderShoes(user);
            ordershoe.setState("notFinished");
            user.addOrderShoe(ordershoe);
            orderShoesRepository.save(ordershoe);
            userRepository.save(user);
            return orderShoesMapper.toDTO(ordershoe);
        }else{
            throw new IllegalArgumentException("User not found");
        }
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
    public void saveOrderShoes(OrderShoesDTO orderShoesDto) {
        OrderShoes orderShoes = orderShoesMapper.toDomain(orderShoesDto);
        orderShoesRepository.save(orderShoes);
    }

    public BigDecimal getTotalPriceExcludingOutOfStock(Long id_cart) {
        BigDecimal totalPrice = orderShoesRepository.getTotalPriceExcludingOutOfStock(id_cart);

        if (totalPrice == null) {
            return BigDecimal.ZERO;
        }

        return totalPrice;
    }

    public List<OrderShoes> getOrderShoesFinishedByUserId(Long userId) {
        return orderShoesRepository.getOrderShoesFinishedByUserId(userId);
    }

    public List<OrderShoes> getPagedOrdersByUserId(int pageStart, Long userId) {
        return orderShoesRepository.getPagedOrdersByUserId(userId, PageRequest.of(pageStart, 5));
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

    public void processOrder(OrderShoesDTO orderDto) {
        // Map with shoeId -> (size -> quantity to decrease)
        Map<Long, Map<String, Integer>> stockUpdates = new HashMap<>();
        OrderShoes order = orderShoesMapper.toDomain(orderDto);
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

    public Optional<OrderShoesDTO> getOrderById(Long orderId) {
        return orderShoesRepository.findById(orderId).
        map(orderShoes -> Optional.ofNullable(orderShoesMapper.toDTO(orderShoes)))
        .orElse(Optional.empty());
    }

    public BigDecimal getTotalPrice(Long orderId) {
        OrderShoes order = orderShoesRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : order.getOrderItems()) {
            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }

        return total;
    }

    public OrderShoesDTO fillDetailsOrder(OrderShoesDTO orderDto, Long userId, String country, String coupon,
            String firstName, String lastName, String email, String address, String phone, String couponString,
            BigDecimal totalPrice) {
        OrderShoes order = orderShoesMapper.toDomain(orderDto);
        order.setCountry(country);
        order.setFirstName(firstName);
        order.setSecondName(lastName);
        order.setEmail(email);
        order.setAddress(address);
        order.setNumerPhone(phone);
        order.setState("Processed");
        order.setActualDate();
        order.setSummary(totalPrice);
        order.setCuponUsed(couponString);
        return orderShoesMapper.toDTO(order);

    }

    public int getLengthOrderShoes(OrderShoesDTO orderShoesDTO) {
        OrderShoes orderShoes = orderShoesMapper.toDomain(orderShoesDTO); // Convertimos DTO a dominio
        return orderShoes.getOrderItems().size(); // Calculamos la longitud del carrito en base a los items
    }

    public Map<String, Object> getShoeIdsAndSizes(OrderShoesDTO orderShoesDTO) {
        // Convertimos el DTO a la entidad de dominio
        OrderShoes orderShoes = orderShoesMapper.toDomain(orderShoesDTO);

        // Extraemos los shoeIds y sizes de los OrderItems
        List<Long> shoeIds = orderShoes.getOrderItems().stream()
                .map(orderItem -> orderItem.getShoe().getId())  // Suponiendo que OrderItem tiene un método getShoe()
                .distinct()
                .collect(Collectors.toList());

        List<String> sizes = orderShoes.getOrderItems().stream()
                .map(OrderItem::getSize)  // Suponiendo que OrderItem tiene un método getSize()
                .distinct()
                .collect(Collectors.toList());

        // Retornamos los resultados en un Map
        Map<String, Object> result = new HashMap<>();
        result.put("shoeIds", shoeIds);
        result.put("sizes", sizes);
        
        return result;
    }


}
