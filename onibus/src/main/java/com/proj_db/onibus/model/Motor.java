package com.proj_db.onibus.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "motores")
public class Motor {

    // --- ATRIBUTOS ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ano_fabricacao", nullable = false)
    @NotNull(message = "Ano de fabricação é obrigatório")
    private Integer anoFabricacao;

    @Column(name = "capacidade_oleo", nullable = false)
    @NotNull(message = "Capacidade de óleo é obrigatória")
    @Positive(message = "Capacidade de óleo deve ser positiva")
    private Double capacidadeOleo;

    @Column(name = "cilindrada")
    @Positive(message = "Cilindrada deve ser positiva")
    private Integer cilindrada;

    @Column(name = "codigo_fabricacao", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Código de fabricação é obrigatório")
    private String codigoFabricacao;

    @Column(name = "data_compra", nullable = false)
    @NotNull(message = "Data de compra é obrigatória")
    private LocalDate dataCompra;

    @ElementCollection
    @CollectionTable(name = "motor_historico_envio_manutencao", joinColumns = @JoinColumn(name = "motor_id"))
    private List<LocalDate> historicoEnvioManutencao = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "motor_historico_envio_revisao", joinColumns = @JoinColumn(name = "motor_id"))
    private List<LocalDate> historicoEnvioRevisao = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "motor_historico_retorno_manutencao", joinColumns = @JoinColumn(name = "motor_id"))
    private List<LocalDate> historicoRetornoManutencao = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "motor_historico_retorno_revisao", joinColumns = @JoinColumn(name = "motor_id"))
    private List<LocalDate> historicoRetornoRevisao = new ArrayList<>();

    // <<< NOVO: Histórico específico para trocas de óleo.
    @ElementCollection
    @CollectionTable(name = "motor_historico_oleo", joinColumns = @JoinColumn(name = "motor_id"))
    private List<RegistroOleo> historicoOleo = new ArrayList<>();

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank(message = "Marca é obrigatória")
    private String marca;

    @Column(name = "modelo", nullable = false, length = 100)
    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;

    @Column(name = "numero_serie", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Número de série é obrigatório")
    private String numeroSerie;
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "onibus_id", referencedColumnName = "id")
    private Onibus onibus;

    @Column(name = "periodo_garantia_meses", nullable = false)
    @NotNull(message = "Período de garantia é obrigatório")
    @Positive(message = "Período de garantia deve ser positivo")
    private Integer periodoGarantiaMeses = 24;

    @Column(name = "potencia", nullable = false)
    @NotNull(message = "Potência é obrigatória")
    @Positive(message = "Potência deve ser positiva")
    private Integer potencia;
    
    @Column(name = "quantidade_oleo", nullable = false)
    @NotNull(message = "Quantidade de óleo é obrigatória")
    @PositiveOrZero(message = "Quantidade de óleo não deve ser negativa")
    private Double quantidadeOleo = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull(message = "Status é obrigatório")
    private StatusMotor status = StatusMotor.NOVO;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    @NotNull(message = "Tipo do motor é obrigatório")
    private TipoMotor tipo;

    @Column(name = "tipo_oleo", length = 50)
    private String tipoOleo;


    // --- CLASSE EMBUTIDA PARA O HISTÓRICO DE ÓLEO ---
    @Embeddable
    @Data
    @NoArgsConstructor
    public static class RegistroOleo {
        @Column(name = "data_troca", nullable = false)
        private LocalDate data;

        @Column(name = "quantidade_movimentada", nullable = false)
        private Double quantidade;
        
        public RegistroOleo(LocalDate data, Double quantidade) {
            this.data = data;
            this.quantidade = quantidade;
        }
    }


    // --- ENUMS ---
    public enum StatusMotor { NOVO, DISPONIVEL, EM_USO, EM_MANUTENCAO, EM_REVISAO, DESCARTADO, VENDIDO }
    public enum TipoMotor { DIESEL, ELETRICO, GASOLINA, HIBRIDO, GNV, BIOCOMBUSTIVEL }


    // --- MÉTODOS DE GESTÃO DE ÓLEO ---

    // <<< NOVO: Método privado para centralizar a adição ao histórico de óleo
    private void registrarTrocaOleo(Double quantidade) {
        this.historicoOleo.add(new RegistroOleo(LocalDate.now(), quantidade));
    }

    public void adicionarOleo(String novoTipoOleo, Double quantidadeAdicionada) {
        // <<< REGRA REFORÇADA: Só permite alteração se estiver em manutenção/revisão
        if (this.status == StatusMotor.EM_MANUTENCAO || this.status == StatusMotor.EM_REVISAO) {
            if (quantidadeAdicionada < 0) {
                throw new IllegalArgumentException("Quantidade de óleo adicionada não pode ser negativa.");
            } else if (this.quantidadeOleo > 0.0 && !this.tipoOleo.equals(novoTipoOleo)) {
                throw new IllegalArgumentException("Óleo adicionado deve ser do mesmo tipo do existente.");
            }
            this.tipoOleo = novoTipoOleo;
            double novaQuantidadeTotal = this.quantidadeOleo + quantidadeAdicionada;
            
            this.quantidadeOleo = Math.min(novaQuantidadeTotal, this.capacidadeOleo);
        } else {
             throw new IllegalStateException("O óleo só pode ser alterado se o motor estiver EM MANUTENÇÃO ou EM REVISÃO.");
        }
    }

    public void completarOleo() {
        if (this.status == StatusMotor.EM_MANUTENCAO || this.status == StatusMotor.EM_REVISAO) {
            if (this.quantidadeOleo < this.capacidadeOleo) {
                double oleoNecessario = this.capacidadeOleo - this.quantidadeOleo;
                adicionarOleo(this.tipoOleo, oleoNecessario);
                if (oleoNecessario >= this.capacidadeOleo*0.5) {
                    registrarTrocaOleo(oleoNecessario); // Só registra como uma nova troca quando completou o óleo em pelo menos metade da capacidade
                }
            }
        } else {
             throw new IllegalStateException("O óleo só pode ser completado se o motor estiver EM MANUTENÇÃO ou EM REVISÃO.");
        }
    }

    public void esgotarOleo() {
        if (this.status == StatusMotor.EM_MANUTENCAO || this.status == StatusMotor.EM_REVISAO) {
            this.quantidadeOleo = 0.0;
        } else {
             throw new IllegalStateException("O óleo só pode ser esgotado se o motor estiver EM MANUTENÇÃO ou EM REVISÃO.");
        }
    }

    public void trocarOleoCompleto(String novoTipoOleo) {
        if (this.status == StatusMotor.EM_MANUTENCAO || this.status == StatusMotor.EM_REVISAO) {
            this.tipoOleo = novoTipoOleo;
            this.quantidadeOleo = this.capacidadeOleo;
            registrarTrocaOleo(this.capacidadeOleo); // <<< REGISTRA O EVENTO
        } else {
             throw new IllegalStateException("O óleo só pode ser trocado se o motor estiver EM MANUTENÇÃO ou EM REVISÃO.");
        }
    }


    // --- MÉTODOS DE CICLO DE VIDA E OPERAÇÃO ---

    public void descartar() {
        // <<< LÓGICA SIMPLIFICADA: Apenas muda o status final. Orquestração via Serviço.
        if (this.status == StatusMotor.DISPONIVEL && this.getQuantidadeOleo() == 0.0) {
            this.status = StatusMotor.DESCARTADO;
        } else {
            throw new IllegalStateException("Para descartar um motor, ele deve estar DISPONÍVEL e com o óleo esgotado via OS de manutenção.");
        }
    }

    public void enviarParaManutencao() {
        if (this.status == StatusMotor.NOVO || this.status == StatusMotor.DISPONIVEL || this.status == StatusMotor.EM_USO) {
            if (this.onibus != null) {
                this.onibus.removerMotor();
            }
            this.status = StatusMotor.EM_MANUTENCAO;
            this.historicoEnvioManutencao.add(LocalDate.now());
        }
    }

    public void enviarParaRevisao() {
        if (this.status == StatusMotor.NOVO || this.status == StatusMotor.DISPONIVEL || this.status == StatusMotor.EM_USO) {
            if (this.onibus != null) {
                this.onibus.removerMotor();
            }
            this.status = StatusMotor.EM_REVISAO;
            this.historicoEnvioRevisao.add(LocalDate.now());
        }
    }
    
    public void instalar(Onibus onibus) {
        if (this.status == StatusMotor.NOVO || this.status == StatusMotor.DISPONIVEL) {
            this.onibus = onibus;
            this.status = StatusMotor.EM_USO;
        } else {
            throw new IllegalStateException("Apenas motores NOVOS ou DISPONÍVEIS podem ser instalados.");
        }
    }

    public void remover(Onibus onibus) {
        if (onibus.getMotor() != this) {
            throw new IllegalStateException("Inconsistência entre motor e ônibus referenciado na remoção.");
        }
        if (this.status == StatusMotor.EM_USO) {
            this.onibus = null;
            this.status = StatusMotor.DISPONIVEL;
        }
    }
    
    public void retornarDaManutencao() {
        if (this.status == StatusMotor.EM_MANUTENCAO) {
            this.status = StatusMotor.DISPONIVEL;
            this.historicoRetornoManutencao.add(LocalDate.now());
        }
    }

    public void retornarDaRevisao() {
        if (this.status == StatusMotor.EM_REVISAO) {
            this.status = StatusMotor.DISPONIVEL;
            this.historicoRetornoRevisao.add(LocalDate.now());
        }
    }
    
    public void vender() {
        if (this.status == StatusMotor.DESCARTADO) {
            this.status = StatusMotor.VENDIDO;
        }
    }

    // --- MÉTODOS DE CONSULTA ---
    
    @Transient
    public LocalDate getDataUltimaManutencao() {
        if (historicoRetornoManutencao.isEmpty()) return null;
        return historicoRetornoManutencao.get(historicoRetornoManutencao.size() - 1);
    }

    @Transient
    public LocalDate getDataUltimaRevisao() {
        if (historicoRetornoRevisao.isEmpty()) return null;
        return historicoRetornoRevisao.get(historicoRetornoRevisao.size() - 1);
    }

    @Transient
    public LocalDate getDataUltimaTrocaOleo() {
        if (historicoOleo.isEmpty()) return null;
        return historicoOleo.stream()
            .max(Comparator.comparing(RegistroOleo::getData))
            .map(RegistroOleo::getData)
            .orElse(null);
    }
    
    public boolean estaEmGarantia() {
        if (this.status == StatusMotor.VENDIDO || this.status == StatusMotor.DESCARTADO || !historicoRetornoRevisao.isEmpty()) {
            return false;
        }
        LocalDate dataFimGarantia = this.dataCompra.plusMonths(this.periodoGarantiaMeses);
        return !LocalDate.now().isAfter(dataFimGarantia);
    }

    // <<< NOVOS MÉTODOS PARA LÓGICA PREVENTIVA PROATIVA >>>
    public Long getDiasRestantesManutencao() {
        if (status == StatusMotor.DESCARTADO || status == StatusMotor.VENDIDO) return Long.MAX_VALUE;
        
        LocalDate ultimaManutencao = getDataUltimaManutencao();
        LocalDate ultimaRevisao = getDataUltimaRevisao();
        LocalDate dataBase = this.dataCompra;
        if (ultimaManutencao != null) dataBase = ultimaManutencao;
        if (ultimaRevisao != null && ultimaRevisao.isAfter(dataBase)) dataBase = ultimaRevisao;
        
        LocalDate proximaManutencao = dataBase.plusMonths(2);
        return ChronoUnit.DAYS.between(LocalDate.now(), proximaManutencao);
    }

    public Long getDiasRestantesRevisao() {
        if (status == StatusMotor.DESCARTADO || status == StatusMotor.VENDIDO) return Long.MAX_VALUE;
        
        LocalDate dataBaseRevisao = (getDataUltimaRevisao() == null) ? this.dataCompra : getDataUltimaRevisao();
        LocalDate proximaRevisao = dataBaseRevisao.plusMonths(6);
        return ChronoUnit.DAYS.between(LocalDate.now(), proximaRevisao);
    }

    public boolean manutencaoPrestesVencer() {
        Long dias = getDiasRestantesManutencao();
        return dias != null && dias <= 30;
    }

    public boolean precisaManutencao() {
        Long dias = getDiasRestantesManutencao();
        return dias != null && dias <= 0;
    }

    public boolean revisaoPrestesVencer() {
        Long dias = getDiasRestantesRevisao();
        return dias != null && dias <= 30;
    }

    public boolean precisaRevisao() {
        Long dias = getDiasRestantesRevisao();
        return dias != null && dias <= 0;
    }
}