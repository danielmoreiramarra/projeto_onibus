package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.model.Estoque;
import com.proj_db.onibus.model.Produto;

public interface EstoqueService {
    
    // --- Métodos de CRUD e Busca ---
    Optional<Estoque> findById(Long id);
    Optional<Estoque> findByProduto(Produto produto);
    Optional<Estoque> findByProdutoId(Long produtoId);
    List<Estoque> findAll();
    List<Estoque> search(EstoqueSearchDTO criteria);
    
    // --- Ações de Negócio ---
    Estoque adicionar(Long produtoId, Double quantidade);
    boolean reservar(Long produtoId, Double quantidade);
    void confirmarConsumoDeReserva(Long produtoId, Double quantidade);
    void liberarReserva(Long produtoId, Double quantidade);

    // --- Relatórios e Alertas ---
    List<Estoque> findEstoqueAbaixoDoMinimo();
    Double calcularValorTotalInventario();
    List<Object[]> calcularValorTotalPorCategoria();

    // DTO para a busca
    record EstoqueSearchDTO(
        Long produtoId,
        String nomeProduto,
        String marcaProduto,
        Produto.Categoria categoriaProduto,
        String localizacao
    ) {}
}