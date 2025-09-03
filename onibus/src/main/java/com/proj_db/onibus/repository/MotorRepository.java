package com.proj_db.onibus.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <<< NOVO
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.model.Motor.StatusMotor;

// <<< Adicionamos JpaSpecificationExecutor para buscas dinâmicas
public interface MotorRepository extends JpaRepository<Motor, Long>, JpaSpecificationExecutor<Motor> {

    // ✅ MÉTODOS DE BUSCA SIMPLES (continuam úteis)
    Optional<Motor> findByNumeroSerie(String numeroSerie);
    Optional<Motor> findByCodigoFabricacao(String codigoFabricacao);
    List<Motor> findByStatus(StatusMotor status);
    
    // ✅ CONSULTAS DE NEGÓCIO CORRIGIDAS
    
    // Encontra motores cuja garantia termina nos próximos X dias
    @Query("SELECT m FROM Motor m WHERE m.status NOT IN ('VENDIDO', 'DESCARTADO') AND " +
           "FUNCTION('DATE_ADD', m.dataCompra, m.periodoGarantiaMeses, 'MONTH') BETWEEN :hoje AND :dataLimite")
    List<Motor> findMotoresComGarantiaVencendo(@Param("hoje") LocalDate hoje, @Param("dataLimite") LocalDate dataLimite);

    // Encontra motores que precisam de revisão (última revisão há mais de 6 meses ou nunca feita)
    // Esta é uma query complexa que demonstra o poder do JPQL
    @Query("SELECT m FROM Motor m WHERE m.status IN ('EM_USO', 'DISPONIVEL') AND " +
           "( " +
           "    (SELECT MAX(h) FROM m.historicoRetornoRevisao h) IS NULL AND FUNCTION('DATE_ADD', m.dataCompra, 6, 'MONTH') <= :hoje " +
           "    OR " +
           "    (SELECT MAX(h) FROM m.historicoRetornoRevisao h) IS NOT NULL AND FUNCTION('DATE_ADD', (SELECT MAX(h) FROM m.historicoRetornoRevisao h), 6, 'MONTH') <= :hoje " +
           ")")
    List<Motor> findMotoresPrecisandoRevisao(@Param("hoje") LocalDate hoje);

    // ✅ MÉTODOS DE RELATÓRIO (estão perfeitos)
    @Query("SELECT m.tipo, COUNT(m) FROM Motor m GROUP BY m.tipo")
    List<Object[]> countByTipo();
    
    @Query("SELECT m.status, COUNT(m) FROM Motor m GROUP BY m.status")
    List<Object[]> countByStatus();
}