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

import com.proj_db.onibus.dto.OnibusCreateDTO;
import com.proj_db.onibus.dto.OnibusResponseDTO;
import com.proj_db.onibus.dto.OnibusUpdateDTO;
import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.Pneu.PosicaoPneu;
import com.proj_db.onibus.service.OnibusService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/onibus")
@CrossOrigin(origins = "*")
public class OnibusController {

    @Autowired
    private OnibusService onibusService;

    // --- Endpoints CRUD ---

    @PostMapping
    public ResponseEntity<OnibusResponseDTO> create(@Valid @RequestBody OnibusCreateDTO dto) {
        Onibus onibusSalvo = onibusService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new OnibusResponseDTO(onibusSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OnibusResponseDTO> update(@PathVariable Long id, @Valid @RequestBody OnibusUpdateDTO dto) {
        Onibus onibusAtualizado = onibusService.update(id, dto);
        return ResponseEntity.ok(new OnibusResponseDTO(onibusAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        onibusService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints de Consulta ---

    @GetMapping("/{id}")
    public ResponseEntity<OnibusResponseDTO> findById(@PathVariable Long id) {
        return onibusService.findById(id)
                .map(OnibusResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OnibusResponseDTO>> findAll() {
        List<OnibusResponseDTO> dtos = onibusService.findAll().stream()
                .map(OnibusResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<OnibusResponseDTO>> search(
            @RequestParam(required = false) String chassi,
            @RequestParam(required = false) String placa,
            @RequestParam(required = false) String numeroFrota,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) Onibus.StatusOnibus status,
            @RequestParam(required = false) Long motorId,
            @RequestParam(required = false) Long cambioId,
            @RequestParam(required = false) Long pneuId
    ) {
        OnibusService.OnibusSearchDTO criteria = new OnibusService.OnibusSearchDTO(chassi, placa, numeroFrota, marca, modelo, status, motorId, cambioId, pneuId);
        List<OnibusResponseDTO> dtos = onibusService.search(criteria).stream()
                .map(OnibusResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // --- Endpoints de Ações de Negócio ---

    @PostMapping("/{id}/registrar-viagem")
    public ResponseEntity<OnibusResponseDTO> registrarViagem(@PathVariable Long id, @RequestParam Double kmPercorridos) {
        Onibus onibus = onibusService.registrarViagem(id, kmPercorridos);
        return ResponseEntity.ok(new OnibusResponseDTO(onibus));
    }

    @PatchMapping("/{id}/colocar-em-operacao")
    public ResponseEntity<OnibusResponseDTO> colocarEmOperacao(@PathVariable Long id) {
        Onibus onibus = onibusService.colocarEmOperacao(id);
        return ResponseEntity.ok(new OnibusResponseDTO(onibus));
    }

    @PatchMapping("/{id}/retirar-de-operacao")
    public ResponseEntity<OnibusResponseDTO> retirarDeOperacao(@PathVariable Long id) {
        Onibus onibus = onibusService.retirarDeOperacao(id);
        return ResponseEntity.ok(new OnibusResponseDTO(onibus));
    }
    
    // --- Endpoints de Gerenciamento de Componentes ---

    @PostMapping("/{onibusId}/motor/{motorId}") // POST para adicionar um novo recurso (relação)
    public ResponseEntity<OnibusResponseDTO> instalarMotor(@PathVariable Long onibusId, @PathVariable Long motorId) {
        Onibus onibus = onibusService.instalarMotor(onibusId, motorId);
        return ResponseEntity.ok(new OnibusResponseDTO(onibus));
    }

    @DeleteMapping("/{onibusId}/motor") // DELETE para remover um recurso (relação)
    public ResponseEntity<OnibusResponseDTO> removerMotor(@PathVariable Long onibusId) {
        Onibus onibus = onibusService.removerMotor(onibusId);
        return ResponseEntity.ok(new OnibusResponseDTO(onibus));
    }

    @PostMapping("/{onibusId}/cambio/{cambioId}")
    public ResponseEntity<OnibusResponseDTO> instalarCambio(@PathVariable Long onibusId, @PathVariable Long cambioId) {
        Onibus onibus = onibusService.instalarCambio(onibusId, cambioId);
        return ResponseEntity.ok(new OnibusResponseDTO(onibus));
    }

    @DeleteMapping("/{onibusId}/cambio")
    public ResponseEntity<OnibusResponseDTO> removerCambio(@PathVariable Long onibusId) {
        Onibus onibus = onibusService.removerCambio(onibusId);
        return ResponseEntity.ok(new OnibusResponseDTO(onibus));
    }

    @PostMapping("/{onibusId}/pneu/{pneuId}")
    public ResponseEntity<OnibusResponseDTO> instalarPneu(
            @PathVariable Long onibusId,
            @PathVariable Long pneuId,
            @RequestParam PosicaoPneu posicao) {
        Onibus onibus = onibusService.instalarPneu(onibusId, pneuId, posicao);
        return ResponseEntity.ok(new OnibusResponseDTO(onibus));
    }

    @DeleteMapping("/{onibusId}/pneu")
    public ResponseEntity<OnibusResponseDTO> removerPneu(
            @PathVariable Long onibusId,
            @RequestParam PosicaoPneu posicao) {
        Onibus onibus = onibusService.removerPneu(onibusId, posicao);
        return ResponseEntity.ok(new OnibusResponseDTO(onibus));
    }
}