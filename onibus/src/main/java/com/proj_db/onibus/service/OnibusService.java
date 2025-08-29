package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.Onibus.StatusOnibus;
import com.proj_db.onibus.model.Pneu;

public interface OnibusService {
    
    // Métodos de negócio para criação e gestão
    Onibus criarOnibus(Onibus onibus);
    Onibus atualizarOnibus(Long id, Onibus onibusAtualizado);
    void excluirOnibus(Long id);
    
    // Métodos de consulta com regras de negócio
    Optional<Onibus> buscarPorId(Long id);
    List<Onibus> buscarTodos();
    Optional<Onibus> buscarPorChassi(String chassi);
    Optional<Onibus> buscarPorCodigoFabricacao(String codigoFabricacao);
    Optional<Onibus> buscarPorNumeroFrota(String numeroFrota);
    
    // Método de busca combinada
    List<Onibus> searchOnibus(Map<String, String> searchTerms);

    // Métodos específicos de negócio
    List<Onibus> buscarPorStatus(StatusOnibus status);
    List<Onibus> buscarDisponiveis();
    List<Onibus> buscarNovos();
    List<Onibus> buscarEmManutencao();
    List<Onibus> buscarPorMarca(String marca);
    List<Onibus> buscarPorModelo(String modelo);
    List<Onibus> buscarPorAnoFabricacao(Integer anoFabricacao);
    List<Onibus> buscarPorCapacidadeMinima(Integer capacidadeMinima);
    
    // Métodos de gestão de status
    Onibus colocarEmManutencao(Long id);
    Onibus retirarDeManutencao(Long id);
    Onibus aposentarOnibus(Long id);
    Onibus venderOnibus(Long id);
    
    // Métodos de verificação e validação
    boolean verificarDisponibilidade(Long onibusId, LocalDate dataInicio, LocalDate dataFim);
    boolean existeChassi(String chassi);
    boolean existeCodigoFabricacao(String codigoFabricacao);
    boolean existeNumeroFrota(String numeroFrota);
    
    // Métodos de relatório e estatísticas
    List<Object[]> estatisticasPorStatus();
    List<Object[]> estatisticasPorMarca();
    List<Object[]> estatisticasPorAno();

    // ✅ NOVOS MÉTODOS para gerenciar os componentes (motor, cambio, pneus)
    Onibus instalarMotor(Long onibusId, Long motorId);
    Onibus removerMotor(Long onibusId, Long motorId);
    Onibus instalarCambio(Long onibusId, Long cambioId);
    Onibus removerCambio(Long onibusId, Long cambioId);
    Onibus instalarPneu(Long onibusId, Long pneuId, Pneu.PosicaoPneu posicao);
    Onibus removerPneu(Long onibusId, Long pneuId);
}