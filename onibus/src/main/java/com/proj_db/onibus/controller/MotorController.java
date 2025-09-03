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

import com.proj_db.onibus.dto.MotorCreateDTO;
import com.proj_db.onibus.dto.MotorResponseDTO;
import com.proj_db.onibus.dto.MotorUpdateDTO;
import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.service.MotorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/motores")
@CrossOrigin(origins = "*") // Em produção, restrinja para o domínio do seu frontend
public class MotorController {

    @Autowired
    private MotorService motorService;

    // --- Endpoints CRUD ---

    @PostMapping
    public ResponseEntity<MotorResponseDTO> create(@Valid @RequestBody MotorCreateDTO dto) {
        // O Controller agora só passa o DTO para o Serviço, que faz todo o trabalho.
        Motor motorSalvo = motorService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MotorResponseDTO(motorSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MotorResponseDTO> update(@PathVariable Long id, @Valid @RequestBody MotorUpdateDTO dto) {
        Motor motorAtualizado = motorService.update(id, dto);
        return ResponseEntity.ok(new MotorResponseDTO(motorAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        motorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints de Consulta ---

    @GetMapping("/{id}")
    public ResponseEntity<MotorResponseDTO> findById(@PathVariable Long id) {
        return motorService.findById(id)
                .map(MotorResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<MotorResponseDTO>> findAll() {
        List<MotorResponseDTO> dtos = motorService.findAll().stream()
                .map(MotorResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<MotorResponseDTO>> search(
            // Mapeia os parâmetros da URL para um DTO de busca, tornando a API mais clara
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) String numeroSerie,
            @RequestParam(required = false) String tipoOleo,
            @RequestParam(required = false) Motor.TipoMotor tipo,
            @RequestParam(required = false) Motor.StatusMotor status,
            @RequestParam(required = false) Integer potenciaMin,
            @RequestParam(required = false) Integer potenciaMax
    ) {
        MotorService.MotorSearchDTO criteria = new MotorService.MotorSearchDTO(marca, modelo, numeroSerie, tipoOleo, tipo, status, potenciaMin, potenciaMax);
        List<MotorResponseDTO> dtos = motorService.search(criteria).stream()
                .map(MotorResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // --- Endpoints de Ações de Negócio ---

    @PatchMapping("/{id}/enviar-manutencao")
    public ResponseEntity<MotorResponseDTO> enviarParaManutencao(@PathVariable Long id) {
        Motor motor = motorService.enviarParaManutencao(id);
        return ResponseEntity.ok(new MotorResponseDTO(motor));
    }
    
    @PatchMapping("/{id}/retornar-manutencao")
    public ResponseEntity<MotorResponseDTO> retornarDeManutencao(@PathVariable Long id) {
        Motor motor = motorService.retornarDaManutencao(id);
        return ResponseEntity.ok(new MotorResponseDTO(motor));
    }

    @PatchMapping("/{id}/enviar-revisao")
    public ResponseEntity<MotorResponseDTO> enviarParaRevisao(@PathVariable Long id) {
        Motor motor = motorService.enviarParaRevisao(id);
        return ResponseEntity.ok(new MotorResponseDTO(motor));
    }

    @PatchMapping("/{id}/retornar-revisao")
    public ResponseEntity<MotorResponseDTO> retornarDeRevisao(@PathVariable Long id) {
        Motor motor = motorService.retornarDaRevisao(id);
        return ResponseEntity.ok(new MotorResponseDTO(motor));
    }
}