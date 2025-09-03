package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.dto.OnibusCreateDTO;
import com.proj_db.onibus.dto.OnibusUpdateDTO;
import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.Pneu;

public interface OnibusService {
    
    // --- CRUD Básico ---
    Onibus save(OnibusCreateDTO onibus);
    Onibus update(Long id, OnibusUpdateDTO onibusDetails);
    void deleteById(Long id);
    Optional<Onibus> findById(Long id);
    List<Onibus> findAll();

    // --- Buscas Específicas ---
    Optional<Onibus> findByChassi(String chassi);
    List<Onibus> search(OnibusSearchDTO criteria);

    // --- Lógica de Negócio (Ciclo de Vida do Ônibus) ---
    Onibus colocarEmOperacao(Long onibusId);
    Onibus retirarDeOperacao(Long onibusId);
    Onibus enviarParaManutencao(Long onibusId);
    Onibus retornarDaManutencao(Long onibusId);
    Onibus enviarParaReforma(Long onibusId);
    Onibus retornarDaReforma(Long onibusId);
    Onibus aposentar(Long onibusId);
    Onibus vender(Long onibusId);

    // --- Lógica de Operação ---
    Onibus registrarViagem(Long onibusId, Double kmPercorridos);

    // --- Gerenciamento de Componentes ---
    Onibus instalarMotor(Long onibusId, Long motorId);
    Onibus removerMotor(Long onibusId);
    Onibus instalarCambio(Long onibusId, Long cambioId);
    Onibus removerCambio(Long onibusId);
    Onibus instalarPneu(Long onibusId, Long pneuId, Pneu.PosicaoPneu posicao);
    Onibus removerPneu(Long onibusId, Pneu.PosicaoPneu posicao);

    // --- Lógica de OS Preventiva ---
    void verificarEGerarOsPreventivas();

    // --- Métodos de Relatório ---
    List<Object[]> countByStatus();
    List<Object[]> countByMarca();
    
    // DTO (Data Transfer Object) para a busca
    record OnibusSearchDTO(
        String chassi,
        String placa,
        String numeroFrota,
        String marca,
        String modelo,
        Onibus.StatusOnibus status,
        Long motorId,
        Long cambioId,
        Long pneuId
    ) {}
}