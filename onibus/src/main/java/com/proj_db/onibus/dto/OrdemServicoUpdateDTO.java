package com.proj_db.onibus.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrdemServicoUpdateDTO {

    @NotBlank(message = "A descrição não pode estar em branco.")
    private String descricao;

    @NotNull(message = "A data de previsão de início é obrigatória.")
    @FutureOrPresent(message = "A data de previsão de início não pode ser no passado.")
    private LocalDate dataPrevisaoInicio;

    @NotNull(message = "A data de previsão de conclusão é obrigatória.")
    @FutureOrPresent(message = "A data de previsão de conclusão não pode ser no passado.")
    private LocalDate dataPrevisaoConclusao;

}