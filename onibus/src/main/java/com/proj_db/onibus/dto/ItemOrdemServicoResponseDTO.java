package com.proj_db.onibus.dto;

import java.time.LocalDate;

import com.proj_db.onibus.model.ItemOrdemServico;

import lombok.Data;

/**
 * DTO para exibir um item dentro de uma Ordem de Serviço.
 */
@Data
public class ItemOrdemServicoResponseDTO {
    private Long id;
    private Long produtoId;
    private String produtoNome;
    private Double quantidade;
    private Double precoUnitarioRegistrado; // O preço "congelado"
    private Double subtotal;
    private String descricao;
    private LocalDate dataAdicao;

    public ItemOrdemServicoResponseDTO(ItemOrdemServico item) {
        this.id = item.getId();
        this.produtoId = item.getProduto().getId();
        this.produtoNome = item.getProduto().getNome();
        this.quantidade = item.getQuantidade();
        this.precoUnitarioRegistrado = item.getPrecoUnitarioRegistrado();
        this.subtotal = item.subtotal();
        this.descricao = item.getDescricao();
        this.dataAdicao = item.getDataAdicao();
    }
}