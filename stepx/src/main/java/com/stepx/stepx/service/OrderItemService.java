package com.stepx.stepx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Set;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.Product;
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

    public OrderItemService(OrderItemRepository orderItemRepository, ShoeRepository shoeRepository,
            OrderShoesRepository orderShoesRepository) {
        this.orderItemRepository = orderItemRepository;
        this.shoeRepository = shoeRepository;
        this.orderShoesRepository = orderShoesRepository;
    }

    public Optional<OrderItem> findByCartAndShoeAndSize(Long userId, Long shoeId, String size) {
        return orderItemRepository.findByCartAndShoeAndSize(userId, shoeId, size);// this return the shoe that the
                                                                                  // cliente is triying to add to the
                                                                                  // cart
        // if exist or not, that will be managed by the controller
    }

    public void save(OrderItem orderItem) {// save the order item in the bbdd
        orderItemRepository.save(orderItem);
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

    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public List<Shoe> getBestSellingShoes(int limit) {
        List<Object[]> results = orderItemRepository.findBestSellingShoes(PageRequest.of(0, limit));
        return results.stream()
                .map(result -> (Shoe) result[0]) // Extraemos solo el objeto Shoe
                .collect(Collectors.toList());
    }


    public List<Shoe.Brand> getBrandsFromLastOrder(Long userId) {
        // Obtener el último pedido del usuario
        OrderShoes lastOrder = orderShoesRepository.findTopByUserIdOrderByIdDesc(userId);

        if (lastOrder == null || lastOrder.getOrderItems().isEmpty()) {
            return new ArrayList<>();  // Si no hay último pedido o no tiene productos, devolver lista vacía
        }

        // Obtener las marcas de los zapatos de ese pedido
        return lastOrder.getOrderItems().stream()
                        .map(orderItem -> orderItem.getShoe().getBrand())
                        .distinct()  // Para evitar marcas duplicadas
                        .collect(Collectors.toList());
    }

    // Recomendación basada en la categoría y la marca de las compras anteriores

    // En OrderItemService.java
    public List<Shoe> getRecommendedShoesForUser(Long userId, int limit) {
        // Obtener las marcas del último pedido del usuario
        System.out.println("esto es unaprueba");
        List<Shoe.Brand> brands = getBrandsFromLastOrder(userId);
        System.out.println("Brands: " + brands);

        if (brands.isEmpty()) {
            return new ArrayList<>(); // No hay compras previas, no se pueden recomendar productos
        }

        // Buscar productos recomendados por marca, pero que el usuario no haya comprado
        List<Shoe> recommendedShoes = shoeRepository.findRecommendedShoesByBrandsExcludingPurchased(brands, userId);
        System.out.println("Zapatos recomendados: " + recommendedShoes);
        // Limitar la cantidad de recomendaciones
        return recommendedShoes.stream().limit(limit).collect(Collectors.toList());
    }

}
