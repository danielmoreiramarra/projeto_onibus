package com.proj_db.onibus.controller;

import java.time.LocalDate;
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

import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.Onibus.StatusOnibus;
import com.proj_db.onibus.model.Pneu.PosicaoPneu;
import com.proj_db.onibus.service.OnibusService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/onibus")
@CrossOrigin(origins = "*")
public class OnibusController {

    @Autowired
    private OnibusService onibusService;

    @PostMapping
    public ResponseEntity<?> criarOnibus(@Valid @RequestBody Onibus onibus) {
        try {
            Onibus novoOnibus = onibusService.criarOnibus(onibus);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoOnibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao criar ônibus: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Onibus>> listarTodos() {
        try {
            List<Onibus> onibus = onibusService.buscarTodos();
            return ResponseEntity.ok(onibus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Optional<Onibus> onibus = onibusService.buscarPorId(id);
            return onibus.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar ônibus: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarOnibus(
            @PathVariable Long id, 
            @Valid @RequestBody Onibus onibusAtualizado) {
        try {
            Onibus onibus = onibusService.atualizarOnibus(id, onibusAtualizado);
            return ResponseEntity.ok(onibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar ônibus: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarOnibus(@PathVariable Long id) {
        try {
            onibusService.excluirOnibus(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar ônibus: " + e.getMessage());
        }
    }

    // ✅ NOVO ENDPOINT DE BUSCA COMBINADA
    @GetMapping("/search")
    public ResponseEntity<?> searchOnibus(@RequestParam Map<String, String> searchTerms) {
        try {
            List<Onibus> onibus = onibusService.searchOnibus(searchTerms);
            return ResponseEntity.ok(onibus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a busca: " + e.getMessage());
        }
    }

    // ✅ ENDPOINT DE BUSCA POR STATUS (RE-ADICIONADO)
    @GetMapping("/status/{status}")
    public ResponseEntity<?> buscarPorStatus(@PathVariable StatusOnibus status) {
        try {
            List<Onibus> onibus = onibusService.buscarPorStatus(status);
            return ResponseEntity.ok(onibus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por status: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/colocar-em-manutencao")
    public ResponseEntity<?> colocarEmManutencao(@PathVariable Long id) {
        try {
            Onibus onibus = onibusService.colocarEmManutencao(id);
            return ResponseEntity.ok(onibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao colocar em manutenção: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/retirar-da-manutencao")
    public ResponseEntity<?> retirarDeManutencao(@PathVariable Long id) {
        try {
            Onibus onibus = onibusService.retirarDeManutencao(id);
            return ResponseEntity.ok(onibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao retirar da manutenção: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/aposentar")
    public ResponseEntity<?> aposentarOnibus(@PathVariable Long id) {
        try {
            Onibus onibus = onibusService.aposentarOnibus(id);
            return ResponseEntity.ok(onibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao aposentar ônibus: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/vender")
    public ResponseEntity<?> venderOnibus(@PathVariable Long id) {
        try {
            Onibus onibus = onibusService.venderOnibus(id);
            return ResponseEntity.ok(onibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao vender ônibus: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/disponibilidade")
    public ResponseEntity<?> verificarDisponibilidade(
            @PathVariable Long id,
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        try {
            boolean disponivel = onibusService.verificarDisponibilidade(id, dataInicio, dataFim);
            return ResponseEntity.ok(disponivel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao verificar disponibilidade: " + e.getMessage());
        }
    }

    @PatchMapping("/{onibusId}/instalar/motor/{motorId}")
    public ResponseEntity<?> instalarMotor(@PathVariable Long onibusId, @PathVariable Long motorId) {
        try {
            Onibus onibus = onibusService.instalarMotor(onibusId, motorId);
            return ResponseEntity.ok(onibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/{onibusId}/remover/motor/{motorId}")
    public ResponseEntity<?> removerMotor(@PathVariable Long onibusId, @PathVariable Long motorId) {
        try {
            Onibus onibus = onibusService.removerMotor(onibusId, motorId);
            return ResponseEntity.ok(onibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/{onibusId}/instalar/cambio/{cambioId}")
    public ResponseEntity<?> instalarCambio(@PathVariable Long onibusId, @PathVariable Long cambioId) {
        try {
            Onibus onibus = onibusService.instalarCambio(onibusId, cambioId);
            return ResponseEntity.ok(onibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/{onibusId}/remover/cambio/{cambioId}")
    public ResponseEntity<?> removerCambio(@PathVariable Long onibusId, @PathVariable Long cambioId) {
        try {
            Onibus onibus = onibusService.removerCambio(onibusId, cambioId);
            return ResponseEntity.ok(onibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/{onibusId}/instalar/pneu/{pneuId}")
    public ResponseEntity<?> instalarPneu(
            @PathVariable Long onibusId,
            @PathVariable Long pneuId,
            @RequestParam PosicaoPneu posicao) {
        try {
            Onibus onibus = onibusService.instalarPneu(onibusId, pneuId, posicao);
            return ResponseEntity.ok(onibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/{onibusId}/remover/pneu/{pneuId}")
    public ResponseEntity<?> removerPneu(@PathVariable Long onibusId, @PathVariable Long pneuId) {
        try {
            Onibus onibus = onibusService.removerPneu(onibusId, pneuId);
            return ResponseEntity.ok(onibus);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}