package com.proj_db.onibus.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "pneus")
public class Pneu {

    // --- CONSTANTES DE LIMITE DE DESGASTE ---
    private static final double KM_LIMITE_MANUTENCAO = 2500;
    private static final double KM_LIMITE_REFORMA = 10000;
    private static final long DIAS_LIMITE_MANUTENCAO = 60; // 2 meses
    private static final long DIAS_LIMITE_REFORMA = 180; // 6 meses

    // --- ATRIBUTOS ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ano_fabricacao", nullable = false)
    @NotNull
    private Integer anoFabricacao;

    @Column(name = "codigo_fabricacao", unique = true, nullable = false, length = 50)
    @NotBlank
    private String codigoFabricacao;

    @Column(name = "data_compra", nullable = false)
    @NotNull
    private LocalDate dataCompra;

    @Column(name = "data_instalacao")
    private LocalDate dataInstalacao;

    @Column(name = "km_rodados", nullable = false)
    @NotNull
    @PositiveOrZero
    private Double kmRodados = 0.0;

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank
    private String marca;

    @Column(name = "medida", nullable = false, length = 20)
    @NotBlank
    private String medida;

    @Column(name = "modelo", nullable = false, length = 100)
    @NotBlank
    private String modelo;

    @Column(name = "numero_serie", unique = true, nullable = false, length = 100)
    @NotBlank
    private String numeroSerie;

