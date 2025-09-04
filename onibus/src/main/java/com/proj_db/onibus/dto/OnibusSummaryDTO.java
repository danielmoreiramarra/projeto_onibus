package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.Onibus;

import lombok.Data;

/**
 * DTO com um resumo das informações de um Ônibus, para ser aninhado em outros DTOs.
 */
@Data
public class OnibusSummaryDTO {
    private Long id;
    private String placa;
    private String numeroFrota;
    private String modelo;
    private Onibus.StatusOnibus status;

    public OnibusSummaryDTO(Onibus onibus) {
        this.id = onibus.getId();
        this.placa = onibus.getPlaca();
        this.numeroFrota = onibus.getNumeroFrota();
        this.modelo = onibus.getModelo();
        this.status = onibus.getStatus();
    }
}