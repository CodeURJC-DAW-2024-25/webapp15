package com.stepx.stepx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import com.stepx.stepx.model.Review;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r JOIN FETCH r.user WHERE r.shoe.id = :shoeId")
    List<Review> findReviewsByShoeId(@Param("shoeId") Long shoeId);

    void deleteById(Long id);

    // Obtener reviews paginadas por el ID del zapato (shoe)
    Page<Review> findByShoeId(Long shoeId, Pageable pageable);

}
