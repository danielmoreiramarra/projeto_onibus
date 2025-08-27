package com.proj_db.onibus.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.model.Motor.StatusMotor;
import com.proj_db.onibus.model.Motor.TipoMotor;
import com.proj_db.onibus.service.MotorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/motores")
@CrossOrigin(origins = "*")
public class MotorController {

    @Autowired
    private MotorService motorService;

    // ✅ CRIAR NOVO MOTOR
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

    // ✅ LISTAR TODOS OS MOTORES
    @GetMapping
    public ResponseEntity<List<Motor>> listarTodos() {
        try {
            List<Motor> motores = motorService.buscarTodos();
            return ResponseEntity.ok(motores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ BUSCAR MOTOR POR ID
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

    // ✅ ATUALIZAR MOTOR
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

    // ✅ DELETAR MOTOR
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

    // ✅ BUSCAR POR NÚMERO DE SÉRIE
    @GetMapping("/numero-serie/{numeroSerie}")
    public ResponseEntity<?> buscarPorNumeroSerie(@PathVariable String numeroSerie) {
        try {
            Optional<Motor> motor = motorService.buscarPorNumeroSerie(numeroSerie);
            return motor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por número de série: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR CÓDIGO DE FABRICAÇÃO
    @GetMapping("/codigo-fabricacao/{codigo}")
    public ResponseEntity<?> buscarPorCodigoFabricacao(@PathVariable String codigo) {
        try {
            Optional<Motor> motor = motorService.buscarPorCodigoFabricacao(codigo);
            return motor.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por código de fabricação: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<?> buscarPorStatus(@PathVariable StatusMotor status) {
        try {
            List<Motor> motores = motorService.buscarPorStatus(status);
            return ResponseEntity.ok(motores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por status: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR TIPO
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> buscarPorTipo(@PathVariable TipoMotor tipo) {
        try {
            List<Motor> motores = motorService.buscarPorTipo(tipo);
            return ResponseEntity.ok(motores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por tipo: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR MARCA
    @GetMapping("/marca/{marca}")
    public ResponseEntity<?> buscarPorMarca(@PathVariable String marca) {
        try {
            List<Motor> motores = motorService.buscarPorMarca(marca);
            return ResponseEntity.ok(motores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por marca: " + e.getMessage());
        }
    }

    // ✅ BUSCAR MOTORES DISPONÍVEIS
    @GetMapping("/disponiveis")
    public ResponseEntity<?> buscarDisponiveis() {
        try {
            List<Motor> motores = motorService.buscarDisponiveis();
            return ResponseEntity.ok(motores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar motores disponíveis: " + e.getMessage());
        }
    }

    // ✅ BUSCAR MOTORES NOVOS
    @GetMapping("/novos")
    public ResponseEntity<?> buscarNovos() {
        try {
            List<Motor> motores = motorService.buscarNovos();
            return ResponseEntity.ok(motores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar motores novos: " + e.getMessage());
        }
    }

    // ✅ BUSCAR MOTORES EM USO
    @GetMapping("/em-uso")
    public ResponseEntity<?> buscarEmUso() {
        try {
            List<Motor> motores = motorService.buscarEmUso();
            return ResponseEntity.ok(motores);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar motores em uso: " + e.getMessage());
        }
    }

    // ✅ ENVIAR PARA MANUTENÇÃO
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

    // ✅ RETORNAR DA MANUTENÇÃO
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

    // ✅ REGISTRAR REVISÃO
    @PatchMapping("/{id}/registrar-revisao")
    public ResponseEntity<?> registrarRevisao(@PathVariable Long id) {
        try {
            boolean sucesso = motorService.registrarRevisao(id);
            return ResponseEntity.ok(sucesso ? "Revisão registrada com sucesso" : "Falha ao registrar revisão");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar revisão: " + e.getMessage());
        }
    }

    // ✅ VERIFICAR SE ESTÁ EM GARANTIA
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

    // ✅ VERIFICAR SE NÚMERO DE SÉRIE EXISTE
    @GetMapping("/existe-numero-serie/{numeroSerie}")
    public ResponseEntity<Boolean> existeNumeroSerie(@PathVariable String numeroSerie) {
        try {
            boolean existe = motorService.existeNumeroSerie(numeroSerie);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ VERIFICAR SE CÓDIGO DE FABRICAÇÃO EXISTE
    @GetMapping("/existe-codigo-fabricacao/{codigo}")
    public ResponseEntity<Boolean> existeCodigoFabricacao(@PathVariable String codigo) {
        try {
            boolean existe = motorService.existeCodigoFabricacao(codigo);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ BUSCAR MOTORES PARA REVISÃO
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

    // ✅ BUSCAR MOTORES COM GARANTIA PRESTES A VENCER
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