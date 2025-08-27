package com.proj_db.onibus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Estoque;
import com.proj_db.onibus.model.Produto.Categoria;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    
    // buscar estoque por produto
    Optional<Estoque> findByProdutoId(Long produtoId);
    
    // buscar estoque por localização
    List<Estoque> findByLocalizacaoFisica(String localizacaoFisica);
    
    // buscar estoque com quantidade maior que
    List<Estoque> findByQuantidadeAtualGreaterThan(Integer quantidade);
    
    // buscar estoque com quantidade menor que
    List<Estoque> findByQuantidadeAtualLessThan(Integer quantidade);
    
    // buscar estoque abaixo do mínimo
    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual < e.produto.estoqueMinimo")
    List<Estoque> findEstoqueAbaixoMinimo();
    
    // buscar estoque com quantidade disponível maior que
    @Query("SELECT e FROM Estoque e WHERE (e.quantidadeAtual - e.quantidadeReservada) >= :quantidade")
    List<Estoque> findByQuantidadeDisponivelGreaterThanEqual(@Param("quantidade") Integer quantidade);
    
    // buscar estoque com quantidade disponível menor que
    @Query("SELECT e FROM Estoque e WHERE (e.quantidadeAtual - e.quantidadeReservada) <= :quantidade")
    List<Estoque> findByQuantidadeDisponivelLessThanEqual(@Param("quantidade") Integer quantidade);
    
    // buscar estoque por categoria de produto
    @Query("SELECT e FROM Estoque e WHERE e.produto.categoria = :categoria")
    List<Estoque> findByCategoriaProduto(@Param("categoria") Categoria categoria);
    
    // buscar estoque que precisa ser reabastecido
    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual <= e.produto.estoqueMinimo")
    List<Estoque> findEstoqueParaReabastecer();
    
    // Calcular valor total do estoque
    @Query("SELECT COALESCE(SUM(e.quantidadeAtual * e.produto.precoUnitario), 0) FROM Estoque e")
    Double calcularValorTotalEstoque();
    
    // Calcular valor total por categoria
    @Query("SELECT e.produto.categoria, COALESCE(SUM(e.quantidadeAtual * e.produto.precoUnitario), 0) " +
           "FROM Estoque e GROUP BY e.produto.categoria")
    List<Object[]> calcularValorTotalPorCategoria();
    
    // buscar produtos com maior giro
    @Query("SELECT e.produto.id, e.produto.nome, " +
           "(SELECT COALESCE(SUM(ios.quantidade), 0) FROM ItemOrdemServico ios " +
           " WHERE ios.produto.id = e.produto.id AND ios.ordemServico.status = 'FINALIZADA') as giro " +
           "FROM Estoque e ORDER BY giro DESC")
    List<Object[]> findProdutosComMaiorGiro();
    
    // buscar movimentação de estoque por período
    @Query("SELECT e.produto.nome, " +
           "SUM(CASE WHEN e.dataUltimaEntrada BETWEEN :startDate AND :endDate THEN 1 ELSE 0 END) as entradas, " +
           "SUM(CASE WHEN e.dataUltimaSaida BETWEEN :startDate AND :endDate THEN 1 ELSE 0 END) as saidas " +
           "FROM Estoque e " +
           "GROUP BY e.produto.id, e.produto.nome")
    List<Object[]> findMovimentacaoEstoquePorPeriodo(
        @Param("startDate") java.time.LocalDate startDate,
        @Param("endDate") java.time.LocalDate endDate);
    
    // buscar estoque crítico (próximo de zerar)
    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual <= 5")
    List<Estoque> findEstoqueCritico();
    
    // buscar estoque com maior tempo sem movimentação
    @Query("SELECT e FROM Estoque e WHERE " +
           "(e.dataUltimaEntrada IS NULL OR e.dataUltimaEntrada < :dataLimite) AND " +
           "(e.dataUltimaSaida IS NULL OR e.dataUltimaSaida < :dataLimite) " +
           "ORDER BY COALESCE(e.dataUltimaEntrada, e.dataUltimaSaida)")
    List<Estoque> findEstoqueInativo(@Param("dataLimite") java.time.LocalDate dataLimite);
    
}