    @Column(name = "periodo_garantia_meses", nullable = false)
    @NotNull
    private Integer periodoGarantiaMeses = 24;
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "onibus_id", referencedColumnName = "id")
    private Onibus onibus;

    @Enumerated(EnumType.STRING)
    @Column(name = "posicao")
    private PosicaoPneu posicao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull
    private StatusPneu status = StatusPneu.NOVO;

    @ElementCollection
    @CollectionTable(name = "pneu_historico_envio_manutencao", joinColumns = @JoinColumn(name = "pneu_id"))
    private List<LocalDate> historicoEnvioManutencao = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "pneu_historico_envio_reforma", joinColumns = @JoinColumn(name = "pneu_id"))
    private List<LocalDate> historicoEnvioReforma = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "pneu_historico_retorno_manutencao", joinColumns = @JoinColumn(name = "pneu_id"))
    private List<LocalDate> historicoRetornoManutencao = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "pneu_historico_retorno_reforma", joinColumns = @JoinColumn(name = "pneu_id"))
    private List<LocalDate> historicoRetornoReforma = new ArrayList<>();

    // --- ENUMS ---
    public enum StatusPneu { NOVO, DISPONIVEL, EM_USO, EM_MANUTENCAO, EM_REFORMA, DESCARTADO, VENDIDO }
    public enum PosicaoPneu { DD, DE, TDE, TDI, TEE, TEI }


    // --- MÉTODOS DE CICLO DE VIDA E OPERAÇÃO ---

    public void descartar() {
        // <<< LÓGICA SIMPLIFICADA: Apenas muda o status. Orquestração via Serviço.
        if (this.status == StatusPneu.DISPONIVEL) {
            this.status = StatusPneu.DESCARTADO;
        } else {
             throw new IllegalStateException("Apenas pneus DISPONÍVEIS podem ser descartados.");
        }
    }

    public void enviarParaManutencao() {
        if (this.status == StatusPneu.NOVO || this.status == StatusPneu.DISPONIVEL || this.status == StatusPneu.EM_USO) {
            if (this.onibus != null) {
                this.onibus.removerPneu(this.getPosicao());
            }
            this.status = StatusPneu.EM_MANUTENCAO;
            this.historicoEnvioManutencao.add(LocalDate.now());
        }
    }

    public void enviarParaReforma() {
        if (this.status == StatusPneu.NOVO || this.status == StatusPneu.DISPONIVEL || this.status == StatusPneu.EM_USO) {
            if (this.onibus != null) {
                this.onibus.removerPneu(this.getPosicao());
            }
            this.status = StatusPneu.EM_REFORMA;
            this.historicoEnvioReforma.add(LocalDate.now());
        }
    }

    public void instalar(Onibus onibus, PosicaoPneu posicao) {
        if (this.status == StatusPneu.NOVO || this.status == StatusPneu.DISPONIVEL) {
            this.onibus = onibus;
            this.posicao = posicao;
            this.status = StatusPneu.EM_USO;
            this.dataInstalacao = LocalDate.now();
        }
    }

    public void remover(Onibus onibus) {
        if (this.onibus == onibus && this.status == StatusPneu.EM_USO) {
            this.onibus = null;
            this.posicao = null;
            this.dataInstalacao = null;
            this.status = StatusPneu.DISPONIVEL;
        } else {
            throw new IllegalStateException("Este pneu não está instalado no ônibus informado.");
        }
    }
    
    public void retornarDaManutencao() {
        if (this.status == StatusPneu.EM_MANUTENCAO) {
            this.status = StatusPneu.DISPONIVEL;
            this.historicoRetornoManutencao.add(LocalDate.now());
        }
    }

    public void retornarDaReforma() {
        // <<< REGRA REFORÇADA: Só permite alteração se estiver em reforma.
        if (this.status == StatusPneu.EM_REFORMA) {
            this.status = StatusPneu.DISPONIVEL;
            this.kmRodados = 0.0; // Reseta a quilometragem
            this.periodoGarantiaMeses = 12; // A garantia de um pneu reformado é menor
            this.historicoRetornoReforma.add(LocalDate.now());
        } else {
            throw new IllegalStateException("Apenas pneus EM REFORMA podem ter seus dados de reforma atualizados.");
        }
    }

    public void vender() {
        if (this.status == StatusPneu.DESCARTADO) {
            this.status = StatusPneu.VENDIDO;
        }
    }

    // --- MÉTODOS DE CONSULTA E LÓGICA PREVENTIVA ---
    
    @Transient
    public LocalDate getDataUltimaManutencao() {
        if (historicoRetornoManutencao.isEmpty()) return null;
        return historicoRetornoManutencao.get(historicoRetornoManutencao.size() - 1);
    }

    @Transient
    public LocalDate getDataUltimaReforma() {
        if (historicoRetornoReforma.isEmpty()) return null;
        return historicoRetornoReforma.get(historicoRetornoReforma.size() - 1);
    }

    public boolean estaEmGarantia() {
        if (this.status == StatusPneu.VENDIDO || this.status == StatusPneu.DESCARTADO) {
            return false;
        }
        LocalDate dataFimGarantia = this.dataCompra.plusMonths(this.periodoGarantiaMeses);
        return LocalDate.now().isBefore(dataFimGarantia) || LocalDate.now().isEqual(dataFimGarantia);
    }

    public Long getDiasRestantesGarantia() {
        if (!estaEmGarantia()) return 0L;
        LocalDate dataFimGarantia = this.dataCompra.plusMonths(this.periodoGarantiaMeses);
        return ChronoUnit.DAYS.between(LocalDate.now(), dataFimGarantia);
    }

    // <<< NOVOS MÉTODOS PARA LÓGICA PREVENTIVA PROATIVA >>>
    
    @Transient
    public Double getKmRestantesManutencao() {
        if (this.status != StatusPneu.EM_USO) return Double.MAX_VALUE;
        return KM_LIMITE_MANUTENCAO - this.kmRodados;
    }

    @Transient
    public Double getKmRestantesReforma() {
        if (this.status != StatusPneu.EM_USO) return Double.MAX_VALUE;
        return KM_LIMITE_REFORMA - this.kmRodados;
    }

    @Transient
    public Long getDiasRestantesManutencao() {
        if (this.status != StatusPneu.EM_USO) return Long.MAX_VALUE;
        
        LocalDate dataBase = (getDataUltimaManutencao() != null) ? getDataUltimaManutencao() : this.dataInstalacao;
        if (dataBase == null) return Long.MAX_VALUE; // Ainda não foi instalado
        
        LocalDate proximaManutencao = dataBase.plusDays(DIAS_LIMITE_MANUTENCAO);
        return ChronoUnit.DAYS.between(LocalDate.now(), proximaManutencao);
    }

    @Transient
    public Long getDiasRestantesReforma() {
        if (this.status != StatusPneu.EM_USO) return Long.MAX_VALUE;

        LocalDate dataBase = (getDataUltimaReforma() != null) ? getDataUltimaReforma() : this.dataCompra;
        LocalDate proximaReforma = dataBase.plusDays(DIAS_LIMITE_REFORMA);
        return ChronoUnit.DAYS.between(LocalDate.now(), proximaReforma);
    }

    public boolean manutencaoPrestesVencer() {
        if (this.status != StatusPneu.EM_USO) return false;
        // Alerta se faltar menos de 30 dias OU menos de 500 km
        return getDiasRestantesManutencao() <= 30 || getKmRestantesManutencao() <= 500;
    }

    public boolean precisaManutencao() {
        if (this.status != StatusPneu.EM_USO) return false;
        return getDiasRestantesManutencao() <= 0 || getKmRestantesManutencao() <= 0;
    }

    public boolean reformaPrestesVencer() {
        if (this.status != StatusPneu.EM_USO) return false;
        // Alerta se faltar menos de 30 dias OU menos de 2000 km
        return getDiasRestantesReforma() <= 30 || getKmRestantesReforma() <= 2000;
    }

    public boolean precisaReforma() {
        if (this.status != StatusPneu.EM_USO) return false;
        return getDiasRestantesReforma() <= 0 || getKmRestantesReforma() <= 0;
    }
}