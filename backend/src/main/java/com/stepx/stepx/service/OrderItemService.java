package com.stepx.stepx.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysql.cj.x.protobuf.MysqlxCrud.Order;
import com.stepx.stepx.dto.OrderItemDTO;
import com.stepx.stepx.dto.ShoeDTO;
import com.stepx.stepx.mapper.OrderItemMapper;
import com.stepx.stepx.mapper.ShoeMapper;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.repository.OrderItemRepository;
import com.stepx.stepx.repository.ShoeRepository;
import com.stepx.stepx.repository.OrderShoesRepository;

@Service
public class OrderItemService {

    private OrderItemRepository orderItemRepository;
    private ShoeRepository shoeRepository;
    private OrderShoesRepository orderShoesRepository;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ShoeMapper shoeMapper;

    public OrderItemService(OrderItemRepository orderItemRepository, ShoeRepository shoeRepository,
            OrderShoesRepository orderShoesRepository) {
        this.orderItemRepository = orderItemRepository;
        this.shoeRepository = shoeRepository;
        this.orderShoesRepository = orderShoesRepository;
    }

    public OrderItemDTO findByCartAndShoeAndSize(Long userId, Long shoeId, String size) {
        
        Optional<OrderItem> orderItem = orderItemRepository.findByCartAndShoeAndSize(userId, shoeId, size);
        if (orderItem.isPresent()) {
            return orderItemMapper.toDTO(orderItem.get());
        }else {
            return null;
        }
        // this return the shoe that the cliente is triying to add to the cart if exist or not, that will be managed by the controller
    }

    public OrderItemDTO findById(Long id) {
        Optional<OrderItem> orderItem = orderItemRepository.findById(id);
        if (orderItem.isPresent()) {
            return orderItemMapper.toDTO(orderItem.get());
        } else {
            return null;
        }
    }

    public List<OrderItemDTO> findAll() {
        Collection<OrderItem> orderItems = orderItemRepository.findAll();
        return orderItemMapper.toDTOs(orderItems);
    }

    @Transactional
    public void addOrUpdateItem(OrderItemDTO dto, int newQuantity) {

        if (dto == null) {
            throw new IllegalArgumentException("DTO cannot be null");
        }

        if (dto.id() != null) {
            Optional<OrderItem> existingItem = orderItemRepository.findById(dto.id());
            if (existingItem.isPresent()) {
                OrderItem item = existingItem.get();
                item.setQuantity(newQuantity);
                orderItemRepository.save(item);
            } else {
                throw new IllegalStateException("OrderItem not found with id: " + dto.id());
            }
        } else {
            OrderItem newItem = orderItemMapper.toDomain(dto);
            newItem.setQuantity(newQuantity);
            orderItemRepository.save(newItem);
        }
    }

