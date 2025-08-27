package com.proj_db.onibus.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.Onibus.StatusOnibus;

public interface OnibusRepository extends JpaRepository<Onibus, Long> {

    // Métodos derivados (já estavam corretos)
    Optional<Onibus> findByChassi(String chassi);
    Optional<Onibus> findByCodigoFabricacao(String codigoFabricacao);
    Optional<Onibus> findByNumeroFrota(String numeroFrota);
    List<Onibus> findByStatus(StatusOnibus status);
    List<Onibus> findByMarca(String marca);
    List<Onibus> findByModelo(String modelo);
    List<Onibus> findByAnoFabricacao(Integer anoFabricacao);
    List<Onibus> findByCapacidadeGreaterThanEqual(Integer capacidade);
    boolean existsByChassi(String chassi);
    boolean existsByCodigoFabricacao(String codigoFabricacao);
    boolean existsByNumeroFrota(String numeroFrota);
    
    // ✅ Consultas adaptadas para os relacionamentos
    @Query("SELECT o FROM Onibus o WHERE o.motor.id = :motorId")
    Optional<Onibus> findByMotorId(@Param("motorId") Long motorId);
    
    @Query("SELECT o FROM Onibus o WHERE o.cambio.id = :cambioId")
    Optional<Onibus> findByCambioId(@Param("cambioId") Long cambioId);
    
    // Busca um ônibus que possui um pneu específico
    @Query("SELECT o FROM Onibus o JOIN o.pneus p WHERE p.id = :pneuId")
    Optional<Onibus> findByPneuId(@Param("pneuId") Long pneuId);
    
    // Busca um ônibus que possui um pneu específico em uma posição específica
    @Query("SELECT o FROM Onibus o JOIN o.pneus p WHERE p.id = :pneuId AND p.posicao = :posicao")
    Optional<Onibus> findByPneuIdAndPosicao(@Param("pneuId") Long pneuId, @Param("posicao") String posicao);
    
    // Busca todos os ônibus que possuem pneus de uma determinada marca
    @Query("SELECT DISTINCT o FROM Onibus o JOIN o.pneus p WHERE p.marca = :marcaPneu")
    List<Onibus> findByMarcaPneu(@Param("marcaPneu") String marcaPneu);
    
    // Busca todos os ônibus com pneus que precisam de troca por quilometragem
    @Query("SELECT DISTINCT o FROM Onibus o JOIN o.pneus p WHERE p.kmRodados >= :kmLimite")
    List<Onibus> findOnibusComPneusParaTroca(@Param("kmLimite") Integer kmLimite);
    
    // Busca todos os ônibus com pneus com garantia prestes a vencer
    @Query("SELECT DISTINCT o FROM Onibus o JOIN o.pneus p WHERE p.dataInstalacao IS NOT NULL AND p.dataInstalacao <= :dataLimiteGarantia")
    List<Onibus> findOnibusComPneusGarantiaPrestesVencer(@Param("dataLimiteGarantia") LocalDate dataLimiteGarantia);
    
    // Busca todos os ônibus com pneus em manutenção
    @Query("SELECT DISTINCT o FROM Onibus o JOIN o.pneus p WHERE p.status = 'EM_MANUTENCAO'")
    List<Onibus> findOnibusComPneusEmManutencao();

    // ✅ Consultas de estatísticas (sem duplicações)
    @Query("SELECT o.status, COUNT(o) FROM Onibus o GROUP BY o.status")
    List<Object[]> countOnibusByStatus();
    
    @Query("SELECT o.marca, COUNT(o) FROM Onibus o GROUP BY o.marca")
    List<Object[]> countOnibusByMarca();
    
    @Query("SELECT o.anoFabricacao, COUNT(o) FROM Onibus o GROUP BY o.anoFabricacao")
    List<Object[]> countOnibusByAnoFabricacao();
}