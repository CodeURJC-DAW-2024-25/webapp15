package com.stepx.stepx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.stepx.stepx.model.OrderItem;
import com.stepx.stepx.model.OrderShoes;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderShoesRepository extends JpaRepository<OrderShoes, Long> {
    @Query("SELECT o FROM OrderShoes o WHERE o.user.id = :userId AND o.state = 'notFinished'")
    Optional<OrderShoes> findCartById(@Param("userId") Long userId);


    
    @Query(value = "SELECT DATE_FORMAT(date, '%m') AS month, COUNT(*) AS orders_count FROM order_shoes WHERE state = 'Processed' GROUP BY DATE_FORMAT(date, '%Y-%m') ORDER BY month", nativeQuery = true)
    List<Map<String, Object>> getOrderCountsByMonth();

    @Query(value = "SELECT DATE_FORMAT(date, '%m') AS month, SUM(summary) AS total_money FROM order_shoes WHERE state = 'Processed' GROUP BY DATE_FORMAT(date, '%Y-%m') ORDER BY month", nativeQuery = true)
    List<Map<String, Object>> getMoneyGainedByMonth();

    

    


}
