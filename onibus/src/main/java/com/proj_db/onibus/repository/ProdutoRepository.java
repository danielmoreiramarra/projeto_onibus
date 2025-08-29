package com.proj_db.onibus.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.model.Produto.Categoria;
import com.proj_db.onibus.model.Produto.StatusProduto;
import com.proj_db.onibus.model.Produto.UnidadeMedida;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // ✅ MÉTODOS DE BUSCA DERIVADOS (MANTIDOS PARA SIMPLICIDADE)
    Optional<Produto> findByCodigoInterno(String codigoInterno);
    Optional<Produto> findByCodigoBarras(String codigoBarras);
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    List<Produto> findByMarcaContainingIgnoreCase(String marca);
    List<Produto> findByStatus(StatusProduto status);
    List<Produto> findByUnidadeMedida(UnidadeMedida unidadeMedida);
    List<Produto> findByPrecoUnitarioBetween(Double precoMinimo, Double precoMaximo);
    List<Produto> findByCategoria(Categoria categoria);
    List<Produto> findByMarca(String marca);
    boolean existsByCodigoInterno(String codigoInterno);
    boolean existsByCodigoBarras(String codigoBarras);

    // ✅ NOVA CONSULTA COMBINADA PARA TODOS OS CAMPOS
    @Query("SELECT p FROM Produto p WHERE " +
           "(:id IS NULL OR p.id = :id) AND " +
           "(:nome IS NULL OR p.nome LIKE %:nome%) AND " +
           "(:marca IS NULL OR p.marca LIKE %:marca%) AND " +
           "(:codigoBarras IS NULL OR p.codigoBarras LIKE %:codigoBarras%) AND " +
           "(:codigoInterno IS NULL OR p.codigoInterno LIKE %:codigoInterno%) AND " +
           "(:descricao IS NULL OR p.descricao LIKE %:descricao% OR p.nome LIKE %:descricao%) AND " +
           "(:precoUnitarioMin IS NULL OR p.precoUnitario >= :precoUnitarioMin) AND " +
           "(:precoUnitarioMax IS NULL OR p.precoUnitario <= :precoUnitarioMax) AND " +
           "(:localizacao IS NULL OR p.localizacao LIKE %:localizacao%) AND " +
           "(:categoria IS NULL OR p.categoria = :categoria) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:unidadeMedida IS NULL OR p.unidadeMedida = :unidadeMedida)")
    List<Produto> searchProduto(
        @Param("id") Long id,
        @Param("nome") String nome,
        @Param("marca") String marca,
        @Param("codigoBarras") String codigoBarras,
        @Param("codigoInterno") String codigoInterno,
        @Param("descricao") String descricao,
        @Param("precoUnitarioMin") Double precoUnitarioMin,
        @Param("precoUnitarioMax") Double precoUnitarioMax,
        @Param("localizacao") String localizacao,
        @Param("categoria") Categoria categoria,
        @Param("status") StatusProduto status,
        @Param("unidadeMedida") UnidadeMedida unidadeMedida
    );
    
    // ✅ MÉTODOS DE RELATÓRIO
    @Query("SELECT p FROM Produto p JOIN Estoque e ON p.id = e.produto.id WHERE e.quantidadeAtual < p.estoqueMinimo")
    List<Produto> findProdutosComEstoqueAbaixoMinimo();
    
    @Query("SELECT p FROM Produto p WHERE p.id NOT IN (" +
           "SELECT ios.produto.id FROM ItemOrdemServico ios)")
    List<Produto> findProdutosNuncaUtilizados();
    
    @Query("SELECT p, COALESCE(SUM(ios.quantidade), 0) FROM Produto p LEFT JOIN ItemOrdemServico ios ON p.id = ios.produto.id " +
           "GROUP BY p.id, p.nome ORDER BY 2 DESC")
    List<Object[]> findProdutosMaisUtilizados();
    
    @Query("SELECT p, COALESCE(SUM(ios.quantidade), 0) FROM Produto p LEFT JOIN ItemOrdemServico ios ON p.id = ios.produto.id " +
           "WHERE ios.ordemServico.status = 'FINALIZADA' GROUP BY p.id ORDER BY 2 DESC")
    List<Object[]> findProdutosPorGiro();
    
    @Query("SELECT p FROM Produto p WHERE p.id NOT IN (" +
           "SELECT ios.produto.id FROM ItemOrdemServico ios WHERE ios.ordemServico.dataConclusao > :dataLimite)")
    List<Produto> findProdutosSemMovimento(@Param("dataLimite") LocalDate dataLimite);
    
    @Query("SELECT p.categoria, COUNT(p) FROM Produto p GROUP BY p.categoria")
    List<Object[]> countProdutosPorCategoria();
    
    @Query("SELECT p.status, COUNT(p) FROM Produto p GROUP BY p.status")
    List<Object[]> countProdutosPorStatus();
    
    @Query("SELECT p.unidadeMedida, COUNT(p) FROM Produto p GROUP BY p.unidadeMedida")
    List<Object[]> countProdutosPorUnidadeMedida();
    
    @Query("SELECT p.categoria, AVG(p.precoUnitario) FROM Produto p WHERE p.status = 'ATIVO' GROUP BY p.categoria")
    List<Object[]> avgPrecoPorCategoria();
    
    @Query("SELECT p, (e.quantidadeAtual * p.precoUnitario) as valorEstoque " +
           "FROM Produto p JOIN Estoque e ON p.id = e.produto.id ORDER BY 2 DESC")
    List<Object[]> findProdutosComMaiorValorEstoque();
    
    @Query("SELECT p, (COUNT(ios) / p.precoUnitario) as custoBeneficio " +
           "FROM Produto p LEFT JOIN ItemOrdemServico ios ON p.id = ios.produto.id " +
           "WHERE p.precoUnitario > 0 GROUP BY p.id, p.precoUnitario ORDER BY 2 DESC")
    List<Object[]> findProdutosMelhorCustoBeneficio();
    
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(p.codigoInterno, 6) AS INTEGER)), 0) + 1 " +
           "FROM Produto p WHERE p.codigoInterno LIKE 'PROD-%'")
    Integer findProximoCodigoInterno();
    
}