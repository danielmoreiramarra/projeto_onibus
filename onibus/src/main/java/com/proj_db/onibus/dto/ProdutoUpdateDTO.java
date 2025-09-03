package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.Produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO para atualizar as informações de um Produto.
 */
@Data
public class ProdutoUpdateDTO {
    @NotBlank String nome;
    @NotBlank String marca;
    String descricao;
    @NotNull Produto.Categoria categoria;
    @NotNull Produto.UnidadeMedida unidadeMedida;
    @NotNull @Positive Integer estoqueMinimo;
    String localizacao;
    String codigoBarras;
    @NotBlank String codigoInterno;
}