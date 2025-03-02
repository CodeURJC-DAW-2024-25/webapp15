package com.stepx.stepx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderShoesRepository extends JpaRepository<OrderShoes, Long> {

    @Query("SELECT o FROM OrderShoes o WHERE o.user.id = :userId AND o.state = 'notFinished'")
    Optional<OrderShoes> findCartById(@Param("userId") Long userId);

    @Query("""
    SELECT SUM(i.quantity * s.price) 
    FROM OrderItem i
    JOIN i.shoe s
    JOIN ShoeSizeStock stock ON stock.shoe.id = s.id AND stock.size = i.size
    WHERE i.orderShoes.id = :cartId AND stock.stock > 0
    """)
    BigDecimal getTotalPriceExcludingOutOfStock(@Param("cartId") Long cartId);


    


}
