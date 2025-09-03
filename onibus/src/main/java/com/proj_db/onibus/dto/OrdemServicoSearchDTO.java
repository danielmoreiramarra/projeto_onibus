package com.proj_db.onibus.dto;

import java.time.LocalDate;

import com.proj_db.onibus.model.OrdemServico;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para encapsular todos os possíveis critérios de busca para uma Ordem de Serviço.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoSearchDTO {

    private String numeroOS;
    private OrdemServico.TipoOrdemServico tipo;
    private OrdemServico.StatusOrdemServico status;
    private Long onibusId;
    private Long motorId;
    private Long cambioId;
    private Long pneuId;
    private Long produtoId; // Para buscar OS que contenham um produto específico
    private LocalDate dataAberturaInicio;
    private LocalDate dataAberturaFim;
    
}