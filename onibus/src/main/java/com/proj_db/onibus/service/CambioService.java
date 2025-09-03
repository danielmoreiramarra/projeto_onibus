package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.dto.CambioCreateDTO;
import com.proj_db.onibus.dto.CambioUpdateDTO;
import com.proj_db.onibus.model.Cambio;

public interface CambioService {
    
    // --- CRUD Básico ---
    Cambio save(CambioCreateDTO cambio);
    Cambio update(Long id, CambioUpdateDTO cambioDetails);
    void deleteById(Long id);
    Optional<Cambio> findById(Long id);
    List<Cambio> findAll();

    // --- Buscas Específicas ---
    Optional<Cambio> findByNumeroSerie(String numeroSerie);
    Optional<Cambio> findByCodigoFabricacao(String codigoFabricacao);
    List<Cambio> search(CambioSearchDTO criteria);

    // --- Lógica de Negócio (Ciclo de Vida) ---
    Cambio enviarParaManutencao(Long cambioId);
    Cambio retornarDeManutencao(Long cambioId);
    Cambio enviarParaRevisao(Long cambioId);
    Cambio retornarDaRevisao(Long cambioId);

    // --- Lógica de OS Preventiva ---
    void verificarEGerarOsPreventivas(); // Verifica TODOS os câmbios

    // --- Métodos de Relatório (vindos do Repositório) ---
    List<Object[]> countByTipo();
    List<Object[]> countByStatus();
    
    // DTO (Data Transfer Object) para a busca, aninhado aqui para simplicidade
    record CambioSearchDTO(
        String marca,
        String modelo,
        String numeroSerie,
        String tipoFluido,
        Cambio.TipoCambio tipo,
        Cambio.StatusCambio status
    ) {}
}