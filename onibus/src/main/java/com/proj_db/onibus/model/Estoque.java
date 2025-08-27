package com.proj_db.onibus.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "estoque")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    @NotNull(message = "Produto é obrigatório")
    private Produto produto;

    @Column(name = "quantidade_atual", nullable = false)
    @NotNull(message = "Quantidade atual é obrigatória")
    private Integer quantidadeAtual = 0;

    @Column(name = "quantidade_reservada", nullable = false)
    private Integer quantidadeReservada = 0; // Para ordens de serviço em andamento

    @Column(name = "localizacao_fisica", length = 100)
    private String localizacaoFisica; // Local exato no almoxarifado

    @Column(name = "data_ultima_entrada")
    private java.time.LocalDate dataUltimaEntrada;

    @Column(name = "data_ultima_saida")
    private java.time.LocalDate dataUltimaSaida;

    // Método para verificar disponibilidade
    public boolean estaDisponivel(Integer quantidadeRequerida) {
        return (quantidadeAtual - quantidadeReservada) >= quantidadeRequerida;
    }

    // Método para verificar se está abaixo do estoque mínimo
    public boolean estaAbaixoEstoqueMinimo() {
        return quantidadeAtual < produto.getEstoqueMinimo();
    }

    // Método para adicionar quantidade ao estoque
    public void adicionarEstoque(Integer quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
        this.quantidadeAtual += quantidade;
        this.dataUltimaEntrada = java.time.LocalDate.now();
    }

    // Método principal para consumir estoque (COM VALIDAÇÃO)
    public boolean consumirEstoque(Integer quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        if (!estaDisponivel(quantidade)) {
            return false; // Não há estoque suficiente
        }

        this.quantidadeAtual -= quantidade;
        this.dataUltimaSaida = java.time.LocalDate.now();
        return true;
    }

    // Método para reservar quantidade (para ordens de serviço)
    public boolean reservarEstoque(Integer quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        if ((quantidadeAtual - quantidadeReservada) < quantidade) {
            return false; // Não há estoque disponível para reserva
        }

        this.quantidadeReservada += quantidade;
        return true;
    }

    // Método para liberar reserva (quando ordem de serviço é concluída/cancelada)
    public void liberarReserva(Integer quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        if (quantidade > quantidadeReservada) {
            throw new IllegalStateException("Quantidade a liberar maior que a reservada");
        }

        this.quantidadeReservada -= quantidade;
    }

    // Método para confirmar consumo de reserva
    public void confirmarConsumoReserva(Integer quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }

        if (quantidade > quantidadeReservada) {
            throw new IllegalStateException("Quantidade a consumir maior que a reservada");
        }

        this.quantidadeReservada -= quantidade;
        this.quantidadeAtual -= quantidade;
        this.dataUltimaSaida = java.time.LocalDate.now();
    }

    // Método para calcular quantidade disponível (não reservada)
    public Integer getQuantidadeDisponivel() {
        return quantidadeAtual - quantidadeReservada;
    }

    // Método para calcular valor total em estoque
    public Double getValorTotalEstoque() {
        return quantidadeAtual * produto.getPrecoUnitario();
    }
}