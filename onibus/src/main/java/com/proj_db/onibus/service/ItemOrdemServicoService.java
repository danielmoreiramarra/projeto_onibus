package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.model.ItemOrdemServico;

public interface ItemOrdemServicoService {
    
    ItemOrdemServico criarItem(ItemOrdemServico item);
    ItemOrdemServico atualizarItem(Long id, ItemOrdemServico itemAtualizado);
    void excluirItem(Long id);
    
    ItemOrdemServico buscarPorId(Long id);
    List<ItemOrdemServico> buscarPorOrdemServico(Long ordemServicoId);
    List<ItemOrdemServico> buscarPorProduto(Long produtoId);
    Optional<ItemOrdemServico> buscarPorOrdemServicoEProduto(Long ordemServicoId, Long produtoId);
    
    Integer calcularQuantidadeTotalConsumida(Long produtoId);
    Integer calcularQuantidadeTotalReservada(Long produtoId);
    
    List<Object[]> buscarProdutosMaisUtilizados();
    List<Object[]> buscarHistoricoConsumo(Long produtoId);
    
    Double calcularValorTotalItens(Long ordemServicoId);
    Integer calcularQuantidadeTotalPorProdutoNaOS(Long ordemServicoId, Long produtoId);
    boolean produtoEstaNaOrdemServico(Long ordemServicoId, Long produtoId);
    ItemOrdemServico atualizarQuantidadeItem(Long ordemServicoId, Long produtoId, Integer novaQuantidade);
}

/*
package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.model.ItemOrdemServico;
import com.proj_db.onibus.model.dto.ItemOrdemServicoDTO;

public interface ItemOrdemServicoService {
    
    ItemOrdemServico criarItem(ItemOrdemServico item);
    ItemOrdemServico atualizarItem(Long id, ItemOrdemServico itemAtualizado);
    void excluirItem(Long id);
    
    // ✅ MÉTODOS DE CONSULTA ADAPTADOS PARA RETORNAR DTOs
    ItemOrdemServicoDTO buscarPorId(Long id);
    List<ItemOrdemServicoDTO> buscarPorOrdemServico(Long ordemServicoId);
    List<ItemOrdemServicoDTO> buscarPorProduto(Long produtoId);
    Optional<ItemOrdemServicoDTO> buscarPorOrdemServicoEProduto(Long ordemServicoId, Long produtoId);
    
    Integer calcularQuantidadeTotalConsumida(Long produtoId);
    Integer calcularQuantidadeTotalReservada(Long produtoId);
    
    List<Object[]> buscarProdutosMaisUtilizados();
    List<Object[]> buscarHistoricoConsumo(Long produtoId);
    
    Double calcularValorTotalItens(Long ordemServicoId);
    Integer calcularQuantidadeTotalPorProdutoNaOS(Long ordemServicoId, Long produtoId);
    boolean produtoEstaNaOrdemServico(Long ordemServicoId, Long produtoId);
    ItemOrdemServico atualizarQuantidadeItem(Long ordemServicoId, Long produtoId, Integer novaQuantidade);
}
*/