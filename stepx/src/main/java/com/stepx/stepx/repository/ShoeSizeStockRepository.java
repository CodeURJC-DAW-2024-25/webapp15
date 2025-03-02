package com.stepx.stepx.repository;

import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.ShoeSizeStock;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoeSizeStockRepository extends JpaRepository<ShoeSizeStock, Long> {
    // Aquí puedes definir métodos de consulta personalizados si los necesitas.
    @Query("SELECT s.stock FROM ShoeSizeStock s WHERE s.shoe.id = :shoeid AND s.size = :size")
    Optional<Integer> findByShoeAndSize(Long shoeid, String size);

    @Query("SELECT s FROM ShoeSizeStock s WHERE s.shoe.id IN :shoeIds")
    List<ShoeSizeStock> findByShoeIds(@Param("shoeIds") List<Long> shoeIds);//all the stock in a single consult

}
