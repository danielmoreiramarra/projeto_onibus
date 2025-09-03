package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.Motor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MotorUpdateDTO {
    @NotNull Motor.TipoMotor tipo;
    String tipoOleo;
    @NotNull @Positive Integer potencia;
    @NotBlank String marca;
    @NotBlank String modelo;
    @Positive Integer cilindrada;
    @NotNull Integer anoFabricacao;
    @NotBlank String numeroSerie;
    @NotNull @Positive Integer periodoGarantiaMeses;
    @NotNull @Positive Double capacidadeOleo;
}