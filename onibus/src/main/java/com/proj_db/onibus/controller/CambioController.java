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

import com.proj_db.onibus.dto.CambioCreateDTO;
import com.proj_db.onibus.dto.CambioResponseDTO;
import com.proj_db.onibus.dto.CambioUpdateDTO;
import com.proj_db.onibus.model.Cambio;
import com.proj_db.onibus.service.CambioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cambios")
@CrossOrigin(origins = "*") // Em produção, restrinja para o domínio do seu frontend
public class CambioController {

    @Autowired
    private CambioService cambioService;

    // --- Endpoints CRUD ---

    @PostMapping
    public ResponseEntity<CambioResponseDTO> create(@Valid @RequestBody CambioCreateDTO dto) {
        // <<< CORREÇÃO APLICADA AQUI
        // A responsabilidade de converter o DTO e criar a entidade agora é do Serviço.
        // O Controller apenas chama o serviço, passando o DTO diretamente.
        Cambio cambioSalvo = cambioService.save(dto);
        
        // A resposta continua convertendo a entidade salva para um ResponseDTO.
        return ResponseEntity.status(HttpStatus.CREATED).body(new CambioResponseDTO(cambioSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CambioResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CambioUpdateDTO dto) {
        // Este método já estava perfeito, passando o DTO para o serviço.
        Cambio cambioAtualizado = cambioService.update(id, dto);
        return ResponseEntity.ok(new CambioResponseDTO(cambioAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cambioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints de Consulta ---

    @GetMapping("/{id}")
    public ResponseEntity<CambioResponseDTO> findById(@PathVariable Long id) {
        return cambioService.findById(id)
                .map(CambioResponseDTO::new) // Forma mais limpa de chamar o construtor
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CambioResponseDTO>> findAll() {
        List<CambioResponseDTO> dtos = cambioService.findAll().stream()
                .map(CambioResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<CambioResponseDTO>> search(
            // Mapeia os parâmetros da URL para um DTO de busca
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String modelo,
            @RequestParam(required = false) String numeroSerie,
            @RequestParam(required = false) String tipoFluido,
            @RequestParam(required = false) Cambio.TipoCambio tipo,
            @RequestParam(required = false) Cambio.StatusCambio status
    ) {
        CambioService.CambioSearchDTO criteria = new CambioService.CambioSearchDTO(marca, modelo, numeroSerie, tipoFluido, tipo, status);
        List<CambioResponseDTO> dtos = cambioService.search(criteria).stream()
                .map(CambioResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // --- Endpoints de Ações de Negócio ---

    @PatchMapping("/{id}/enviar-manutencao")
    public ResponseEntity<CambioResponseDTO> enviarParaManutencao(@PathVariable Long id) {
        Cambio cambio = cambioService.enviarParaManutencao(id);
        return ResponseEntity.ok(new CambioResponseDTO(cambio));
    }
    
    @PatchMapping("/{id}/retornar-manutencao")
    public ResponseEntity<CambioResponseDTO> retornarDeManutencao(@PathVariable Long id) {
        Cambio cambio = cambioService.retornarDeManutencao(id);
        return ResponseEntity.ok(new CambioResponseDTO(cambio));
    }

    @PatchMapping("/{id}/enviar-revisao")
    public ResponseEntity<CambioResponseDTO> enviarParaRevisao(@PathVariable Long id) {
        Cambio cambio = cambioService.enviarParaRevisao(id);
        return ResponseEntity.ok(new CambioResponseDTO(cambio));
    }

    @PatchMapping("/{id}/retornar-revisao")
    public ResponseEntity<CambioResponseDTO> retornarDeRevisao(@PathVariable Long id) {
        Cambio cambio = cambioService.retornarDaRevisao(id);
        return ResponseEntity.ok(new CambioResponseDTO(cambio));
    }
}