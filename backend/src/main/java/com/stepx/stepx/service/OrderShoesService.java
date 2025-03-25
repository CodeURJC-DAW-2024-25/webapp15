package com.stepx.stepx.service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.stepx.stepx.dto.*;
import com.stepx.stepx.mapper.OrderItemMapper;
import com.stepx.stepx.mapper.OrderShoesMapper;
import com.stepx.stepx.mapper.UserMapper;
import com.stepx.stepx.model.Coupon;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.CouponRepository;
import com.stepx.stepx.repository.OrderItemRepository;
import com.stepx.stepx.repository.OrderShoesRepository;
import com.stepx.stepx.repository.ShoeRepository;
import com.stepx.stepx.repository.UserRepository;

import io.jsonwebtoken.security.Jwks.OP;
import jakarta.transaction.Transactional;

@Service

public class OrderShoesService {

    private final OrderShoesRepository orderShoesRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ShoeSizeStockService shoeSizeStockService;
    private final CouponRepository couponRepository;

    @Autowired
    private OrderShoesMapper orderShoesMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ShoeRepository shoeRepository;

    public OrderShoesService(OrderShoesRepository orderShoesRepository, UserRepository userRepository,
            OrderItemRepository orderItemRepository, ShoeSizeStockService shoeSizeStockService,CouponRepository couponRepository) {
        this.orderShoesRepository = orderShoesRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.shoeSizeStockService = shoeSizeStockService;
        this.couponRepository=couponRepository;
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
    public OrderShoesDTO deleteOrderItems(Long userId, Long itemId) {
        Optional<OrderItem> itemOptional = orderItemRepository.findById(itemId);

        if (itemOptional.isPresent()) {
            OrderItem item = itemOptional.get();
            OrderShoes cart = item.getOrderShoes();

            if (cart == null || !cart.getUser().getId().equals(userId)) {
                return null;
            }

            orderItemRepository.delete(item);
            cart.getOrderItems().remove(item);
            OrderShoes saved=orderShoesRepository.save(cart);
            return orderShoesMapper.toDTO(saved);
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

    public List<OrderShoesDTO> getOrderShoesFinishedByUserId(Long userId) {
        return Optional.ofNullable(orderShoesRepository.getOrderShoesFinishedByUserId(userId))
                .map(orderShoesMapper::toDTOs)
                .orElseGet(List::of);
    }
    

    public List<OrderShoesDTO> getPagedOrdersByUserId(int pageStart, Long userId) {
        return Optional.ofNullable(orderShoesRepository.getPagedOrdersByUserId(userId, PageRequest.of(pageStart, 5)))
                .map(orderShoesMapper::toDTOs)
                .orElseGet(List::of);
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

    //api get by id
    public Optional<OrderShoesDTO> getOrderById(Long orderId) {
        return orderShoesRepository.findById(orderId).
        map(orderShoes -> Optional.ofNullable(orderShoesMapper.toDTO(orderShoes)))
        .orElse(Optional.empty());
    }

    //api find all
    public List<OrderShoesDTO> getAll(){
        return orderShoesRepository.findAll()
                .stream()
                .map(orderShoesMapper::toDTO)
                .collect(Collectors.toList());
    }

    //api find by user id
    public List<OrderShoesDTO> findOrdersByUserId(Long userId){
        List<OrderShoes> orderShoesList=orderShoesRepository.findOrdersByUserId(userId);
        if(orderShoesList.isEmpty()){
            return List.of();
        }
        return orderShoesMapper.toDTOs(orderShoesList);
    }

    //api paginated ordershoes
    public Page<OrderShoesDTO> getPagedShoes(int page,int size){
         Pageable pageable = PageRequest.of(page, size);
        return orderShoesRepository.findAll(pageable).map(orderShoesMapper::toDTO);
    } 

    //api create a ordershoe for a user
    public Optional<OrderShoesDTO> saveSingleOrderForUser(OrderShoesDTO orderShoesDTO,Long userId){
        
        Optional<User> userOptional = userRepository.findById(userId);

        if(!userOptional.isPresent()){
            return Optional.empty();
        }

        User user = userOptional.get();
        OrderShoes orderShoes=orderShoesMapper.toDomain(orderShoesDTO);
        orderShoes.setUser(user);
        
        List<OrderItem> orderItems = orderShoesDTO.orderItems().stream()
        .map(orderItemDTO -> {
            OrderItem item = orderItemMapper.toDomain(orderItemDTO);
            item.setOrderShoes(orderShoes);
            return item;
        })
        .collect(Collectors.toList());

        if (orderItems.isEmpty()) {
            return Optional.empty();
        }

        boolean allProcessed = orderShoesDTO.state().equals("Processed");

        if (!allProcessed) {
            return Optional.empty(); // o lanza una excepción también aquí
        }

        orderShoes.setOrderItems(orderItems);
        OrderShoes savedOrder=orderShoesRepository.save(orderShoes);

        return Optional.of(orderShoesMapper.toDTO(savedOrder));
    }

    //update single ordershoe by it id
    @Transactional
    public Optional<OrderShoesDTO> updateOrderShoe(Long orderShoeId,OrderShoesDTO orderShoesDTO){
        
        Optional<OrderShoes> orderOptional = orderShoesRepository.findById(orderShoeId);
        Optional<User> userOptional = userRepository.findById(orderShoesDTO.userId());
        if(!userOptional.isPresent()){
            return Optional.empty();
        }
        if(orderOptional.isEmpty()){
            return Optional.empty();
        }
        boolean allProcessed = orderShoesDTO.state().equals("Processed");

        if (!allProcessed) {
            return Optional.empty();
        }
        

        OrderShoes order = orderOptional.get();
        order.setDate(orderShoesDTO.date());
        order.setCountry(orderShoesDTO.country());
        order.setFirstName(orderShoesDTO.firstName());
        order.setSecondName(orderShoesDTO.secondName());
        order.setEmail(orderShoesDTO.email());
        order.setAddress(orderShoesDTO.address());
        order.setNumerPhone(orderShoesDTO.numerPhone());
        order.setSummary(orderShoesDTO.summary());
        order.setState(orderShoesDTO.state());
        order.setUser(userOptional.get());

        if (orderShoesDTO.coupon() != null && orderShoesDTO.coupon().id() != null) {
            Coupon coupon = couponRepository.findById(orderShoesDTO.coupon().id())
                    .orElse(null);
            order.setCoupon(coupon);
        }

        if (orderShoesDTO.orderItems() != null) {
            // Limpiamos la colección actual
            order.getOrderItems().clear();
    
            for (OrderItemDTO dto : orderShoesDTO.orderItems()) {
                OrderItem item = orderItemMapper.toDomain(dto);
                item.setOrderShoes(order); // Setear relación bidireccional
                order.getOrderItems().add(item); // Agregar a la misma colección
            }
        }
    
        OrderShoes saved = orderShoesRepository.save(order);
        return Optional.of(orderShoesMapper.toDTO(saved));
    }

    //delete order from user
    @Transactional
public boolean deleteOrderByUser(Long orderId, Long userId) {
    Optional<OrderShoes> optionalOrder = orderShoesRepository.findById(orderId);

    if (optionalOrder.isEmpty()) return false;

    OrderShoes order = optionalOrder.get();

    if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
        return false;
    }

    // Romper relaciones por seguridad
    order.getOrderItems().forEach(item -> item.setOrderShoes(null));
    order.getOrderItems().clear();
    order.setUser(null);
    order.setCoupon(null);
    orderShoesRepository.saveAndFlush(order);

    // ⚠️ Eliminar directo por SQL
    orderShoesRepository.forceDeleteById(orderId);

    return true;
}

    


//int entero = orderShoesRepository.deleteByIdAndUserId(orderId,userId);    


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
        for (OrderItem item : order.getOrderItems()) {
            if (item.getShoe() == null || item.getShoe().getId() == null) continue;
            Long shoeId = item.getShoe().getId();
            Shoe shoe = shoeRepository.findById(shoeId).orElse(null); // o repository
            if (shoe != null) {
                item.setShoe(shoe);
                item.setShoeName(shoe.getName());
            }
        }
    
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

    public List<Map<String, Object>> getOrderCountsByMonth() {
        return orderShoesRepository.getOrderCountsByMonth();
    }
    
    public List<Map<String, Object>> getMoneyGainedByMonth() {
        return orderShoesRepository.getMoneyGainedByMonth();
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
