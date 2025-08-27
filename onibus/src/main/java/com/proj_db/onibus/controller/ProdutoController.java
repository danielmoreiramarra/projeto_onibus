package com.proj_db.onibus.controller;

import java.util.List;
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
import com.proj_db.onibus.model.Produto.Categoria;
import com.proj_db.onibus.model.Produto.StatusProduto;
import com.proj_db.onibus.model.Produto.UnidadeMedida;
import com.proj_db.onibus.service.ProdutoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    // ✅ CRIAR NOVO PRODUTO
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

    // ✅ LISTAR TODOS OS PRODUTOS
    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        try {
            List<Produto> produtos = produtoService.buscarTodos();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ BUSCAR PRODUTO POR ID
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

    // ✅ ATUALIZAR PRODUTO
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

    // ✅ EXCLUIR PRODUTO (INATIVAR)
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

    // ✅ BUSCAR POR CÓDIGO INTERNO
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

    // ✅ BUSCAR POR CÓDIGO DE BARRAS
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

    // ✅ BUSCAR POR STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<?> buscarPorStatus(@PathVariable StatusProduto status) {
        try {
            List<Produto> produtos = produtoService.buscarPorStatus(status);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por status: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR MARCA
    @GetMapping("/marca/{marca}")
    public ResponseEntity<?> buscarPorMarca(@PathVariable String marca) {
        try {
            List<Produto> produtos = produtoService.buscarPorMarca(marca);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por marca: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR CATEGORIA
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<?> buscarPorCategoria(@PathVariable Categoria categoria) { // Alterado para Categoria
        try {
            List<Produto> produtos = ((com.proj_db.onibus.service.ProdutoImpl) produtoService).buscarPorCategoria(categoria);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por categoria: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR UNIDADE DE MEDIDA
    @GetMapping("/unidade-medida/{unidadeMedida}")
    public ResponseEntity<?> buscarPorUnidadeMedida(@PathVariable UnidadeMedida unidadeMedida) {
        try {
            List<Produto> produtos = produtoService.buscarPorUnidadeMedida(unidadeMedida);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por unidade de medida: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR NOME
    @GetMapping("/nome/{nome}")
    public ResponseEntity<?> buscarPorNome(@PathVariable String nome) {
        try {
            List<Produto> produtos = produtoService.buscarPorNome(nome);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por nome: " + e.getMessage());
        }
    }

    // ✅ BUSCAR POR INTERVALO DE PREÇO
    @GetMapping("/preco")
    public ResponseEntity<?> buscarPorIntervaloPreco(
            @RequestParam Double precoMinimo,
            @RequestParam Double precoMaximo) {
        try {
            List<Produto> produtos = produtoService.buscarPorIntervaloPreco(precoMinimo, precoMaximo);
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar por intervalo de preço: " + e.getMessage());
        }
    }

    // ✅ BUSCAR PRODUTOS ATIVOS
    @GetMapping("/ativos")
    public ResponseEntity<?> buscarProdutosAtivos() {
        try {
            List<Produto> produtos = produtoService.buscarProdutosAtivos();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar produtos ativos: " + e.getMessage());
        }
    }

    // ✅ BUSCAR PRODUTOS COM ESTOQUE ABAIXO DO MÍNIMO
    @GetMapping("/estoque-abaixo-minimo")
    public ResponseEntity<?> buscarProdutosComEstoqueAbaixoMinimo() {
        try {
            List<Produto> produtos = produtoService.buscarProdutosComEstoqueAbaixoMinimo();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar produtos com estoque abaixo do mínimo: " + e.getMessage());
        }
    }

    // ✅ BUSCAR PRODUTOS NUNCA UTILIZADOS
    @GetMapping("/nunca-utilizados")
    public ResponseEntity<?> buscarProdutosNuncaUtilizados() {
        try {
            List<Produto> produtos = produtoService.buscarProdutosNuncaUtilizados();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar produtos nunca utilizados: " + e.getMessage());
        }
    }

    // ✅ BUSCAR PRODUTOS SEM MOVIMENTO
    @GetMapping("/sem-movimento")
    public ResponseEntity<?> buscarProdutosSemMovimento() {
        try {
            List<Produto> produtos = produtoService.buscarProdutosSemMovimento();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar produtos sem movimento: " + e.getMessage());
        }
    }

    // ✅ VERIFICAR SE CÓDIGO INTERNO EXISTE
    @GetMapping("/existe-codigo-interno/{codigoInterno}")
    public ResponseEntity<Boolean> existeCodigoInterno(@PathVariable String codigoInterno) {
        try {
            boolean existe = produtoService.existeCodigoInterno(codigoInterno);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ VERIFICAR SE CÓDIGO DE BARRAS EXISTE
    @GetMapping("/existe-codigo-barras/{codigoBarras}")
    public ResponseEntity<Boolean> existeCodigoBarras(@PathVariable String codigoBarras) {
        try {
            boolean existe = produtoService.existeCodigoBarras(codigoBarras);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ GERAR PRÓXIMO CÓDIGO INTERNO
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

    // ✅ BUSCAR PRODUTOS MAIS UTILIZADOS
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

    // ✅ BUSCAR PRODUTOS POR GIRO
    @GetMapping("/por-giro")
    public ResponseEntity<?> buscarProdutosPorGiro() {
        try {
            List<Object[]> produtos = produtoService.buscarProdutosPorGiro();
            return ResponseEntity.ok(produtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar produtos por giro: " + e.getMessage());
        }
    }

    // ✅ BUSCAR ESTATÍSTICAS POR CATEGORIA
    @GetMapping("/estatisticas-categoria")
    public ResponseEntity<?> buscarEstatisticasPorCategoria() {
        try {
            List<Object[]> estatisticas = produtoService.buscarEstatisticasPorCategoria();
            return ResponseEntity.ok(estatisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao buscar estatísticas por categoria: " + e.getMessage());
        }
    }
}