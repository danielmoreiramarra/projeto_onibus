package com.proj_db.onibus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.model.Pneu.StatusPneu;

public interface PneuRepository extends JpaRepository<Pneu, Long>, JpaSpecificationExecutor<Pneu> {
    Optional<Pneu> findByNumeroSerie(String numeroSerie);
    Optional<Pneu> findByCodigoFabricacao(String codigoFabricacao);
    List<Pneu> findByOnibusIdAndStatus(Long onibusId, StatusPneu statusPneu);
    List<Pneu> findByStatus(StatusPneu statusPneu);

    @Query("SELECT p.marca, COUNT(p) FROM Pneu p GROUP BY p.marca")
    List<Object[]> countByMarca();
    
    @Query("SELECT p.status, COUNT(p) FROM Pneu p GROUP BY p.status")
    List<Object[]> countByStatus();

    @Query("SELECT p FROM Pneu p WHERE p.status = 'DISPONIVEL'")
    List<Pneu> findPneusDisponiveis();

    @Query("SELECT p.marca, AVG(p.kmRodados) FROM Pneu p WHERE p.kmRodados > 0 GROUP BY p.marca")
    List<Object[]> avgKmPorMarca();
}