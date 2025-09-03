package com.proj_db.onibus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {
    Optional<Produto> findByCodigoInterno(String codigoInterno);
    Optional<Produto> findByCodigoBarras(String codigoBarras);

    @Query("SELECT p FROM Produto p WHERE p.categoria = 'FLUIDO' AND lower(p.nome) = lower(:nomeFluido)")
    Optional<Produto> findProdutoParaPreventiva(@Param("nomeFluido") String nomeFluido);

    @Query("SELECT p.categoria, COUNT(p) FROM Produto p GROUP BY p.categoria")
    List<Object[]> countByCategoria();


    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(p.codigoInterno, 6) AS INTEGER)), 0) + 1 " +
           "FROM Produto p WHERE p.codigoInterno LIKE 'PROD-%'")
    Optional<Integer> findProximoCodigoInterno();

    @Query("SELECT e.produto FROM Estoque e WHERE e.quantidadeAtual < e.produto.estoqueMinimo")
    List<Produto> findProdutosComEstoqueAbaixoMinimo();
    
    @Query("SELECT p, SUM(ios.quantidade) as totalUtilizado " +
           "FROM ItemOrdemServico ios JOIN ios.produto p " +
           "WHERE ios.ordemServico.status = 'FINALIZADA' " +
           "GROUP BY p " +
           "ORDER BY totalUtilizado DESC")
    List<Object[]> findProdutosMaisUtilizados();
}