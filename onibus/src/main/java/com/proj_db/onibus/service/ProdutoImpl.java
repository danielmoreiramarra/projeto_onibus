package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.dto.ProdutoCreateDTO;
import com.proj_db.onibus.dto.ProdutoUpdateDTO;
import com.proj_db.onibus.model.Estoque;
import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.repository.EstoqueRepository;
import com.proj_db.onibus.repository.ProdutoRepository;

@Service
@Transactional
public class ProdutoImpl implements ProdutoService {

    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private EstoqueRepository estoqueRepository;

    // --- CRUD e Lógica de Negócio ---

    @Override
    public Produto save(ProdutoCreateDTO dto) {
        // 1. Validações para garantir que os códigos são únicos
        produtoRepository.findByCodigoInterno(dto.getCodigoInterno()).ifPresent(p -> {
            throw new IllegalArgumentException("Já existe um produto com o código interno: " + dto.getCodigoInterno());
        });
        if (dto.getCodigoBarras() != null && !dto.getCodigoBarras().isEmpty()) {
            produtoRepository.findByCodigoBarras(dto.getCodigoBarras()).ifPresent(p -> {
                throw new IllegalArgumentException("Já existe um produto com o código de barras: " + dto.getCodigoBarras());
            });
        }

        // 2. Mapeia o DTO para a Entidade, usando o construtor que definimos
        Produto novoProduto = new Produto(
            dto.getNome(),
            dto.getMarca(),
            dto.getUnidadeMedida(),
            dto.getCodigoInterno(),
            dto.getCategoria()
        );
        // Define os campos restantes
        novoProduto.setDescricao(dto.getDescricao());
        novoProduto.setCodigoBarras(dto.getCodigoBarras());
        novoProduto.setEstoqueMinimo(dto.getEstoqueMinimo());
        
        // 3. Usa o método do modelo para definir o preço inicial e criar o primeiro registro no histórico
        novoProduto.atualizarPreco(dto.getPrecoInicial());

        // 4. Salva o novo produto
        Produto produtoSalvo = produtoRepository.save(novoProduto);
        
        // 5. Cria atomicamente um registro de estoque correspondente
        Estoque novoEstoque = new Estoque();
        novoEstoque.setProduto(produtoSalvo);
        novoEstoque.setLocalizacaoFisica("A DEFINIR"); // Localização padrão
        estoqueRepository.save(novoEstoque);
        
        return produtoSalvo;
    }
    @Override
    public Produto update(Long id, ProdutoUpdateDTO produtoDetails) {
        Produto produtoExistente = findById(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        // Valida código interno se foi alterado
        if (!produtoExistente.getCodigoInterno().equals(produtoDetails.getCodigoInterno())) {
            produtoRepository.findByCodigoInterno(produtoDetails.getCodigoInterno()).ifPresent(outro -> {
                throw new IllegalArgumentException("O código interno '" + produtoDetails.getCodigoInterno() + "' já está em uso.");
            });
        }
        
        // Valida código de barras se foi alterado
        if (produtoDetails.getCodigoBarras() != null && !produtoDetails.getCodigoBarras().isEmpty() && !produtoDetails.getCodigoBarras().equals(produtoExistente.getCodigoBarras())) {
             produtoRepository.findByCodigoBarras(produtoDetails.getCodigoBarras()).ifPresent(outro -> {
                throw new IllegalArgumentException("O código de barras '" + produtoDetails.getCodigoBarras() + "' já está em uso.");
            });
        }

        // Atualiza os atributos do produto
        produtoExistente.setNome(produtoDetails.getNome());
        produtoExistente.setMarca(produtoDetails.getMarca());
        produtoExistente.setDescricao(produtoDetails.getDescricao());
        produtoExistente.setCategoria(produtoDetails.getCategoria());
        produtoExistente.setUnidadeMedida(produtoDetails.getUnidadeMedida());
        produtoExistente.setEstoqueMinimo(produtoDetails.getEstoqueMinimo());
        produtoExistente.setCodigoBarras(produtoDetails.getCodigoBarras());
        
        return produtoRepository.save(produtoExistente);
    }
    
    @Override
    public Produto updatePrice(Long id, Double novoPreco) {
        Produto produto = findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
        // Usa o método do modelo para atualizar o preço, garantindo que o histórico seja criado
        produto.atualizarPreco(novoPreco);
        return produtoRepository.save(produto);
    }

    @Override
    public void archiveById(Long id) {
        Produto produto = findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));

        // Verifica no estoque se o produto pode ser inativado
        Estoque estoque = estoqueRepository.findByProdutoId(id).orElse(null);
        if (estoque != null && (estoque.getQuantidadeAtual() > 0 || estoque.getQuantidadeReservada() > 0)) {
            throw new IllegalStateException("Não é possível inativar um produto com saldo ("+estoque.getQuantidadeAtual()+") ou reservas ("+estoque.getQuantidadeReservada()+") em estoque.");
        }
        
        produto.setStatus(Produto.StatusProduto.INATIVO);
        produtoRepository.save(produto);
    }

    // --- Buscas ---

    @Override
    @Transactional(readOnly = true)
    public Optional<Produto> findById(Long id) {
        return produtoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produto> findAll() {
        return produtoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Produto> findByCodigoInterno(String codigoInterno) {
        return produtoRepository.findByCodigoInterno(codigoInterno);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Produto> search(ProdutoSearchDTO criteria) {
        return produtoRepository.findAll(ProdutoSpecification.searchByCriteria(criteria));
    }

    // --- Lógica de Negócio Adicional ---
    
    @Override
    public String gerarProximoCodigoInterno() {
        Integer proximoNumero = produtoRepository.findProximoCodigoInterno().orElse(1);
        return "PROD-" + String.format("%04d", proximoNumero);
    }

    // --- Métodos de Relatório ---

    @Override
    @Transactional(readOnly = true)
    public List<Produto> findProdutosComEstoqueAbaixoMinimo() {
        return produtoRepository.findProdutosComEstoqueAbaixoMinimo();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> findProdutosMaisUtilizados() {
        return produtoRepository.findProdutosMaisUtilizados();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> countByCategoria() {
        return produtoRepository.countByCategoria();
    }
}