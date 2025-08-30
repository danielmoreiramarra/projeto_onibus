package com.proj_db.onibus.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.model.Motor.StatusMotor;
import com.proj_db.onibus.model.Motor.TipoMotor;

public interface MotorRepository extends JpaRepository<Motor, Long> {
    
    // ✅ MÉTODOS DE BUSCA INDIVIDUAIS
    Optional<Motor> findByNumeroSerie(String numeroSerie);
    Optional<Motor> findByCodigoFabricacao(String codigoFabricacao);
    List<Motor> findByMarca(String marca);
    List<Motor> findByModelo(String modelo);
    List<Motor> findByTipo(TipoMotor tipo);
    List<Motor> findByStatus(StatusMotor status);
    boolean existsByNumeroSerie(String numeroSerie);
    boolean existsByCodigoFabricacao(String codigoFabricacao);

    @Query("SELECT m FROM Motor m WHERE m.dataCompra IS NOT NULL AND " +
           "m.dataCompra <= :dataLimite")
    List<Motor> findMotoresGarantiaPrestesVencer(@Param("dataLimite") LocalDate dataLimite);

    @Query("SELECT m FROM Motor m WHERE m.dataUltimaRevisao IS NULL OR " +
           "m.dataUltimaRevisao <= :dataLimite")
    List<Motor> findMotoresPrecisandoRevisao(@Param("dataLimite") LocalDate dataLimite);
    
    // ✅ NOVA CONSULTA COMBINADA PARA TODOS OS CAMPOS
    @Query("SELECT m FROM Motor m WHERE " +
           "(:marca IS NULL OR m.marca LIKE %:marca%) AND " +
           "(:numeroSerie IS NULL OR m.numeroSerie LIKE %:numeroSerie%) AND " +
           "(:codigoFabricacao IS NULL OR m.codigoFabricacao LIKE %:codigoFabricacao%) AND " +
           "(:modelo IS NULL OR m.modelo LIKE %:modelo%) AND " +
           "(:tipo IS NULL OR m.tipo = :tipo) AND " +
           "(:status IS NULL OR m.status = :status) AND " +
           "(:potenciaMinima IS NULL OR m.potencia >= :potenciaMinima) AND " +
           "(:potenciaMaxima IS NULL OR m.potencia <= :potenciaMaxima) AND " +
           "(:onibusId IS NULL OR m.onibus.id = :onibusId) AND " +
           "(:cilindrada IS NULL OR m.cilindrada = :cilindrada) AND" + 
           "(:tipoOleo IS NULL OR m.tipoOleo = : tipoOleo)")
    List<Motor> searchMotor(
        @Param("marca") String marca,
        @Param("numeroSerie") String numeroSerie,
        @Param("codigoFabricacao") String codigoFabricacao,
        @Param("modelo") String modelo,
        @Param("tipo") TipoMotor tipo,
        @Param("status") StatusMotor status,
        @Param("potenciaMinima") Integer potenciaMinima,
        @Param("potenciaMaxima") Integer potenciaMaxima,
        @Param("onibusId") Long onibusId,
        @Param("cilindrada") Integer cilindrada,
        @Param("tipoOleo") String tipoOleo
    );
 
    // ✅ MÉTODOS DE RELATÓRIO
    @Query("SELECT m.tipo, COUNT(m) FROM Motor m GROUP BY m.tipo")
    List<Object[]> countMotoresPorTipo();
    
    @Query("SELECT m.status, COUNT(m) FROM Motor m GROUP BY m.status")
    List<Object[]> countMotoresPorStatus();
    
    @Query("SELECT m.marca, AVG(m.potencia) FROM Motor m GROUP BY m.marca")
    List<Object[]> avgPotenciaPorMarca();
}