package com.proj_db.onibus.dto;

import java.time.LocalDate;

import com.proj_db.onibus.model.Motor;

import lombok.Data;

/**
 * DTO para enviar informações detalhadas de um Motor de volta ao cliente.
 * Inclui campos da entidade e dados calculados por métodos.
 */
@Data
public class MotorResponseDTO {
    // Atributos principais
    private Long id;
    private String marca;
    private String modelo;
    private Motor.TipoMotor tipo;
    private Motor.StatusMotor status;
    private Integer potencia;
    private Double quantidadeOleo;
    private Double capacidadeOleo;
    private OnibusSummaryDTO onibus;

    // Atributos detalhados
    private Integer anoFabricacao;
    private Integer cilindrada;
    private String codigoFabricacao;
    private LocalDate dataCompra;
    private String numeroSerie;
    private Integer periodoGarantiaMeses;
    private String tipoOleo;

    // Dados calculados para a view de detalhes
    private LocalDate dataUltimaManutencao;
    private LocalDate dataUltimaRevisao;
    private Long diasRestantesGarantia;
    private Long diasRestantesManutencao;
    private Long diasRestantesRevisao;

    public MotorResponseDTO(Motor motor) {
        // Mapeamento dos atributos principais
        this.id = motor.getId();
        this.marca = motor.getMarca();
        this.modelo = motor.getModelo();
        this.tipo = motor.getTipo();
        this.status = motor.getStatus();
        this.potencia = motor.getPotencia();
        this.quantidadeOleo = motor.getQuantidadeOleo();
        this.capacidadeOleo = motor.getCapacidadeOleo();
        this.onibus = (motor.getOnibus() != null) ? new OnibusSummaryDTO(motor.getOnibus()) : null;

        // Mapeamento dos atributos detalhados
        this.anoFabricacao = motor.getAnoFabricacao();
        this.cilindrada = motor.getCilindrada();
        this.codigoFabricacao = motor.getCodigoFabricacao();
        this.dataCompra = motor.getDataCompra();
        this.numeroSerie = motor.getNumeroSerie();
        this.periodoGarantiaMeses = motor.getPeriodoGarantiaMeses();
        this.tipoOleo = motor.getTipoOleo();

        // Mapeamento dos dados calculados
        this.dataUltimaManutencao = motor.getDataUltimaManutencao();
        this.dataUltimaRevisao = motor.getDataUltimaRevisao();
        this.diasRestantesGarantia = motor.getDiasRestantesGarantia();
        this.diasRestantesManutencao = motor.getDiasRestantesManutencao();
        this.diasRestantesRevisao = motor.getDiasRestantesRevisao();
    }
}
