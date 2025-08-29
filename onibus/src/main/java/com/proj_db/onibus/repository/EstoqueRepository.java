package com.proj_db.onibus.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Estoque;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    
    // ✅ MÉTODOS DE BUSCA INDIVIDUAIS
    Optional<Estoque> findByProdutoId(Long produtoId);
    List<Estoque> findByLocalizacaoFisica(String localizacaoFisica);
    
    // ✅ NOVA CONSULTA COMBINADA PARA TODOS OS CAMPOS
    @Query("SELECT e FROM Estoque e JOIN e.produto p WHERE " +
           "(:estoqueId IS NULL OR e.id = :estoqueId) AND " +
           "(:produtoId IS NULL OR p.id = :produtoId) AND " +
           "(:localizacaoFisica IS NULL OR e.localizacaoFisica LIKE %:localizacaoFisica%) AND " +
           "(:categoria IS NULL OR p.categoria = :categoria) AND " +
           "(:marca IS NULL OR p.marca LIKE %:marca%) AND " +
           "(:unidadeMedida IS NULL OR p.unidadeMedida = :unidadeMedida) AND " +
           "(:quantidadeAtualMin IS NULL OR e.quantidadeAtual >= :quantidadeAtualMin) AND " +
           "(:quantidadeAtualMax IS NULL OR e.quantidadeAtual <= :quantidadeAtualMax) AND " +
           "(:quantidadeReservadaMin IS NULL OR e.quantidadeReservada >= :quantidadeReservadaMin) AND " +
           "(:quantidadeReservadaMax IS NULL OR e.quantidadeReservada <= :quantidadeReservadaMax) AND " +
           "(:dataEntradaMin IS NULL OR e.dataUltimaEntrada >= :dataEntradaMin) AND " +
           "(:dataEntradaMax IS NULL OR e.dataUltimaEntrada <= :dataEntradaMax) AND " +
           "(:dataSaidaMin IS NULL OR e.dataUltimaSaida >= :dataSaidaMin) AND " +
           "(:dataSaidaMax IS NULL OR e.dataUltimaSaida <= :dataSaidaMax)")
    List<Estoque> searchEstoque(
        @Param("estoqueId") Long estoqueId,
        @Param("produtoId") Long produtoId,
        @Param("localizacaoFisica") String localizacaoFisica,
        @Param("categoria") String categoria,
        @Param("marca") String marca,
        @Param("unidadeMedida") String unidadeMedida,
        @Param("quantidadeAtualMin") Double quantidadeAtualMin,
        @Param("quantidadeAtualMax") Double quantidadeAtualMax,
        @Param("quantidadeReservadaMin") Double quantidadeReservadaMin,
        @Param("quantidadeReservadaMax") Double quantidadeReservadaMax,
        @Param("dataEntradaMin") LocalDate dataEntradaMin,
        @Param("dataEntradaMax") LocalDate dataEntradaMax,
        @Param("dataSaidaMin") LocalDate dataSaidaMin,
        @Param("dataSaidaMax") LocalDate dataSaidaMax
    );

    // ✅ MÉTODOS DE RELATÓRIO
    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual < e.produto.estoqueMinimo")
    List<Estoque> findEstoqueAbaixoMinimo();

    @Query("SELECT e FROM Estoque e WHERE e.quantidadeAtual <= 5")
    List<Estoque> findEstoqueCritico();

    @Query("SELECT COALESCE(SUM(e.quantidadeAtual * e.produto.precoUnitario), 0) FROM Estoque e")
    Double calcularValorTotalEstoque();

    @Query("SELECT e FROM Estoque e WHERE (e.dataUltimaEntrada IS NULL OR e.dataUltimaEntrada < :dataLimite) AND " +
           "(e.dataUltimaSaida IS NULL OR e.dataUltimaSaida < :dataLimite) " +
           "ORDER BY COALESCE(e.dataUltimaEntrada, e.dataUltimaSaida)")
    List<Estoque> findEstoqueInativo(@Param("dataLimite") LocalDate dataLimite);
    
    @Query("SELECT e.produto.categoria, COALESCE(SUM(e.quantidadeAtual * e.produto.precoUnitario), 0) " +
           "FROM Estoque e GROUP BY e.produto.categoria")
    List<Object[]> calcularValorTotalPorCategoria();

    @Query("SELECT e.produto.marca, COALESCE(SUM(e.quantidadeAtual * e.produto.precoUnitario), 0) " +
           "FROM Estoque e GROUP BY e.produto.marca")
    List<Object[]> calcularValorTotalPorMarca();

    @Query("SELECT e.produto.id, e.produto.nome, " +
           "(SELECT COALESCE(SUM(ios.quantidade), 0) FROM ItemOrdemServico ios " +
           " WHERE ios.produto.id = e.produto.id AND ios.ordemServico.status = 'FINALIZADA') as giro " +
           "FROM Estoque e ORDER BY giro DESC")
    List<Object[]> findProdutosComMaiorGiro();
    
    // ✅ NOVOS MÉTODOS DE RELATÓRIO DE GIRO
    @Query("SELECT p.categoria, SUM(ios.quantidade) FROM Estoque e JOIN e.produto p JOIN ItemOrdemServico ios ON p.id = ios.produto.id GROUP BY p.categoria")
    List<Object[]> findGiroPorCategoria();
    
    @Query("SELECT p.marca, SUM(ios.quantidade) FROM Estoque e JOIN e.produto p JOIN ItemOrdemServico ios ON p.id = ios.produto.id GROUP BY p.marca")
    List<Object[]> findGiroPorMarca();
    
    @Query("SELECT e.localizacaoFisica, SUM(e.quantidadeAtual) FROM Estoque e GROUP BY e.localizacaoFisica")
    List<Object[]> findGiroPorLocal();
}