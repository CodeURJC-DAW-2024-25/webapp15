package com.stepx.stepx.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stepx.stepx.model.Shoe;

@Repository
public interface ShoeRepository extends JpaRepository<Shoe, Long> {

       // to load 9 without filter aplicated
       @Query(value = "SELECT * FROM shoe", nativeQuery = true)
       Page<Shoe> findNineShoes(Pageable pageable);

       // to load 3 more without filter aplicated
       Page<Shoe> findAll(Pageable page);

       //get all shoes from ddbb
       @Query(value = "Select * from shoe",nativeQuery = true)
       List<Shoe> findAllShoes();

       // to load 9 with brand filter aplicated
       @Query(value = "SELECT * FROM shoe WHERE brand=:brand", nativeQuery = true)
       Page<Shoe> findFirst9ByBrand(@Param("brand") String brand, Pageable pageable);

       // to load 3 more with brand filter aplicated
       @Query(value = "SELECT * FROM shoe WHERE brand=:brand", nativeQuery = true)
       Page<Shoe> findByBrand(@Param("brand") String brand, Pageable pageable);

       // to load 9 with category filter aplicated
       @Query(value = "SELECT * FROM shoe WHERE category=:category", nativeQuery = true)
       Page<Shoe> findFirst9ByCategory(@Param("category") String category, Pageable pageable);

       @Query(value = "SELECT * FROM shoe WHERE category=:category", nativeQuery = true)
       Page<Shoe> findByCategory(@Param("category") String category, Pageable pageable);

       @Query("SELECT SUM(price) FROM Shoe")
       BigDecimal sumOfAllPrices();

       // Buscar por categoría o marca
       @Query("SELECT s FROM Shoe s WHERE s.category = :category OR s.brand = :brand")
       List<Shoe> findByCategoryOrBrand(@Param("category") Shoe.Category category, @Param("brand") Shoe.Brand brand);

       @Query("SELECT s FROM Shoe s WHERE s.brand IN :brands " +
                     "AND s.id NOT IN (" +
                     "  SELECT oi.shoe.id FROM OrderItem oi " +
                     "  JOIN oi.orderShoes os WHERE os.user.id = :userId)")
       List<Shoe> findRecommendedShoesByBrandsExcludingPurchased(@Param("brands") List<Shoe.Brand> brands,
                     @Param("userId") Long userId);

       //find a single shoe
       Optional<Shoe> findById(Long id);

}