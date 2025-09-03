package com.proj_db.onibus.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PneuCreateDTO {
    @NotBlank String marca;
    @NotBlank String medida;
    @NotBlank String modelo;
    @NotBlank String codigoFabricacao;
    @NotNull Integer anoFabricacao;
    @NotBlank String numeroSerie;
    @NotNull LocalDate dataCompra;
    @NotNull @Positive Integer periodoGarantiaMeses;
}