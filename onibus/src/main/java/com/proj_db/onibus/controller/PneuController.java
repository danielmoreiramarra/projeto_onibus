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

import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.service.PneuService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pneus")
@CrossOrigin(origins = "*")
public class PneuController {

    @Autowired
    private PneuService pneuService;

    // ✅ CRIAR NOVO PNEU
    @PostMapping
    public ResponseEntity<?> criarPneu(@Valid @RequestBody Pneu pneu) {
        try {
            Pneu novoPneu = pneuService.criarPneu(pneu);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoPneu);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao criar pneu: " + e.getMessage());
        }
    }

    // ✅ LISTAR TODOS OS PNEUS
    @GetMapping
    public ResponseEntity<List<Pneu>> listarTodos() {
        try {
            List<Pneu> pneus = pneuService.buscarTodos();
            return ResponseEntity.ok(pneus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ BUSCAR PNEU POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Optional<Pneu> pneu = pneuService.buscarPorId(id);
            return pneu.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar pneu: " + e.getMessage());
        }
    }

    // ✅ ATUALIZAR PNEU
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarPneu(
            @PathVariable Long id, 
            @Valid @RequestBody Pneu pneuAtualizado) {
        try {
            Pneu pneu = pneuService.atualizarPneu(id, pneuAtualizado);
            return ResponseEntity.ok(pneu);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar pneu: " + e.getMessage());
        }
    }

    // ✅ EXCLUIR PNEU
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirPneu(@PathVariable Long id) {
        try {
            pneuService.excluirPneu(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir pneu: " + e.getMessage());
        }
    }

    // ✅ NOVO ENDPOINT DE BUSCA COMBINADA
    @GetMapping("/search")
    public ResponseEntity<?> searchPneu(@RequestParam Map<String, String> searchTerms) {
        try {
            List<Pneu> pneus = pneuService.searchPneu(searchTerms);
            return ResponseEntity.ok(pneus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a busca: " + e.getMessage());
        }
    }

    // ✅ ENVIAR PARA MANUTENÇÃO
    @PatchMapping("/{id}/enviar-manutencao")
    public ResponseEntity<?> enviarParaManutencao(@PathVariable Long id) {
        try {
            Pneu pneu = pneuService.enviarParaManutencao(id);
            return ResponseEntity.ok(pneu);
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
            Pneu pneu = pneuService.retornarDeManutencao(id);
            return ResponseEntity.ok(pneu);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao retornar da manutenção: " + e.getMessage());
        }
    }

    // ✅ DESCARTAR PNEU
    @PatchMapping("/{id}/descartar")
    public ResponseEntity<?> descartarPneu(@PathVariable Long id) {
        try {
            Pneu pneu = pneuService.descartarPneu(id);
            return ResponseEntity.ok(pneu);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao descartar pneu: " + e.getMessage());
        }
    }

    // ✅ REGISTRAR KM RODADOS (retorna o objeto atualizado)
    @PatchMapping("/{id}/registrar-km")
    public ResponseEntity<?> registrarKmRodados(
            @PathVariable Long id,
            @RequestParam Integer kmAdicionais) {
        try {
            Pneu pneu = pneuService.registrarKmRodados(id, kmAdicionais);
            return ResponseEntity.ok(pneu);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar KM: " + e.getMessage());
        }
    }

    // ✅ VERIFICAR SE PRECISA DE TROCA
    @GetMapping("/{id}/precisa-troca")
    public ResponseEntity<?> precisaTroca(@PathVariable Long id) {
        try {
            boolean precisaTroca = pneuService.precisaTroca(id);
            return ResponseEntity.ok(precisaTroca);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao verificar necessidade de troca: " + e.getMessage());
        }
    }
}