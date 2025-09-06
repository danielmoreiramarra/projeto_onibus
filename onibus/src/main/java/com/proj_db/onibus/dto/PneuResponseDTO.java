package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.Pneu;
import java.time.LocalDate;
import lombok.Data;

/**
 * DTO para enviar informações detalhadas de um Pneu de volta ao cliente.
 */
@Data
public class PneuResponseDTO {
    // Atributos principais
    private Long id;
    private String marca;
    private String modelo;
    private String medida;
    private Pneu.StatusPneu status;
    private Pneu.PosicaoPneu posicao;
    private Double kmRodados;
    private OnibusSummaryDTO onibus;

    // Atributos detalhados
    private Integer anoFabricacao;
    private String codigoFabricacao;
    private String numeroSerie;
    private LocalDate dataCompra;
    private LocalDate dataInstalacao;
    private Integer periodoGarantiaMeses;

    // Dados calculados para a view de detalhes
    private LocalDate dataUltimaManutencao;
    private LocalDate dataUltimaReforma;
    private Long diasRestantesGarantia;
    private Long diasRestantesManutencao;
    private Double kmRestantesManutencao;
    private Long diasRestantesReforma;
    private Double kmRestantesReforma;

    public PneuResponseDTO(Pneu pneu) {
        // Mapeamento dos atributos principais
        this.id = pneu.getId();
        this.marca = pneu.getMarca();
        this.modelo = pneu.getModelo();
        this.medida = pneu.getMedida();
        this.status = pneu.getStatus();
        this.posicao = pneu.getPosicao();
        this.kmRodados = pneu.getKmRodados();
        this.onibus = (pneu.getOnibus() != null) ? new OnibusSummaryDTO(pneu.getOnibus()) : null;

        // Mapeamento dos atributos detalhados
        this.anoFabricacao = pneu.getAnoFabricacao();
        this.codigoFabricacao = pneu.getCodigoFabricacao();
        this.numeroSerie = pneu.getNumeroSerie();
        this.dataCompra = pneu.getDataCompra();
        this.dataInstalacao = pneu.getDataInstalacao();
        this.periodoGarantiaMeses = pneu.getPeriodoGarantiaMeses();

        // Mapeamento dos dados calculados
        this.dataUltimaManutencao = pneu.getDataUltimaManutencao();
        this.dataUltimaReforma = pneu.getDataUltimaReforma();
        this.diasRestantesGarantia = pneu.getDiasRestantesGarantia();
        this.diasRestantesManutencao = pneu.getDiasRestantesManutencao();
        this.kmRestantesManutencao = pneu.getKmRestantesManutencao();
        this.diasRestantesReforma = pneu.getDiasRestantesReforma();
        this.kmRestantesReforma = pneu.getKmRestantesReforma();
    }
}
