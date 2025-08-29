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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.service.ProdutoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<?> criarProduto(@Valid @RequestBody Produto produto) {
        try {
            Produto novoProduto = produtoService.criarProduto(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno ao criar produto: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        try {
            List<Produto> produtos = produtoService.buscarTodos();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            Optional<Produto> produto = produtoService.buscarPorId(id);
            return produto.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar produto: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarProduto(
            @PathVariable Long id, 
            @Valid @RequestBody Produto produtoAtualizado) {
        try {
            Produto produto = produtoService.atualizarProduto(id, produtoAtualizado);
            return ResponseEntity.ok(produto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirProduto(@PathVariable Long id) {
        try {
            produtoService.excluirProduto(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao excluir produto: " + e.getMessage());
        }
    }

    // ✅ NOVO ENDPOINT DE BUSCA COMBINADA (substitui várias buscas)
    @GetMapping("/search")
    public ResponseEntity<?> searchProduto(@RequestParam Map<String, String> searchTerms) {
        try {
            List<Produto> produtos = produtoService.searchProduto(searchTerms);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao realizar a busca: " + e.getMessage());
        }
    }

    @GetMapping("/codigo-interno/{codigoInterno}")
    public ResponseEntity<?> buscarPorCodigoInterno(@PathVariable String codigoInterno) {
        try {
            Optional<Produto> produto = produtoService.buscarPorCodigoInterno(codigoInterno);
            return produto.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por código interno: " + e.getMessage());
        }
    }

    @GetMapping("/codigo-barras/{codigoBarras}")
    public ResponseEntity<?> buscarPorCodigoBarras(@PathVariable String codigoBarras) {
        try {
            Optional<Produto> produto = produtoService.buscarPorCodigoBarras(codigoBarras);
            return produto.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por código de barras: " + e.getMessage());
        }
    }

    @GetMapping("/proximo-codigo-interno")
    public ResponseEntity<?> gerarProximoCodigoInterno() {
        try {
            String proximoCodigo = produtoService.gerarProximoCodigoInterno();
            return ResponseEntity.ok(proximoCodigo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao gerar próximo código interno: " + e.getMessage());
        }
    }

    @GetMapping("/mais-utilizados")
    public ResponseEntity<?> buscarProdutosMaisUtilizados() {
        try {
            List<Object[]> produtos = produtoService.buscarProdutosMaisUtilizados();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar produtos mais utilizados: " + e.getMessage());
        }
    }
}