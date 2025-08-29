package com.proj_db.onibus.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.ItemOrdemServico;
import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.repository.ItemOrdemServicoRepository;
import com.proj_db.onibus.repository.OrdemServicoRepository;
import com.proj_db.onibus.repository.ProdutoRepository;

@Service
@Transactional
public class ItemOrdemServicoImpl implements ItemOrdemServicoService {

    @Autowired
    private ItemOrdemServicoRepository itemOrdemServicoRepository;

    @Autowired
    private OrdemServicoRepository ordemServicoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;
    
    @Override
    public ItemOrdemServico criarItem(ItemOrdemServico item) {
        OrdemServico os = ordemServicoRepository.findById(item.getOrdemServico().getId())
            .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada com ID: " + item.getOrdemServico().getId()));
        
        Produto produto = produtoRepository.findById(item.getProduto().getId())
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + item.getProduto().getId()));
        
        if (os.getStatus() != OrdemServico.StatusOrdemServico.ABERTA) {
            throw new RuntimeException("Só é possível adicionar itens em OSs com status ABERTA");
        }
        
        if (item.getQuantidade() == null || item.getQuantidade() <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }
        
        Optional<ItemOrdemServico> itemExistente = itemOrdemServicoRepository
            .findByOrdemServicoIdAndProdutoId(os.getId(), produto.getId());
        
        if (itemExistente.isPresent()) {
            ItemOrdemServico itemAtual = itemExistente.get();
            itemAtual.setQuantidade(itemAtual.getQuantidade() + item.getQuantidade());
            return itemOrdemServicoRepository.save(itemAtual);
        }
        
        if (item.getPrecoUnitario() == null || item.getPrecoUnitario() <= 0) {
            item.setPrecoUnitario(produto.getPrecoUnitario());
        }
        
        item.setOrdemServico(os);
        item.setProduto(produto);
        
        return itemOrdemServicoRepository.save(item);
    }

    @Override
    public ItemOrdemServico atualizarItem(Long id, ItemOrdemServico itemAtualizado) {
        ItemOrdemServico itemExistente = itemOrdemServicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item de Ordem de Serviço não encontrado com ID: " + id));
        
        OrdemServico os = itemExistente.getOrdemServico();
        if (os.getStatus() != OrdemServico.StatusOrdemServico.ABERTA) {
            throw new RuntimeException("Só é possível modificar itens em OSs com status ABERTA");
        }
        
        if (itemAtualizado.getQuantidade() != null && itemAtualizado.getQuantidade() <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }
        
        if (itemAtualizado.getQuantidade() != null) {
            itemExistente.setQuantidade(itemAtualizado.getQuantidade());
        }
        
        if (itemAtualizado.getDescricao() != null) {
            itemExistente.setDescricao(itemAtualizado.getDescricao());
        }
        
        if (itemAtualizado.getPrecoUnitario() != null && itemAtualizado.getPrecoUnitario() > 0) {
            itemExistente.setPrecoUnitario(itemAtualizado.getPrecoUnitario());
        }
        
        return itemOrdemServicoRepository.save(itemExistente);
    }

    @Override
    public void excluirItem(Long id) {
        ItemOrdemServico item = itemOrdemServicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item de Ordem de Serviço não encontrado com ID: " + id));
        
        OrdemServico os = item.getOrdemServico();
        if (os.getStatus() != OrdemServico.StatusOrdemServico.ABERTA) {
            throw new RuntimeException("Só é possível excluir itens em OSs com status ABERTA");
        }
        
        itemOrdemServicoRepository.deleteById(id);
    }

    @Override
    public Optional<ItemOrdemServico> buscarPorId(Long id) {
        return itemOrdemServicoRepository.findById(id);
    }

    @Override
    public List<ItemOrdemServico> buscarPorOrdemServico(Long ordemServicoId) {
        ordemServicoRepository.findById(ordemServicoId)
            .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada com ID: " + ordemServicoId));
        
        return itemOrdemServicoRepository.findByOrdemServicoId(ordemServicoId);
    }

    @Override
    public List<ItemOrdemServico> buscarPorProduto(Long produtoId) {
        produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));
        
        return itemOrdemServicoRepository.findByProdutoId(produtoId);
    }

    // ✅ IMPLEMENTAÇÃO DO NOVO MÉTODO DE BUSCA COMBINADA
    @Override
    public List<ItemOrdemServico> searchItemOrdemServico(Map<String, String> searchTerms) {
        Long itemId = searchTerms.containsKey("itemId") && !searchTerms.get("itemId").isEmpty() ? Long.valueOf(searchTerms.get("itemId")) : null;
        Long ordemServicoId = searchTerms.containsKey("ordemServicoId") && !searchTerms.get("ordemServicoId").isEmpty() ? Long.valueOf(searchTerms.get("ordemServicoId")) : null;
        Long produtoId = searchTerms.containsKey("produtoId") && !searchTerms.get("produtoId").isEmpty() ? Long.valueOf(searchTerms.get("produtoId")) : null;
        Integer quantidade = searchTerms.containsKey("quantidade") && !searchTerms.get("quantidade").isEmpty() ? Integer.valueOf(searchTerms.get("quantidade")) : null;
        Double precoUnitario = searchTerms.containsKey("precoUnitario") && !searchTerms.get("precoUnitario").isEmpty() ? Double.valueOf(searchTerms.get("precoUnitario")) : null;
        String descricao = searchTerms.get("descricao");

        return itemOrdemServicoRepository.searchItemOrdemServico(itemId, ordemServicoId, produtoId, quantidade, precoUnitario, descricao);
    }

    @Override
    public Integer calcularQuantidadeTotalConsumida(Long produtoId) {
        produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));
        
        return itemOrdemServicoRepository.calcularQuantidadeTotalConsumida(produtoId);
    }

    @Override
    public Integer calcularQuantidadeTotalReservada(Long produtoId) {
        produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));
        
        return itemOrdemServicoRepository.calcularQuantidadeTotalReservada(produtoId);
    }

    @Override
    public List<Object[]> buscarProdutosMaisUtilizados() {
        return itemOrdemServicoRepository.findProdutosMaisUtilizados();
    }

    @Override
    public List<Object[]> buscarHistoricoConsumo(Long produtoId) {
        produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));
        
        return itemOrdemServicoRepository.findHistoricoConsumoPorProduto(produtoId);
    }

    @Override
    public Double calcularValorTotalItens(Long ordemServicoId) {
        ordemServicoRepository.findById(ordemServicoId)
            .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada com ID: " + ordemServicoId));
        
        return itemOrdemServicoRepository.calcularValorTotalPorOrdemServico(ordemServicoId)
            .orElse(0.0);
    }

    @Override
    public Optional<ItemOrdemServico> buscarPorOrdemServicoEProduto(Long ordemServicoId, Long produtoId) {
        ordemServicoRepository.findById(ordemServicoId)
            .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada com ID: " + ordemServicoId));
        
        produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));
        
        return itemOrdemServicoRepository.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId);
    }

    @Override
    public ItemOrdemServico atualizarQuantidadeItem(Long ordemServicoId, Long produtoId, Integer novaQuantidade) {
        if (novaQuantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser maior que zero");
        }
        
        ItemOrdemServico item = itemOrdemServicoRepository.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId)
            .orElseThrow(() -> new RuntimeException("Item não encontrado para a OS e produto especificados"));
        
        OrdemServico os = item.getOrdemServico();
        if (os.getStatus() != OrdemServico.StatusOrdemServico.ABERTA) {
            throw new RuntimeException("Só é possível modificar itens em OSs com status ABERTA");
        }
        
        item.setQuantidade(novaQuantidade);
        ItemOrdemServico itemAtualizado = itemOrdemServicoRepository.save(item);
        
        return itemAtualizado;
    }

    @Override
    public boolean produtoEstaNaOrdemServico(Long ordemServicoId, Long produtoId) {
        return itemOrdemServicoRepository.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId).isPresent();
    }

    @Override
    public Integer calcularQuantidadeTotalPorProdutoNaOS(Long ordemServicoId, Long produtoId) {
        return itemOrdemServicoRepository.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId)
            .map(ItemOrdemServico::getQuantidade)
            .orElse(0);
    }
}