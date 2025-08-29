package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.proj_db.onibus.model.Pneu;

public interface PneuService {
    
    Pneu criarPneu(Pneu pneu);
    Pneu atualizarPneu(Long id, Pneu pneuAtualizado);
    void excluirPneu(Long id);
    
    Optional<Pneu> buscarPorId(Long id);
    List<Pneu> buscarTodos();
    Optional<Pneu> buscarPorNumeroSerie(String numeroSerie);
    Optional<Pneu> buscarPorCodigoFabricacao(String codigoFabricacao);
    
    // ✅ Métodos de busca individuais removidos, a busca combinada cobre isso.
    
    // ✅ NOVO MÉTODO: Busca combinada para todos os campos
    List<Pneu> searchPneu(Map<String, String> searchTerms);
    
    Pneu enviarParaManutencao(Long pneuId);
    Pneu retornarDeManutencao(Long pneuId);
    Pneu descartarPneu(Long pneuId);
    
    Pneu registrarKmRodados(Long pneuId, Integer kmAdicionais); // ✅ Tipo de retorno alterado para Pneu
    boolean precisaTroca(Long pneuId);
    
    boolean existeNumeroSerie(String numeroSerie);
    boolean existeCodigoFabricacao(String codigoFabricacao);
    
    List<Pneu> buscarPneusParaTroca();
    List<Pneu> buscarPneusComGarantiaPrestesVencer();
    List<Object[]> estatisticasPorStatus();
    List<Object[]> estatisticasPorMarca();

    // ✅ NOVOS MÉTODOS DE RELATÓRIO
    List<Object[]> avgKmPorPosicaoNoPeriodo(LocalDate startDate, LocalDate endDate);
    List<Object[]> avgKmPorMarcaNoPeriodo(LocalDate startDate, LocalDate endDate);

}