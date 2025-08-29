package com.proj_db.onibus.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.proj_db.onibus.model.Cambio;

public interface CambioService {
    
    Cambio criarCambio(Cambio cambio);
    Cambio atualizarCambio(Long id, Cambio cambioAtualizado);
    void excluirCambio(Long id);
    
    Optional<Cambio> buscarPorId(Long id);
    List<Cambio> buscarTodos();
    Optional<Cambio> buscarPorNumeroSerie(String numeroSerie);
    Optional<Cambio> buscarPorCodigoFabricacao(String codigoFabricacao);
    
    // ✅ Métodos de busca individuais removidos, a busca combinada cobre isso.
    
    // ✅ NOVO MÉTODO: Busca combinada para todos os campos
    List<Cambio> searchCambio(Map<String, String> searchTerms);
    
    Cambio enviarParaManutencao(Long cambioId);
    Cambio retornarDeManutencao(Long cambioId);
    
    Cambio trocarFluido(Long cambioId, String novoTipoFluido, Double novaQuantidade);
    Cambio registrarRevisao(Long cambioId);
    
    boolean estaEmGarantia(Long cambioId);
    
    boolean existeNumeroSerie(String numeroSerie);
    boolean existeCodigoFabricacao(String codigoFabricacao);
    
    List<Cambio> buscarCambiosParaRevisao();
    List<Cambio> buscarCambiosComGarantiaPrestesVencer();

    List<Object[]> countCambiosPorTipo();
    List<Object[]> countCambiosPorMarchas();
    List<Object[]> countCambiosPorStatus();

}