package com.stepx.stepx.repository;

import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.Shoe.Brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ShoeRepository extends JpaRepository<Shoe, Long> {

    //to load 9 without filter aplicated
    @Query(value = "SELECT * FROM shoe", nativeQuery = true)
    Page<Shoe> findNineShoes(Pageable pageable);

    //to load 3 more without filter aplicated
    Page<Shoe> findAll(Pageable page);

    //to load 9 with brand filter aplicated
    @Query(value = "SELECT * FROM shoe WHERE brand=:brand", nativeQuery = true)
    Page<Shoe> findFirst9ByBrand(@Param("brand") String brand,Pageable pageable);

    //to load 3 more with brand filter aplicated
    @Query(value = "SELECT * FROM shoe WHERE brand=:brand", nativeQuery = true)
    Page<Shoe> findByBrand(@Param("brand") String brand, Pageable pageable);
    

    //to load 9 with category filter aplicated
    @Query(value = "SELECT * FROM shoe WHERE category=:category", nativeQuery = true)
    Page<Shoe> findFirst9ByCategory(@Param("category") String category,Pageable pageable);

    @Query(value="SELECT * FROM shoe WHERE category=:category",nativeQuery = true)
    Page<Shoe> findByCategory(@Param("category") String category,Pageable pageable);

     @Query("SELECT SUM(price) FROM Shoe")
    BigDecimal sumOfAllPrices();
    
}