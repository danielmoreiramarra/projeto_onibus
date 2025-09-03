package com.proj_db.onibus.controller;

import java.util.List;
import java.util.stream.Collectors;

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

import com.proj_db.onibus.dto.ProdutoCreateDTO;
import com.proj_db.onibus.dto.ProdutoResponseDTO;
import com.proj_db.onibus.dto.ProdutoUpdateDTO;
import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.service.ProdutoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;
    
    // DTO simples para a requisição de atualização de preço
    public record UpdatePriceDTO(@NotNull @Positive Double novoPreco){}

    // --- Endpoints CRUD ---

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> create(@Valid @RequestBody ProdutoCreateDTO dto) {
        Produto produtoSalvo = produtoService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProdutoResponseDTO(produtoSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProdutoUpdateDTO dto) {
        Produto produtoAtualizado = produtoService.update(id, dto);
        return ResponseEntity.ok(new ProdutoResponseDTO(produtoAtualizado));
    }

    @PatchMapping("/{id}/price") // Endpoint específico para atualizar o preço
    public ResponseEntity<ProdutoResponseDTO> updatePrice(@PathVariable Long id, @Valid @RequestBody UpdatePriceDTO dto) {
        Produto produtoComPrecoAtualizado = produtoService.updatePrice(id, dto.novoPreco());
        return ResponseEntity.ok(new ProdutoResponseDTO(produtoComPrecoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archive(@PathVariable Long id) {
        // Usa o método de "soft delete" (inativar) do serviço
        produtoService.archiveById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints de Consulta ---

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> findById(@PathVariable Long id) {
        return produtoService.findById(id)
                .map(ProdutoResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> findAll() {
        List<ProdutoResponseDTO> dtos = produtoService.findAll().stream()
                .map(ProdutoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProdutoResponseDTO>> search(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String codigoInterno,
            @RequestParam(required = false) Produto.Categoria categoria,
            @RequestParam(required = false) Produto.StatusProduto status
    ) {
        ProdutoService.ProdutoSearchDTO criteria = new ProdutoService.ProdutoSearchDTO(nome, marca, codigoInterno, categoria, status);
        List<ProdutoResponseDTO> dtos = produtoService.search(criteria).stream()
                .map(ProdutoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    // --- Endpoints de Relatório e Auxiliares ---

    @GetMapping("/proximo-codigo")
    public ResponseEntity<String> getNextInternalCode() {
        return ResponseEntity.ok(produtoService.gerarProximoCodigoInterno());
    }

    @GetMapping("/alertas/estoque-baixo")
    public ResponseEntity<List<ProdutoResponseDTO>> findProdutosComEstoqueAbaixoMinimo() {
        List<ProdutoResponseDTO> dtos = produtoService.findProdutosComEstoqueAbaixoMinimo().stream()
                .map(ProdutoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/relatorios/mais-utilizados")
    public ResponseEntity<List<Object[]>> findProdutosMaisUtilizados() {
        // Relatórios com agregações podem retornar List<Object[]> diretamente
        return ResponseEntity.ok(produtoService.findProdutosMaisUtilizados());
    }

    @GetMapping("/relatorios/contagem-por-categoria")
    public ResponseEntity<List<Object[]>> countByCategoria() {
        return ResponseEntity.ok(produtoService.countByCategoria());
    }
}