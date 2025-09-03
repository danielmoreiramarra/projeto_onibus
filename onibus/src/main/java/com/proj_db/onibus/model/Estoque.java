package com.proj_db.onibus.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Entity
@Table(name = "estoque")
public class Estoque {

    // --- ATRIBUTOS ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // <<< NOVO: Histórico detalhado de movimentações
    @ElementCollection(fetch = FetchType.LAZY) // Usar LAZY para performance, EAGER se sempre precisar carregar
    @CollectionTable(name = "estoque_historico_movimentacao", joinColumns = @JoinColumn(name = "estoque_id")) // <<< CORREÇÃO
    private List<RegistroMovimentacao> historicoMovimentacao = new ArrayList<>();

    @Column(name = "localizacao_fisica", length = 100)
    private String localizacaoFisica;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    @NotNull(message = "Produto é obrigatório")
    private Produto produto;

    @Column(name = "quantidade_atual", nullable = false)
    @NotNull(message = "Quantidade atual é obrigatória")
    @PositiveOrZero(message = "Quantidade atual não pode ser negativa")
    private Double quantidadeAtual = 0.0;

    @Column(name = "quantidade_reservada", nullable = false)
    @NotNull(message = "Quantidade reservada é obrigatória")
    @PositiveOrZero(message = "Quantidade reservada não pode ser negativa")
    private Double quantidadeReservada = 0.0;


    // --- CLASSE EMBUTIDA PARA O HISTÓRICO ---
    @Embeddable
    @Data
    public static class RegistroMovimentacao {
        @Column(name = "data_registro", nullable = false)
        private LocalDate data;

        @Column(name = "quantidade_movimentada", nullable = false)
        private Double quantidade;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_registro", nullable = false)
        private TipoRegistro tipoRegistro;

        public enum TipoRegistro {
            ENTRADA,
            SAIDA
        }
    }


    // --- MÉTODOS DE LÓGICA DE NEGÓCIO ---

    public void adicionarEstoque(Double quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a ser adicionada deve ser positiva.");
        }
        this.quantidadeAtual += quantidade;
        
        // <<< CORREÇÃO LÓGICA: Registra a quantidade que foi movimentada
        RegistroMovimentacao registro = new RegistroMovimentacao();
        registro.setData(LocalDate.now());
        registro.setQuantidade(quantidade); // Registra o valor da transação
        registro.setTipoRegistro(RegistroMovimentacao.TipoRegistro.ENTRADA);
        this.historicoMovimentacao.add(registro);
    }

    public void confirmarConsumoReserva(Double quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a ser consumida deve ser positiva.");
        }
        if (quantidade > this.quantidadeReservada) {
            throw new IllegalStateException("Tentativa de consumir mais do que a quantidade reservada.");
        }
        this.quantidadeReservada -= quantidade;
        this.quantidadeAtual -= quantidade;
        
        // <<< CORREÇÃO LÓGICA: Registra a quantidade que foi movimentada
        RegistroMovimentacao registro = new RegistroMovimentacao();
        registro.setData(LocalDate.now());
        registro.setQuantidade(quantidade); // Registra o valor da transação
        registro.setTipoRegistro(RegistroMovimentacao.TipoRegistro.SAIDA);
        this.historicoMovimentacao.add(registro);
    }
    
    public void liberarReserva(Double quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a ser liberada deve ser positiva.");
        }
        if (quantidade > this.quantidadeReservada) {
            throw new IllegalStateException("Tentativa de liberar mais do que a quantidade reservada.");
        }
        this.quantidadeReservada -= quantidade;
    }
    
    public boolean reservarEstoque(Double quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a ser reservada deve ser positiva.");
        }
        if (getQuantidadeDisponivel() < quantidade) {
            return false;
        }
        this.quantidadeReservada += quantidade;
        return true;
    }


    // --- MÉTODOS DE CONSULTA ---

    public boolean estaAbaixoEstoqueMinimo() {
        if (this.produto == null) {
            return false;
        }
        return this.quantidadeAtual < this.produto.getEstoqueMinimo();
    }

    @Transient
    public Double getQuantidadeDisponivel() {
        return this.quantidadeAtual - this.quantidadeReservada;
    }

    @Transient
    public Double getValorTotalEstoque() {
        if (this.produto == null || this.produto.getPrecoUnitarioAtual() == null) {
            return 0.0;
        }
        return this.quantidadeAtual * this.produto.getPrecoUnitarioAtual();
    }

    @Transient
    public LocalDate getDataUltimaEntrada() {
        // <<< LÓGICA CORRIGIDA: Ordena por data decrescente, pega o primeiro (o mais recente) e extrai a data
        return this.historicoMovimentacao.stream()
                .filter(h -> h.getTipoRegistro() == RegistroMovimentacao.TipoRegistro.ENTRADA)
                .max(Comparator.comparing(RegistroMovimentacao::getData))
                .map(RegistroMovimentacao::getData)
                .orElse(null);
    }

    @Transient
    public LocalDate getDataUltimaSaida() {
        // <<< LÓGICA CORRIGIDA: Ordena por data decrescente, pega o primeiro (o mais recente) e extrai a data
        return this.historicoMovimentacao.stream()
                .filter(h -> h.getTipoRegistro() == RegistroMovimentacao.TipoRegistro.SAIDA)
                .max(Comparator.comparing(RegistroMovimentacao::getData))
                .map(RegistroMovimentacao::getData)
                .orElse(null);
    }
}