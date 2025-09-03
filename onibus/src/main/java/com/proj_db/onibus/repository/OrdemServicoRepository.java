package com.proj_db.onibus.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Cambio;
import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.model.OrdemServico.StatusOrdemServico;
import com.proj_db.onibus.model.OrdemServico.TipoOrdemServico;
import com.proj_db.onibus.model.Pneu;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long>, JpaSpecificationExecutor<OrdemServico> {
    Optional<OrdemServico> findByNumeroOS(String numeroOS);
    List<OrdemServico> findByStatus(StatusOrdemServico statusOrdemServico);
    boolean existsByCambioAndStatusAndTipo(Cambio cambio, StatusOrdemServico statusOrdemServico, TipoOrdemServico tipoOrdemServico);
    boolean existsByCambioAndStatusIn(Cambio cambio, List<StatusOrdemServico> statusOrdemServicos);
    boolean existsByMotorAndStatusAndTipo(Motor motor, StatusOrdemServico statusOrdemServico, TipoOrdemServico tipoOrdemServico);
    boolean existsByMotorAndStatusIn(Motor motor, List<StatusOrdemServico> statusOrdemServicos);
    boolean existsByPneuAndStatusIn(Pneu pneu, List<StatusOrdemServico> statusOrdemServicos);
    boolean existsByOnibusAndStatusIn(Onibus onibus, List<StatusOrdemServico> statusOrdemServicos);

    @Query("SELECT o FROM OrdemServico o WHERE o.status = 'ABERTA' AND o.dataPrevisaoConclusao < :hoje")
    List<OrdemServico> findOsAtrasadas(@Param("hoje") LocalDate hoje);
    
    @Query("SELECT o.tipo, COUNT(o) FROM OrdemServico o GROUP BY o.tipo")
    List<Object[]> countByTipo();
    
    @Query("SELECT o.status, COUNT(o) FROM OrdemServico o GROUP BY o.status")
    List<Object[]> countByStatus();

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(os.numeroOS, :prefixLength) AS INTEGER)), 0) FROM OrdemServico os WHERE os.numeroOS LIKE :prefix")
    Integer findMaxNumeroByPrefix(@Param("prefix") String prefix, @Param("prefixLength") int prefixLength);

}