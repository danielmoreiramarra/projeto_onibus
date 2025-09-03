package com.proj_db.onibus.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor // <<< Adicionado para garantir compatibilidade com JPA
@Table(name = "itens_ordem_servico")
public class ItemOrdemServico {

    // --- ATRIBUTOS ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_adicao", nullable = false)
    @NotNull
    private LocalDate dataAdicao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    @JsonBackReference
    @NotNull(message = "Ordem de serviço é obrigatória")
    private OrdemServico ordemServico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    @NotNull(message = "Produto é obrigatório")
    private Produto produto;

    @Column(name = "quantidade", nullable = false)
    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    private Double quantidade; // <<< AJUSTE: Alterado para Double por consistência

    @Column(name = "preco_unitario_registrado", nullable = false) // <<< AJUSTE: Nome mais claro
    @NotNull(message = "Preço unitário é obrigatório")
    @Positive(message = "Preço unitário deve ser positivo")
    private Double precoUnitarioRegistrado; // <<< AJUSTE: "Congela" o preço

    @Column(name = "descricao", length = 500) // <<< Ótima adição da sua parte!
    private String descricao;

    
    // --- CONSTRUTOR ---
    /*
     * <<< NOVO: Construtor para criar um item de forma segura,
     * "congelando" o preço do produto no momento da criação.
     */
    public ItemOrdemServico(OrdemServico ordemServico, Produto produto, Double quantidade, String descricao) {
        this.dataAdicao = LocalDate.now();
        this.ordemServico = ordemServico;
        this.produto = produto;
        this.quantidade = quantidade;
        this.descricao = descricao;
        this.precoUnitarioRegistrado = produto.getPrecoUnitarioAtual(); // Lógica de "congelamento"
    }


    // --- MÉTODOS DE CONSULTA ---
    /**
     * Calcula o valor total do item. Renomeado para getSubtotal por clareza.
     */
    @Transient // <<< AJUSTE: Boa prática para campos calculados
    public Double getSubtotal() {
        if (precoUnitarioRegistrado == null || quantidade == null) {
            return 0.0;
        }
        return precoUnitarioRegistrado * quantidade;
    }
}