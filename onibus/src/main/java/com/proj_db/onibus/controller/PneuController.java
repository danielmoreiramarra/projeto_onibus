package com.proj_db.onibus.controller;

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

import com.proj_db.onibus.dto.PneuCreateDTO;
import com.proj_db.onibus.dto.PneuResponseDTO;
import com.proj_db.onibus.dto.PneuUpdateDTO;
import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.service.PneuService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pneus")
@CrossOrigin(origins = "*")
public class PneuController {

    @Autowired
    private PneuService pneuService;

    // --- Endpoints CRUD ---

    @PostMapping
    public ResponseEntity<PneuResponseDTO> create(@Valid @RequestBody PneuCreateDTO dto) {
        Pneu pneuSalvo = pneuService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new PneuResponseDTO(pneuSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PneuResponseDTO> update(@PathVariable Long id, @Valid @RequestBody PneuUpdateDTO dto) {
        Pneu pneuAtualizado = pneuService.update(id, dto);
        return ResponseEntity.ok(new PneuResponseDTO(pneuAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pneuService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints de Consulta ---

    @GetMapping("/{id}")
    public ResponseEntity<PneuResponseDTO> findById(@PathVariable Long id) {
        return pneuService.findById(id)
                .map(PneuResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PneuResponseDTO>> findAll() {
        List<PneuResponseDTO> dtos = pneuService.findAll().stream()
                .map(PneuResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PneuResponseDTO>> search(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String medida,
            @RequestParam(required = false) String numeroSerie,
            @RequestParam(required = false) Pneu.StatusPneu status,
            @RequestParam(required = false) Double kmRodadosMin,
            @RequestParam(required = false) Double kmRodadosMax,
            @RequestParam(required = false) Long onibusId
    ) {
        PneuService.PneuSearchDTO criteria = new PneuService.PneuSearchDTO(marca, medida, numeroSerie, status, kmRodadosMin, kmRodadosMax, onibusId);
        List<PneuResponseDTO> dtos = pneuService.search(criteria).stream()
                .map(PneuResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // --- Endpoints de Ações de Negócio (Ciclo de Vida) ---

    @PatchMapping("/{id}/enviar-manutencao")
    public ResponseEntity<PneuResponseDTO> enviarParaManutencao(@PathVariable Long id) {
        Pneu pneu = pneuService.enviarParaManutencao(id);
        return ResponseEntity.ok(new PneuResponseDTO(pneu));
    }

    @PatchMapping("/{id}/retornar-manutencao")
    public ResponseEntity<PneuResponseDTO> retornarDeManutencao(@PathVariable Long id) {
        Pneu pneu = pneuService.retornarDeManutencao(id);
        return ResponseEntity.ok(new PneuResponseDTO(pneu));
    }

    @PatchMapping("/{id}/enviar-reforma")
    public ResponseEntity<PneuResponseDTO> enviarParaReforma(@PathVariable Long id) {
        Pneu pneu = pneuService.enviarParaReforma(id);
        return ResponseEntity.ok(new PneuResponseDTO(pneu));
    }

    @PatchMapping("/{id}/retornar-reforma")
    public ResponseEntity<PneuResponseDTO> retornarDeReforma(@PathVariable Long id) {
        Pneu pneu = pneuService.retornarDeReforma(id);
        return ResponseEntity.ok(new PneuResponseDTO(pneu));
    }
}