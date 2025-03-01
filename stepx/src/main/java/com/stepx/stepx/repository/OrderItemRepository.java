package com.stepx.stepx.repository;

import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import com.stepx.stepx.model.ShoeSizeStock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
@Query("SELECT oi FROM OrderItem oi WHERE oi.orderShoes.user.id = :userId AND oi.orderShoes.state = 'notFinished' AND oi.shoe.id = :shoeId AND oi.size = :size")
Optional<OrderItem> findByCartAndShoeAndSize(@Param("userId") Long userId, @Param("shoeId") Long shoeId, @Param("size") String size);

public void deleteById(Long id);

public Optional<OrderItem> findById(Long id);

}