package com.stepx.stepx.repository;

import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.ShoeSizeStock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

@Repository
public interface ShoeSizeStockRepository extends JpaRepository<ShoeSizeStock, Long> {
    // here you define custom request methods if u need
    @Query("SELECT s.stock FROM ShoeSizeStock s WHERE s.shoe.id = :shoeid AND s.size = :size")
    Optional<Integer> findByShoeAndSize(Long shoeid, String size);

    @Query("SELECT s FROM ShoeSizeStock s WHERE s.shoe.id IN :shoeIds AND s.size IN :sizes")
    List<ShoeSizeStock> findByShoeIdsAndSizes(@Param("shoeIds") List<Long> shoeIds, @Param("sizes") List<String> sizes);
//all the stock in a single consult

    @Modifying
    @Transactional
    @Query("UPDATE ShoeSizeStock s SET s.stock = s.stock - :quantity " +
           "WHERE s.shoe.id = :shoeId AND s.size = :size AND s.stock >= :quantity")
    int reduceStock(@Param("shoeId") Long shoeId, @Param("size") String size, @Param("quantity") int quantity);
}
