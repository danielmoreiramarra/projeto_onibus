package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.dto.PneuCreateDTO;
import com.proj_db.onibus.dto.PneuUpdateDTO;
import com.proj_db.onibus.model.Pneu;

public interface PneuService {
    
    // --- CRUD Básico ---
    Pneu save(PneuCreateDTO pneu);
    Pneu update(Long id, PneuUpdateDTO pneuDetails);
    void deleteById(Long id);
    Optional<Pneu> findById(Long id);
    List<Pneu> findAll();

    // --- Buscas Específicas ---
    Optional<Pneu> findByNumeroSerie(String numeroSerie);
    List<Pneu> search(PneuSearchDTO criteria);

    // --- Lógica de Negócio (Ciclo de Vida) ---
    Pneu enviarParaManutencao(Long pneuId);
    Pneu retornarDeManutencao(Long pneuId);
    Pneu enviarParaReforma(Long pneuId);
    Pneu retornarDeReforma(Long pneuId);
    void descartarPneu(Long pneuId); // Descarte é um processo final

    // --- Lógica de OS Preventiva ---
    void verificarEGerarOsPreventivas();

    // --- Métodos de Relatório ---
    List<Object[]> countByStatus();
    List<Object[]> avgKmPorMarca();
    
    // DTO (Data Transfer Object) para a busca
    record PneuSearchDTO(
        String marca,
        String medida,
        String numeroSerie,
        Pneu.StatusPneu status,
        Double kmRodadosMin,
        Double kmRodadosMax,
        Long onibusId
    ) {}
}