package com.proj_db.onibus.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateItemDTO(
    @NotNull @Positive Double quantidade,
    String descricao
) {}