package com.proj_db.onibus.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.proj_db.onibus.model.Onibus;

import lombok.Data;

/**
 * DTO para enviar informações detalhadas de um Ônibus de volta ao cliente.
 */
@Data
public class OnibusResponseDTO {
    // Atributos principais
    private Long id;
    private String placa;
    private String numeroFrota;
    private String marca;
    private String modelo;
    private Onibus.StatusOnibus status;
    private Double quilometragem;
    
    // Componentes (resumidos)
    private MotorResponseDTO motor;
    private CambioResponseDTO cambio;
    private List<PneuResponseDTO> pneus;

    // Atributos detalhados
    private Integer anoFabricacao;
    private Integer capacidade;
    private String chassi;
    private String codigoFabricacao;
    private LocalDate dataCompra;

    // Dados calculados para a view de detalhes
    private LocalDate dataUltimaManutencao;
    private LocalDate dataUltimaReforma;
    private Long diasRestantesManutencao;
    private Long diasRestantesReforma;

    public OnibusResponseDTO(Onibus onibus) {
        // Mapeamento dos atributos principais
        this.id = onibus.getId();
        this.placa = onibus.getPlaca();
        this.numeroFrota = onibus.getNumeroFrota();
        this.marca = onibus.getMarca();
        this.modelo = onibus.getModelo();
        this.status = onibus.getStatus();
        this.quilometragem = onibus.getQuilometragem();

        // Mapeamento dos componentes (usando seus próprios DTOs)
        this.motor = (onibus.getMotor() != null) ? new MotorResponseDTO(onibus.getMotor()) : null;
        this.cambio = (onibus.getCambio() != null) ? new CambioResponseDTO(onibus.getCambio()) : null;
        this.pneus = onibus.getPneus().stream().map(PneuResponseDTO::new).collect(Collectors.toList());

        // Mapeamento dos atributos detalhados
        this.anoFabricacao = onibus.getAnoFabricacao();
        this.capacidade = onibus.getCapacidade();
        this.chassi = onibus.getChassi();
        this.codigoFabricacao = onibus.getCodigoFabricacao();
        this.dataCompra = onibus.getDataCompra();

        // Mapeamento dos dados calculados
        this.dataUltimaManutencao = onibus.getDataUltimaManutencao();
        this.dataUltimaReforma = onibus.getDataUltimaReforma();
        this.diasRestantesManutencao = onibus.getDiasRestantesManutencao();
        this.diasRestantesReforma = onibus.getDiasRestantesReforma();
    }
}
