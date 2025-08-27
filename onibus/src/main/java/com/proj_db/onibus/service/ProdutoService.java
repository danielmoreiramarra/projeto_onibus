package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.model.Produto.Categoria;
import com.proj_db.onibus.model.Produto.StatusProduto;
import com.proj_db.onibus.model.Produto.UnidadeMedida;

public interface ProdutoService {
    
    Produto criarProduto(Produto produto);
    Produto atualizarProduto(Long id, Produto produtoAtualizado);
    void excluirProduto(Long id);
    
    Optional<Produto> buscarPorId(Long id);
    List<Produto> buscarTodos();
    Optional<Produto> buscarPorCodigoInterno(String codigoInterno);
    Optional<Produto> buscarPorCodigoBarras(String codigoBarras);
    
    List<Produto> buscarPorStatus(StatusProduto status);
    List<Produto> buscarPorMarca(String marca);
    List<Produto> buscarPorCategoria(Categoria categoria);
    List<Produto> buscarPorUnidadeMedida(UnidadeMedida unidadeMedida);
    List<Produto> buscarPorNome(String nome);
    List<Produto> buscarPorIntervaloPreco(Double precoMinimo, Double precoMaximo);
    
    List<Produto> buscarProdutosAtivos();
    List<Produto> buscarProdutosComEstoqueAbaixoMinimo();
    List<Produto> buscarProdutosNuncaUtilizados();
    List<Produto> buscarProdutosSemMovimento();
    
    boolean existeCodigoInterno(String codigoInterno);
    boolean existeCodigoBarras(String codigoBarras);
    
    String gerarProximoCodigoInterno();
    List<Object[]> buscarProdutosMaisUtilizados();
    List<Object[]> buscarProdutosPorGiro();
    List<Object[]> buscarEstatisticasPorCategoria();
}