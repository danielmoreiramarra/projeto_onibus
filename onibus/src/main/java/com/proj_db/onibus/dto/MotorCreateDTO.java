package com.proj_db.onibus.dto;

import java.time.LocalDate;

import com.proj_db.onibus.model.Motor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MotorCreateDTO {
    @NotNull Motor.TipoMotor tipo;
    @NotNull @Positive Double capacidadeOleo;
    String tipoOleo;
    @NotNull @Positive Integer potencia;
    @NotBlank String marca;
    @NotBlank String modelo;
    @NotNull Integer anoFabricacao;
    @NotBlank String codigoFabricacao;
    @NotBlank String numeroSerie;
    @Positive Integer cilindrada;
    @NotNull LocalDate dataCompra;
    @NotNull @Positive Integer periodoGarantiaMeses;
}