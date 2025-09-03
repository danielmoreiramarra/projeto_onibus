package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.Produto;

import lombok.Data;

/**
 * DTO para enviar informações de um Produto de volta ao cliente.
 */
@Data
public class ProdutoResponseDTO {
    private Long id;
    private String nome;
    private String marca;
    private String codigoInterno;
    private Double precoUnitarioAtual; // O preço mais recente do histórico
    private Produto.Categoria categoria;
    private Produto.StatusProduto status;
    private Produto.UnidadeMedida unidadeMedida;

    public ProdutoResponseDTO(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.marca = produto.getMarca();
        this.codigoInterno = produto.getCodigoInterno();
        this.precoUnitarioAtual = produto.getPrecoUnitarioAtual();
        this.categoria = produto.getCategoria();
        this.status = produto.getStatus();
        this.unidadeMedida = produto.getUnidadeMedida();
    }
}