package com.proj_db.onibus.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.proj_db.onibus.model.Motor;

public interface MotorService {
    
    Motor criarMotor(Motor motor);
    Motor atualizarMotor(Long id, Motor motorAtualizado);
    void excluirMotor(Long id);
    
    Optional<Motor> buscarPorId(Long id);
    List<Motor> buscarTodos();
    Optional<Motor> buscarPorNumeroSerie(String numeroSerie);
    Optional<Motor> buscarPorCodigoFabricacao(String codigoFabricacao);
    
    // ✅ Removido métodos redundantes como buscarDisponiveis() e buscarEmUso()
    // A busca combinada já cobre todos esses casos.
    
    // ✅ NOVO MÉTODO: Busca combinada para todos os campos
    List<Motor> searchMotor(Map<String, String> searchTerms);
    
    Motor enviarParaManutencao(Long motorId);
    Motor retornarDeManutencao(Long motorId);
    
    Motor registrarRevisao(Long motorId);
    
    boolean estaEmGarantia(Long motorId);
    
    boolean existeNumeroSerie(String numeroSerie);
    boolean existeCodigoFabricacao(String codigoFabricacao);
    
    List<Motor> buscarMotoresParaRevisao();
    List<Motor> buscarMotoresComGarantiaPrestesVencer();
    
    List<Object[]> countMotoresPorTipo();
    List<Object[]> countMotoresPorStatus();
    List<Object[]> avgPotenciaPorMarca();

}