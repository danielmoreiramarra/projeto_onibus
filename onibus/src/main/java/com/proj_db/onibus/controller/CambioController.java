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

import com.proj_db.onibus.model.Cambio;
import com.proj_db.onibus.model.Cambio.StatusCambio;
import com.proj_db.onibus.model.Cambio.TipoCambio;
import com.proj_db.onibus.service.CambioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cambios")
@CrossOrigin(origins = "*")
public class CambioController {

    @Autowired
    private CambioService cambioService;

    // ✅ CRIAR NOVO CÂMBIO
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

    // ✅ LISTAR TODOS OS CÂMBIOS
    @GetMapping
    public ResponseEntity<List<Cambio>> listarTodos() {
        try {
            List<Cambio> cambios = cambioService.buscarTodos();
            return ResponseEntity.ok(cambios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ BUSCAR CÂMBIO POR ID
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

    // ✅ ATUALIZAR CÂMBIO
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

    // ✅ DELETAR CÂMBIO
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

    // ✅ BUSCAR POR NÚMERO DE SÉRIE
    @GetMapping("/numero-serie/{numeroSerie}")
    public ResponseEntity<?> buscarPorNumeroSerie(@PathVariable String numeroSerie) {
        try {
            Optional<Cambio> cambio = cambioService.buscarPorNumeroSerie(numeroSerie);
            return cambio.map(ResponseEntity::ok)
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
            Optional<Cambio> cambio = cambioService.buscarPorCodigoFabricacao(codigo);
            return cambio.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por código de fabricação: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<?> buscarPorStatus(@PathVariable StatusCambio status) {
        try {
            List<Cambio> cambios = cambioService.buscarPorStatus(status);
            return ResponseEntity.ok(cambios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por status: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR TIPO
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<?> buscarPorTipo(@PathVariable TipoCambio tipo) {
        try {
            List<Cambio> cambios = cambioService.buscarPorTipo(tipo);
            return ResponseEntity.ok(cambios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por tipo: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR MARCA
    @GetMapping("/marca/{marca}")
    public ResponseEntity<?> buscarPorMarca(@PathVariable String marca) {
        try {
            List<Cambio> cambios = cambioService.buscarPorMarca(marca);
            return ResponseEntity.ok(cambios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por marca: " + e.getMessage());
        }
    }

    // ✅ BUSCAR CÂMBIOS DISPONÍVEIS
    @GetMapping("/disponiveis")
    public ResponseEntity<?> buscarDisponiveis() {
        try {
            List<Cambio> cambios = cambioService.buscarDisponiveis();
            return ResponseEntity.ok(cambios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar câmbios disponíveis: " + e.getMessage());
        }
    }

    // ✅ BUSCAR CÂMBIOS EM USO
    @GetMapping("/em-uso")
    public ResponseEntity<?> buscarEmUso() {
        try {
            List<Cambio> cambios = cambioService.buscarEmUso();
            return ResponseEntity.ok(cambios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar câmbios em uso: " + e.getMessage());
        }
    }

    // ✅ ENVIAR PARA MANUTENÇÃO
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

    // ✅ RETORNAR DA MANUTENÇÃO
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

    // ✅ TROCAR FLUIDO
    @PatchMapping("/{id}/trocar-fluido")
    public ResponseEntity<?> trocarFluido(
            @PathVariable Long id,
            @RequestParam String novoTipoFluido,
            @RequestParam Double novaQuantidade) {
        try {
            boolean sucesso = cambioService.trocarFluido(id, novoTipoFluido, novaQuantidade);
            return ResponseEntity.ok(sucesso ? "Fluido trocado com sucesso" : "Falha ao trocar fluido");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao trocar fluido: " + e.getMessage());
        }
    }

    // ✅ REGISTRAR REVISÃO
    @PatchMapping("/{id}/registrar-revisao")
    public ResponseEntity<?> registrarRevisao(@PathVariable Long id) {
        try {
            boolean sucesso = cambioService.registrarRevisao(id);
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
            boolean emGarantia = cambioService.estaEmGarantia(id);
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
            boolean existe = cambioService.existeNumeroSerie(numeroSerie);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ VERIFICAR SE CÓDIGO DE FABRICAÇÃO EXISTE
    @GetMapping("/existe-codigo-fabricacao/{codigo}")
    public ResponseEntity<Boolean> existeCodigoFabricacao(@PathVariable String codigo) {
        try {
            boolean existe = cambioService.existeCodigoFabricacao(codigo);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ BUSCAR CÂMBIOS PARA REVISÃO
    @GetMapping("/para-revisao")
    public ResponseEntity<?> buscarCambiosParaRevisao() {
        try {
            List<Cambio> cambios = cambioService.buscarCambiosParaRevisao();
            return ResponseEntity.ok(cambios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar câmbios para revisão: " + e.getMessage());
        }
    }

    // ✅ BUSCAR CÂMBIOS COM GARANTIA PRESTES A VENCER
    @GetMapping("/garantia-prestes-vencer")
    public ResponseEntity<?> buscarCambiosComGarantiaPrestesVencer() {
        try {
            List<Cambio> cambios = cambioService.buscarCambiosComGarantiaPrestesVencer();
            return ResponseEntity.ok(cambios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar câmbios com garantia prestes a vencer: " + e.getMessage());
        }
    }
}