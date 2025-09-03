package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.Cambio;

import lombok.Data;

@Data
public class CambioResponseDTO {
    private Long id;
    private String marca;
    private String modelo;
    private Cambio.TipoCambio tipo;
    private Cambio.StatusCambio status;
    private Integer numeroMarchas;
    private String tipoFluido;
    private Double quantidadeFluido;
    private Double capacidadeFluido;
    private Long onibusId;

    public CambioResponseDTO(Cambio cambio) {
        this.id = cambio.getId();
        this.marca = cambio.getMarca();
        this.modelo = cambio.getModelo();
        this.tipo = cambio.getTipo();
        this.status = cambio.getStatus();
        this.numeroMarchas = cambio.getNumeroMarchas();
        this.tipoFluido = cambio.getTipoFluido();
        this.quantidadeFluido = cambio.getQuantidadeFluido();
        this.capacidadeFluido = cambio.getCapacidadeFluido();
        this.onibusId = (cambio.getOnibus() != null) ? cambio.getOnibus().getId() : null;
    }
}