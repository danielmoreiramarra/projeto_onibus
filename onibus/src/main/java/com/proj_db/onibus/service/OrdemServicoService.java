package com.proj_db.onibus.service;

import java.util.List;
import java.util.Optional;

import com.proj_db.onibus.dto.OrdemServicoCreateDTO;
import com.proj_db.onibus.dto.OrdemServicoSearchDTO;
import com.proj_db.onibus.dto.OrdemServicoUpdateDTO;
import com.proj_db.onibus.model.OrdemServico;

// <<< Imports para os DTOs externos que estão na pasta /dto
public interface OrdemServicoService {
    
    // --- CRUD e Ciclo de Vida ---
    OrdemServico create(OrdemServicoCreateDTO dto);
    OrdemServico updateInfo(Long osId, OrdemServicoUpdateDTO dto);
    void delete(Long osId); // Apenas se CANCELADA

    Optional<OrdemServico> findById(Long id);
    List<OrdemServico> findAll();
    List<OrdemServico> search(OrdemServicoSearchDTO criteria);

    // --- Ações Principais do Fluxo de Trabalho ---
    OrdemServico startExecution(Long osId);
    OrdemServico finishExecution(Long osId);
    OrdemServico cancel(Long osId);

    // --- Gerenciamento de Itens (através da OS) ---
    OrdemServico addItem(Long osId, Long produtoId, Double quantidade, String descricao);
    OrdemServico removeItem(Long osId, Long itemId);
    OrdemServico updateItemQuantity(Long osId, Long itemId, Double novaQuantidade);
    
}