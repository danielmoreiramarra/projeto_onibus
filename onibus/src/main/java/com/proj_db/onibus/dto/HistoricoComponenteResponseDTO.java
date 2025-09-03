package com.proj_db.onibus.dto;

import com.proj_db.onibus.model.HistoricoComponente;
import com.proj_db.onibus.model.Pneu;
import java.time.LocalDate;
import lombok.Data;

/**
 * DTO para exibir um evento do histórico de componentes de um ônibus.
 */
@Data
public class HistoricoComponenteResponseDTO {
    private Long id;
    private LocalDate dataEvento;
    private HistoricoComponente.TipoEvento tipoEvento;
    private Double quilometragemOnibusNoEvento;
    private String componenteTipo;
    private String componenteDescricao;
    private Pneu.PosicaoPneu posicaoPneu;

    public HistoricoComponenteResponseDTO(HistoricoComponente historico) {
        this.id = historico.getId();
        this.dataEvento = historico.getDataEvento();
        this.tipoEvento = historico.getTipoEvento();
        this.quilometragemOnibusNoEvento = historico.getQuilometragemOnibusNoEvento();
        this.posicaoPneu = historico.getPosicaoPneu();

        // Lógica para identificar e descrever o componente
        if (historico.getMotor() != null) {
            this.componenteTipo = "Motor";
            this.componenteDescricao = historico.getMotor().getMarca() + " " + historico.getMotor().getModelo();
        } else if (historico.getCambio() != null) {
            this.componenteTipo = "Câmbio";
            this.componenteDescricao = historico.getCambio().getMarca() + " " + historico.getCambio().getModelo();
        } else if (historico.getPneu() != null) {
            this.componenteTipo = "Pneu";
            this.componenteDescricao = historico.getPneu().getMarca() + " " + historico.getPneu().getModelo();
        }
    }
}