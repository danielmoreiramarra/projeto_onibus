package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.model.ItemOrdemServico;

public interface ItemOrdemServicoService {
    
    // --- Métodos de Busca ---
    Optional<ItemOrdemServico> findById(Long id);
    List<ItemOrdemServico> findByOrdemServicoId(Long ordemServicoId);
    List<ItemOrdemServico> findByProdutoId(Long produtoId);
    List<ItemOrdemServico> search(ItemSearchDTO criteria);

    // --- Métodos de Relatório ---
    Double sumValorTotalByOrdemServicoId(Long ordemServicoId);
    List<Object[]> findHistoricoConsumoByProduto(Long produtoId);
    
    // DTO para a busca
    record ItemSearchDTO(
        Long ordemServicoId,
        Long produtoId,
        String descricao
    ) {}
}