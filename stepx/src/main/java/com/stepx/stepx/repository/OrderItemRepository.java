package com.stepx.stepx.repository;

import com.stepx.stepx.model.OrderItem;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderShoes.user.id = :userId AND oi.orderShoes.state = 'notFinished' AND oi.shoe.id = :shoeId AND oi.size = :size")
    Optional<OrderItem> findByCartAndShoeAndSize(@Param("userId") Long userId, @Param("shoeId") Long shoeId,
            @Param("size") String size);

    public void deleteById(Long id);

    public Optional<OrderItem> findById(Long id);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE OrderItem oi SET oi.quantity = :quantity WHERE oi.id = :id")
    void updateOrderItemQuantity(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Query("SELECT oi.shoe, SUM(oi.quantity) as totalSold FROM OrderItem oi " +
            "JOIN oi.orderShoes os " +
            "WHERE os.state = 'Processed' " +
            "GROUP BY oi.shoe " +
            "ORDER BY totalSold DESC")
    List<Object[]> findBestSellingShoes(Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.orderShoes.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

}