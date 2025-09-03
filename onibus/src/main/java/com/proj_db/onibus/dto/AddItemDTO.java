package com.proj_db.onibus.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddItemDTO(
    @NotNull Long produtoId,
    @NotNull @Positive Double quantidade,
    String descricao
) {}