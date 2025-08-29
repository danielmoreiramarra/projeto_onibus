package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.Estoque;
import com.proj_db.onibus.repository.EstoqueRepository;
import com.proj_db.onibus.repository.ProdutoRepository;

@Service
@Transactional
public class EstoqueImpl implements EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

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
    public List<Estoque> searchEstoque(Map<String, String> searchTerms) {
        Long estoqueId = searchTerms.containsKey("estoqueId") && !searchTerms.get("estoqueId").isEmpty() ? Long.valueOf(searchTerms.get("estoqueId")) : null;
        Long produtoId = searchTerms.containsKey("produtoId") && !searchTerms.get("produtoId").isEmpty() ? Long.valueOf(searchTerms.get("produtoId")) : null;
        String localizacaoFisica = searchTerms.get("localizacaoFisica");
        String categoria = searchTerms.get("categoria");
        String marca = searchTerms.get("marca");
        String unidadeMedida = searchTerms.get("unidadeMedida");
        Double quantidadeAtualMin = searchTerms.containsKey("quantidadeAtualMin") && !searchTerms.get("quantidadeAtualMin").isEmpty() ? Double.valueOf(searchTerms.get("quantidadeAtualMin")) : null;
        Double quantidadeAtualMax = searchTerms.containsKey("quantidadeAtualMax") && !searchTerms.get("quantidadeAtualMax").isEmpty() ? Double.valueOf(searchTerms.get("quantidadeAtualMax")) : null;
        Double quantidadeReservadaMin = searchTerms.containsKey("quantidadeReservadaMin") && !searchTerms.get("quantidadeReservadaMin").isEmpty() ? Double.valueOf(searchTerms.get("quantidadeReservadaMin")) : null;
        Double quantidadeReservadaMax = searchTerms.containsKey("quantidadeReservadaMax") && !searchTerms.get("quantidadeReservadaMax").isEmpty() ? Double.valueOf(searchTerms.get("quantidadeReservadaMax")) : null;
        LocalDate dataEntradaMin = searchTerms.containsKey("dataEntradaMin") && !searchTerms.get("dataEntradaMin").isEmpty() ? LocalDate.parse(searchTerms.get("dataEntradaMin")) : null;
        LocalDate dataEntradaMax = searchTerms.containsKey("dataEntradaMax") && !searchTerms.get("dataEntradaMax").isEmpty() ? LocalDate.parse(searchTerms.get("dataEntradaMax")) : null;
        LocalDate dataSaidaMin = searchTerms.containsKey("dataSaidaMin") && !searchTerms.get("dataSaidaMin").isEmpty() ? LocalDate.parse(searchTerms.get("dataSaidaMin")) : null;
        LocalDate dataSaidaMax = searchTerms.containsKey("dataSaidaMax") && !searchTerms.get("dataSaidaMax").isEmpty() ? LocalDate.parse(searchTerms.get("dataSaidaMax")) : null;

        return estoqueRepository.searchEstoque(
            estoqueId, produtoId, localizacaoFisica, categoria, marca, unidadeMedida,
            quantidadeAtualMin, quantidadeAtualMax, quantidadeReservadaMin, quantidadeReservadaMax,
            dataEntradaMin, dataEntradaMax, dataSaidaMin, dataSaidaMax);
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
        return estoqueRepository.findEstoqueAbaixoMinimo();
    }

    @Override
    public Estoque adicionarEstoque(Long produtoId, Integer quantidade) {
        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser positiva");
        }
        
        Estoque estoque = buscarPorProdutoId(produtoId);
        estoque.adicionarEstoque(quantidade);
        estoqueRepository.save(estoque);
        
        return estoque;
    }

    @Override
    public Estoque reservarEstoque(Long produtoId, Integer quantidade) {
        if (quantidade <= 0) {
            throw new RuntimeException("Quantidade deve ser positiva");
        }
        
        Estoque estoque = buscarPorProdutoId(produtoId);
        
        if (!estoque.reservarEstoque(quantidade)) {
            throw new RuntimeException("Estoque insuficiente para reserva. Disponível: " + estoque.getQuantidadeDisponivel() + ", Solicitado: " + quantidade);
        }
        
        estoqueRepository.save(estoque);
        
        return estoque;
    }

    @Override
    public Estoque consumirEstoque(Long produtoId, Integer quantidade) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        
        if (!estoque.consumirEstoque(quantidade)) {
            throw new RuntimeException("Estoque insuficiente para consumo. Disponível: " + estoque.getQuantidadeAtual() + ", Solicitado: " + quantidade);
        }
        
        estoqueRepository.save(estoque);
        
        return estoque;
    }

    @Override
    public Estoque liberarReserva(Long produtoId, Integer quantidade) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        estoque.liberarReserva(quantidade);
        estoqueRepository.save(estoque);
        
        return estoque;
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
        return estoque.estaDisponivel(quantidade);
    }

    @Override
    public boolean verificarEstoqueMinimo(Long produtoId) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        return estoque.estaAbaixoEstoqueMinimo();
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
        estoque.confirmarConsumoReserva(quantidade);
        estoqueRepository.save(estoque);
        return true;
    }

    @Override
    public boolean precisaReabastecer(Long produtoId) {
        Estoque estoque = buscarPorProdutoId(produtoId);
        return estoque.estaAbaixoEstoqueMinimo();
    }

    @Override
    public List<Estoque> getAlertasEstoque() {
        return estoqueRepository.findEstoqueAbaixoMinimo();
    }

    @Override
    public List<Object[]> findGiroPorCategoria() {
        return estoqueRepository.findGiroPorCategoria();
    }

    @Override
    public List<Object[]> findGiroPorMarca() {
        return estoqueRepository.findGiroPorMarca();
    }

    @Override
    public List<Object[]> findGiroPorLocal() {
        return estoqueRepository.findGiroPorLocal();
    }
}