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
@Table(name = "cambios")
public class Cambio {

    // --- ATRIBUTOS ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ano_fabricacao", nullable = false)
    @NotNull(message = "Ano de fabricação é obrigatório")
    private Integer anoFabricacao;
    
    @Column(name = "capacidade_fluido", nullable = false)
    @NotNull(message = "Capacidade de fluido é obrigatória")
    @Positive(message = "Capacidade de fluido deve ser positiva")
    private Double capacidadeFluido;

    @Column(name = "codigo_fabricacao", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Código de fabricação é obrigatório")
    private String codigoFabricacao;

    @Column(name = "data_compra", nullable = false)
    @NotNull(message = "Data de compra é obrigatória")
    private LocalDate dataCompra;

    // <<< NOVO: Históricos para rastreabilidade
    @ElementCollection
    @CollectionTable(name = "cambio_historico_envio_manutencao", joinColumns = @JoinColumn(name = "cambio_id"))
    @Column(name = "data_envio")
    private List<LocalDate> historicoEnvioManutencao = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "cambio_historico_envio_revisao", joinColumns = @JoinColumn(name = "cambio_id"))
    @Column(name = "data_envio")
    private List<LocalDate> historicoEnvioRevisao = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "cambio_historico_fluido", joinColumns = @JoinColumn(name = "cambio_id"))
    private List<RegistroFluido> historicoFluido = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "cambio_historico_retorno_manutencao", joinColumns = @JoinColumn(name = "cambio_id"))
    @Column(name = "data_retorno")
    private List<LocalDate> historicoRetornoManutencao = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "cambio_historico_retorno_revisao", joinColumns = @JoinColumn(name = "cambio_id"))
    @Column(name = "data_retorno")
    private List<LocalDate> historicoRetornoRevisao = new ArrayList<>();

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank(message = "Marca é obrigatória")
    private String marca;

    @Column(name = "modelo", nullable = false, length = 100)
    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;

    @Column(name = "numero_marchas", nullable = false)
    @NotNull(message = "Número de marchas é obrigatório")
    @Positive(message = "Número de marchas deve ser positivo")
    private Integer numeroMarchas;

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
    
    // <<< AJUSTE: Adicionadas validações
    @Column(name = "quantidade_fluido", nullable = false)
    @NotNull(message = "Quantidade de fluido é obrigatória")
    @PositiveOrZero(message = "Quantidade de fluido não pode ser negativa")
    private Double quantidadeFluido = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull(message = "Status é obrigatório")
    private StatusCambio status = StatusCambio.NOVO;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    @NotNull(message = "Tipo do câmbio é obrigatório")
    private TipoCambio tipo;

    @Column(name = "tipo_fluido", length = 50)
    private String tipoFluido;


    // --- ENUMS ---
    // <<< AJUSTE: Adicionado status EM_REVISAO para consistência
    public enum StatusCambio {
        NOVO,
        DISPONIVEL,
        EM_USO,
        EM_MANUTENCAO,
        EM_REVISAO, 
        DESCARTADO,
        VENDIDO
    }

    public enum TipoCambio {
        MANUAL,
        AUTOMATICO,
        AUTOMATIZADO,
        CVT,
        SEMI_AUTOMATICO,
        DUALOGIC
    }

    // --- CLASSE EMBUTIDA PARA O HISTÓRICO DE FLUIDO ---
    @Embeddable
    @Data
    @NoArgsConstructor
    public static class RegistroFluido {
        @Column(name = "data_troca", nullable = false)
        private LocalDate data;

        @Column(name = "quantidade_movimentada", nullable = false)
        private Double quantidade;
        
        // Construtor para facilitar
        public RegistroFluido(LocalDate data, Double quantidade) {
            this.data = data;
            this.quantidade = quantidade;
        }
    }

    // --- MÉTODOS DE GESTÃO DE FLUIDO (NOVOS E REFATORADOS) ---

    public void adicionarFluido(String novoTipoFluido, Double quantidadeAdicionada) {
        if (this.status == StatusCambio.EM_MANUTENCAO || this.status == StatusCambio.EM_REVISAO) {
            if (quantidadeAdicionada < 0) {
                throw new IllegalArgumentException("Quantidade de fluido adicionada não pode ser negativa.");
            } else if (this.quantidadeFluido > 0.0 && !this.tipoFluido.equals(novoTipoFluido)) {
                throw new IllegalArgumentException("Quantidade de fluido adicionada deve ser do mesmo tipo da existente.");
            }
            this.tipoFluido = novoTipoFluido;
            double novaQuantidadeTotal = this.quantidadeFluido + quantidadeAdicionada;
            
            if (novaQuantidadeTotal > this.capacidadeFluido) {
                this.quantidadeFluido = this.capacidadeFluido;
            } else {
                this.quantidadeFluido = novaQuantidadeTotal;
            }
        }
    }

    // Este método também registra o complemento no histórico
    public void completarFluido() {
        if (this.status == StatusCambio.EM_MANUTENCAO || this.status == StatusCambio.EM_REVISAO) {
            if (this.quantidadeFluido < this.capacidadeFluido) {
                double fluidoNecessario = this.capacidadeFluido - this.quantidadeFluido;
                adicionarFluido(this.tipoFluido, fluidoNecessario);
                // <<< REGISTRA O EVENTO
                if (fluidoNecessario >= this.capacidadeFluido*0.5) {
                    registrarTrocaFluido(fluidoNecessario); // Só registra como uma nova troca quando completou o fluido em pelo menos metade da capacidade
                }
            }
        }
    }

    public void esgotarFluido() {
        if (this.status == StatusCambio.EM_MANUTENCAO || this.status == StatusCambio.EM_REVISAO) {
            this.quantidadeFluido = 0.0;
        }
    }

    // Este método agora também registra a troca no novo histórico
    public void trocarFluidoCompleto(String novoTipoFluido) {
        if (this.status == StatusCambio.EM_MANUTENCAO || this.status == StatusCambio.EM_REVISAO) {
            this.tipoFluido = novoTipoFluido;
            this.quantidadeFluido = this.capacidadeFluido;
            // <<< REGISTRA O EVENTO
            registrarTrocaFluido(this.capacidadeFluido);
        }
    }

    // <<< NOVO: Método privado para centralizar a adição ao histórico de fluido
    private void registrarTrocaFluido(Double quantidade) {
        this.historicoFluido.add(new RegistroFluido(LocalDate.now(), quantidade));
    }

    @Transient
    public LocalDate getDataUltimaTrocaFluido() {
        if (this.historicoFluido == null || this.historicoFluido.isEmpty()) {
            return null;
        }
        return this.historicoFluido.stream()
            .max(Comparator.comparing(RegistroFluido::getData))
            .map(RegistroFluido::getData)
            .orElse(null);
    }

    // --- MÉTODOS DE CICLO DE VIDA E OPERAÇÃO (REFATORADOS) ---

    public void descartar() {
        // A lógica de esgotar fluido via OS foi movida para o CambioService.
        // Aqui, apenas mudamos o status final, assumindo que os pré-requisitos foram cumpridos.
        if (this.status == StatusCambio.DISPONIVEL && this.getQuantidadeFluido() == 0.0) {
            this.status = StatusCambio.DESCARTADO;
        } else {
            throw new IllegalStateException("Para descartar um câmbio, ele deve estar DISPONÍVEL e com o fluido esgotado via OS de manutenção.");
        }
    }

    public void enviarParaManutencao() {
        if (this.status == StatusCambio.NOVO || this.status == StatusCambio.DISPONIVEL || this.status == StatusCambio.EM_USO) {
            if (this.onibus != null) {
                this.onibus.removerCambio();
            }
            this.status = StatusCambio.EM_MANUTENCAO;
            this.historicoEnvioManutencao.add(LocalDate.now());
        }
    }

    public void enviarParaRevisao() {
        if (this.status == StatusCambio.NOVO || this.status == StatusCambio.DISPONIVEL || this.status == StatusCambio.EM_USO) {
            if (this.onibus != null) {
                this.onibus.removerCambio();
            }
            this.status = StatusCambio.EM_REVISAO;
            this.historicoEnvioRevisao.add(LocalDate.now());
        }
    }

    public void instalar(Onibus onibus) {
        if (this.status == StatusCambio.NOVO || this.status == StatusCambio.DISPONIVEL) {
            if (onibus.getCambio() == null) {
                this.onibus = onibus;
                this.status = StatusCambio.EM_USO;
            } else {
                throw new IllegalStateException("Ônibus indicado já possui um câmbio instalado.");
            }
            completarFluido();
        }
    }

    public void remover(Onibus onibus) {
        if (onibus.getCambio() != this) {
            throw new IllegalStateException("Inconsistência entre câmbio e ônibus referenciado na remoção.");
        }
        if (this.status == StatusCambio.EM_USO) {
            this.onibus = null;
            this.status = StatusCambio.DISPONIVEL;
        }
    }
    
    public void retornarDaManutencao() {
        if (this.status == StatusCambio.EM_MANUTENCAO) {
            this.status = StatusCambio.DISPONIVEL;
            this.historicoRetornoManutencao.add(LocalDate.now());
        }
    }

    public void retornarDaRevisao() {
        if (this.status == StatusCambio.EM_REVISAO) {
            this.status = StatusCambio.DISPONIVEL;
            trocarFluidoCompleto(this.tipoFluido);
            this.historicoRetornoRevisao.add(LocalDate.now());
        }
    }
    
    public void vender() {
        if (this.status == StatusCambio.DESCARTADO) {
            this.status = StatusCambio.VENDIDO;
        }
    }

    // --- MÉTODOS DE CONSULTA (GETTERS, GARANTIA, ETC.) ---

    // <<< AJUSTE: Métodos @Transient para pegar a última data de forma dinâmica
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
    
    public Long getDiasRestantesGarantia() {
        if (!estaEmGarantia()) return 0L;
        LocalDate dataFimGarantia = this.dataCompra.plusMonths(this.periodoGarantiaMeses);
        return ChronoUnit.DAYS.between(LocalDate.now(), dataFimGarantia);
    }
    
    public boolean estaEmGarantia() {
        if (this.status == StatusCambio.VENDIDO || this.status == StatusCambio.DESCARTADO || !historicoRetornoRevisao.isEmpty()) {
            return false;
        }
        LocalDate dataFimGarantia = this.dataCompra.plusMonths(this.periodoGarantiaMeses);
        return LocalDate.now().isBefore(dataFimGarantia) || LocalDate.now().isEqual(dataFimGarantia);
    }

    public boolean garantiaPrestesVencer() {
        if (!estaEmGarantia()) return false;
        LocalDate dataFimGarantia = this.dataCompra.plusMonths(this.periodoGarantiaMeses);
        LocalDate trintaDiasAntes = dataFimGarantia.minusDays(30);
        return LocalDate.now().isAfter(trintaDiasAntes) || LocalDate.now().isEqual(trintaDiasAntes);
    }

    public Long getDiasRestantesManutencao() {
        if (precisaManutencao()) return 0L;
        LocalDate ultimaManutencao = getDataUltimaManutencao();
        LocalDate ultimaRevisao = getDataUltimaRevisao();
        LocalDate dataBase = this.dataCompra;
        if (ultimaManutencao != null) dataBase = ultimaManutencao;
        if (ultimaRevisao != null && ultimaRevisao.isAfter(dataBase)) dataBase = ultimaRevisao;
        LocalDate proximaManutencao = dataBase.plusMonths(2);
        return ChronoUnit.DAYS.between(LocalDate.now(), proximaManutencao);
    }

    public Long getDiasRestantesRevisao() {
        if (precisaRevisao()) return 0L;
        LocalDate dataBaseRevisao = (getDataUltimaRevisao() == null) ? this.dataCompra : getDataUltimaRevisao();
        LocalDate proximaRevisao = dataBaseRevisao.plusMonths(6);
        return ChronoUnit.DAYS.between(LocalDate.now(), proximaRevisao);
    }

    public boolean manutencaoPrestesVencer() {
        return getDiasRestantesManutencao() < 30;
    }

    public boolean precisaManutencao() {
        if (status == StatusCambio.DESCARTADO || status == StatusCambio.VENDIDO) return false;
        LocalDate ultimaManutencao = getDataUltimaManutencao();
        LocalDate ultimaRevisao = getDataUltimaRevisao();
        LocalDate dataBase = this.dataCompra;
        if (ultimaManutencao != null) dataBase = ultimaManutencao;
        if (ultimaRevisao != null && ultimaRevisao.isAfter(dataBase)) dataBase = ultimaRevisao;
        LocalDate proximaManutencao = dataBase.plusMonths(2);
        return !LocalDate.now().isBefore(proximaManutencao);
    }

    public boolean precisaRevisao() {
        if (status == StatusCambio.DESCARTADO || status == StatusCambio.VENDIDO) return false;
        LocalDate dataBaseRevisao = (getDataUltimaRevisao() == null) ? this.dataCompra : getDataUltimaRevisao();
        LocalDate proximaRevisao = dataBaseRevisao.plusMonths(6);
        return !LocalDate.now().isBefore(proximaRevisao);
    }

    public boolean revisaoPrestesVencer() {
        return getDiasRestantesRevisao() < 30;
    }

}
