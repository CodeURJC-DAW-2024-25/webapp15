package com.stepx.stepx.service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.stepx.stepx.dto.*;
import com.stepx.stepx.mapper.OrderItemMapper;
import com.stepx.stepx.mapper.OrderShoesMapper;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.User;
import com.stepx.stepx.repository.CouponRepository;
import com.stepx.stepx.repository.OrderItemRepository;
import com.stepx.stepx.repository.OrderShoesRepository;
import com.stepx.stepx.repository.ShoeRepository;
import com.stepx.stepx.repository.UserRepository;
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
    

        OrderShoes order = orderOptional.get();
        boolean goingToProcessed =!"Processed".equals(order.getState()) && "Processed".equals(orderShoesDTO.state());
        order.setDate(orderShoesDTO.date());
        order.setCountry(orderShoesDTO.country());
        order.setFirstName(orderShoesDTO.firstName());
        order.setSecondName(orderShoesDTO.secondName());
        order.setEmail(orderShoesDTO.email());
        order.setAddress(orderShoesDTO.address());
        order.setNumerPhone(orderShoesDTO.numerPhone());
        order.setState(orderShoesDTO.state());
        order.setUser(userOptional.get());
        
        order.setCuponUsed(orderShoesDTO.cuponUsed());
        if (orderShoesDTO.coupon() != null && orderShoesDTO.coupon().id() != null) {
            couponRepository.findById(orderShoesDTO.coupon().id()).ifPresent(order::setCoupon);
        }else{
            order.setCoupon(null);
        }

        
            // Limpiamos la colección actual
        order.getOrderItems().clear();
    
        for (OrderItemDTO dto : orderShoesDTO.orderItems()) {
            OrderItem item = orderItemMapper.toDomain(dto);
            item.setOrderShoes(order);
            order.getOrderItems().add(item);
        }

        order.setSummary(orderShoesDTO.summary());
    
        OrderShoes saved = orderShoesRepository.save(order);
        if (goingToProcessed) {
            Map<Long, Map<String, Integer>> stockMap = orderShoesDTO.orderItems().stream()
                    .collect(Collectors.groupingBy(
                            OrderItemDTO::shoeId,
                            Collectors.toMap(
                                    OrderItemDTO::size,
                                    OrderItemDTO::quantity,
                                    Integer::sum)));

            shoeSizeStockService.updateStock(stockMap);
}
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

  
    order.getOrderItems().forEach(item -> item.setOrderShoes(null));
    order.getOrderItems().clear();
    order.setUser(null);
    order.setCoupon(null);
    orderShoesRepository.saveAndFlush(order);

    
    orderShoesRepository.forceDeleteById(orderId);

    return true;
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
        OrderShoes orderShoes = orderShoesMapper.toDomain(orderShoesDTO); 
        return orderShoes.getOrderItems().size(); 
    }

    public Map<String, Object> getShoeIdsAndSizes(OrderShoesDTO orderShoesDTO) {
        
        OrderShoes orderShoes = orderShoesMapper.toDomain(orderShoesDTO);

        
        List<Long> shoeIds = orderShoes.getOrderItems().stream()
                .map(orderItem -> orderItem.getShoe().getId()) 
                .distinct()
                .collect(Collectors.toList());

        List<String> sizes = orderShoes.getOrderItems().stream()
                .map(OrderItem::getSize)  
                .distinct()
                .collect(Collectors.toList());

       
        Map<String, Object> result = new HashMap<>();
        result.put("shoeIds", shoeIds);
        result.put("sizes", sizes);
        
        return result;
    }
   

public Map<String, Object> generateOrderCountChartData() {
    // Get order counts by month from repository
    List<Map<String, Object>> orderCounts = getOrderCountsByMonth();
    
    // Create result structure with labels and data arrays
    Map<String, Object> chartData = new HashMap<>();
    String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
    Integer[] monthData = new Integer[12];
    
    // Initialize with zeros
    for (int i = 0; i < 12; i++) {
        monthData[i] = 0;
    }
    
    // Fill in the actual data
    for (Map<String, Object> entry : orderCounts) {
        String monthStr = (String) entry.get("month");
        Long count = ((Number) entry.get("orders_count")).longValue();
        
        // Convert month string to zero-based index (01 -> 0, 02 -> 1, etc.)
        int monthIndex = Integer.parseInt(monthStr) - 1;
        if (monthIndex >= 0 && monthIndex < 12) {
            monthData[monthIndex] = count.intValue();
        } else {
            System.out.println("Invalid month index: " + monthIndex + " for month string: " + monthStr);
        }
    }
    
    chartData.put("labels", monthNames);
    chartData.put("data", monthData);
    
    return chartData;
}

public Map<String, Object> generateMoneyGainedChartData() {
    // Get money gained by month
    List<Map<String, Object>> moneyGained = getMoneyGainedByMonth();
    
    // Create result structure with labels and data arrays
    Map<String, Object> chartData = new HashMap<>();
    String[] monthNames = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
    Double[] monthData = new Double[12];
    
    // Initialize with zeros
    for (int i = 0; i < 12; i++) {
        monthData[i] = 0.0;
    }
    
    // Fill in the actual money data
    for (Map<String, Object> entry : moneyGained) {
        String monthStr = (String) entry.get("month");
        Number amount = (Number) entry.get("total_money");
        Double totalMoney = amount != null ? amount.doubleValue() : 0.0;
        
        // Convert month string to zero-based index
        int monthIndex = Integer.parseInt(monthStr) - 1;
        if (monthIndex >= 0 && monthIndex < 12) {
            monthData[monthIndex] = totalMoney;
        } else {
            System.out.println("Invalid month index: " + monthIndex + " for month string: " + monthStr);
        }
    }
    
    chartData.put("labels", monthNames);
    chartData.put("data", monthData);
    
    return chartData;
}


public Map<String, Object> getAdminDashboardStats() {
    Map<String, Object> stats = new HashMap<>();
    
    // Get total number of users
    long userCount = userRepository.count();
    stats.put("userCount", userCount);
    
    // Get total number of shoes
    long shoeCount = shoeRepository.count();
    stats.put("shoeCount", shoeCount);
    
    // Get total number of processed orders
    long processedOrderCount = orderShoesRepository.countProcessedOrders();
    stats.put("processedOrderCount", processedOrderCount);
    
    // Get total money gained from processed orders
    BigDecimal totalMoneyGained = orderShoesRepository.getTotalMoneyGained();
    stats.put("totalMoneyGained", totalMoneyGained);
    
    return stats;
}
}