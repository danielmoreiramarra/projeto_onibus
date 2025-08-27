package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.Estoque;
import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.model.Produto.Categoria;
import com.proj_db.onibus.model.Produto.StatusProduto;
import com.proj_db.onibus.model.Produto.UnidadeMedida;
import com.proj_db.onibus.repository.ProdutoRepository;

@Service
@Transactional
public class ProdutoImpl implements ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private EstoqueService estoqueService;

    @Override
    public Produto criarProduto(Produto produto) {
        if (existeCodigoInterno(produto.getCodigoInterno())) {
            throw new RuntimeException("Já existe um produto com este código interno: " + produto.getCodigoInterno());
        }
        
        if (produto.getCodigoBarras() != null && existeCodigoBarras(produto.getCodigoBarras())) {
            throw new RuntimeException("Já existe um produto com este código de barras: " + produto.getCodigoBarras());
        }
        
        if (produto.getStatus() == null) {
            produto.setStatus(StatusProduto.ATIVO);
        }
        
        if (produto.getEstoqueMinimo() == null) {
            produto.setEstoqueMinimo(5);
        }
        
        return produtoRepository.save(produto);
    }

    @Override
    public Produto atualizarProduto(Long id, Produto produtoAtualizado) {
        Produto produtoExistente = buscarProdutoPorId(id);
        
        if (!produtoExistente.getCodigoInterno().equals(produtoAtualizado.getCodigoInterno()) && 
            existeCodigoInterno(produtoAtualizado.getCodigoInterno())) {
            throw new RuntimeException("Já existe um produto com este código interno: " + produtoAtualizado.getCodigoInterno());
        }
        
        if (produtoAtualizado.getCodigoBarras() != null && 
            !produtoAtualizado.getCodigoBarras().equals(produtoExistente.getCodigoBarras()) && 
            existeCodigoBarras(produtoAtualizado.getCodigoBarras())) {
            throw new RuntimeException("Já existe um produto com este código de barras: " + produtoAtualizado.getCodigoBarras());
        }
        
        produtoExistente.setNome(produtoAtualizado.getNome());
        produtoExistente.setMarca(produtoAtualizado.getMarca());
        produtoExistente.setDescricao(produtoAtualizado.getDescricao());
        produtoExistente.setCategoria(produtoAtualizado.getCategoria());
        produtoExistente.setUnidadeMedida(produtoAtualizado.getUnidadeMedida());
        produtoExistente.setPrecoUnitario(produtoAtualizado.getPrecoUnitario());
        produtoExistente.setEstoqueMinimo(produtoAtualizado.getEstoqueMinimo());
        produtoExistente.setLocalizacao(produtoAtualizado.getLocalizacao());
        
        return produtoRepository.save(produtoExistente);
    }

    @Override
    public void excluirProduto(Long id) {
        Produto produto = buscarProdutoPorId(id);
        
        if (produtoEstaEmUso(id)) {
            throw new RuntimeException("Não é possível excluir produto que está em uso");
        }
        
        produto.setStatus(StatusProduto.INATIVO);
        produtoRepository.save(produto);
    }

    @Override
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    @Override
    public List<Produto> buscarTodos() {
        return produtoRepository.findAll();
    }

    @Override
    public Optional<Produto> buscarPorCodigoInterno(String codigoInterno) {
        return produtoRepository.findByCodigoInterno(codigoInterno);
    }

    @Override
    public Optional<Produto> buscarPorCodigoBarras(String codigoBarras) {
        return produtoRepository.findByCodigoBarras(codigoBarras);
    }

    @Override
    public List<Produto> buscarPorStatus(StatusProduto status) {
        return produtoRepository.findByStatus(status);
    }

    @Override
    public List<Produto> buscarPorMarca(String marca) {
        return produtoRepository.findByMarca(marca);
    }

    @Override
    public List<Produto> buscarPorCategoria(Categoria categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    @Override
    public List<Produto> buscarPorUnidadeMedida(UnidadeMedida unidadeMedida) {
        return produtoRepository.findByUnidadeMedida(unidadeMedida);
    }

    @Override
    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Override
    public List<Produto> buscarPorIntervaloPreco(Double precoMinimo, Double precoMaximo) {
        return produtoRepository.findByPrecoUnitarioBetween(precoMinimo, precoMaximo);
    }

    @Override
    public List<Produto> buscarProdutosAtivos() {
        return produtoRepository.findProdutosAtivos();
    }

    @Override
    public List<Produto> buscarProdutosComEstoqueAbaixoMinimo() {
        return produtoRepository.findProdutosComEstoqueAbaixoMinimo();
    }

    @Override
    public List<Produto> buscarProdutosNuncaUtilizados() {
        return produtoRepository.findProdutosNuncaUtilizados();
    }

    @Override
    public List<Produto> buscarProdutosSemMovimento() {
        LocalDate seisMesesAtras = LocalDate.now().minusMonths(6);
        return produtoRepository.findProdutosSemMovimento(seisMesesAtras);
    }

    @Override
    public boolean existeCodigoInterno(String codigoInterno) {
        return produtoRepository.existsByCodigoInterno(codigoInterno);
    }

    @Override
    public boolean existeCodigoBarras(String codigoBarras) {
        return produtoRepository.existsByCodigoBarras(codigoBarras);
    }

    @Override
    public String gerarProximoCodigoInterno() {
        Integer proximoNumero = produtoRepository.findProximoCodigoInterno();
        return "PROD-" + String.format("%04d", proximoNumero);
    }

    @Override
    public List<Object[]> buscarProdutosMaisUtilizados() {
        return produtoRepository.findProdutosMaisUtilizados();
    }

    @Override
    public List<Object[]> buscarProdutosPorGiro() {
        return produtoRepository.findProdutosPorGiro();
    }

    @Override
    public List<Object[]> buscarEstatisticasPorCategoria() {
        return produtoRepository.countProdutosPorCategoria();
    }

    private Produto buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado com ID: " + id));
    }

    private boolean produtoEstaEmUso(Long produtoId) {
        Estoque estoque = estoqueService.buscarPorProdutoId(produtoId);
        return estoque.getQuantidadeAtual() > 0 || estoque.getQuantidadeReservada() > 0;
    }
}