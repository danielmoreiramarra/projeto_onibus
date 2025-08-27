package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.model.Motor.StatusMotor;
import com.proj_db.onibus.model.Motor.TipoMotor;

public interface MotorService {
    
    Motor criarMotor(Motor motor);
    Motor atualizarMotor(Long id, Motor motorAtualizado);
    void excluirMotor(Long id);
    
    Optional<Motor> buscarPorId(Long id);
    List<Motor> buscarTodos();
    Optional<Motor> buscarPorNumeroSerie(String numeroSerie);
    Optional<Motor> buscarPorCodigoFabricacao(String codigoFabricacao);
    
    List<Motor> buscarPorStatus(StatusMotor status);
    List<Motor> buscarPorTipo(TipoMotor tipo);
    List<Motor> buscarPorMarca(String marca);
    List<Motor> buscarDisponiveis();
    List<Motor> buscarNovos();
    List<Motor> buscarEmUso();
    
    Motor enviarParaManutencao(Long motorId);
    Motor retornarDeManutencao(Long motorId);
    
    boolean registrarRevisao(Long motorId);
    
    boolean estaEmGarantia(Long motorId);
    
    boolean existeNumeroSerie(String numeroSerie);
    boolean existeCodigoFabricacao(String codigoFabricacao);
    
    List<Motor> buscarMotoresParaRevisao();
    List<Motor> buscarMotoresComGarantiaPrestesVencer();

}