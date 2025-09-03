package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.dto.ProdutoCreateDTO;
import com.proj_db.onibus.dto.ProdutoUpdateDTO;
import com.proj_db.onibus.model.Produto;

public interface ProdutoService {
    
    // --- CRUD e Lógica de Negócio ---
    Produto save(ProdutoCreateDTO produto);
    Produto update(Long id, ProdutoUpdateDTO produtoDetails);
    Produto updatePrice(Long id, Double novoPreco);
    void archiveById(Long id); // "Soft delete" - inativa o produto
    
    Optional<Produto> findById(Long id);
    List<Produto> findAll();
    Optional<Produto> findByCodigoInterno(String codigoInterno);
    List<Produto> search(ProdutoSearchDTO criteria);

    String gerarProximoCodigoInterno();

    // --- Métodos de Relatório ---
    List<Produto> findProdutosComEstoqueAbaixoMinimo();
    List<Object[]> findProdutosMaisUtilizados();
    List<Object[]> countByCategoria();
    // ... outros métodos de relatório podem ser adicionados aqui
    
    // DTO para a busca
    record ProdutoSearchDTO(
        String nome,
        String marca,
        String codigoInterno,
        Produto.Categoria categoria,
        Produto.StatusProduto status
    ) {}
}