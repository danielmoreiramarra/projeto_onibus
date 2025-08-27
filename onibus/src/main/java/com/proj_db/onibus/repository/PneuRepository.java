package com.proj_db.onibus.repository;

import com.proj_db.onibus.model.Onibus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.model.Pneu.PosicaoPneu;
import com.proj_db.onibus.model.Pneu.StatusPneu;

public interface PneuRepository extends JpaRepository<Pneu, Long> {
    
    // Métodos derivados da sua implementação
    Optional<Pneu> findByNumeroSerie(String numeroSerie);
    
    Optional<Pneu> findByCodigoFabricacao(String codigoFabricacao);
    
    List<Pneu> findByMarca(String marca);
    
    List<Pneu> findByMedida(String medida);
    
    List<Pneu> findByMarcaAndMedida(String marca, String medida);
    
    List<Pneu> findByStatus(StatusPneu status);
    
    List<Pneu> findByAnoFabricacao(Integer anoFabricacao);
    
    List<Pneu> findByKmRodadosGreaterThanEqual(Integer kmLimite);
    
    List<Pneu> findByKmRodadosLessThanEqual(Integer kmLimite);
    
    boolean existsByNumeroSerie(String numeroSerie);
    
    boolean existsByCodigoFabricacao(String codigoFabricacao);
    
    // Métodos personalizados (Queries)
    
    // Um ônibus pode ter vários pneus, então o retorno deve ser uma lista
    @Query("SELECT p FROM Pneu p WHERE p.onibus.id = :onibusId")
    List<Pneu> findByOnibusId(@Param("onibusId") Long onibusId);
    
    // Buscar Pneus por ônibus específico e posição
    @Query("SELECT p FROM Pneu p WHERE p.onibus = :onibus AND p.posicao = :posicao")
    Optional<Pneu> findByOnibusAndPosicao(
        @Param("onibus") Onibus onibus,
        @Param("posicao") PosicaoPneu posicao);

    // Buscar Pneus novos
    @Query("SELECT p FROM Pneu p WHERE p.onibus IS NULL AND p.status = 'NOVO'")
    List<Pneu> findPneusNovos();
    
    // Buscar Pneus disponíveis (não instalados)
    @Query("SELECT p FROM Pneu p WHERE p.onibus IS NULL AND p.status = 'DISPONIVEL'")
    List<Pneu> findPneusDisponiveis();
    
    // Buscar Pneus em uso
    @Query("SELECT p FROM Pneu p WHERE p.onibus IS NOT NULL AND p.status = 'EM_USO'")
    List<Pneu> findPneusEmUso();
    
    // Buscar Pneus com garantia prestes a vencer
    @Query("SELECT p FROM Pneu p WHERE p.dataCompra IS NOT NULL AND " +
           "p.dataCompra <= :dataLimite")
    List<Pneu> findPneusGarantiaPrestesVencer(@Param("dataLimite") LocalDate dataLimite);
    
    // Buscar Pneus que precisam de reforma (com base na data de compra ou última reforma)
    @Query("SELECT p FROM Pneu p WHERE p.status != 'DESCARTADO' AND p.status != 'VENDIDO' AND " +
           "(p.dataUltimaReforma IS NULL OR p.dataUltimaReforma <= :dataLimite) AND p.kmRodados >= 10000")
    List<Pneu> findPneusPrecisandoReforma(@Param("dataLimite") LocalDate dataLimite);
    
    // Contar Pneus por status
    @Query("SELECT p.status, COUNT(p) FROM Pneu p GROUP BY p.status")
    List<Object[]> countPneusPorStatus();
    
    // Calcular km médio dos Pneus por marca
    @Query("SELECT p.marca, AVG(p.kmRodados) FROM Pneu p WHERE p.kmRodados > 0 GROUP BY p.marca")
    List<Object[]> avgKmPorMarca();
}