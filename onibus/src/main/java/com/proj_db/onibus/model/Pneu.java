package com.proj_db.onibus.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
@Table(name = "pneus")
public class Pneu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank(message = "Marca é obrigatória")
    private String marca;

    @Column(name = "medida", nullable = false, length = 20)
    @NotBlank(message = "Medida é obrigatória")
    private String medida;

    @Column(name = "codigo_fabricacao", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Código de fabricação é obrigatório")
    private String codigoFabricacao;

    @Column(name = "ano_fabricacao", nullable = false)
    @NotNull(message = "Ano de fabricação é obrigatório")
    private Integer anoFabricacao;

    @Column(name = "numero_serie", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Número de série é obrigatório")
    private String numeroSerie;

    @Column(name = "data_compra", nullable = false)
    @NotNull(message = "Data de compra é obrigatória")
    private LocalDate dataCompra;

    @Column(name = "data_ultima_reforma")
    private LocalDate dataUltimaReforma;

    @Column(name = "periodo_garantia_meses", nullable = false)
    @NotNull(message = "Período de garantia é obrigatório")
    @Positive(message = "Período de garantia deve ser positivo")
    private Integer periodoGarantiaMeses = 24;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull(message = "Status é obrigatório")
    private StatusPneu status = StatusPneu.NOVO;

    @Column(name = "km_rodados")
    private Integer kmRodados = 0;

    @Column(name = "data_instalacao")
    private LocalDate dataInstalacao;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "onibus_id", referencedColumnName = "id")
    private Onibus onibus;

    @Enumerated(EnumType.STRING)
    @Column(name = "posicao", length = 10)
    private PosicaoPneu posicao;  // "DD", "DE", "TDE", "TDI", "TEE", "TEI"

    public enum StatusPneu {
        NOVO,
        DISPONIVEL,
        EM_USO,
        EM_MANUTENCAO,
        REFORMADO,
        DESCARTADO,
        VENDIDO
    }

    public enum PosicaoPneu {
        DD,
        DE,
        TDE,
        TDI,
        TEE,
        TEI
    }

    // Método para verificar se está na garantia
    public boolean estaEmGarantia() {
        if (this.status == StatusPneu.VENDIDO || this.status == StatusPneu.DESCARTADO) {
            return false; // Pneus que foram vendidos ou descartados não podem estar em garantia
        }
        
        LocalDate dataFimGarantia = this.dataCompra.plusMonths(this.periodoGarantiaMeses);
        return LocalDate.now().isBefore(dataFimGarantia) || LocalDate.now().isEqual(dataFimGarantia);
    }

    // Método para calcular dias restantes de garantia
    public Long getDiasRestantesGarantia() {
        if (!estaEmGarantia()) {
            return 0L;
        }
       
        LocalDate dataFimGarantia = this.dataCompra.plusMonths(this.periodoGarantiaMeses);
        return ChronoUnit.DAYS.between(LocalDate.now(), dataFimGarantia);
    }

    // Método para verificar se a garantia está prestes a vencer (últimos 30 dias)
    public boolean garantiaPrestesVencer() {
        if (!estaEmGarantia()) {
            return false;
        }
        
        LocalDate dataFimGarantia = this.dataCompra.plusMonths(this.periodoGarantiaMeses);
        LocalDate trintaDiasAntes = dataFimGarantia.minusDays(30);
        
        return LocalDate.now().isAfter(trintaDiasAntes) || LocalDate.now().isEqual(trintaDiasAntes);
    }

    // Método para instalar o pneu que passou por revisão (muda status de NOVO ou DISPONIVEL para EM_USO)
    public void instalar() {
        if (this.status == StatusPneu.NOVO || this.status == StatusPneu.DISPONIVEL) {
            this.status = StatusPneu.EM_USO;
        }
    }

    // Método para remover um pneu (só pode remover se o Pneu estiver com status EM_USO)
    public void remover() {
        if (this.status == StatusPneu.EM_USO) {
            this.status = StatusPneu.DISPONIVEL;
        }
    }

    // Método para fazer a revisão de um pneu (muda status de NOVO, DISPONIVEL ou EM_USO para REFORMADO)
    public void reformar() {
        if (this.status == StatusPneu.NOVO || this.status == StatusPneu.DISPONIVEL || this.status == StatusPneu.EM_USO) {
            this.status = StatusPneu.REFORMADO;
            this.kmRodados = 0; // Reseta a quilometrogem do pneu
            this.periodoGarantiaMeses = 12;
            this.dataUltimaReforma = LocalDate.now(); // Alterar data da última revisão
        }
    }

    // Método para descartar um pneu (só pode descartar se o Pneu estiver com status DSIPONIVEL ou EM_USO)
    public void descartar() {
        if (this.status == StatusPneu.DISPONIVEL || this.status == StatusPneu.EM_USO) {
            this.status = StatusPneu.DESCARTADO;
        }
    }

    // Método para vender um pneu (só pode vender se o Pneu já foi descartado)
    public void vender() {
        if (this.status == StatusPneu.DESCARTADO) {
            this.status = StatusPneu.VENDIDO;
        }
    }

    // Método para verificar se precisa de revisão (a cada 6 meses)
    public boolean precisaReforma() {
        if (this.status != StatusPneu.DESCARTADO && this.status != StatusPneu.VENDIDO) {
            LocalDate dataUltimaReforma_var = this.dataUltimaReforma;
            if (this.dataUltimaReforma == null) {
                dataUltimaReforma_var = getDataCompra();
            }
            LocalDate proximaReforma = dataUltimaReforma_var.plusMonths(6);
            return LocalDate.now().isAfter(proximaReforma) || LocalDate.now().isEqual(proximaReforma) || this.kmRodados >= 10000;
        }
        return false;
    }
}