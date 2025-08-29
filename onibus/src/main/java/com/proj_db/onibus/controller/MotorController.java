package com.proj_db.onibus.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.service.MotorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/motores")
@CrossOrigin(origins = "*")
public class MotorController {

    @Autowired
    private MotorService motorService;

    @PostMapping
    public ResponseEntity<?> criarMotor(@Valid @RequestBody Motor motor) {
        try {
            Motor novoMotor = motorService.criarMotor(motor);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoMotor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao criar motor: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Motor>> listarTodos() {
        try {
            List<Motor> motores = motorService.buscarTodos();
            return ResponseEntity.ok(motores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Optional<Motor> motor = motorService.buscarPorId(id);
            return motor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar motor: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarMotor(
            @PathVariable Long id, 
            @Valid @RequestBody Motor motorAtualizado) {
        try {
            Motor motor = motorService.atualizarMotor(id, motorAtualizado);
            return ResponseEntity.ok(motor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar motor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarMotor(@PathVariable Long id) {
        try {
            motorService.excluirMotor(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar motor: " + e.getMessage());
        }
    }

    // ✅ NOVO ENDPOINT DE BUSCA COMBINADA
    @GetMapping("/search")
    public ResponseEntity<?> searchMotor(@RequestParam Map<String, String> searchTerms) {
        try {
            List<Motor> motores = motorService.searchMotor(searchTerms);
            return ResponseEntity.ok(motores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a busca: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/enviar-manutencao")
    public ResponseEntity<?> enviarParaManutencao(@PathVariable Long id) {
        try {
            Motor motor = motorService.enviarParaManutencao(id);
            return ResponseEntity.ok(motor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao enviar para manutenção: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/retornar-manutencao")
    public ResponseEntity<?> retornarDeManutencao(@PathVariable Long id) {
        try {
            Motor motor = motorService.retornarDeManutencao(id);
            return ResponseEntity.ok(motor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao retornar da manutenção: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/registrar-revisao")
    public ResponseEntity<?> registrarRevisao(@PathVariable Long id) {
        try {
            Motor motor = motorService.registrarRevisao(id);
            return ResponseEntity.ok(motor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar revisão: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/em-garantia")
    public ResponseEntity<?> estaEmGarantia(@PathVariable Long id) {
        try {
            boolean emGarantia = motorService.estaEmGarantia(id);
            return ResponseEntity.ok(emGarantia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao verificar garantia: " + e.getMessage());
        }
    }

    @GetMapping("/existe-numero-serie/{numeroSerie}")
    public ResponseEntity<Boolean> existeNumeroSerie(@PathVariable String numeroSerie) {
        try {
            boolean existe = motorService.existeNumeroSerie(numeroSerie);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/existe-codigo-fabricacao/{codigo}")
    public ResponseEntity<Boolean> existeCodigoFabricacao(@PathVariable String codigo) {
        try {
            boolean existe = motorService.existeCodigoFabricacao(codigo);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/para-revisao")
    public ResponseEntity<?> buscarMotoresParaRevisao() {
        try {
            List<Motor> motores = motorService.buscarMotoresParaRevisao();
            return ResponseEntity.ok(motores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar motores para revisão: " + e.getMessage());
        }
    }

    @GetMapping("/garantia-prestes-vencer")
    public ResponseEntity<?> buscarMotoresComGarantiaPrestesVencer() {
        try {
            List<Motor> motores = motorService.buscarMotoresComGarantiaPrestesVencer();
            return ResponseEntity.ok(motores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar motores com garantia prestes a vencer: " + e.getMessage());
        }
    }
}