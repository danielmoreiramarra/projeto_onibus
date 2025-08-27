package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.model.Pneu.StatusPneu;

public interface PneuService {
    
    Pneu criarPneu(Pneu pneu);
    Pneu atualizarPneu(Long id, Pneu pneuAtualizado);
    void excluirPneu(Long id);
    
    Optional<Pneu> buscarPorId(Long id);
    List<Pneu> buscarTodos();
    Optional<Pneu> buscarPorNumeroSerie(String numeroSerie);
    Optional<Pneu> buscarPorCodigoFabricacao(String codigoFabricacao);
    
    List<Pneu> buscarPorStatus(StatusPneu status);
    List<Pneu> buscarPorMarca(String marca);
    List<Pneu> buscarPorMedida(String medida);
    List<Pneu> buscarPorMarcaEMedida(String marca, String medida);
    List<Pneu> buscarDisponiveis();
    List<Pneu> buscarEmUso();
    
    Pneu enviarParaManutencao(Long pneuId);
    Pneu retornarDeManutencao(Long pneuId);
    Pneu descartarPneu(Long pneuId);
    
    boolean registrarKmRodados(Long pneuId, Integer kmAdicionais);
    boolean precisaTroca(Long pneuId);
    
    boolean existeNumeroSerie(String numeroSerie);
    boolean existeCodigoFabricacao(String codigoFabricacao);
    
    List<Pneu> buscarPneusParaTroca();
    List<Pneu> buscarPneusComGarantiaPrestesVencer();
    List<Object[]> estatisticasPorStatus();
    List<Object[]> estatisticasPorMarca();
}