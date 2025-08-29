package com.proj_db.onibus.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.model.Pneu.PosicaoPneu;
import com.proj_db.onibus.model.Pneu.StatusPneu;

public interface PneuRepository extends JpaRepository<Pneu, Long> {
    
    // ✅ MÉTODOS DE BUSCA INDIVIDUAIS
    Optional<Pneu> findByNumeroSerie(String numeroSerie);
    
    Optional<Pneu> findByCodigoFabricacao(String codigoFabricacao);
    
    List<Pneu> findByMarca(String marca);
    
    List<Pneu> findByMedida(String medida);
    
    List<Pneu> findByMarcaAndMedida(String marca, String medida);
    
    List<Pneu> findByStatus(StatusPneu status);
    
    List<Pneu> findByAnoFabricacao(Integer anoFabricacao);
    
    boolean existsByNumeroSerie(String numeroSerie);
    
    boolean existsByCodigoFabricacao(String codigoFabricacao);

    // Buscar pneus por ônibus específico e posição
    @Query("SELECT p FROM Pneu p WHERE p.onibus = :onibus AND p.posicao =:posicao")
    Optional<Pneu>findByOnibusAndPosicao(
        @Param("onibus") Onibus onibus,
        @Param("posicao") Pneu.PosicaoPneu posicao);
    
    // ✅ NOVA CONSULTA COMBINADA PARA TODOS OS CAMPOS
    @Query("SELECT p FROM Pneu p WHERE " +
           "(:id IS NULL OR p.id = :id) AND " +
           "(:posicao IS NULL OR p.posicao = :posicao) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:onibusId IS NULL OR p.onibus.id = :onibusId) AND " +
           "(:medida IS NULL OR p.medida LIKE %:medida%) AND " +
           "(:marca IS NULL OR p.marca LIKE %:marca%) AND " +
           "(:numeroSerie IS NULL OR p.numeroSerie LIKE %:numeroSerie%) AND " +
           "(:codigoFabricacao IS NULL OR p.codigoFabricacao LIKE %:codigoFabricacao%) AND " +
           "(:kmRodadosMin IS NULL OR p.kmRodados >= :kmRodadosMin) AND " +
           "(:kmRodadosMax IS NULL OR p.kmRodados <= :kmRodadosMax) AND " +
           "(:dataInstalacaoMin IS NULL OR p.dataInstalacao >= :dataInstalacaoMin) AND " +
           "(:dataInstalacaoMax IS NULL OR p.dataInstalacao <= :dataInstalacaoMax) AND " +
           "(:dataCompraMin IS NULL OR p.dataCompra >= :dataCompraMin) AND " +
           "(:dataCompraMax IS NULL OR p.dataCompra <= :dataCompraMax) AND " +
           "(:dataUltimaReformaMin IS NULL OR p.dataUltimaReforma >= :dataUltimaReformaMin) AND " +
           "(:dataUltimaReformaMax IS NULL OR p.dataUltimaReforma <= :dataUltimaReformaMax)")
    List<Pneu> searchPneu(
        @Param("id") Long id,
        @Param("posicao") PosicaoPneu posicao,
        @Param("status") StatusPneu status,
        @Param("onibusId") Long onibusId,
        @Param("medida") String medida,
        @Param("marca") String marca,
        @Param("numeroSerie") String numeroSerie,
        @Param("codigoFabricacao") String codigoFabricacao,
        @Param("kmRodadosMin") Integer kmRodadosMin,
        @Param("kmRodadosMax") Integer kmRodadosMax,
        @Param("dataInstalacaoMin") LocalDate dataInstalacaoMin,
        @Param("dataInstalacaoMax") LocalDate dataInstalacaoMax,
        @Param("dataCompraMin") LocalDate dataCompraMin,
        @Param("dataCompraMax") LocalDate dataCompraMax,
        @Param("dataUltimaReformaMin") LocalDate dataUltimaReformaMin,
        @Param("dataUltimaReformaMax") LocalDate dataUltimaReformaMax
    );

    // ✅ MÉTODOS ADICIONADOS
    List<Pneu> findByKmRodadosGreaterThanEqual(Integer kmLimite);
    
    @Query("SELECT p FROM Pneu p WHERE p.dataCompra IS NOT NULL AND " +
           "p.dataCompra <= :dataLimite")
    List<Pneu> findPneusGarantiaPrestesVencer(@Param("dataLimite") LocalDate dataLimite);

    // ✅ MÉTODOS DE RELATÓRIO
    @Query("SELECT p.status, COUNT(p) FROM Pneu p GROUP BY p.status")
    List<Object[]> countPneusPorStatus();

    @Query("SELECT p.marca, AVG(p.kmRodados) FROM Pneu p WHERE p.kmRodados > 0 GROUP BY p.marca")
    List<Object[]> avgKmPorMarca();

    @Query("SELECT p.posicao, AVG(p.kmRodados) FROM Pneu p WHERE " +
           "p.dataInstalacao >= :startDate AND p.dataInstalacao <= :endDate " +
           "GROUP BY p.posicao")
    List<Object[]> avgKmPorPosicaoNoPeriodo(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate dataLimite);

    @Query("SELECT p.marca, AVG(p.kmRodados) FROM Pneu p WHERE " +
           "p.dataInstalacao >= :startDate AND p.dataInstalacao <= :endDate " +
           "GROUP BY p.marca")
    List<Object[]> avgKmPorMarcaNoPeriodo(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}