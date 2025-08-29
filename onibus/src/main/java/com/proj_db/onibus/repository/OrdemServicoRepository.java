package com.proj_db.onibus.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.model.OrdemServico.StatusOrdemServico;
import com.proj_db.onibus.model.OrdemServico.TipoOrdemServico;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    
    // ✅ CONSULTA COMBINADA: Substitui várias consultas individuais
    @Query("SELECT os FROM OrdemServico os WHERE " +
           "(:osId IS NULL OR os.id = :osId) AND " +
           "(:numeroOS IS NULL OR os.numeroOS LIKE %:numeroOS%) AND " +
           "(:tipo IS NULL OR os.tipo = :tipo) AND " +
           "(:status IS NULL OR os.status = :status) AND " +
           "(:onibusId IS NULL OR os.onibus.id = :onibusId) AND " +
           "(:dataAberturaInicio IS NULL OR os.dataAbertura >= :dataAberturaInicio) AND " +
           "(:dataAberturaFim IS NULL OR os.dataAbertura <= :dataAberturaFim) AND " +
           "(:dataConclusaoInicio IS NULL OR os.dataConclusao >= :dataConclusaoInicio) AND " +
           "(:dataConclusaoFim IS NULL OR os.dataConclusao <= :dataConclusaoFim) AND " +
           "(:valorTotalMin IS NULL OR os.valorTotal >= :valorTotalMin) AND " +
           "(:valorTotalMax IS NULL OR os.valorTotal <= :valorTotalMax) AND " +
           "(:produtoId IS NULL OR os.id IN (" +
           "SELECT ios.ordemServico.id FROM ItemOrdemServico ios WHERE ios.produto.id = :produtoId))")
    List<OrdemServico> searchOrdemServico(
        @Param("osId") Long osId,
        @Param("numeroOS") String numeroOS,
        @Param("tipo") TipoOrdemServico tipo,
        @Param("status") StatusOrdemServico status,
        @Param("onibusId") Long onibusId,
        @Param("dataAberturaInicio") LocalDate dataAberturaInicio,
        @Param("dataAberturaFim") LocalDate dataAberturaFim,
        @Param("dataConclusaoInicio") LocalDate dataConclusaoInicio,
        @Param("dataConclusaoFim") LocalDate dataConclusaoFim,
        @Param("valorTotalMin") Double valorTotalMin,
        @Param("valorTotalMax") Double valorTotalMax,
        @Param("produtoId") Long produtoId
    );
    
    // ✅ MÉTODOS MANTIDOS: Eles têm lógicas de negócio específicas
    Optional<OrdemServico> findByNumeroOS(String numeroOS);
    boolean existsByNumeroOS(String numeroOS);
    List<OrdemServico> findByStatus(StatusOrdemServico status);
    List<OrdemServico> findByTipo(TipoOrdemServico tipo);
    List<OrdemServico> findByOnibusId(Long onibusId);
    List<OrdemServico> findByDataAberturaBetween(LocalDate dataInicio, LocalDate dataFim);

    @Query("SELECT os FROM OrdemServico os WHERE os.dataPrevisaoConclusao < CURRENT_DATE AND os.status IN ('ABERTA', 'EM_EXECUCAO')")
    List<OrdemServico> findOrdensComPrevisaoVencida();

    @Query("SELECT os FROM OrdemServico os WHERE os.status = 'EM_EXECUCAO'")
    List<OrdemServico> findOrdensEmExecucao();

    @Query("SELECT os FROM OrdemServico os WHERE os.status = 'ABERTA'")
    List<OrdemServico> findOrdensEmAberto();
    
    @Query("SELECT os FROM OrdemServico os WHERE os.status = 'FINALIZADA' AND os.dataConclusao BETWEEN :startDate AND :endDate")
    List<OrdemServico> findOrdensFinalizadasNoPeriodo(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT os FROM OrdemServico os WHERE os.status = 'CANCELADA' AND os.dataCancelamento BETWEEN :startDate AND :endDate")
    List<OrdemServico> findOrdensCanceladasNoPeriodo(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    // Métodos de Relatório e Contagem
    @Query("SELECT os.status, COUNT(os) FROM OrdemServico os GROUP BY os.status")
    List<Object[]> countOrdensPorStatus();
    
    @Query("SELECT os.tipo, COUNT(os) FROM OrdemServico os GROUP BY os.tipo")
    List<Object[]> countOrdensPorTipo();
    
    @Query("SELECT COALESCE(SUM(os.valorTotal), 0) FROM OrdemServico os WHERE os.status = 'FINALIZADA' AND os.dataConclusao BETWEEN :startDate AND :endDate")
    Double calcularValorTotalFinalizadoNoPeriodo(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(os.numeroOS, 4) AS INTEGER)), 0) + 1 FROM OrdemServico os WHERE os.numeroOS LIKE 'OS-%'")
    Integer findProximoNumeroOS();
    
}