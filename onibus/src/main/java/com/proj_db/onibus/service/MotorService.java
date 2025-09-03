package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.dto.MotorCreateDTO;
import com.proj_db.onibus.dto.MotorUpdateDTO;
import com.proj_db.onibus.model.Motor;

public interface MotorService {
    
    // --- CRUD Básico ---
    Motor save(MotorCreateDTO motor);
    Motor update(Long id, MotorUpdateDTO motorDetails);
    void deleteById(Long id);
    Optional<Motor> findById(Long id);
    List<Motor> findAll();

    // --- Buscas Específicas ---
    Optional<Motor> findByNumeroSerie(String numeroSerie);
    List<Motor> search(MotorSearchDTO criteria);

    // --- Lógica de Negócio (Ciclo de Vida) ---
    Motor enviarParaManutencao(Long motorId);
    Motor retornarDaManutencao(Long motorId);
    Motor enviarParaRevisao(Long motorId);
    Motor retornarDaRevisao(Long motorId);

    // --- Lógica de OS Preventiva ---
    void verificarEGerarOsPreventivas();

    // --- Métodos de Relatório ---
    List<Object[]> countByTipo();
    List<Object[]> countByStatus();
    
    // DTO (Data Transfer Object) para a busca
    record MotorSearchDTO(
        String marca,
        String modelo,
        String numeroSerie,
        String tipoOleo,
        Motor.TipoMotor tipo,
        Motor.StatusMotor status,
        Integer potenciaMin,
        Integer potenciaMax
    ) {}
}