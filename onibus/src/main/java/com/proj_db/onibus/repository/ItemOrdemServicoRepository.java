package com.proj_db.onibus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.ItemOrdemServico;

public interface ItemOrdemServicoRepository extends JpaRepository<ItemOrdemServico, Long>, JpaSpecificationExecutor<ItemOrdemServico> {
    List<ItemOrdemServico> findByOrdemServicoId(Long ordemServicoId);
    List<ItemOrdemServico> findByProdutoId(Long produtoId);
    Optional<ItemOrdemServico> findByOrdemServicoIdAndProdutoId(Long ordemServicoId, Long produtoId);

    // Query para relatórios: agrupa o total gasto por produto
    @Query("SELECT i.produto.nome, SUM(i.subtotal) FROM ItemOrdemServico i WHERE i.ordemServico.status = 'FINALIZADA' GROUP BY i.produto.nome")
    List<Object[]> sumGastoTotalPorProduto();

    @Query("SELECT SUM(ios.quantidade * ios.precoUnitarioRegistrado) FROM ItemOrdemServico ios " +
           "WHERE ios.ordemServico.id = :ordemServicoId")
    Optional<Double> calcularValorTotalPorOrdemServico(@Param("ordemServicoId") Long ordemServicoId);

    @Query("SELECT ios.ordemServico.dataConclusao, ios.quantidade, ios.ordemServico.numeroOS " +
           "FROM ItemOrdemServico ios " +
           "WHERE ios.produto.id = :produtoId AND ios.ordemServico.status = 'FINALIZADA' " +
           "ORDER BY ios.ordemServico.dataConclusao DESC")
    List<Object[]> findHistoricoConsumoPorProduto(@Param("produtoId") Long produtoId);

}