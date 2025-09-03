package com.proj_db.onibus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.proj_db.onibus.model.Cambio;

public interface CambioRepository extends JpaRepository<Cambio, Long>, JpaSpecificationExecutor<Cambio> {
    Optional<Cambio> findByNumeroSerie(String numeroSerie);
    Optional<Cambio> findByCodigoFabricacao(String codigoFabricacao);

    @Query("SELECT c.tipo, COUNT(c) FROM Cambio c GROUP BY c.tipo")
    List<Object[]> countByTipo();
    
    @Query("SELECT c.status, COUNT(c) FROM Cambio c GROUP BY c.status")
    List<Object[]> countByStatus();
}