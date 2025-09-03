package com.proj_db.onibus.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OnibusCreateDTO {
    @NotBlank String chassi;
    @NotBlank String placa;
    @NotBlank String modelo;
    @NotBlank String marca;
    @NotBlank String codigoFabricacao;
    @NotNull Integer capacidade;
    @NotNull Integer anoFabricacao;
    @NotBlank String numeroFrota;
    @NotNull LocalDate dataCompra;
}