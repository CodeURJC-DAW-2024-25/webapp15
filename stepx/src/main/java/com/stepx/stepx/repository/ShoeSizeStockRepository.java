package com.stepx.stepx.repository;

import com.stepx.stepx.model.Shoe;
import com.stepx.stepx.model.ShoeSizeStock;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoeSizeStockRepository extends JpaRepository<ShoeSizeStock, Long> {
    // Aquí puedes definir métodos de consulta personalizados si los necesitas.
    @Query("SELECT s FROM ShoeSizeStock s WHERE s.shoe.id = :shoeid AND s.size = :size")
    Optional<ShoeSizeStock> findByShoeAndSize(Long shoeid, String size);
}
