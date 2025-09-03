package com.proj_db.onibus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OnibusUpdateDTO {
    @NotBlank String placa;
    @NotBlank String modelo;
    @NotBlank String marca;
    @NotNull Integer capacidade;
    @NotBlank String numeroFrota;
    @NotNull Integer anoFabricacao;
}