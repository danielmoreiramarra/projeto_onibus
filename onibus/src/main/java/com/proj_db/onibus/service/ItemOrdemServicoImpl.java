package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.ItemOrdemServico;
import com.proj_db.onibus.repository.ItemOrdemServicoRepository;

@Service
@Transactional(readOnly = true) // Padrão para serviços de apenas leitura
public class ItemOrdemServicoImpl implements ItemOrdemServicoService {

    @Autowired
    private ItemOrdemServicoRepository itemRepository;

    @Override
    public Optional<ItemOrdemServico> findById(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public List<ItemOrdemServico> findByOrdemServicoId(Long ordemServicoId) {
        return itemRepository.findByOrdemServicoId(ordemServicoId);
    }

    @Override
    public List<ItemOrdemServico> findByProdutoId(Long produtoId) {
        return itemRepository.findByProdutoId(produtoId);
    }
    
    @Override
    public List<ItemOrdemServico> search(ItemSearchDTO criteria) {
        return itemRepository.findAll(ItemOrdemServicoSpecification.searchByCriteria(criteria));
    }

    // --- Métodos de Relatório ---

    @Override
    public Double sumValorTotalByOrdemServicoId(Long ordemServicoId) {
        return itemRepository.calcularValorTotalPorOrdemServico(ordemServicoId).orElse(0.0);
    }

    @Override
    public List<Object[]> findHistoricoConsumoByProduto(Long produtoId) {
        // Validação pode ser adicionada aqui se necessário
        return itemRepository.findHistoricoConsumoPorProduto(produtoId);
    }
}