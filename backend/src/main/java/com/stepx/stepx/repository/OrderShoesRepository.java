package com.stepx.stepx.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.stepx.stepx.model.OrderShoes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderShoesRepository extends JpaRepository<OrderShoes, Long> {
    @Query(value = "SELECT COUNT(*) FROM order_shoes WHERE state = 'Processed'", nativeQuery = true)
    long countProcessedOrders();

    @Query(value = "SELECT COALESCE(SUM(summary), 0) FROM order_shoes WHERE state = 'Processed'", nativeQuery = true)
    BigDecimal getTotalMoneyGained();

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

    @Query(value = "SELECT DATE_FORMAT(date, '%m') AS month, SUM(summary) AS total_spent " +
            "FROM order_shoes " +
            "WHERE user_id = :userId AND state = 'Processed' " +
            "GROUP BY DATE_FORMAT(date, '%Y-%m'), DATE_FORMAT(date, '%m') " +
            "ORDER BY month", nativeQuery = true)
    List<Map<String, Object>> getMonthlySpendingByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT DATE_FORMAT(date, '%m') AS month, COUNT(*) AS orders_count FROM order_shoes WHERE state = 'Processed' GROUP BY DATE_FORMAT(date, '%Y-%m') ORDER BY month", nativeQuery = true)
    List<Map<String, Object>> getOrderCountsByMonth();

    @Query(value = "SELECT DATE_FORMAT(date, '%m') AS month, SUM(summary) AS total_money FROM order_shoes WHERE state = 'Processed' GROUP BY DATE_FORMAT(date, '%Y-%m') ORDER BY month", nativeQuery = true)
    List<Map<String, Object>> getMoneyGainedByMonth();

    @Query("SELECT o FROM OrderShoes o WHERE o.user.id = :userId ORDER BY o.id DESC LIMIT 1")
    OrderShoes findTopByUserIdOrderByIdDesc(@Param("userId") Long userId);

    @Query("SELECT o FROM OrderShoes o WHERE o.user.id = :userId AND o.state = 'Processed' ORDER BY o.id DESC")
    List<OrderShoes> getOrderShoesFinishedByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM OrderShoes o WHERE o.user.id = :userId AND o.state = 'Processed' ORDER BY o.id DESC")
    List<OrderShoes> getPagedOrdersByUserId(@Param("userId") Long userId, Pageable pageable);


    @Query("SELECT o FROM OrderShoes o WHERE o.user.id = :userId")
    List<OrderShoes> findOrdersByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM order_shoes WHERE id = :orderId", nativeQuery = true)
    void forceDeleteById(@Param("orderId") Long orderId);

}
