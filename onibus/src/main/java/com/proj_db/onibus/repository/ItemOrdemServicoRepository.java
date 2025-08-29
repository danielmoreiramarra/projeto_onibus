package com.proj_db.onibus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.ItemOrdemServico;

public interface ItemOrdemServicoRepository extends JpaRepository<ItemOrdemServico, Long> {
    
    // ✅ MÉTODOS DE BUSCA INDIVIDUAIS
    List<ItemOrdemServico> findByOrdemServicoId(Long ordemServicoId);
    
    List<ItemOrdemServico> findByProdutoId(Long produtoId);
    
    Optional<ItemOrdemServico> findByOrdemServicoIdAndProdutoId(Long ordemServicoId, Long produtoId);
    
    List<ItemOrdemServico> findByQuantidadeGreaterThan(Integer quantidade);
    
    // ✅ NOVA CONSULTA COMBINADA PARA TODOS OS CAMPOS
    @Query("SELECT ios FROM ItemOrdemServico ios WHERE " +
           "(:itemId IS NULL OR ios.id = :itemId) AND " +
           "(:ordemServicoId IS NULL OR ios.ordemServico.id = :ordemServicoId) AND " +
           "(:produtoId IS NULL OR ios.produto.id = :produtoId) AND " +
           "(:quantidade IS NULL OR ios.quantidade = :quantidade) AND " +
           "(:precoUnitario IS NULL OR ios.precoUnitario = :precoUnitario) AND " +
           "(:descricao IS NULL OR ios.descricao LIKE %:descricao%)")
    List<ItemOrdemServico> searchItemOrdemServico(
        @Param("itemId") Long itemId,
        @Param("ordemServicoId") Long ordemServicoId,
        @Param("produtoId") Long produtoId,
        @Param("quantidade") Integer quantidade,
        @Param("precoUnitario") Double precoUnitario,
        @Param("descricao") String descricao
    );
    
    // ✅ MÉTODOS DE RELATÓRIO
    @Query("SELECT COALESCE(SUM(ios.quantidade), 0) FROM ItemOrdemServico ios " +
           "WHERE ios.produto.id = :produtoId AND ios.ordemServico.status = 'FINALIZADA'")
    Integer calcularQuantidadeTotalConsumida(@Param("produtoId") Long produtoId);
    
    @Query("SELECT COALESCE(SUM(ios.quantidade), 0) FROM ItemOrdemServico ios " +
           "WHERE ios.produto.id = :produtoId AND ios.ordemServico.status = 'EM_EXECUCAO'")
    Integer calcularQuantidadeTotalReservada(@Param("produtoId") Long produtoId);
    
    @Query("SELECT ios.produto.id, ios.produto.nome, SUM(ios.quantidade) as total " +
           "FROM ItemOrdemServico ios " +
           "WHERE ios.ordemServico.status = 'FINALIZADA' " +
           "GROUP BY ios.produto.id, ios.produto.nome " +
           "ORDER BY total DESC")
    List<Object[]> findProdutosMaisUtilizados();
    
    @Query("SELECT ios.ordemServico.dataConclusao, ios.quantidade, ios.ordemServico.numeroOS " +
           "FROM ItemOrdemServico ios " +
           "WHERE ios.produto.id = :produtoId AND ios.ordemServico.status = 'FINALIZADA' " +
           "ORDER BY ios.ordemServico.dataConclusao DESC")
    List<Object[]> findHistoricoConsumoPorProduto(@Param("produtoId") Long produtoId);
    
    @Query("SELECT SUM(ios.quantidade * ios.precoUnitario) FROM ItemOrdemServico ios " +
           "WHERE ios.ordemServico.id = :ordemServicoId")
    Optional<Double> calcularValorTotalPorOrdemServico(@Param("ordemServicoId") Long ordemServicoId);

    // ✅ NOVO MÉTODO: Calcular a soma total das quantidades por ordem de serviço
    @Query("SELECT SUM(ios.quantidade) FROM ItemOrdemServico ios WHERE ios.ordemServico.id = :ordemServicoId")
    Integer sumQuantidadeByOrdemServicoId(@Param("ordemServicoId") Long ordemServicoId);
}