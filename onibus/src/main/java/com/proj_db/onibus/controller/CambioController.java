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

import com.proj_db.onibus.model.Cambio;
import com.proj_db.onibus.service.CambioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cambios")
@CrossOrigin(origins = "*")
public class CambioController {

    @Autowired
    private CambioService cambioService;

    @PostMapping
    public ResponseEntity<?> criarCambio(@Valid @RequestBody Cambio cambio) {
        try {
            Cambio novoCambio = cambioService.criarCambio(cambio);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoCambio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao criar câmbio: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Cambio>> listarTodos() {
        try {
            List<Cambio> cambios = cambioService.buscarTodos();
            return ResponseEntity.ok(cambios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Optional<Cambio> cambio = cambioService.buscarPorId(id);
            return cambio.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar câmbio: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCambio(
            @PathVariable Long id, 
            @Valid @RequestBody Cambio cambioAtualizado) {
        try {
            Cambio cambio = cambioService.atualizarCambio(id, cambioAtualizado);
            return ResponseEntity.ok(cambio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar câmbio: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCambio(@PathVariable Long id) {
        try {
            cambioService.excluirCambio(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar câmbio: " + e.getMessage());
        }
    }

    // ✅ NOVO ENDPOINT DE BUSCA COMBINADA
    @GetMapping("/search")
    public ResponseEntity<?> searchCambio(@RequestParam Map<String, String> searchTerms) {
        try {
            List<Cambio> cambios = cambioService.searchCambio(searchTerms);
            return ResponseEntity.ok(cambios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a busca: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/enviar-manutencao")
    public ResponseEntity<?> enviarParaManutencao(@PathVariable Long id) {
        try {
            Cambio cambio = cambioService.enviarParaManutencao(id);
            return ResponseEntity.ok(cambio);
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
            Cambio cambio = cambioService.retornarDeManutencao(id);
            return ResponseEntity.ok(cambio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao retornar da manutenção: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/trocar-fluido")
    public ResponseEntity<?> trocarFluido(
            @PathVariable Long id,
            @RequestParam String novoTipoFluido,
            @RequestParam Double novaQuantidade) {
        try {
            Cambio cambio = cambioService.trocarFluido(id, novoTipoFluido, novaQuantidade);
            return ResponseEntity.ok(cambio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao trocar fluido: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/registrar-revisao")
    public ResponseEntity<?> registrarRevisao(@PathVariable Long id) {
        try {
            Cambio cambio = cambioService.registrarRevisao(id);
            return ResponseEntity.ok(cambio);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar revisão: " + e.getMessage());
        }
    }
    
    // ✅ ENDPOINTS DE BUSCA INDIVIDUAL REMOVIDOS
    
    @GetMapping("/{id}/em-garantia")
    public ResponseEntity<?> estaEmGarantia(@PathVariable Long id) {
        try {
            boolean emGarantia = cambioService.estaEmGarantia(id);
            return ResponseEntity.ok(emGarantia);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao verificar garantia: " + e.getMessage());
        }
    }
}