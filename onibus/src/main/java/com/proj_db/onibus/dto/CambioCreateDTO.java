package com.proj_db.onibus.dto;

import java.time.LocalDate;

import com.proj_db.onibus.model.Cambio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CambioCreateDTO {
    @NotBlank String marca;
    @NotBlank String modelo;
    @NotNull Cambio.TipoCambio tipo;
    @NotNull @Positive Integer numeroMarchas;
    @NotBlank String codigoFabricacao;
    @NotBlank String numeroSerie;
    @NotNull Integer anoFabricacao;
    @NotNull @Positive Double capacidadeFluido;
    @NotNull String tipoFluido;
    @NotNull LocalDate dataCompra;
    @NotNull @Positive Integer periodoGarantiaMeses;
}