package com.proj_db.onibus.dto;

import java.time.LocalDate;

import com.proj_db.onibus.model.OrdemServico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para criar uma nova Ordem de Serviço.
 */
@Data
public class OrdemServicoCreateDTO {
    @NotBlank String numeroOS;
    @NotNull OrdemServico.TipoOrdemServico tipo;
    String descricao;
    @NotNull LocalDate dataPrevisaoInicio;
    @NotNull LocalDate dataPrevisaoConclusao;
    // IDs dos alvos. Apenas um deve ser preenchido, a lógica de negócio cuidará da validação.
    private Long onibusId;
    private Long motorId;
    private Long cambioId;
    private Long pneuId;
}