package com.proj_db.onibus.repository;

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
    
    // Buscar por código interno
    Optional<Produto> findByCodigoInterno(String codigoInterno);
    
    // Buscar por código de barras
    Optional<Produto> findByCodigoBarras(String codigoBarras);
    
    // Buscar por nome
    List<Produto> findByNome(String nome);
    
    // Buscar por nome contendo (busca parcial)
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    
    // Buscar por marca
    List<Produto> findByMarca(String marca);
    
    // Buscar por marca contendo (busca parcial)
    List<Produto> findByMarcaContainingIgnoreCase(String marca);
    
    // Buscar por categoria
    List<Produto> findByCategoria(Categoria categoria);
    
    // Buscar por unidade de medida
    List<Produto> findByUnidadeMedida(UnidadeMedida unidadeMedida);
    
    // Buscar por status
    List<Produto> findByStatus(StatusProduto status);
    
    // Buscar por localização
    List<Produto> findByLocalizacao(String localizacao);
    
    // Buscar por preço maior ou igual
    List<Produto> findByPrecoUnitarioGreaterThanEqual(Double precoMinimo);
    
    // Buscar por preço menor ou igual
    List<Produto> findByPrecoUnitarioLessThanEqual(Double precoMaximo);
    
    // Buscar por intervalo de preço
    List<Produto> findByPrecoUnitarioBetween(Double precoMinimo, Double precoMaximo);
    
    // Buscar por estoque mínimo
    List<Produto> findByEstoqueMinimo(Integer estoqueMinimo);
    
    // Verificar se código interno existe
    boolean existsByCodigoInterno(String codigoInterno);
    
    // Verificar se código de barras existe
    boolean existsByCodigoBarras(String codigoBarras);
    
    // Buscar produtos ativos
    @Query("SELECT p FROM Produto p WHERE p.status = 'ATIVO'")
    List<Produto> findProdutosAtivos();
    
    // Buscar produtos inativos
    @Query("SELECT p FROM Produto p WHERE p.status = 'INATIVO'")
    List<Produto> findProdutosInativos();
    
    // Buscar produtos por descrição (busca em nome e descrição)
    @Query("SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Produto> searchByNomeOrDescricao(@Param("termo") String termo);
    
    // Buscar produtos por categoria e status
    @Query("SELECT p FROM Produto p WHERE p.categoria = :categoria AND p.status = 'ATIVO'")
    List<Produto> findByCategoriaAndAtivo(@Param("categoria") String categoria);
    
    // Buscar produtos com estoque abaixo do mínimo
    @Query("SELECT p FROM Produto p WHERE p.id IN (" +
           "SELECT e.produto.id FROM Estoque e WHERE e.quantidadeAtual < p.estoqueMinimo)")
    List<Produto> findProdutosComEstoqueAbaixoMinimo();
    
    // Buscar produtos nunca utilizados
    @Query("SELECT p FROM Produto p WHERE p.id NOT IN (" +
           "SELECT ios.produto.id FROM ItemOrdemServico ios)")
    List<Produto> findProdutosNuncaUtilizados();
    
    // Buscar produtos mais utilizados (top 10)
    @Query("SELECT p, COUNT(ios) as totalUsos " +
           "FROM Produto p LEFT JOIN ItemOrdemServico ios ON p.id = ios.produto.id " +
           "GROUP BY p.id, p.nome " +
           "ORDER BY totalUsos DESC")
    List<Object[]> findProdutosMaisUtilizados();
    
    // Buscar produtos por giro (quantidade total consumida)
    @Query("SELECT p, COALESCE(SUM(ios.quantidade), 0) as totalConsumido " +
           "FROM Produto p LEFT JOIN ItemOrdemServico ios ON p.id = ios.produto.id " +
           "WHERE ios.ordemServico.status = 'FINALIZADA' " +
           "GROUP BY p.id, p.nome " +
           "ORDER BY totalConsumido DESC")
    List<Object[]> findProdutosPorGiro();
    
    // Buscar produtos sem movimento há mais de X dias
    @Query("SELECT p FROM Produto p WHERE p.id NOT IN (" +
           "SELECT ios.produto.id FROM ItemOrdemServico ios " +
           "WHERE ios.ordemServico.dataConclusao > :dataLimite)")
    List<Produto> findProdutosSemMovimento(@Param("dataLimite") java.time.LocalDate dataLimite);
    
    // Contar produtos por categoria
    @Query("SELECT p.categoria, COUNT(p) FROM Produto p WHERE p.status = 'ATIVO' GROUP BY p.categoria")
    List<Object[]> countProdutosPorCategoria();
    
    // Contar produtos por status
    @Query("SELECT p.status, COUNT(p) FROM Produto p GROUP BY p.status")
    List<Object[]> countProdutosPorStatus();
    
    // Contar produtos por unidade de medida
    @Query("SELECT p.unidadeMedida, COUNT(p) FROM Produto p WHERE p.status = 'ATIVO' GROUP BY p.unidadeMedida")
    List<Object[]> countProdutosPorUnidadeMedida();
    
    // Calcular preço médio por categoria
    @Query("SELECT p.categoria, AVG(p.precoUnitario) FROM Produto p WHERE p.status = 'ATIVO' GROUP BY p.categoria")
    List<Object[]> avgPrecoPorCategoria();
    
    // Buscar produtos com maior valor em estoque
    @Query("SELECT p, (e.quantidadeAtual * p.precoUnitario) as valorEstoque " +
           "FROM Produto p JOIN Estoque e ON p.id = e.produto.id " +
           "ORDER BY valorEstoque DESC")
    List<Object[]> findProdutosComMaiorValorEstoque();
    
    // Buscar produtos com melhor custo-benefício (mais usados com preço baixo)
    @Query("SELECT p, (COUNT(ios) / p.precoUnitario) as custoBeneficio " +
           "FROM Produto p LEFT JOIN ItemOrdemServico ios ON p.id = ios.produto.id " +
           "WHERE p.precoUnitario > 0 " +
           "GROUP BY p.id, p.nome, p.precoUnitario " +
           "ORDER BY custoBeneficio DESC")
    List<Object[]> findProdutosMelhorCustoBeneficio();
    
    // Buscar produtos que precisam ser reavaliados (preço muito alto ou muito baixo)
    @Query("SELECT p FROM Produto p WHERE p.precoUnitario > (" +
           "SELECT AVG(p2.precoUnitario) * 2 FROM Produto p2 WHERE p2.categoria = p.categoria) OR " +
           "p.precoUnitario < (SELECT AVG(p3.precoUnitario) * 0.5 FROM Produto p3 WHERE p3.categoria = p.categoria)")
    List<Produto> findProdutosParaReavaliacaoPreco();
    
    // Gerar próximo código interno automaticamente
    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(p.codigoInterno, 4) AS INTEGER)), 0) + 1 " +
           "FROM Produto p WHERE p.codigoInterno LIKE 'PROD-%'")
    Integer findProximoCodigoInterno();
    
}