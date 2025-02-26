package com.stepx.stepx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stepx.stepx.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{
    

}
