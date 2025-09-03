package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.Cambio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CambioUpdateDTO {
    @NotBlank String marca;
    @NotBlank String modelo;
    @NotNull Cambio.TipoCambio tipo;
    @NotNull @Positive Integer numeroMarchas;
    @NotNull String tipoFluido;
    @NotNull @Positive Double capacidadeFluido;
    @NotBlank String codigoFabricacao;
    @NotNull Integer anoFabricacao;
    @NotNull @Positive Integer periodoGarantiaMeses;
    @NotBlank String numeroSerie;
}