package com.proj_db.onibus.controller;

import java.time.LocalDate;
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

import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.service.OrdemServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ordens-servico")
@CrossOrigin(origins = "*")
public class OrdemServicoController {

    @Autowired
    private OrdemServicoService ordemServicoService;

    @PostMapping
    public ResponseEntity<?> criarOrdemServico(@Valid @RequestBody OrdemServico ordemServico) {
        try {
            OrdemServico novaOS = ordemServicoService.criarOrdemServico(ordemServico);
            return ResponseEntity.status(HttpStatus.CREATED).body(novaOS);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao criar ordem de serviço: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<OrdemServico>> listarTodas() {
        try {
            List<OrdemServico> ordens = ordemServicoService.buscarTodas();
            return ResponseEntity.ok(ordens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            OrdemServico ordemServico = ordemServicoService.buscarPorId(id);
            return ResponseEntity.ok(ordemServico);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar ordem de serviço: " + e.getMessage());
        }
    }

    // ✅ O controller agora lida com o retorno Optional
    @GetMapping("/numero/{numeroOS}")
    public ResponseEntity<?> buscarPorNumeroOS(@PathVariable String numeroOS) {
        try {
            Optional<OrdemServico> ordemServico = ordemServicoService.buscarPorNumeroOS(numeroOS);
            return ordemServico.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por número da OS: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarOrdemServico(
            @PathVariable Long id, 
            @Valid @RequestBody OrdemServico ordemServicoAtualizada) {
        try {
            OrdemServico os = ordemServicoService.atualizarOrdemServico(id, ordemServicoAtualizada);
            return ResponseEntity.ok(os);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar ordem de serviço: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirOrdemServico(@PathVariable Long id) {
        try {
            ordemServicoService.excluirOrdemServico(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir ordem de serviço: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarOrdemServico(@PathVariable Long id) {
        try {
            OrdemServico os = ordemServicoService.cancelarOrdemServico(id);
            return ResponseEntity.ok(os);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cancelar ordem de serviço: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/cancelar-com-motivo")
    public ResponseEntity<?> cancelarOrdemServicoComMotivo(
            @PathVariable Long id,
            @RequestParam String motivo) {
        try {
            OrdemServico os = ordemServicoService.cancelarOrdemServico(id, motivo);
            return ResponseEntity.ok(os);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cancelar ordem de serviço: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/iniciar-execucao")
    public ResponseEntity<?> iniciarExecucao(@PathVariable Long id) {
        try {
            OrdemServico os = ordemServicoService.iniciarExecucao(id);
            return ResponseEntity.ok(os);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao iniciar execução: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<?> finalizarOrdemServico(@PathVariable Long id) {
        try {
            OrdemServico os = ordemServicoService.finalizarOrdemServico(id);
            return ResponseEntity.ok(os);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao finalizar ordem de serviço: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/verificar-estoque")
    public ResponseEntity<?> verificarEstoqueSuficiente(@PathVariable Long id) {
        try {
            boolean estoqueSuficiente = ordemServicoService.verificarEstoqueSuficiente(id);
            return ResponseEntity.ok(estoqueSuficiente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao verificar estoque: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/valor-total")
    public ResponseEntity<?> calcularValorTotal(@PathVariable Long id) {
        try {
            Double valorTotal = ordemServicoService.calcularValorTotal(id);
            return ResponseEntity.ok(valorTotal);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao calcular valor total: " + e.getMessage());
        }
    }

    @GetMapping("/em-aberto")
    public ResponseEntity<?> buscarOrdensEmAberto() {
        try {
            List<OrdemServico> ordens = ordemServicoService.buscarOrdensEmAberto();
            return ResponseEntity.ok(ordens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar ordens em aberto: " + e.getMessage());
        }
    }

    @GetMapping("/em-execucao")
    public ResponseEntity<?> buscarOrdensEmExecucao() {
        try {
            List<OrdemServico> ordens = ordemServicoService.buscarOrdensEmExecucao();
            return ResponseEntity.ok(ordens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar ordens em execução: " + e.getMessage());
        }
    }

    @GetMapping("/finalizadas-periodo")
    public ResponseEntity<?> buscarOrdensFinalizadasNoPeriodo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        try {
            List<OrdemServico> ordens = ordemServicoService.buscarOrdensFinalizadasNoPeriodo(dataInicio, dataFim);
            return ResponseEntity.ok(ordens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar ordens finalizadas: " + e.getMessage());
        }
    }

    @GetMapping("/previsao-vencida")
    public ResponseEntity<?> buscarOrdensComPrevisaoVencida() {
        try {
            List<OrdemServico> ordens = ordemServicoService.buscarOrdensComPrevisaoVencida();
            return ResponseEntity.ok(ordens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar ordens com previsão vencida: " + e.getMessage());
        }
    }

    @GetMapping("/proximo-numero")
    public ResponseEntity<?> gerarProximoNumeroOS() {
        try {
            String proximoNumero = ordemServicoService.gerarProximoNumeroOS();
            return ResponseEntity.ok(proximoNumero);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar próximo número: " + e.getMessage());
        }
    }

    @GetMapping("/estatisticas-status")
    public ResponseEntity<?> buscarEstatisticasPorStatus() {
        try {
            List<Object[]> estatisticas = ordemServicoService.buscarEstatisticasPorStatus();
            return ResponseEntity.ok(estatisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar estatísticas por status: " + e.getMessage());
        }
    }

    @GetMapping("/estatisticas-tipo")
    public ResponseEntity<?> buscarEstatisticasPorTipo() {
        try {
            List<Object[]> estatisticas = ordemServicoService.buscarEstatisticasPorTipo();
            return ResponseEntity.ok(estatisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar estatísticas por tipo: " + e.getMessage());
        }
    }

    @GetMapping("/faturamento-periodo")
    public ResponseEntity<?> calcularFaturamentoPeriodo(
            @RequestParam LocalDate dataInicio,
            @RequestParam LocalDate dataFim) {
        try {
            Double faturamento = ordemServicoService.calcularFaturamentoPeriodo(dataInicio, dataFim);
            return ResponseEntity.ok(faturamento);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao calcular faturamento: " + e.getMessage());
        }
    }
}