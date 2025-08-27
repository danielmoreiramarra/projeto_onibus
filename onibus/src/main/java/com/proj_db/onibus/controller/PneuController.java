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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.model.Pneu.StatusPneu;
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

    // ✅ BUSCAR POR NÚMERO DE SÉRIE
    @GetMapping("/numero-serie/{numeroSerie}")
    public ResponseEntity<?> buscarPorNumeroSerie(@PathVariable String numeroSerie) {
        try {
            Optional<Pneu> pneu = pneuService.buscarPorNumeroSerie(numeroSerie);
            return pneu.map(ResponseEntity::ok)
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
            Optional<Pneu> pneu = pneuService.buscarPorCodigoFabricacao(codigo);
            return pneu.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por código de fabricação: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<?> buscarPorStatus(@PathVariable StatusPneu status) {
        try {
            List<Pneu> pneus = pneuService.buscarPorStatus(status);
            return ResponseEntity.ok(pneus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por status: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR MARCA
    @GetMapping("/marca/{marca}")
    public ResponseEntity<?> buscarPorMarca(@PathVariable String marca) {
        try {
            List<Pneu> pneus = pneuService.buscarPorMarca(marca);
            return ResponseEntity.ok(pneus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por marca: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR MEDIDA
    @GetMapping("/medida/{medida}")
    public ResponseEntity<?> buscarPorMedida(@PathVariable String medida) {
        try {
            List<Pneu> pneus = pneuService.buscarPorMedida(medida);
            return ResponseEntity.ok(pneus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por medida: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR MARCA E MEDIDA
    @GetMapping("/marca/{marca}/medida/{medida}")
    public ResponseEntity<?> buscarPorMarcaEMedida(
            @PathVariable String marca,
            @PathVariable String medida) {
        try {
            List<Pneu> pneus = pneuService.buscarPorMarcaEMedida(marca, medida);
            return ResponseEntity.ok(pneus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por marca e medida: " + e.getMessage());
        }
    }

    // ✅ BUSCAR PNEUS DISPONÍVEIS
    @GetMapping("/disponiveis")
    public ResponseEntity<?> buscarDisponiveis() {
        try {
            List<Pneu> pneus = pneuService.buscarDisponiveis();
            return ResponseEntity.ok(pneus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar pneus disponíveis: " + e.getMessage());
        }
    }

    // ✅ BUSCAR PNEUS EM USO
    @GetMapping("/em-uso")
    public ResponseEntity<?> buscarEmUso() {
        try {
            List<Pneu> pneus = pneuService.buscarEmUso();
            return ResponseEntity.ok(pneus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar pneus em uso: " + e.getMessage());
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

    // ✅ REGISTRAR KM RODADOS
    @PatchMapping("/{id}/registrar-km")
    public ResponseEntity<?> registrarKmRodados(
            @PathVariable Long id,
            @RequestParam Integer kmAdicionais) {
        try {
            boolean sucesso = pneuService.registrarKmRodados(id, kmAdicionais);
            return ResponseEntity.ok(sucesso ? "KM registrados com sucesso" : "Falha ao registrar KM");
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

    // ✅ VERIFICAR SE NÚMERO DE SÉRIE EXISTE
    @GetMapping("/existe-numero-serie/{numeroSerie}")
    public ResponseEntity<Boolean> existeNumeroSerie(@PathVariable String numeroSerie) {
        try {
            boolean existe = pneuService.existeNumeroSerie(numeroSerie);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ VERIFICAR SE CÓDIGO DE FABRICAÇÃO EXISTE
    @GetMapping("/existe-codigo-fabricacao/{codigo}")
    public ResponseEntity<Boolean> existeCodigoFabricacao(@PathVariable String codigo) {
        try {
            boolean existe = pneuService.existeCodigoFabricacao(codigo);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ BUSCAR PNEUS PARA TROCA
    @GetMapping("/para-troca")
    public ResponseEntity<?> buscarPneusParaTroca() {
        try {
            List<Pneu> pneus = pneuService.buscarPneusParaTroca();
            return ResponseEntity.ok(pneus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar pneus para troca: " + e.getMessage());
        }
    }

    // ✅ BUSCAR PNEUS COM GARANTIA PRESTES A VENCER
    @GetMapping("/garantia-prestes-vencer")
    public ResponseEntity<?> buscarPneusComGarantiaPrestesVencer() {
        try {
            List<Pneu> pneus = pneuService.buscarPneusComGarantiaPrestesVencer();
            return ResponseEntity.ok(pneus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar pneus com garantia prestes a vencer: " + e.getMessage());
        }
    }

    // ✅ BUSCAR ESTATÍSTICAS POR STATUS
    @GetMapping("/estatisticas-status")
    public ResponseEntity<?> estatisticasPorStatus() {
        try {
            List<Object[]> estatisticas = pneuService.estatisticasPorStatus();
            return ResponseEntity.ok(estatisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar estatísticas por status: " + e.getMessage());
        }
    }

    // ✅ BUSCAR ESTATÍSTICAS POR MARCA
    @GetMapping("/estatisticas-marca")
    public ResponseEntity<?> estatisticasPorMarca() {
        try {
            List<Object[]> estatisticas = pneuService.estatisticasPorMarca();
            return ResponseEntity.ok(estatisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar estatísticas por marca: " + e.getMessage());
        }
    }
}