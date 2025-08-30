package com.proj_db.onibus.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proj_db.onibus.model.Estoque;
import com.proj_db.onibus.service.EstoqueService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/estoque")
@CrossOrigin(origins = "*")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    // ✅ CRIAR NOVO REGISTRO DE ESTOQUE
    @PostMapping
    public ResponseEntity<?> criarEstoque(@Valid @RequestBody Estoque estoque) {
        try {
            Estoque novoEstoque = estoqueService.criarRegistroEstoque(estoque);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoEstoque);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao criar registro de estoque: " + e.getMessage());
        }
    }

    // ✅ LISTAR TODOS OS REGISTROS DE ESTOQUE
    @GetMapping
    public ResponseEntity<List<Estoque>> listarTodos() {
        try {
            List<Estoque> estoques = estoqueService.buscarTodos();
            return ResponseEntity.ok(estoques);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ BUSCAR ESTOQUE POR ID DO PRODUTO
    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<?> buscarPorProdutoId(@PathVariable Long produtoId) {
        try {
            Estoque estoque = estoqueService.buscarPorProdutoId(produtoId);
            return ResponseEntity.ok(estoque);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar estoque: " + e.getMessage());
        }
    }

    // ✅ ATUALIZAR REGISTRO DE ESTOQUE
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarEstoque(
            @PathVariable Long id, 
            @Valid @RequestBody Estoque estoqueAtualizado) {
        try {
            Estoque estoque = estoqueService.atualizarEstoque(id, estoqueAtualizado);
            return ResponseEntity.ok(estoque);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar estoque: " + e.getMessage());
        }
    }
    
    // ✅ NOVO ENDPOINT DE BUSCA COMBINADA
    @GetMapping("/search")
    public ResponseEntity<?> searchEstoque(@RequestParam Map<String, String> searchTerms) {
        try {
            List<Estoque> estoques = estoqueService.searchEstoque(searchTerms);
            return ResponseEntity.ok(estoques);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a busca: " + e.getMessage());
        }
    }

    // ✅ ADICIONAR ESTOQUE (retorna o objeto atualizado)
    @PatchMapping("/produto/{produtoId}/adicionar")
    public ResponseEntity<?> adicionarEstoque(
            @PathVariable Long produtoId,
            @RequestParam Integer quantidade) {
        try {
            Estoque estoque = estoqueService.adicionarEstoque(produtoId, quantidade);
            return ResponseEntity.ok(estoque);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao adicionar estoque: " + e.getMessage());
        }
    }

    // ✅ RESERVAR ESTOQUE (retorna o objeto atualizado)
    @PatchMapping("/produto/{produtoId}/reservar")
    public ResponseEntity<?> reservarEstoque(
            @PathVariable Long produtoId,
            @RequestParam Integer quantidade) {
        try {
            Estoque estoque = estoqueService.reservarEstoque(produtoId, quantidade);
            return ResponseEntity.ok(estoque);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao reservar estoque: " + e.getMessage());
        }
    }

    // ✅ CONSUMIR ESTOQUE (retorna o objeto atualizado)
    @PatchMapping("/produto/{produtoId}/consumir")
    public ResponseEntity<?> consumirEstoque(
            @PathVariable Long produtoId,
            @RequestParam Integer quantidade) {
        try {
            Estoque estoque = estoqueService.consumirEstoque(produtoId, quantidade);
            return ResponseEntity.ok(estoque);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao consumir estoque: " + e.getMessage());
        }
    }

    // ✅ LIBERAR RESERVA (retorna o objeto atualizado)
    @PatchMapping("/produto/{produtoId}/liberar-reserva")
    public ResponseEntity<?> liberarReserva(
            @PathVariable Long produtoId,
            @RequestParam Integer quantidade) {
        try {
            Estoque estoque = estoqueService.liberarReserva(produtoId, quantidade);
            return ResponseEntity.ok(estoque);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao liberar reserva: " + e.getMessage());
        }
    }

    // ✅ CONSULTAR QUANTIDADE DISPONÍVEL
    @GetMapping("/produto/{produtoId}/quantidade-disponivel")
    public ResponseEntity<?> consultarQuantidadeDisponivel(@PathVariable Long produtoId) {
        try {
            Integer quantidade = estoqueService.consultarQuantidadeDisponivel(produtoId);
            return ResponseEntity.ok(quantidade);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao consultar quantidade disponível: " + e.getMessage());
        }
    }

    // ✅ VERIFICAR DISPONIBILIDADE
    @GetMapping("/produto/{produtoId}/verificar-disponibilidade")
    public ResponseEntity<?> verificarDisponibilidade(
            @PathVariable Long produtoId,
            @RequestParam Integer quantidade) {
        try {
            boolean disponivel = estoqueService.verificarDisponibilidade(produtoId, quantidade);
            return ResponseEntity.ok(disponivel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao verificar disponibilidade: " + e.getMessage());
        }
    }

    // ✅ CALCULAR VALOR TOTAL DO ESTOQUE
    @GetMapping("/valor-total")
    public ResponseEntity<?> calcularValorTotalEstoque() {
        try {
            Double valorTotal = estoqueService.calcularValorTotalEstoque();
            return ResponseEntity.ok(valorTotal);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao calcular valor total do estoque: " + e.getMessage());
        }
    }
    
    // ✅ BUSCAR PRODUTOS COM MAIOR GIRO
    @GetMapping("/produtos-maior-giro")
    public ResponseEntity<?> buscarProdutosComMaiorGiro() {
        try {
            List<Object[]> produtos = estoqueService.buscarProdutosComMaiorGiro();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar produtos com maior giro: " + e.getMessage());
        }
    }
    
    // ✅ BUSCAR ESTOQUE ABAIXO DO MÍNIMO (RE-ADICIONADO)
    @GetMapping("/abaixo-minimo")
    public ResponseEntity<?> buscarEstoqueAbaixoMinimo() {
        try {
            List<Estoque> estoques = estoqueService.buscarEstoqueAbaixoMinimo();
            return ResponseEntity.ok(estoques);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar estoque abaixo do mínimo: " + e.getMessage());
        }
    }
    
    // ✅ BUSCAR ESTOQUE CRÍTICO (RE-ADICIONADO)
    @GetMapping("/critico")
    public ResponseEntity<?> buscarEstoqueCritico() {
        try {
            List<Estoque> estoques = estoqueService.buscarEstoqueCritico();
            return ResponseEntity.ok(estoques);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar estoque crítico: " + e.getMessage());
        }
    }
    
    // ✅ BUSCAR ESTOQUE PARA REABASTECER (RE-ADICIONADO)
    @GetMapping("/para-reabastecer")
    public ResponseEntity<?> buscarEstoqueParaReabastecer() {
        try {
            List<Estoque> estoques = estoqueService.buscarEstoqueParaReabastecer();
            return ResponseEntity.ok(estoques);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar estoque para reabastecer: " + e.getMessage());
        }
    }
}