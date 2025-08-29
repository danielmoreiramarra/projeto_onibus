package com.proj_db.onibus.service;

import java.util.List;
import java.util.Map;

import com.proj_db.onibus.model.Estoque;

public interface EstoqueService {
    
    Estoque criarRegistroEstoque(Estoque estoque);
    Estoque atualizarEstoque(Long id, Estoque estoqueAtualizado);
    
    Estoque buscarPorProdutoId(Long produtoId);
    List<Estoque> buscarTodos();
    List<Estoque> buscarPorLocalizacao(String localizacao);
    
    // ✅ NOVO MÉTODO: Busca combinada para todos os campos
    List<Estoque> searchEstoque(Map<String, String> searchTerms);
    
    List<Estoque> buscarEstoqueAbaixoMinimo();
    List<Estoque> buscarEstoqueCritico();
    List<Estoque> buscarEstoqueParaReabastecer();
    
    Estoque adicionarEstoque(Long produtoId, Integer quantidade);
    Estoque reservarEstoque(Long produtoId, Integer quantidade);
    Estoque consumirEstoque(Long produtoId, Integer quantidade);
    Estoque liberarReserva(Long produtoId, Integer quantidade);
    
    Integer consultarQuantidadeDisponivel(Long produtoId);
    Integer consultarQuantidadeReservada(Long produtoId);
    Integer consultarQuantidadeTotal(Long produtoId);
    
    boolean verificarDisponibilidade(Long produtoId, Integer quantidade);
    boolean verificarEstoqueMinimo(Long produtoId);
    
    Double calcularValorTotalEstoque();
    List<Object[]> calcularValorTotalPorCategoria();
    List<Object[]> buscarProdutosComMaiorGiro();
    boolean precisaReabastecer(Long produtoId);
    boolean consumirReserva(Long produtoId, Integer quantidade);
    List<Estoque> getAlertasEstoque();

    // ✅ NOVOS MÉTODOS DE RELATÓRIO
    List<Object[]> findGiroPorCategoria();
    List<Object[]> findGiroPorMarca();
    List<Object[]> findGiroPorLocal();

}