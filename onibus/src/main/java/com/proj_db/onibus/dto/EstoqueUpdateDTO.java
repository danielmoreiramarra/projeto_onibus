package com.proj_db.onibus.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para atualizar a localização de um item no estoque.
 */
@Data
public class EstoqueUpdateDTO {
    @NotBlank String localizacaoFisica;
}