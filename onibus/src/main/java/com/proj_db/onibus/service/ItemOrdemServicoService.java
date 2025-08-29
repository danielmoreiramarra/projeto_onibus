package com.proj_db.onibus.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.proj_db.onibus.model.ItemOrdemServico;

public interface ItemOrdemServicoService {
    
    ItemOrdemServico criarItem(ItemOrdemServico item);
    ItemOrdemServico atualizarItem(Long id, ItemOrdemServico itemAtualizado);
    void excluirItem(Long id);
    
    // ✅ TIPO DE RETORNO CORRIGIDO
    Optional<ItemOrdemServico> buscarPorId(Long id);
    List<ItemOrdemServico> buscarPorOrdemServico(Long ordemServicoId);
    List<ItemOrdemServico> buscarPorProduto(Long produtoId);
    Optional<ItemOrdemServico> buscarPorOrdemServicoEProduto(Long ordemServicoId, Long produtoId);
    
    // ✅ NOVO MÉTODO: Busca combinada
    List<ItemOrdemServico> searchItemOrdemServico(Map<String, String> searchTerms);
    
    Integer calcularQuantidadeTotalConsumida(Long produtoId);
    Integer calcularQuantidadeTotalReservada(Long produtoId);
    
    List<Object[]> buscarProdutosMaisUtilizados();
    List<Object[]> buscarHistoricoConsumo(Long produtoId);
    
    Double calcularValorTotalItens(Long ordemServicoId);
    Integer calcularQuantidadeTotalPorProdutoNaOS(Long ordemServicoId, Long produtoId);
    boolean produtoEstaNaOrdemServico(Long ordemServicoId, Long produtoId);
    ItemOrdemServico atualizarQuantidadeItem(Long ordemServicoId, Long produtoId, Integer novaQuantidade);

}