package com.proj_db.onibus.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor // Construtor vazio para o JPA
@Table(name = "produtos")
public class Produto {

    // --- ATRIBUTOS ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", length = 50, nullable = false)
    @NotNull(message = "Categoria é obrigatória")
    private Categoria categoria = Categoria.OUTRO;

    @Column(name = "codigo_barras", unique = true, length = 50)
    private String codigoBarras;

    @Column(name = "codigo_interno", unique = true, nullable = false, length = 50)
    @NotBlank(message = "Código interno é obrigatório")
    private String codigoInterno;
    
    // <<< NOVO: Atributo para data de cadastro
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    @NotNull
    private LocalDate dataCadastro;

    @Column(name = "descricao", length = 500)
    private String descricao;

    @Column(name = "estoque_minimo", nullable = false)
    @NotNull(message = "Estoque mínimo é obrigatório")
    @Positive(message = "Estoque mínimo deve ser positivo")
    private Integer estoqueMinimo = 5;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "produto_historico_preco", joinColumns = @JoinColumn(name = "produto_id"))
    private List<RegistroContabil> historicoPrecoUnitario = new ArrayList<>();

    @Column(name = "localizacao", length = 50)
    private String localizacao;

    @Column(name = "marca", nullable = false, length = 100)
    @NotBlank(message = "Marca do produto é obrigatória")
    private String marca;

    @Column(name = "nome", nullable = false, length = 100)
    @NotBlank(message = "Nome do produto é obrigatório")
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @NotNull(message = "Status é obrigatório")
    private StatusProduto status = StatusProduto.ATIVO;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidade_medida", nullable = false, length = 20)
    @NotNull(message = "Unidade de medida é obrigatória")
    private UnidadeMedida unidadeMedida;


    // --- CLASSE EMBUTIDA PARA O HISTÓRICO DE PREÇOS ---
    @Embeddable
    @Data
    @NoArgsConstructor
    public static class RegistroContabil {
        @Column(name = "data_registro", nullable = false)
        private LocalDate data;

        @Column(name = "preco_unitario", nullable = false)
        private Double precoUnitario;

        @Enumerated(EnumType.STRING)
        @Column(name = "tipo_alteracao", nullable = false)
        private TipoAlteracao tipoAlteracao;

        public enum TipoAlteracao {
            INICIAL,
            AUMENTO,
            DECLINIO
        }

        public RegistroContabil(LocalDate data, Double precoUnitario, TipoAlteracao tipo) {
            this.data = data;
            this.precoUnitario = precoUnitario;
            this.tipoAlteracao = tipo;
        }
    }


    // --- CONSTRUTOR ---
    public Produto(String nome, String marca, UnidadeMedida unidadeMedida, String codigoInterno, Categoria categoria) {
        this.nome = nome;
        this.marca = marca;
        this.unidadeMedida = unidadeMedida;
        this.codigoInterno = codigoInterno;
        this.categoria = categoria;
        this.dataCadastro = LocalDate.now(); // <<< Define a data de cadastro na criação
        this.status = StatusProduto.ATIVO;
    }


    // --- ENUMS ---
    public enum Categoria { ITEM_GENERICO, FERRAMENTA, FLUIDO, OUTRO }
    public enum StatusProduto { ATIVO, INATIVO }
    public enum UnidadeMedida { UNIDADE, LITRO, QUILOGRAMA, GRAMA, METRO, CENTIMETRO, PAR, CAIXA, FRASCO, LATÃO }
    

    // --- MÉTODOS DE LÓGICA DE NEGÓCIO ---
    public void atualizarPreco(Double novoPreco) {
        if (novoPreco == null || novoPreco < 0) { // Permite preço 0.0
            throw new IllegalArgumentException("O novo preço deve ser um valor positivo ou zero.");
        }

        Double precoAtual = getPrecoUnitarioAtual();
        if (novoPreco.equals(precoAtual)) {
            return; // Não cria um novo registro se o preço for o mesmo
        }

        RegistroContabil.TipoAlteracao tipo;
        
        // <<< LÓGICA ALTERADA: Usa o tamanho da lista como critério principal
        if (this.historicoPrecoUnitario.isEmpty()) {
            tipo = RegistroContabil.TipoAlteracao.INICIAL;
        } else if (novoPreco > precoAtual) {
            tipo = RegistroContabil.TipoAlteracao.AUMENTO;
        } else {
            tipo = RegistroContabil.TipoAlteracao.DECLINIO;
        }

        RegistroContabil novoRegistro = new RegistroContabil(LocalDate.now(), novoPreco, tipo);
        this.historicoPrecoUnitario.add(novoRegistro);
    }
    
    @Transient
    public Double getPrecoUnitarioAtual() {
        if (this.historicoPrecoUnitario == null || this.historicoPrecoUnitario.isEmpty()) {
            return 0.0;
        }
        
        Optional<RegistroContabil> registroMaisRecente = this.historicoPrecoUnitario.stream()
                .max(Comparator.comparing(RegistroContabil::getData));
        
        return registroMaisRecente.map(RegistroContabil::getPrecoUnitario).orElse(0.0);
    }

    public boolean isProdutoParaPreventiva() {
        return this.categoria == Categoria.FLUIDO && this.unidadeMedida == UnidadeMedida.LITRO;
    }
}