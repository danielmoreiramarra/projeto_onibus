package com.proj_db.onibus.controller;

import java.time.LocalDate; // Importa todos os DTOs
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proj_db.onibus.dto.AddItemDTO;
import com.proj_db.onibus.dto.OrdemServicoCreateDTO;
import com.proj_db.onibus.dto.OrdemServicoResponseDTO;
import com.proj_db.onibus.dto.OrdemServicoSearchDTO;
import com.proj_db.onibus.dto.OrdemServicoUpdateDTO;
import com.proj_db.onibus.dto.UpdateItemDTO;
import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.service.OrdemServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ordens-servico")
@CrossOrigin(origins = "*")
public class OrdemServicoController {

    @Autowired
    private OrdemServicoService osService;

    // --- Endpoints CRUD da Ordem de Serviço ---

    @PostMapping
    public ResponseEntity<OrdemServicoResponseDTO> create(@Valid @RequestBody OrdemServicoCreateDTO dto) {
        OrdemServico novaOS = osService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new OrdemServicoResponseDTO(novaOS));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> updateInfo(@PathVariable Long id, @Valid @RequestBody OrdemServicoUpdateDTO dto) {
        OrdemServico osAtualizada = osService.updateInfo(id, dto);
        return ResponseEntity.ok(new OrdemServicoResponseDTO(osAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        osService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints de Consulta ---

    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponseDTO> findById(@PathVariable Long id) {
        return osService.findById(id)
                .map(OrdemServicoResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OrdemServicoResponseDTO>> findAll() {
        List<OrdemServicoResponseDTO> dtos = osService.findAll().stream()
                .map(OrdemServicoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<OrdemServicoResponseDTO>> search(
            @RequestParam(required = false) String numeroOS,
            @RequestParam(required = false) OrdemServico.TipoOrdemServico tipo,
            @RequestParam(required = false) OrdemServico.StatusOrdemServico status,
            @RequestParam(required = false) Long onibusId,
            @RequestParam(required = false) Long produtoId,
            @RequestParam(required = false) LocalDate dataAberturaInicio,
            @RequestParam(required = false) LocalDate dataAberturaFim
    ) {
        OrdemServicoSearchDTO criteria = new OrdemServicoSearchDTO(
                numeroOS, tipo, status, onibusId, null, null, null, produtoId, dataAberturaInicio, dataAberturaFim);
        
        List<OrdemServicoResponseDTO> dtos = osService.search(criteria).stream()
                .map(OrdemServicoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // --- Endpoints de Ações de Ciclo de Vida ---

    @PatchMapping("/{id}/start")
    public ResponseEntity<OrdemServicoResponseDTO> startExecution(@PathVariable Long id) {
        OrdemServico os = osService.startExecution(id);
        return ResponseEntity.ok(new OrdemServicoResponseDTO(os));
    }

    @PatchMapping("/{id}/finish")
    public ResponseEntity<OrdemServicoResponseDTO> finishExecution(@PathVariable Long id) {
        OrdemServico os = osService.finishExecution(id);
        return ResponseEntity.ok(new OrdemServicoResponseDTO(os));
    }


    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrdemServicoResponseDTO> cancel(@PathVariable Long id) {
        OrdemServico os = osService.cancel(id);
        return ResponseEntity.ok(new OrdemServicoResponseDTO(os));
    }

    // --- Endpoints de Gerenciamento de Itens (Padrão Agregado) ---

    @PostMapping("/{osId}/itens")
    public ResponseEntity<OrdemServicoResponseDTO> addItem(
            @PathVariable Long osId,
            @Valid @RequestBody AddItemDTO itemDTO) {
        OrdemServico osAtualizada = osService.addItem(osId, itemDTO.produtoId(), itemDTO.quantidade(), itemDTO.descricao());
        return ResponseEntity.ok(new OrdemServicoResponseDTO(osAtualizada));
    }

    @PutMapping("/{osId}/itens/{itemId}")
    public ResponseEntity<OrdemServicoResponseDTO> updateItem(
            @PathVariable Long osId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateItemDTO itemDTO) {
        OrdemServico osAtualizada = osService.updateItemQuantity(osId, itemId, itemDTO.quantidade()); // Expandir DTO se necessário
        return ResponseEntity.ok(new OrdemServicoResponseDTO(osAtualizada));
    }

    @DeleteMapping("/{osId}/itens/{itemId}")
    public ResponseEntity<OrdemServicoResponseDTO> removeItem(
            @PathVariable Long osId,
            @PathVariable Long itemId) {
        OrdemServico osAtualizada = osService.removeItem(osId, itemId);
        return ResponseEntity.ok(new OrdemServicoResponseDTO(osAtualizada));
    }
}