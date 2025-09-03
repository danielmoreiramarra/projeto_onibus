package com.proj_db.onibus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.proj_db.onibus.model.Estoque;
import com.proj_db.onibus.model.Produto;

public interface EstoqueRepository extends JpaRepository<Estoque, Long>, JpaSpecificationExecutor<Estoque> {
    Optional<Estoque> findByProduto(Produto produto);
    Optional<Estoque> findByProdutoId(Long produtoId);

    // Query para encontrar itens que estão abaixo do estoque mínimo definido no produto
    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual < e.produto.estoqueMinimo")
    List<Estoque> findItensAbaixoDoEstoqueMinimo();
    
    @Query("SELECT SUM(e.quantidadeAtual * rc.precoUnitario) " +
           "FROM Estoque e " +
           "JOIN e.produto p " +
           "JOIN p.historicoPrecoUnitario rc " +
           "WHERE rc.data = (SELECT MAX(rc2.data) FROM p.historicoPrecoUnitario rc2)")
    Optional<Double> calcularValorTotalInventario();

    @Query("SELECT p.categoria, SUM(e.quantidadeAtual * rc.precoUnitario) " +
           "FROM Estoque e " +
           "JOIN e.produto p " +
           "JOIN p.historicoPrecoUnitario rc " +
           "WHERE rc.data = (SELECT MAX(rc2.data) FROM p.historicoPrecoUnitario rc2) " +
           "GROUP BY p.categoria")
    List<Object[]> calcularValorTotalPorCategoria();
}