    public OrderItemDTO save(OrderItemDTO orderItemDTO) {
        OrderShoes orderShoe = orderShoesRepository.findById(orderItemDTO.orderId())
                .orElseThrow(() -> new IllegalArgumentException("OrderShoe not found"));

        Shoe shoe = shoeRepository.findById(orderItemDTO.shoeId())
                .orElseThrow(() -> new IllegalArgumentException("Shoe not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderShoes(orderShoe);
        orderItem.setShoe(shoe);
        orderItem.setShoeName(orderItemDTO.shoeName());
        orderItem.setQuantity(orderItemDTO.quantity());
        orderItem.setSize(orderItemDTO.size());

        if (orderItemDTO.price() != null) {
            orderItem.setPrice(orderItemDTO.price());
        } else {
            orderItem.setPrice(shoe.getPrice().multiply(BigDecimal.valueOf(orderItemDTO.quantity())));
        }

        OrderItem saved = orderItemRepository.save(orderItem);
        return orderItemMapper.toDTO(saved);
    }


    @Transactional
    public void updateOrderItem(Long id, int quantity) {
        Optional<OrderItem> item_Optional = orderItemRepository.findById(id);
        OrderItem item = item_Optional.get();
        item.setQuantity(quantity);
        orderItemRepository.save(item);

    }

    @Transactional
    public void updateOrderItemsBatch(List<Long> ids, List<Integer> quantities) {
        if (ids.size() != quantities.size()) {
            throw new IllegalArgumentException("Las listas de IDs y cantidades deben tener el mismo tamaño.");
        }

        for (int i = 0; i < ids.size(); i++) {
            orderItemRepository.updateOrderItemQuantity(ids.get(i), quantities.get(i));
        }
    }

    public Optional<OrderItemDTO> updateAllOrderItem(Long id, OrderItemDTO orderItemDTO) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);
        if (orderItemOptional.isEmpty()) {
            return Optional.empty();
        }
        OrderItem orderItem = orderItemOptional.get();

        OrderShoes orderShoe = orderShoesRepository.findById(orderItemDTO.orderId())
                .orElseThrow(() -> new IllegalArgumentException("OrderShoe not found"));

        Shoe shoe = shoeRepository.findById(orderItemDTO.shoeId())
                .orElseThrow(() -> new IllegalArgumentException("Shoe not found"));

        orderItem.setOrderShoes(orderShoe);
        orderItem.setShoe(shoe);
        orderItem.setShoeName(shoe.getName()); // Usamos el nombre correcto del zapato
        orderItem.setQuantity(orderItemDTO.quantity());
        orderItem.setSize(orderItemDTO.size());

        if (orderItemDTO.price() != null) {
            orderItem.setPrice(orderItemDTO.price());
        } else {
            orderItem.setPrice(shoe.getPrice().multiply(BigDecimal.valueOf(orderItemDTO.quantity())));
        }

        OrderItem saved = orderItemRepository.save(orderItem);
        return Optional.of(orderItemMapper.toDTO(saved));
    }

    public Optional<OrderItemDTO> deleteOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id).orElse(null);
        if (orderItem != null) {
            orderItemRepository.deleteById(id);
            return Optional.of(orderItemMapper.toDTO(orderItem));   
        }
        return Optional.empty();
    }

    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        Collection<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        return orderItemMapper.toDTOs(orderItems);
    }

    
    public List<ShoeDTO> getBestSellingShoes(int limit) {
        List<Object[]> results = orderItemRepository.findBestSellingShoes(PageRequest.of(0, limit));

        return results.stream()
                .map(result -> shoeMapper.toDTO((Shoe) result[0])) // MapStruct hace la conversión
                .collect(Collectors.toList());
    }

    public List<Shoe.Brand> getBrandsFromLastOrder(Long userId) {
        // obtain last order of user
        OrderShoes lastOrder = orderShoesRepository.findTopByUserIdOrderByIdDesc(userId);

        if (lastOrder == null || lastOrder.getOrderItems().isEmpty()) {
            return new ArrayList<>();
        }

        //get brands from shoes of that order
        return lastOrder.getOrderItems().stream()
                .map(orderItem -> orderItem.getShoe().getBrand())
                .distinct() // to not repeat brands
                .collect(Collectors.toList());
    }

    public List<ShoeDTO> getRecommendedShoesForUser(Long userId, int limit) {
        List<Shoe.Brand> brands = getBrandsFromLastOrder(userId);
        if (brands.isEmpty()) {
            return new ArrayList<>();
        }
        List<Shoe> recommendedShoes = shoeRepository.findRecommendedShoesByBrandsExcludingPurchased(brands, userId);
        return recommendedShoes.stream()
                .limit(limit)
                .map(shoeMapper::toDTO) // Usamos MapStruct para la conversión
                .collect(Collectors.toList());
    }

    public List<OrderItem> convertToOrderItemList(Long shoeId, List<OrderItemDTO> orderItemDTOs) {
        if (shoeId == null) {
            throw new IllegalArgumentException("El ID del zapato no puede ser nulo.");
        }
    
        if (orderItemDTOs == null || orderItemDTOs.isEmpty()) {
            return new ArrayList<>();
        }
    

        Shoe shoe = shoeRepository.findById(shoeId).orElseThrow(
            () -> new IllegalArgumentException("Shoe not found with ID: " + shoeId)
        );
    
        return orderItemDTOs.stream().map(dto -> {
            OrderShoes orderShoes = null;
            if (dto.orderId() != null) {
                orderShoes = orderShoesRepository.findById(dto.orderId()).orElse(null);
            }
    
            return new OrderItem(orderShoes, shoe, dto.quantity(), dto.size());
        }).collect(Collectors.toList());
    }

   

}
