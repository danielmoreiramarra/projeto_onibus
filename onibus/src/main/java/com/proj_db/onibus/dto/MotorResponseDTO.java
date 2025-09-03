package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.Motor;

import lombok.Data;

@Data
public class MotorResponseDTO {
    private Long id;
    private String marca;
    private String modelo;
    private Motor.TipoMotor tipo;
    private Motor.StatusMotor status;
    private Integer potencia;
    private Double quantidadeOleo;
    private Double capacidadeOleo;
    private Long onibusId;

    public MotorResponseDTO(Motor motor) {
        this.id = motor.getId();
        this.marca = motor.getMarca();
        this.modelo = motor.getModelo();
        this.tipo = motor.getTipo();
        this.status = motor.getStatus();
        this.potencia = motor.getPotencia();
        this.quantidadeOleo = motor.getQuantidadeOleo();
        this.capacidadeOleo = motor.getCapacidadeOleo();
        this.onibusId = (motor.getOnibus() != null) ? motor.getOnibus().getId() : null;
    }
}