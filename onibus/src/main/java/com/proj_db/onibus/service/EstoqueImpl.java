package com.proj_db.onibus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.Estoque;
import com.proj_db.onibus.repository.EstoqueRepository;

@Service
@Transactional
public class EstoqueImpl implements EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Override
    public Estoque criarRegistroEstoque(Estoque estoque) {
        if (estoqueRepository.findByProdutoId(estoque.getProduto().getId()).isPresent()) {
            throw new RuntimeException("Já existe registro de estoque para este produto");
        }
        
        if (estoque.getQuantidadeAtual() == null) {
            estoque.setQuantidadeAtual(0);
        }
        
        if (estoque.getQuantidadeReservada() == null) {
            estoque.setQuantidadeReservada(0);
        }
        
        return estoqueRepository.save(estoque);
    }

    @Override
    public Estoque atualizarEstoque(Long id, Estoque estoqueAtualizado) {
        Estoque estoqueExistente = buscarPorId(id);
        
        if (estoqueExistente == null) {
            throw new RuntimeException("Registro de estoque não encontrado com ID: " + id);
        }
        
        // Atualizar campos permitidos
        estoqueExistente.setLocalizacaoFisica(estoqueAtualizado.getLocalizacaoFisica());
        
        return estoqueRepository.save(estoqueExistente);
    }

    @Override
    public Estoque buscarPorProdutoId(Long produtoId) {
        return estoqueRepository.findByProdutoId(produtoId)
            .orElseThrow(() -> new RuntimeException("Registro de estoque não encontrado para o produto ID: " + produtoId));
    }

    @Override
    public List<Estoque> buscarTodos() {
        return estoqueRepository.findAll();
    }

    @Override
    public List<Estoque> buscarPorLocalizacao(String localizacao) {
        return estoqueRepository.findByLocalizacaoFisica(localizacao);
    }

    @Override
    public List<Estoque> buscarEstoqueAbaixoMinimo() {
        return estoqueRepository.findEstoqueAbaixoMinimo();
    }

    @Override
    public List<Estoque> buscarEstoqueCritico() {
        return estoqueRepository.findEstoqueCritico();
    }

    @Override
    public List<Estoque> buscarEstoqueParaReabastecer() {
        return estoqueRepository.findEstoqueParaReabastecer();
    }

    @Override
    public boolean adicionarEstoque(Long produtoId, Integer quantidade) {
        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser positiva");
        }
        
        Estoque estoque = buscarPorProdutoId(produtoId);
        estoque.adicionarEstoque(quantidade); // Usando a lógica do modelo
        estoqueRepository.save(estoque);
        
        return true;
    }

    @Override
    public boolean reservarEstoque(Long produtoId, Integer quantidade) {
        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser positiva");
        }
        
        Estoque estoque = buscarPorProdutoId(produtoId);
        
        if (!estoque.reservarEstoque(quantidade)) { // Usando a lógica do modelo
            throw new RuntimeException("Estoque insuficiente para reserva. Disponível: " + estoque.getQuantidadeDisponivel() + ", Solicitado: " + quantidade);
        }
        
        estoqueRepository.save(estoque);
        
        return true;
    }

    @Override
    public boolean consumirEstoque(Long produtoId, Integer quantidade) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        
        if (!estoque.consumirEstoque(quantidade)) { // Usando a lógica do modelo
            throw new RuntimeException("Estoque insuficiente para consumo. Disponível: " + estoque.getQuantidadeAtual() + ", Solicitado: " + quantidade);
        }
        
        estoqueRepository.save(estoque);
        
        return true;
    }

    @Override
    public boolean liberarReserva(Long produtoId, Integer quantidade) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        estoque.liberarReserva(quantidade); // Usando a lógica do modelo
        estoqueRepository.save(estoque);
        
        return true;
    }

    @Override
    public Integer consultarQuantidadeDisponivel(Long produtoId) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        return estoque.getQuantidadeDisponivel();
    }

    @Override
    public Integer consultarQuantidadeReservada(Long produtoId) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        return estoque.getQuantidadeReservada();
    }

    @Override
    public Integer consultarQuantidadeTotal(Long produtoId) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        return estoque.getQuantidadeAtual();
    }

    @Override
    public boolean verificarDisponibilidade(Long produtoId, Integer quantidade) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        return estoque.estaDisponivel(quantidade); // Usando a lógica do modelo
    }

    @Override
    public boolean verificarEstoqueMinimo(Long produtoId) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        return estoque.estaAbaixoEstoqueMinimo(); // Usando a lógica do modelo
    }

    @Override
    public Double calcularValorTotalEstoque() {
        return estoqueRepository.calcularValorTotalEstoque();
    }

    @Override
    public List<Object[]> calcularValorTotalPorCategoria() {
        return estoqueRepository.calcularValorTotalPorCategoria();
    }

    @Override
    public List<Object[]> buscarProdutosComMaiorGiro() {
        return estoqueRepository.findProdutosComMaiorGiro();
    }

    private Estoque buscarPorId(Long id) {
        return estoqueRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Estoque não encontrado com ID: " + id));
    }

    @Override
    public boolean consumirReserva(Long produtoId, Integer quantidade) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        estoque.confirmarConsumoReserva(quantidade); // Usando a lógica do modelo
        estoqueRepository.save(estoque);
        return true;
    }

    @Override
    public boolean precisaReabastecer(Long produtoId) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        return estoque.estaAbaixoEstoqueMinimo(); // Usando a lógica do modelo
    }

    @Override
    public List<Estoque> getAlertasEstoque() {
        return estoqueRepository.findEstoqueParaReabastecer(); // Usando a query mais apropriada
    }
}