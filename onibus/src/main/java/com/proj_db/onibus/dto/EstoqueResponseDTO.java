package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.Estoque;

import lombok.Data;

/**
 * DTO para enviar informações de um registro de Estoque de volta ao cliente.
 */
@Data
public class EstoqueResponseDTO {
    private Long id;
    private ProdutoResponseDTO produto; // Inclui info do produto associado
    private String localizacaoFisica;
    private Double quantidadeAtual;
    private Double quantidadeReservada;
    private Double quantidadeDisponivel; // Campo calculado

    public EstoqueResponseDTO(Estoque estoque) {
        this.id = estoque.getId();
        this.produto = new ProdutoResponseDTO(estoque.getProduto());
        this.localizacaoFisica = estoque.getLocalizacaoFisica();
        this.quantidadeAtual = estoque.getQuantidadeAtual();
        this.quantidadeReservada = estoque.getQuantidadeReservada();
        this.quantidadeDisponivel = estoque.getQuantidadeDisponivel();
    }
}