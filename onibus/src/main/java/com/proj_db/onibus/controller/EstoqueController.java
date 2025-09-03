package com.proj_db.onibus.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proj_db.onibus.dto.EstoqueResponseDTO;
import com.proj_db.onibus.dto.EstoqueUpdateDTO;
import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.service.EstoqueService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/estoque")
@CrossOrigin(origins = "*")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    // DTO para a ação de adicionar estoque
    public record AdicionarEstoqueDTO(@NotNull @Positive Double quantidade) {}

    // --- Endpoints de Consulta ---

    @GetMapping
    public ResponseEntity<List<EstoqueResponseDTO>> findAll() {
        List<EstoqueResponseDTO> dtos = estoqueService.findAll().stream()
                .map(EstoqueResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<EstoqueResponseDTO> findByProdutoId(@PathVariable Long produtoId) {
        return estoqueService.findByProdutoId(produtoId)
                .map(EstoqueResponseDTO::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<EstoqueResponseDTO>> search(
            @RequestParam(required = false) Long produtoId,
            @RequestParam(required = false) String nomeProduto,
            @RequestParam(required = false) String marcaProduto,
            @RequestParam(required = false) Produto.Categoria categoriaProduto,
            @RequestParam(required = false) String localizacao
    ) {
        EstoqueService.EstoqueSearchDTO criteria = new EstoqueService.EstoqueSearchDTO(
                produtoId, nomeProduto, marcaProduto, categoriaProduto, localizacao);
        
        List<EstoqueResponseDTO> dtos = estoqueService.search(criteria).stream()
                .map(EstoqueResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // --- Endpoints de Ações de Negócio ---

    @PatchMapping("/{id}/localizacao")
    public ResponseEntity<EstoqueResponseDTO> updateLocation(
            @PathVariable Long id, 
            @Valid @RequestBody EstoqueUpdateDTO dto) {
        // Lógica para atualizar a localização será adicionada ao EstoqueService
        // Estoque estoque = estoqueService.updateLocation(id, dto);
        // return ResponseEntity.ok(new EstoqueResponseDTO(estoque));
        // Por enquanto, retornamos um placeholder:
        return ResponseEntity.ok().build(); // TODO: Implementar a lógica no serviço
    }

    @PostMapping("/produto/{produtoId}/adicionar")
    public ResponseEntity<EstoqueResponseDTO> adicionarEstoque(
            @PathVariable Long produtoId, 
            @Valid @RequestBody AdicionarEstoqueDTO dto) {
        var estoqueAtualizado = estoqueService.adicionar(produtoId, dto.quantidade());
        return ResponseEntity.ok(new EstoqueResponseDTO(estoqueAtualizado));
    }

    // --- Endpoints de Relatórios e Alertas ---

    @GetMapping("/alertas/estoque-baixo")
    public ResponseEntity<List<EstoqueResponseDTO>> findEstoqueAbaixoDoMinimo() {
        List<EstoqueResponseDTO> dtos = estoqueService.findEstoqueAbaixoDoMinimo().stream()
                .map(EstoqueResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/relatorios/valor-total-inventario")
    public ResponseEntity<Double> getValorTotalInventario() {
        return ResponseEntity.ok(estoqueService.calcularValorTotalInventario());
    }

    @GetMapping("/relatorios/valor-por-categoria")
    public ResponseEntity<List<Object[]>> getValorTotalPorCategoria() {
        return ResponseEntity.ok(estoqueService.calcularValorTotalPorCategoria());
    }
}