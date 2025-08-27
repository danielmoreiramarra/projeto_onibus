package com.proj_db.onibus.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.model.Motor.StatusMotor;

public interface MotorRepository extends JpaRepository<Motor, Long> {
    
    // Buscar por número de série
    Optional<Motor> findByNumeroSerie(String numeroSerie);
    
    // Buscar por código de fabricação
    Optional<Motor> findByCodigoFabricacao(String codigoFabricacao);
    
    // Buscar por marca
    List<Motor> findByMarca(String marca);
    
    // Buscar por modelo
    List<Motor> findByModelo(String modelo);
    
    // Buscar por tipo
    List<Motor> findByTipo(Motor.TipoMotor tipo);
    
    // Buscar por status
    List<Motor> findByStatus(StatusMotor status);
    
    // Buscar por potência mínima
    List<Motor> findByPotenciaGreaterThanEqual(Integer potenciaMinima);
    
    // Buscar por potência máxima
    List<Motor> findByPotenciaLessThanEqual(Integer potenciaMaxima);
    
    // Verificar se número de série existe
    boolean existsByNumeroSerie(String numeroSerie);
    
    // Verificar se código de fabricação existe
    boolean existsByCodigoFabricacao(String codigoFabricacao);
    
    // Buscar motores disponíveis (não instalados)
    @Query("SELECT m FROM Motor m WHERE m.onibus IS NULL AND m.status = 'DISPONIVEL'")
    List<Motor> findMotoresDisponiveis();
    
    // Buscar motores novos
    @Query("SELECT m FROM Motor m WHERE m.onibus IS NULL AND m.status = 'NOVO'")
    List<Motor> findMotoresNovos();

    // Buscar motores em uso
    @Query("SELECT m FROM Motor m WHERE m.onibus IS NOT NULL AND m.status = 'EM_USO'")
    List<Motor> findMotoresEmUso();
    
    // Buscar motores que precisam de revisão
    @Query("SELECT m FROM Motor m WHERE m.dataUltimaRevisao IS NULL OR " +
           "m.dataUltimaRevisao <= :dataLimite")
    List<Motor> findMotoresPrecisandoRevisao(@Param("dataLimite") LocalDate dataLimite);
    
    // Buscar motores com garantia prestes a vencer
    @Query("SELECT m FROM Motor m WHERE m.dataCompra IS NOT NULL AND " +
           "m.dataCompra <= :dataLimite")
    List<Motor> findMotoresGarantiaPrestesVencer(@Param("dataLimite") LocalDate dataLimite);
    
    // Buscar motores por ônibus específico
    @Query("SELECT c FROM Motor c WHERE c.onibus.id = :onibusId")
    Optional<Motor> findByOnibusId(@Param("onibusId") Long onibusId);
    
    // Contar motores por tipo
    @Query("SELECT m.tipo, COUNT(m) FROM Motor m GROUP BY m.tipo")
    List<Object[]> countMotoresPorTipo();
    
    // Contar motores por status
    @Query("SELECT m.status, COUNT(m) FROM Motor m GROUP BY m.status")
    List<Object[]> countMotoresPorStatus();
    
    // Calcular potência média por marca
    @Query("SELECT m.marca, AVG(m.potencia) FROM Motor m GROUP BY m.marca")
    List<Object[]> avgPotenciaPorMarca();
    
}