package com.proj_db.onibus.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "ordens_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_os", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Número da OS é obrigatório")
    private String numeroOS;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    @NotNull(message = "Tipo da OS é obrigatório")
    private TipoOrdemServico tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Status da OS é obrigatório")
    private StatusOrdemServico status = StatusOrdemServico.ABERTA;

    @ManyToOne
    @JoinColumn(name = "onibus_id", nullable = false)
    @NotNull(message = "Ônibus é obrigatório")
    private Onibus onibus;

    @Column(name = "descricao", length = 1000)
    private String descricao;

    @Column(name = "data_abertura", nullable = false)
    @NotNull(message = "Data de abertura é obrigatória")
    private LocalDate dataAbertura;

    @Column(name = "data_previsao_conclusao")
    private LocalDate dataPrevisaoConclusao;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Column(name = "data_cancelamento")
    private LocalDate dataCancelamento;

    @Column(name = "valor_total")
    private Double valorTotal = 0.0;

    @JsonManagedReference
    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrdemServico> itens = new ArrayList<>();
    
    // ✅ NOVAS PROPRIEDADES para vincular serviços preventivos
    @Column(name = "servico_motor_oleo")
    private Boolean servicoMotorOleo = false;
    @Column(name = "servico_motor_revisao")
    private Boolean servicoMotorRevisao = false;
    @Column(name = "servico_cambio_oleo")
    private Boolean servicoCambioOleo = false;
    @Column(name = "servico_cambio_revisao")
    private Boolean servicoCambioRevisao = false;
    @Column(name = "servico_pneu_reforma")
    private Boolean servicoPneuReforma = false;
    // Enum para tipo de ordem de serviço
    public enum TipoOrdemServico {
        CORRETIVA,
        PREVENTIVA
    }

    // Enum para status da ordem de serviço
    public enum StatusOrdemServico {
        ABERTA,         // Criada mas não iniciada
        EM_EXECUCAO,    // Em andamento (materiais reservados)
        FINALIZADA,     // Concluída com sucesso
        CANCELADA       // Cancelada (materiais liberados)
    }
}