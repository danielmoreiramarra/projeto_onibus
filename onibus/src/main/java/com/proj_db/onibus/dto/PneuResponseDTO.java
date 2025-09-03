package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.Pneu;

import lombok.Data;

@Data
public class PneuResponseDTO {
    private Long id;
    private String marca;
    private String modelo;
    private String medida;
    private Pneu.StatusPneu status;
    private Pneu.PosicaoPneu posicao;
    private Double kmRodados;
    private Long onibusId;

    public PneuResponseDTO(Pneu pneu) {
        this.id = pneu.getId();
        this.marca = pneu.getMarca();
        this.modelo = pneu.getModelo();
        this.medida = pneu.getMedida();
        this.status = pneu.getStatus();
        this.posicao = pneu.getPosicao();
        this.kmRodados = pneu.getKmRodados();
        this.onibusId = (pneu.getOnibus() != null) ? pneu.getOnibus().getId() : null;
    }
}