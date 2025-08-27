package com.proj_db.onibus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.ItemOrdemServico;

public interface ItemOrdemServicoRepository extends JpaRepository<ItemOrdemServico, Long> {
    
    // Buscar itens por ordem de serviço
    List<ItemOrdemServico> findByOrdemServicoId(Long ordemServicoId);
    
    // Buscar itens por produto
    List<ItemOrdemServico> findByProdutoId(Long produtoId);
    
    // Buscar itens por ordem de serviço e produto
    Optional<ItemOrdemServico> findByOrdemServicoIdAndProdutoId(Long ordemServicoId, Long produtoId);
    
    // Buscar itens com quantidade maior que
    List<ItemOrdemServico> findByQuantidadeGreaterThan(Integer quantidade);
    
    // Calcular quantidade total consumida de um produto
    @Query("SELECT COALESCE(SUM(ios.quantidade), 0) FROM ItemOrdemServico ios " +
           "WHERE ios.produto.id = :produtoId AND ios.ordemServico.status = 'FINALIZADA'")
    Integer calcularQuantidadeTotalConsumida(@Param("produtoId") Long produtoId);
    
    // Calcular quantidade total reservada de um produto (OSs em execução)
    @Query("SELECT COALESCE(SUM(ios.quantidade), 0) FROM ItemOrdemServico ios " +
           "WHERE ios.produto.id = :produtoId AND ios.ordemServico.status = 'EM_EXECUCAO'")
    Integer calcularQuantidadeTotalReservada(@Param("produtoId") Long produtoId);
    
    // Buscar produtos mais utilizados
    @Query("SELECT ios.produto.id, ios.produto.nome, SUM(ios.quantidade) as total " +
           "FROM ItemOrdemServico ios " +
           "WHERE ios.ordemServico.status = 'FINALIZADA' " +
           "GROUP BY ios.produto.id, ios.produto.nome " +
           "ORDER BY total DESC")
    List<Object[]> findProdutosMaisUtilizados();
    
    // Buscar histórico de consumo por produto
    @Query("SELECT ios.ordemServico.dataConclusao, ios.quantidade, ios.ordemServico.numeroOS " +
           "FROM ItemOrdemServico ios " +
           "WHERE ios.produto.id = :produtoId AND ios.ordemServico.status = 'FINALIZADA' " +
           "ORDER BY ios.ordemServico.dataConclusao DESC")
    List<Object[]> findHistoricoConsumoPorProduto(@Param("produtoId") Long produtoId);
    
    // Calcular valor total por ordem de serviço
    @Query("SELECT ios.ordemServico.id, SUM(ios.quantidade * ios.precoUnitario) " +
           "FROM ItemOrdemServico ios " +
           "WHERE ios.ordemServico.id = :ordemServicoId " +
           "GROUP BY ios.ordemServico.id")
    Optional<Double> calcularValorTotalPorOrdemServico(@Param("ordemServicoId") Long ordemServicoId);
    
}