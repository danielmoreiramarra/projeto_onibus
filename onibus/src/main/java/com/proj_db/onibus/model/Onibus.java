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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "onibus")
public class Onibus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chassi", nullable = false, length = 100, unique = true)
    @NotBlank(message = "Chassi é obrigatório")
    private String chassi;

    @Column(name = "placa", nullable = false, length = 100, unique = true)
    @NotBlank(message = "Placa é obrigatório")
    private String placa;

    @Column(name = "modelo", nullable = false, length = 50)
    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank(message = "Marca é obrigatória")
    private String marca;

    @Column(name = "codigo_fabricacao", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Código de fabricação é obrigatório")
    private String codigoFabricacao;

    @Column(name = "capacidade", nullable = false)
    @NotNull(message = "Capacidade é obrigatória")
    private Integer capacidade;

    @Column(name = "ano_fabricacao", nullable = false)
    @NotNull(message = "Ano de fabricação é obrigatório")
    private Integer anoFabricacao;

    @Column(name = "numero_frota", unique = true, nullable = false, length = 20)
    @NotBlank(message = "Número da frota é obrigatório")
    private String numeroFrota;

    @Column(name = "data_ultima_reforma")
    private LocalDate dataUltimaReforma;

    @OneToOne
    @JsonManagedReference
    @JoinColumn(name = "motor_id", referencedColumnName = "id")
    private Motor motor;

    @OneToOne
    @JsonManagedReference
    @JoinColumn(name = "cambio_id", referencedColumnName = "id")
    private Cambio cambio;

    @JsonManagedReference
    @OneToMany(mappedBy = "onibus", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pneu> pneus = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull(message = "Status é obrigatório")
    private StatusOnibus status = StatusOnibus.NOVO;

    public enum StatusOnibus {
        NOVO,
        DISPONIVEL,
        EM_OPERACAO,
        EM_MANUTENCAO,
        REFORMADO,
        APOSENTADO,
        VENDIDO
    }

    // Método para colocar um ônibus em operação (muda status de NOVO ou DISPONIVEL para EM_OPERACAO)
    public void colocarEmOperacao() {
        if (this.status == StatusOnibus.NOVO || this.status == StatusOnibus.DISPONIVEL) {
            this.status = StatusOnibus.EM_OPERACAO;
        }
    }

    // Método para fazer a reforma de um ônibus (muda status de NOVO, DISPONIVEL ou EM_USO para REVISADO)
    public void reformar() {
        if (this.status == StatusOnibus.NOVO || this.status == StatusOnibus.DISPONIVEL || this.status == StatusOnibus.EM_OPERACAO) {
            this.status = StatusOnibus.REFORMADO;
            this.dataUltimaReforma = LocalDate.now(); // Alterar data da última revisão
        }
    }

    // Método para descartar um ônibus (só pode descartar se o ônibus estiver com status DSIPONIVEL ou EM_USO)
    public void aposentar() {
        if (this.status == StatusOnibus.DISPONIVEL || this.status == StatusOnibus.EM_OPERACAO) {
            this.status = StatusOnibus.APOSENTADO;
        }
    }

    // Método para vender um ônibus (só pode vender se o ônibus já foi aposentado)
    public void vender() {
        if (this.status == StatusOnibus.APOSENTADO) {
            this.status = StatusOnibus.VENDIDO;
        }
    }
}