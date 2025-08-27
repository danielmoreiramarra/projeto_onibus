package com.proj_db.onibus.service;

import java.util.List;
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
        // Validar ordem de serviço e produto
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
        
        // Usar o repositório para verificar se item já existe
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
        ItemOrdemServico itemExistente = buscarPorId(id);
        
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
        ItemOrdemServico item = buscarPorId(id);
        
        OrdemServico os = item.getOrdemServico();
        if (os.getStatus() != OrdemServico.StatusOrdemServico.ABERTA) {
            throw new RuntimeException("Só é possível excluir itens em OSs com status ABERTA");
        }
        
        itemOrdemServicoRepository.deleteById(id);
    }

    @Override
    public ItemOrdemServico buscarPorId(Long id) {
        return itemOrdemServicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item de Ordem de Serviço não encontrado com ID: " + id));
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
        
        ItemOrdemServico item = buscarPorOrdemServicoEProduto(ordemServicoId, produtoId)
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

/*
package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.ItemOrdemServico;
import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.model.dto.ItemOrdemServicoDTO;
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

    // ✅ NOVO MÉTODO AUXILIAR para converter Entidade -> DTO
    private ItemOrdemServicoDTO convertToDTO(ItemOrdemServico item) {
        if (item == null) return null;
        ItemOrdemServicoDTO dto = new ItemOrdemServicoDTO();
        dto.setId(item.getId());
        dto.setOrdemServicoId(item.getOrdemServico().getId());
        dto.setProdutoId(item.getProduto().getId());
        dto.setProdutoNome(item.getProduto().getNome());
        dto.setQuantidade(item.getQuantidade());
        dto.setPrecoUnitario(item.getPrecoUnitario());
        dto.setDescricao(item.getDescricao());
        return dto;
    }
    
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
    public ItemOrdemServicoDTO buscarPorId(Long id) {
        ItemOrdemServico item = itemOrdemServicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item de Ordem de Serviço não encontrado com ID: " + id));
        return convertToDTO(item);
    }

    @Override
    public List<ItemOrdemServicoDTO> buscarPorOrdemServico(Long ordemServicoId) {
        ordemServicoRepository.findById(ordemServicoId)
            .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada com ID: " + ordemServicoId));
        
        return itemOrdemServicoRepository.findByOrdemServicoId(ordemServicoId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<ItemOrdemServicoDTO> buscarPorProduto(Long produtoId) {
        produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));
        
        return itemOrdemServicoRepository.findByProdutoId(produtoId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemOrdemServicoDTO> buscarPorOrdemServicoEProduto(Long ordemServicoId, Long produtoId) {
        ordemServicoRepository.findById(ordemServicoId)
            .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada com ID: " + ordemServicoId));
        
        produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + produtoId));
        
        return itemOrdemServicoRepository.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId)
            .map(this::convertToDTO);
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
    public Integer calcularQuantidadeTotalPorProdutoNaOS(Long ordemServicoId, Long produtoId) {
        return itemOrdemServicoRepository.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId)
            .map(ItemOrdemServico::getQuantidade)
            .orElse(0);
    }

    @Override
    public boolean produtoEstaNaOrdemServico(Long ordemServicoId, Long produtoId) {
        return itemOrdemServicoRepository.findByOrdemServicoIdAndProdutoId(ordemServicoId, produtoId).isPresent();
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

}

*/