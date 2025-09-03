package com.proj_db.onibus.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.proj_db.onibus.model.Onibus;

import lombok.Data;

@Data
public class OnibusResponseDTO {
    private Long id;
    private String placa;
    private String numeroFrota;
    private String marca;
    private String modelo;
    private Onibus.StatusOnibus status;
    private Double quilometragem;
    private MotorResponseDTO motor;
    private CambioResponseDTO cambio;
    private List<PneuResponseDTO> pneus;

    public OnibusResponseDTO(Onibus onibus) {
        this.id = onibus.getId();
        this.placa = onibus.getPlaca();
        this.numeroFrota = onibus.getNumeroFrota();
        this.marca = onibus.getMarca();
        this.modelo = onibus.getModelo();
        this.status = onibus.getStatus();
        this.quilometragem = onibus.getQuilometragem();
        this.motor = (onibus.getMotor() != null) ? new MotorResponseDTO(onibus.getMotor()) : null;
        this.cambio = (onibus.getCambio() != null) ? new CambioResponseDTO(onibus.getCambio()) : null;
        this.pneus = onibus.getPneus().stream().map(PneuResponseDTO::new).collect(Collectors.toList());
    }
}