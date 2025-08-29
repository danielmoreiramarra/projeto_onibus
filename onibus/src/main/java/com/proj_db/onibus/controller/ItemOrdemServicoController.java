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

import com.proj_db.onibus.model.ItemOrdemServico;
import com.proj_db.onibus.service.ItemOrdemServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/itens-ordem-servico")
@CrossOrigin(origins = "*")
public class ItemOrdemServicoController {

    @Autowired
    private ItemOrdemServicoService itemOrdemServicoService;

    // ✅ CRIAR NOVO ITEM DE ORDEM DE SERVIÇO
    @PostMapping
    public ResponseEntity<?> criarItem(@Valid @RequestBody ItemOrdemServico item) {
        try {
            ItemOrdemServico novoItem = itemOrdemServicoService.criarItem(item);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao criar item: " + e.getMessage());
        }
    }

    // ✅ ATUALIZAR ITEM
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarItem(
            @PathVariable Long id, 
            @Valid @RequestBody ItemOrdemServico itemAtualizado) {
        try {
            ItemOrdemServico item = itemOrdemServicoService.atualizarItem(id, itemAtualizado);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar item: " + e.getMessage());
        }
    }

    // ✅ DELETAR ITEM
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarItem(@PathVariable Long id) {
        try {
            itemOrdemServicoService.excluirItem(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao deletar item: " + e.getMessage());
        }
    }
    
    // ✅ NOVO ENDPOINT DE BUSCA COMBINADA
    @GetMapping("/search")
    public ResponseEntity<?> searchItemOrdemServico(@RequestParam Map<String, String> searchTerms) {
        try {
            List<ItemOrdemServico> itens = itemOrdemServicoService.searchItemOrdemServico(searchTerms);
            return ResponseEntity.ok(itens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a busca: " + e.getMessage());
        }
    }
    
    // ✅ BUSCAR ITEM POR ID
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Optional<ItemOrdemServico> item = itemOrdemServicoService.buscarPorId(id);
            return item.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar item: " + e.getMessage());
        }
    }

    // ✅ CALCULAR QUANTIDADE TOTAL CONSUMIDA DE UM PRODUTO
    @GetMapping("/produto/{produtoId}/quantidade-consumida")
    public ResponseEntity<?> calcularQuantidadeTotalConsumida(@PathVariable Long produtoId) {
        try {
            Integer quantidade = itemOrdemServicoService.calcularQuantidadeTotalConsumida(produtoId);
            return ResponseEntity.ok(quantidade);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao calcular quantidade consumida: " + e.getMessage());
        }
    }

    // ✅ CALCULAR QUANTIDADE TOTAL RESERVADA DE UM PRODUTO
    @GetMapping("/produto/{produtoId}/quantidade-reservada")
    public ResponseEntity<?> calcularQuantidadeTotalReservada(@PathVariable Long produtoId) {
        try {
            Integer quantidade = itemOrdemServicoService.calcularQuantidadeTotalReservada(produtoId);
            return ResponseEntity.ok(quantidade);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao calcular quantidade reservada: " + e.getMessage());
        }
    }

    // ✅ BUSCAR PRODUTOS MAIS UTILIZADOS
    @GetMapping("/produtos-mais-utilizados")
    public ResponseEntity<?> buscarProdutosMaisUtilizados() {
        try {
            List<Object[]> produtos = itemOrdemServicoService.buscarProdutosMaisUtilizados();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar produtos mais utilizados: " + e.getMessage());
        }
    }

    // ✅ BUSCAR HISTÓRICO DE CONSUMO POR PRODUTO
    @GetMapping("/produto/{produtoId}/historico-consumo")
    public ResponseEntity<?> buscarHistoricoConsumo(@PathVariable Long produtoId) {
        try {
            List<Object[]> historico = itemOrdemServicoService.buscarHistoricoConsumo(produtoId);
            return ResponseEntity.ok(historico);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar histórico de consumo: " + e.getMessage());
        }
    }

    // ✅ CALCULAR VALOR TOTAL DOS ITENS DE UMA ORDEM DE SERVIÇO
    @GetMapping("/ordem-servico/{ordemServicoId}/valor-total")
    public ResponseEntity<?> calcularValorTotalItens(@PathVariable Long ordemServicoId) {
        try {
            Double valorTotal = itemOrdemServicoService.calcularValorTotalItens(ordemServicoId);
            return ResponseEntity.ok(valorTotal);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao calcular valor total: " + e.getMessage());
        }
    }

    // ✅ ATUALIZAR QUANTIDADE DE ITEM EXISTENTE
    @PatchMapping("/ordem-servico/{ordemServicoId}/produto/{produtoId}/quantidade")
    public ResponseEntity<?> atualizarQuantidadeItem(
            @PathVariable Long ordemServicoId,
            @PathVariable Long produtoId,
            @RequestParam Integer novaQuantidade) {
        try {
            ItemOrdemServico item = itemOrdemServicoService
                    .atualizarQuantidadeItem(ordemServicoId, produtoId, novaQuantidade);
            return ResponseEntity.ok(item);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar quantidade: " + e.getMessage());
        }
    }
}