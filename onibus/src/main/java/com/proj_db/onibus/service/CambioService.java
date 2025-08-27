package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.model.Cambio;
import com.proj_db.onibus.model.Cambio.StatusCambio;
import com.proj_db.onibus.model.Cambio.TipoCambio;

public interface CambioService {
    
    Cambio criarCambio(Cambio cambio);
    Cambio atualizarCambio(Long id, Cambio cambioAtualizado);
    void excluirCambio(Long id);
    
    Optional<Cambio> buscarPorId(Long id);
    List<Cambio> buscarTodos();
    Optional<Cambio> buscarPorNumeroSerie(String numeroSerie);
    Optional<Cambio> buscarPorCodigoFabricacao(String codigoFabricacao);
    
    List<Cambio> buscarPorStatus(StatusCambio status);
    List<Cambio> buscarPorTipo(TipoCambio tipo);
    List<Cambio> buscarPorMarca(String marca);
    List<Cambio> buscarDisponiveis();
    List<Cambio> buscarEmUso();
    
    Cambio enviarParaManutencao(Long cambioId);
    Cambio retornarDeManutencao(Long cambioId);
    
    boolean trocarFluido(Long cambioId, String novoTipoFluido, Double novaQuantidade);
    boolean registrarRevisao(Long cambioId);
    
    boolean estaEmGarantia(Long cambioId);
    
    boolean existeNumeroSerie(String numeroSerie);
    boolean existeCodigoFabricacao(String codigoFabricacao);
    
    List<Cambio> buscarCambiosParaRevisao();
    List<Cambio> buscarCambiosComGarantiaPrestesVencer();
}