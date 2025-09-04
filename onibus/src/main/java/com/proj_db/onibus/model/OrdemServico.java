package com.proj_db.onibus.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "ordens_servico")
public class OrdemServico {

    // --- ATRIBUTOS ---
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

    @Column(name = "descricao", length = 1000)
    private String descricao;

    @Column(name = "data_abertura", nullable = false, updatable = false)
    @NotNull
    private LocalDate dataAbertura;

    @Column(name = "data_previsao_inicio", nullable = false)
    @NotNull
    private LocalDate dataPrevisaoInicio;

    @Column(name = "data_previsao_conclusao")
    private LocalDate dataPrevisaoConclusao;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Column(name = "data_cancelamento")
    private LocalDate dataCancelamento;

    // <<< REMOVIDO: valorTotal será calculado dinamicamente

    // --- RELACIONAMENTOS COM OS ALVOS (APENAS UM PODE SER PREENCHIDO) ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onibus_id")
    private Onibus onibus;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motor_id")
    private Motor motor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cambio_id")
    private Cambio cambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pneu_id")
    private Pneu pneu;

    // --- RELACIONAMENTO COM OS ITENS ---
    @JsonManagedReference
    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemOrdemServico> itens = new ArrayList<>();
    

    // --- ENUMS ---
    public enum TipoOrdemServico { CORRETIVA, PREVENTIVA }
    public enum StatusOrdemServico { ABERTA, EM_EXECUCAO, FINALIZADA, CANCELADA }


    // --- CONSTRUTOR ---
    public OrdemServico(String numeroOS, TipoOrdemServico tipo, String descricao, LocalDate dataPrevisaoInicio, LocalDate dataPrevisaoConclusao) {
        this.numeroOS = numeroOS;
        this.tipo = tipo;
        this.descricao = descricao;
        this.dataPrevisaoInicio = (dataPrevisaoInicio == null) ? LocalDate.now() : dataPrevisaoInicio;
        this.dataPrevisaoConclusao = dataPrevisaoConclusao;
        this.dataAbertura = LocalDate.now();
        this.status = StatusOrdemServico.ABERTA;
    }


    // --- MÉTODOS DE LÓGICA DE NEGÓCIO ---

    /*
     * Adiciona um novo item à Ordem de Serviço.
     * Só é permitido se a OS estiver com status ABERTA.
     */
    public void adicionarItem(Produto produto, Double quantidade, String descricaoItem) {
        if (this.status != StatusOrdemServico.ABERTA) {
            throw new IllegalStateException("Itens só podem ser adicionados a uma Ordem de Serviço com status ABERTA.");
        }
        // Validação para OS Preventiva
        if (this.tipo == TipoOrdemServico.PREVENTIVA && !produto.isProdutoParaPreventiva()) {
            throw new IllegalArgumentException("Ordens de Serviço Preventivas só podem adicionar produtos da categoria FLUIDO medidos em LITRO.");
        }
        
        ItemOrdemServico novoItem = new ItemOrdemServico(this, produto, quantidade, descricaoItem);
        this.itens.add(novoItem);
    }
    
    /*
     * Inicia a execução da Ordem de Serviço.
     */
    public void iniciarExecucao() {
        if (this.status != StatusOrdemServico.ABERTA) {
            throw new IllegalStateException("Apenas Ordens de Serviço ABERTAS podem ser iniciadas.");
        }
        if (this.itens.isEmpty()) {
            throw new IllegalStateException("Não é possível iniciar uma OS sem itens.");
        }
        this.status = StatusOrdemServico.EM_EXECUCAO;
    }

    /*
     * Finaliza a Ordem de Serviço.
     */
    public void finalizar() {
        if (this.status != StatusOrdemServico.EM_EXECUCAO) {
            throw new IllegalStateException("Apenas Ordens de Serviço EM EXECUÇÃO podem ser finalizadas.");
        }
        this.status = StatusOrdemServico.FINALIZADA;
        this.dataConclusao = LocalDate.now();
    }

    /*
     * Cancela a Ordem de Serviço.
     */
    public void cancelar() {
        if (this.status == StatusOrdemServico.FINALIZADA) {
            throw new IllegalStateException("Ordens de Serviço FINALIZADAS não podem ser canceladas.");
        }
        this.status = StatusOrdemServico.CANCELADA;
        this.dataCancelamento = LocalDate.now();
    }


    // --- MÉTODOS DE CONSULTA ---
    
    /*
     * Calcula o valor total da OS somando o subtotal de todos os seus itens.
     * Retorna O valor total.
     */
    @Transient
    public Double getValorTotal() {
        if (this.itens == null) {
            return 0.0;
        }
        return this.itens.stream()
                .mapToDouble(ItemOrdemServico::subtotal)
                .sum();
    }
    
    /*
     * Retorna o alvo principal da OS, seguindo a hierarquia de precedência.
     * Retorna O objeto de domínio que é o alvo (Motor, Cambio, Pneu ou Onibus).
     */
    @Transient
    public Object getAlvo() {
        // A hierarquia que definimos: componente específico tem precedência sobre o geral.
        return Stream.of(motor, cambio, pneu, onibus)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}