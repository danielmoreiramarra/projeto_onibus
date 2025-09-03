package com.proj_db.onibus.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.proj_db.onibus.model.OrdemServico;

import lombok.Data;

/**
 * DTO para enviar os detalhes completos de uma Ordem de Serviço de volta ao cliente.
 */
@Data
public class OrdemServicoResponseDTO {
    private Long id;
    private String numeroOS;
    private OrdemServico.TipoOrdemServico tipo;
    private OrdemServico.StatusOrdemServico status;
    private String descricao;
    private LocalDate dataAbertura;
    private LocalDate dataPrevisaoInicio;
    private LocalDate dataPrevisaoConclusao;
    private LocalDate dataConclusao;
    private Double valorTotal;
    private String alvoDescricao; // Uma descrição simples do alvo (ex: "Ônibus Placa ABC-1234")
    private List<ItemOrdemServicoResponseDTO> itens;

    public OrdemServicoResponseDTO(OrdemServico os) {
        this.id = os.getId();
        this.numeroOS = os.getNumeroOS();
        this.tipo = os.getTipo();
        this.status = os.getStatus();
        this.descricao = os.getDescricao();
        this.dataAbertura = os.getDataAbertura();
        this.dataPrevisaoInicio = os.getDataPrevisaoInicio();
        this.dataPrevisaoConclusao = os.getDataPrevisaoConclusao();
        this.dataConclusao = os.getDataConclusao();
        this.valorTotal = os.getValorTotal();
        this.itens = os.getItens().stream().map(ItemOrdemServicoResponseDTO::new).collect(Collectors.toList());
        
        // Lógica para criar uma descrição simples do alvo
        Object alvo = os.getAlvo();
        if (alvo != null) {
            // Em um caso real, teríamos DTOs para os alvos também, mas para simplificar:
            if (alvo instanceof com.proj_db.onibus.model.Onibus a) this.alvoDescricao = "Ônibus: " + a.getPlaca();
            if (alvo instanceof com.proj_db.onibus.model.Motor a) this.alvoDescricao = "Motor: " + a.getNumeroSerie();
            if (alvo instanceof com.proj_db.onibus.model.Cambio a) this.alvoDescricao = "Câmbio: " + a.getNumeroSerie();
            if (alvo instanceof com.proj_db.onibus.model.Pneu a) this.alvoDescricao = "Pneu: " + a.getNumeroSerie();
        }
    }
}