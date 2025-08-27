package com.proj_db.onibus.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    @NotBlank(message = "Nome do produto é obrigatório")
    private String nome;

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank(message = "Marca do produto é obrigatória")
    private String marca;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida", nullable = false, length = 20)
    @NotNull(message = "Unidade de medida é obrigatória")
    private UnidadeMedida unidadeMedida;

    @Column(name = "codigo_barras", unique = true, length = 50)
    private String codigoBarras;

    @Column(name = "codigo_interno", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Código interno é obrigatório")
    private String codigoInterno;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "preco_unitario", nullable = false)
    @NotNull(message = "Preço unitário é obrigatório")
    @Positive(message = "Preço unitário deve ser positivo")
    private Double precoUnitario;

    @Column(name = "estoque_minimo", nullable = false)
    @NotNull(message = "Estoque mínimo é obrigatório")
    @Positive(message = "Estoque mínimo deve ser positivo")
    private Integer estoqueMinimo = 5;

    @Column(name = "localizacao", length = 50)
    private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", length = 50)
    @NotNull(message = "Status é obrigatório")
    private Categoria categoria = Categoria.OUTRO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull(message = "Status é obrigatório")
    private StatusProduto status = StatusProduto.ATIVO;

    // Enum para unidade de medida
    public enum UnidadeMedida {
        UNIDADE,
        LITRO,
        QUILOGRAMA,
        GRAMA,
        METRO,
        CENTIMETRO,
        PAR,
        CAIXA,
        FRASCO,
        LATÃO
    }

    // Enum para categoria
    public enum Categoria {
        PECA_GENERICA,
        FERRAMENTA,
        FLUIDO,
        OUTRO
    }

    // Enum para status do produto
    public enum StatusProduto {
        ATIVO,
        INATIVO,
    }
}