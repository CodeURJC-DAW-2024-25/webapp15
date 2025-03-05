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

    // Obtener productos comprados por el usuario
    public List<Shoe> getPurchasedShoesByUser(Long userId) {
        // Obtener todas las órdenes del usuario
        Optional<OrderShoes> orders = orderShoesRepository.findCartById(userId);
        
        //System.out.println("Órdenes encontradas para el usuario: " + orders.size());  // Verificar el número de órdenes
    
        // Obtener todos los OrderItem asociados a esas órdenes y mapearlos a productos (Shoe)
        List<Shoe> purchasedShoes = orders.stream()
                                          .flatMap(order -> order.getOrderItems().stream())
                                          .map(OrderItem::getShoe)
                                          .distinct() // Eliminar productos duplicados
                                          .collect(Collectors.toList());
        
        System.out.println("Productos comprados: " + purchasedShoes.size());  // Verificar los productos comprados
    
        return purchasedShoes;
    }

    // Recomendación basada en la categoría y la marca de las compras anteriores
    public List<Shoe> getRecommendedShoes(Long userId, int limit) {
        List<Shoe> purchasedShoes = getPurchasedShoesByUser(userId);

        // Agrupamos por categorías y marcas
        List<Shoe> recommendedShoes = new ArrayList<>();
        for (Shoe purchasedShoe : purchasedShoes) {
            recommendedShoes.addAll(
                    shoeRepository.findByCategoryOrBrand(purchasedShoe.getCategory(), purchasedShoe.getBrand()));
        }

        // Filtrar los productos ya comprados
        recommendedShoes = recommendedShoes.stream()
                .filter(shoe -> !purchasedShoes.contains(shoe))
                .distinct()
                .limit(limit) // Limitar la cantidad de recomendaciones
                .collect(Collectors.toList());
        return recommendedShoes;
    }
}   
