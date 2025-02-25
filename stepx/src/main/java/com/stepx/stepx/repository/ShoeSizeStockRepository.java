package com.stepx.stepx.repository;

import com.stepx.stepx.model.ShoeSizeStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoeSizeStockRepository extends JpaRepository<ShoeSizeStock, Long> {
    // Aquí puedes definir métodos de consulta personalizados si los necesitas.
}
