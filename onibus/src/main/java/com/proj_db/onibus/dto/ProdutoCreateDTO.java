package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.Produto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO para criar um novo Produto no catálogo.
 */
@Data
public class ProdutoCreateDTO {
    @NotBlank String nome;
    @NotBlank String marca;
    @NotNull Produto.UnidadeMedida unidadeMedida;
    @NotBlank String codigoInterno;
    @NotNull Produto.Categoria categoria;
    @NotNull @Positive Double precoInicial; // O primeiro preço a ser adicionado ao histórico
    String codigoBarras;
    String descricao;
    @NotNull @Positive Integer estoqueMinimo;
}