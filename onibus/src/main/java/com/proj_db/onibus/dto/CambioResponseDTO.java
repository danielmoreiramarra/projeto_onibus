package com.proj_db.onibus.dto;

import java.time.LocalDate;

import com.proj_db.onibus.model.Cambio;

import lombok.Data;

/**
 * DTO para enviar informações detalhadas de um Câmbio de volta ao cliente.
 */
@Data
public class CambioResponseDTO {
    // Atributos principais
    private Long id;
    private String marca;
    private String modelo;
    private Cambio.TipoCambio tipo;
    private Cambio.StatusCambio status;
    private Integer numeroMarchas;
    private OnibusSummaryDTO onibus;

    // Atributos detalhados
    private Integer anoFabricacao;
    private String codigoFabricacao;
    private LocalDate dataCompra;
    private String numeroSerie;
    private Integer periodoGarantiaMeses;
    private String tipoFluido;
    private Double capacidadeFluido;
    private Double quantidadeFluido;

    // Dados calculados para a view de detalhes
    private LocalDate dataUltimaManutencao;
    private LocalDate dataUltimaRevisao;
    private Long diasRestantesGarantia;
    private Long diasRestantesManutencao;
    private Long diasRestantesRevisao;

    public CambioResponseDTO(Cambio cambio) {
        // Mapeamento dos atributos principais
        this.id = cambio.getId();
        this.marca = cambio.getMarca();
        this.modelo = cambio.getModelo();
        this.tipo = cambio.getTipo();
        this.status = cambio.getStatus();
        this.numeroMarchas = cambio.getNumeroMarchas();
        this.onibus = (cambio.getOnibus() != null) ? new OnibusSummaryDTO(cambio.getOnibus()) : null;

        // Mapeamento dos atributos detalhados
        this.anoFabricacao = cambio.getAnoFabricacao();
        this.codigoFabricacao = cambio.getCodigoFabricacao();
        this.dataCompra = cambio.getDataCompra();
        this.numeroSerie = cambio.getNumeroSerie();
        this.periodoGarantiaMeses = cambio.getPeriodoGarantiaMeses();
        this.tipoFluido = cambio.getTipoFluido();
        this.capacidadeFluido = cambio.getCapacidadeFluido();
        this.quantidadeFluido = cambio.getQuantidadeFluido();

        // Mapeamento dos dados calculados
        this.dataUltimaManutencao = cambio.getDataUltimaManutencao();
        this.dataUltimaRevisao = cambio.getDataUltimaRevisao();
        this.diasRestantesGarantia = cambio.getDiasRestantesGarantia();
        this.diasRestantesManutencao = cambio.getDiasRestantesManutencao();
        this.diasRestantesRevisao = cambio.getDiasRestantesRevisao();
    }
}
