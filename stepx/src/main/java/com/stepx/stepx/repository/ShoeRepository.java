package com.stepx.stepx.repository;

import com.stepx.stepx.model.Shoe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ShoeRepository extends JpaRepository<Shoe, Long> {

    @Query(value = "SELECT * FROM shoe LIMIT 9", nativeQuery = true)
    List<Shoe> findNineShoes();

    Page<Shoe> findAll(Pageable page);
    


    
}