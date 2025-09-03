package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.Estoque;
import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.repository.EstoqueRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class EstoqueImpl implements EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Estoque> findById(Long id) {
        return estoqueRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estoque> findByProduto(Produto produto) {
        return estoqueRepository.findByProduto(produto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Estoque> findByProdutoId(Long produtoId) {
        return estoqueRepository.findByProdutoId(produtoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Estoque> findAll() {
        return estoqueRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Estoque> search(EstoqueSearchDTO criteria) {
        return estoqueRepository.findAll(EstoqueSpecification.searchByCriteria(criteria));
    }

    // --- Ações de Negócio ---

    @Override
    public Estoque adicionar(Long produtoId, Double quantidade) {
        Estoque estoque = findByProdutoId(produtoId)
            .orElseThrow(() -> new EntityNotFoundException("Registro de estoque não encontrado para o produto ID: " + produtoId));
        
        estoque.adicionarEstoque(quantidade);
        return estoqueRepository.save(estoque);
    }

    @Override
    public boolean reservar(Long produtoId, Double quantidade) {
        Estoque estoque = findByProdutoId(produtoId)
            .orElseThrow(() -> new EntityNotFoundException("Registro de estoque não encontrado para o produto ID: " + produtoId));
        
        boolean sucesso = estoque.reservarEstoque(quantidade);
        if (sucesso) {
            estoqueRepository.save(estoque);
        }
        return sucesso;
    }

    @Override
    public void confirmarConsumoDeReserva(Long produtoId, Double quantidade) {
        Estoque estoque = findByProdutoId(produtoId)
            .orElseThrow(() -> new EntityNotFoundException("Registro de estoque não encontrado para o produto ID: " + produtoId));
        
        estoque.confirmarConsumoReserva(quantidade);
        estoqueRepository.save(estoque);
    }
    
    @Override
    public void liberarReserva(Long produtoId, Double quantidade) {
        Estoque estoque = findByProdutoId(produtoId)
            .orElseThrow(() -> new EntityNotFoundException("Registro de estoque não encontrado para o produto ID: " + produtoId));
        
        estoque.liberarReserva(quantidade);
        estoqueRepository.save(estoque);
    }

    // --- Relatórios e Alertas ---

    @Override
    @Transactional(readOnly = true)
    public List<Estoque> findEstoqueAbaixoDoMinimo() {
        return estoqueRepository.findItensAbaixoDoEstoqueMinimo();
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularValorTotalInventario() {
        return estoqueRepository.calcularValorTotalInventario().orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> calcularValorTotalPorCategoria() {
        return estoqueRepository.calcularValorTotalPorCategoria();
    }
}