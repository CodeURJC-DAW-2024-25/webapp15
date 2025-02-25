package com.stepx.stepx.repository;

import com.stepx.stepx.model.Shoe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ShoeRepository extends JpaRepository<Shoe, Long> {
    
}