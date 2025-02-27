package com.stepx.stepx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stepx.stepx.model.OrderShoes;
import java.util.List;

public interface OrderShoesRepository extends JpaRepository<OrderShoes, Long>{
    
}
