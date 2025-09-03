package com.proj_db.onibus.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.proj_db.onibus.model.Pneu.PosicaoPneu;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor // Lombok para criar construtor vazio, necessário para JPA
@Table(name = "historico_componentes")
public class HistoricoComponente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "onibus_id", nullable = false)
    private Onibus onibus;

    // Apenas UM dos componentes abaixo será preenchido por evento
    @ManyToOne
    @JoinColumn(name = "motor_id")
    private Motor motor;

    @ManyToOne
    @JoinColumn(name = "cambio_id")
    private Cambio cambio;

    @ManyToOne
    @JoinColumn(name = "pneu_id")
    private Pneu pneu;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false)
    @NotNull
    private TipoEvento tipoEvento;

    @Column(name = "data_evento", nullable = false)
    @NotNull
    private LocalDate dataEvento;

    @Column(name = "quilometragem_onibus_no_evento", nullable = false)
    @NotNull
    private Double quilometragemOnibusNoEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "posicao_pneu") // Preenchido apenas se o evento for de um pneu
    private PosicaoPneu posicaoPneu;

    public enum TipoEvento {
        INSTALADO,
        REMOVIDO
    }

    // Construtor para facilitar a criação dos registros
    public HistoricoComponente(Onibus onibus, Object componente, TipoEvento tipoEvento, PosicaoPneu posicao) {
        this.onibus = onibus;
        this.dataEvento = LocalDate.now();
        this.quilometragemOnibusNoEvento = onibus.getQuilometragem();
        this.tipoEvento = tipoEvento;
        this.posicaoPneu = posicao;

        if (componente instanceof Motor) {
            this.motor = (Motor) componente;
        } else if (componente instanceof Cambio) {
            this.cambio = (Cambio) componente;
        } else if (componente instanceof Pneu) {
            this.pneu = (Pneu) componente;
        }
    }
}