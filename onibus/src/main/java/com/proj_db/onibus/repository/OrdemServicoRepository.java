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
    
    // Buscar por número da OS
    Optional<OrdemServico> findByNumeroOS(String numeroOS);
    
    // Buscar por tipo
    List<OrdemServico> findByTipo(TipoOrdemServico tipo);
    
    // Buscar por status
    List<OrdemServico> findByStatus(StatusOrdemServico status);
    
    // Buscar por ônibus
    List<OrdemServico> findByOnibusId(Long onibusId);
    
    // Buscar por data de abertura
    List<OrdemServico> findByDataAbertura(LocalDate dataAbertura);
    
    // Buscar por data de abertura entre datas
    List<OrdemServico> findByDataAberturaBetween(LocalDate startDate, LocalDate endDate);
    
    // Buscar por data de conclusão
    List<OrdemServico> findByDataConclusao(LocalDate dataConclusao);
    
    // Verificar se número da OS existe
    boolean existsByNumeroOS(String numeroOS);
    
    // Buscar OSs em aberto
    @Query("SELECT os FROM OrdemServico os WHERE os.status = 'ABERTA'")
    List<OrdemServico> findOrdensEmAberto();
    
    // Buscar OSs em execução
    @Query("SELECT os FROM OrdemServico os WHERE os.status = 'EM_EXECUCAO'")
    List<OrdemServico> findOrdensEmExecucao();
    
    // Buscar OSs finalizadas no período
    @Query("SELECT os FROM OrdemServico os WHERE os.status = 'FINALIZADA' AND os.dataConclusao BETWEEN :startDate AND :endDate")
    List<OrdemServico> findOrdensFinalizadasNoPeriodo(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
    
    // Buscar OSs canceladas no período
    @Query("SELECT os FROM OrdemServico os WHERE os.status = 'CANCELADA' AND os.dataCancelamento BETWEEN :startDate AND :endDate")
    List<OrdemServico> findOrdensCanceladasNoPeriodo(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
    
    // Buscar OSs por valor total mínimo
    @Query("SELECT os FROM OrdemServico os WHERE os.valorTotal >= :valorMinimo")
    List<OrdemServico> findByValorTotalMinimo(@Param("valorMinimo") Double valorMinimo);
    
    // Buscar OSs que utilizam um produto específico
    @Query("SELECT os FROM OrdemServico os WHERE os.id IN (" +
           "SELECT ios.ordemServico.id FROM ItemOrdemServico ios WHERE ios.produto.id = :produtoId)")
    List<OrdemServico> findByProdutoId(@Param("produtoId") Long produtoId);
    
    // Buscar OSs com previsão de conclusão vencida
    @Query("SELECT os FROM OrdemServico os WHERE os.dataPrevisaoConclusao < CURRENT_DATE AND os.status IN ('ABERTA', 'EM_EXECUCAO')")
    List<OrdemServico> findOrdensComPrevisaoVencida();
    
    // Contar OSs por status
    @Query("SELECT os.status, COUNT(os) FROM OrdemServico os GROUP BY os.status")
    List<Object[]> countOrdensPorStatus();
    
    // Contar OSs por tipo
    @Query("SELECT os.tipo, COUNT(os) FROM OrdemServico os GROUP BY os.tipo")
    List<Object[]> countOrdensPorTipo();
    
    // Calcular valor total de OSs finalizadas no período
    @Query("SELECT COALESCE(SUM(os.valorTotal), 0) FROM OrdemServico os WHERE os.status = 'FINALIZADA' AND os.dataConclusao BETWEEN :startDate AND :endDate")
    Double calcularValorTotalFinalizadoNoPeriodo(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
    
    // Buscar próxima número de OS disponível
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(os.numeroOS, 4) AS INTEGER)), 0) + 1 FROM OrdemServico os WHERE os.numeroOS LIKE 'OS-%'")
    Integer findProximoNumeroOS();
    
}