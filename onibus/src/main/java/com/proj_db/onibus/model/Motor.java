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
@Table(name = "motores")
public class Motor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    @NotNull(message = "Tipo do motor é obrigatório")
    private TipoMotor tipo;

    @Column(name = "quantidade_oleo", nullable = false)
    @NotNull(message = "Quantidade de óleo é obrigatória")
    @Positive(message = "Quantidade de óleo deve ser positiva")
    private Double quantidadeOleo; // Em litros

    @Column(name = "tipo_oleo", length = 50)
    private String tipoOleo; // Tipo de fluido de motor

    @Column(name = "potencia", nullable = false)
    @NotNull(message = "Potência é obrigatória")
    @Positive(message = "Potência deve ser positiva")
    private Integer potencia; // Em cavalos (CV)

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank(message = "Marca é obrigatória")
    private String marca;

    @Column(name = "modelo", nullable = false, length = 100)
    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;

    @Column(name = "ano_fabricacao", nullable = false)
    @NotNull(message = "Ano de fabricação é obrigatório")
    private Integer anoFabricacao;

    @Column(name = "data_ultima_revisao")
    private LocalDate dataUltimaRevisao;

    @Column(name = "data_ultima_manutencao")
    private LocalDate dataUltimaManutencao;

    @Column(name = "codigo_fabricacao", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Código de fabricação é obrigatório")
    private String codigoFabricacao;

    @Column(name = "numero_serie", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Número de série é obrigatório")
    private String numeroSerie;

    @Column(name = "cilindrada")
    @Positive(message = "Cilindrada deve ser positiva")
    private Integer cilindrada; // Em cc (opcional)

    @Column(name = "data_compra", nullable = false)
    @NotNull(message = "Data de compra é obrigatória")
    private LocalDate dataCompra;

    @Column(name = "periodo_garantia_meses", nullable = false)
    @NotNull(message = "Período de garantia é obrigatório")
    @Positive(message = "Período de garantia deve ser positivo")
    private Integer periodoGarantiaMeses = 24; // Default 2 anos

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull(message = "Status é obrigatório")
    private StatusMotor status = StatusMotor.NOVO;
    
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "onibus_id", referencedColumnName = "id")
    private Onibus onibus;

    // Enum para tipo de motor
    public enum TipoMotor {
        DIESEL,
        ELETRICO,
        GASOLINA,
        HIBRIDO,
        GNV,
        BIOCOMBUSTIVEL
    }

    // Enum para status do motor
    public enum StatusMotor {
        NOVO,
        DISPONIVEL,
        EM_USO,
        EM_MANUTENCAO,
        REVISADO,
        DESCARTADO,
        VENDIDO
    }

    // Método para verificar se está na garantia
    public boolean estaEmGarantia() {
        if (this.status == StatusMotor.VENDIDO || this.status == StatusMotor.DESCARTADO || this.status == StatusMotor.REVISADO) {
            return false; // Motores que passaram por revisão, foram vendidos ou descartados não podem estar em garantia
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

    // Método para instalar o motor que passou por revisão (muda status de NOVO ou DISPONIVEL para EM_USO)
    public void instalar() {
        if (this.status == StatusMotor.NOVO || this.status == StatusMotor.DISPONIVEL) {
            this.status = StatusMotor.EM_USO;
        }
    }

    // Método para fazer a revisão de um motor (muda status de NOVO, DISPONIVEL ou EM_USO para REVISADO)
    public void revisar() {
        if (this.status == StatusMotor.NOVO || this.status == StatusMotor.DISPONIVEL || this.status == StatusMotor.EM_USO) {
            this.status = StatusMotor.REVISADO;
            trocarOleo(tipoOleo, quantidadeOleo); // Faz a troca do Oleo de motor
            this.dataUltimaRevisao = LocalDate.now(); // Alterar data da última revisão
        }
    }

    // Método para remover um motor (só pode remover se o Motor estiver com status DSIPONIVEL ou EM_USO)
    public void remover() {
        if (this.status == StatusMotor.EM_USO) {
            this.status = StatusMotor.DISPONIVEL;
        }
    }

    // Método para vender um motor (só pode vender se o Motor já foi descartado)
    public void vender() {
        if (this.status == StatusMotor.DESCARTADO) {
            this.status = StatusMotor.VENDIDO;
        }
    }

    // Método para trocar Oleo
    public void trocarOleo(String novoTipoOleo, Double novaQuantidade) {
        if (this.status != StatusMotor.DESCARTADO && this.status != StatusMotor.VENDIDO) {
            this.tipoOleo = novoTipoOleo;
            this.quantidadeOleo = novaQuantidade;
            this.dataUltimaManutencao = LocalDate.now();
        }
    }

    // Método para verificar se precisa de revisão (a cada 6 meses)
    public boolean precisaRevisao() {
        if (this.status == StatusMotor.DESCARTADO || this.status == StatusMotor.VENDIDO) {
            return false;
        }
        
        LocalDate dataBaseRevisao = (this.dataUltimaRevisao == null) ? this.dataCompra : this.dataUltimaRevisao;
        LocalDate proximaRevisao = dataBaseRevisao.plusMonths(6);
        
        return !LocalDate.now().isBefore(proximaRevisao);
}
}