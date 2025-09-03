package com.proj_db.onibus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.proj_db.onibus.model.Onibus;

public interface OnibusRepository extends JpaRepository<Onibus, Long>, JpaSpecificationExecutor<Onibus> {
    Optional<Onibus> findByChassi(String chassi);
    Optional<Onibus> findByPlaca(String placa);
    Optional<Onibus> findByNumeroFrota(String numeroFrota);

    @Query("SELECT o.marca, COUNT(o) FROM Onibus o GROUP BY o.marca")
    List<Object[]> countByMarca();
    
    @Query("SELECT o.status, COUNT(o) FROM Onibus o GROUP BY o.status")
    List<Object[]> countByStatus();
}