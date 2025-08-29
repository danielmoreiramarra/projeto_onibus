package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.model.OrdemServico.StatusOrdemServico;
import com.proj_db.onibus.model.OrdemServico.TipoOrdemServico;

public interface OrdemServicoService {
    
    OrdemServico criarOrdemServico(OrdemServico ordemServico);
    OrdemServico atualizarOrdemServico(Long id, OrdemServico ordemServicoAtualizada);
    void excluirOrdemServico(Long id);
    
    OrdemServico buscarPorId(Long id);
    List<OrdemServico> buscarTodas();
    Optional<OrdemServico> buscarPorNumeroOS(String numeroOS);
    
    // ✅ NOVO MÉTODO: Busca combinada para todos os campos
    List<OrdemServico> searchOrdemServico(Map<String, String> searchTerms);
    
    List<OrdemServico> buscarPorStatus(StatusOrdemServico status);
    List<OrdemServico> buscarPorTipo(TipoOrdemServico tipo);
    List<OrdemServico> buscarPorOnibus(Long onibusId);
    List<OrdemServico> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim);
    
    // ✅ Retornam a entidade atualizada para o cliente
    OrdemServico iniciarExecucao(Long ordemServicoId);
    OrdemServico finalizarOrdemServico(Long ordemServicoId);
    OrdemServico cancelarOrdemServico(Long ordemServicoId);
    OrdemServico cancelarOrdemServico(Long ordemServicoId, String motivo);
    
    boolean adicionarItem(Long ordemServicoId, Long produtoId, Integer quantidade);
    boolean removerItem(Long ordemServicoId, Long produtoId);
    
    boolean verificarEstoqueSuficiente(Long ordemServicoId);
    Double calcularValorTotal(Long ordemServicoId);
    
    List<OrdemServico> buscarOrdensEmAberto();
    List<OrdemServico> buscarOrdensEmExecucao();
    List<OrdemServico> buscarOrdensFinalizadasNoPeriodo(LocalDate dataInicio, LocalDate dataFim);
    List<OrdemServico> buscarOrdensComPrevisaoVencida();
    
    String gerarProximoNumeroOS();
    List<Object[]> buscarEstatisticasPorStatus();
    List<Object[]> buscarEstatisticasPorTipo();
    Double calcularFaturamentoPeriodo(LocalDate dataInicio, LocalDate dataFim);
}