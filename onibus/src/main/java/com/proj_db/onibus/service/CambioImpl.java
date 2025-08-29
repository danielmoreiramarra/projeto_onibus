package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.Cambio;
import com.proj_db.onibus.model.Cambio.StatusCambio;
import com.proj_db.onibus.model.Cambio.TipoCambio;
import com.proj_db.onibus.repository.CambioRepository;

@Service
@Transactional
public class CambioImpl implements CambioService {

    @Autowired
    private CambioRepository cambioRepository;

    @Override
    public Cambio criarCambio(Cambio cambio) {
        if (existeNumeroSerie(cambio.getNumeroSerie())) {
            throw new RuntimeException("Já existe um câmbio com este número de série: " + cambio.getNumeroSerie());
        }
        
        if (existeCodigoFabricacao(cambio.getCodigoFabricacao())) {
            throw new RuntimeException("Já existe um câmbio com este código de fabricação: " + cambio.getCodigoFabricacao());
        }
        
        if (cambio.getStatus() == null) {
            cambio.setStatus(StatusCambio.DISPONIVEL);
        }
        
        if (cambio.getDataCompra() == null) {
            cambio.setDataCompra(LocalDate.now());
        }
        
        return cambioRepository.save(cambio);
    }

    @Override
    public Cambio atualizarCambio(Long id, Cambio cambioAtualizado) {
        Optional<Cambio> optionalCambio = buscarPorId(id);
        if (optionalCambio.isEmpty()) {
            throw new RuntimeException("Câmbio não encontrado com ID: " + id);
        }
        
        Cambio cambioExistente = optionalCambio.get();
        
        if (!cambioExistente.getNumeroSerie().equals(cambioAtualizado.getNumeroSerie()) && 
            existeNumeroSerie(cambioAtualizado.getNumeroSerie())) {
            throw new RuntimeException("Já existe um câmbio com este número de série: " + cambioAtualizado.getNumeroSerie());
        }
        
        if (!cambioExistente.getCodigoFabricacao().equals(cambioAtualizado.getCodigoFabricacao()) && 
            existeCodigoFabricacao(cambioAtualizado.getCodigoFabricacao())) {
            throw new RuntimeException("Já existe um câmbio com este código de fabricação: " + cambioAtualizado.getCodigoFabricacao());
        }
        
        cambioExistente.setMarca(cambioAtualizado.getMarca());
        cambioExistente.setModelo(cambioAtualizado.getModelo());
        cambioExistente.setTipo(cambioAtualizado.getTipo());
        cambioExistente.setNumeroMarchas(cambioAtualizado.getNumeroMarchas());
        cambioExistente.setTipoFluido(cambioAtualizado.getTipoFluido());
        cambioExistente.setQuantidadeFluido(cambioAtualizado.getQuantidadeFluido());
        cambioExistente.setAnoFabricacao(cambioAtualizado.getAnoFabricacao());
        
        return cambioRepository.save(cambioExistente);
    }

    @Override
    public void excluirCambio(Long id) {
        Cambio cambio = buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Câmbio não encontrado com ID: " + id));
        
        if (cambio.getStatus() == StatusCambio.EM_USO) {
            throw new RuntimeException("Não é possível excluir câmbio em uso");
        }
        
        cambioRepository.deleteById(id);
    }

    @Override
    public Optional<Cambio> buscarPorId(Long id) {
        return cambioRepository.findById(id);
    }

    @Override
    public List<Cambio> buscarTodos() {
        return cambioRepository.findAll();
    }

    @Override
    public Optional<Cambio> buscarPorNumeroSerie(String numeroSerie) {
        return cambioRepository.findByNumeroSerie(numeroSerie);
    }

    @Override
    public Optional<Cambio> buscarPorCodigoFabricacao(String codigoFabricacao) {
        return cambioRepository.findByCodigoFabricacao(codigoFabricacao);
    }
    
    // ✅ IMPLEMENTAÇÃO DO NOVO MÉTODO DE BUSCA COMBINADA
    @Override
    public List<Cambio> searchCambio(Map<String, String> searchTerms) {
        Long id = searchTerms.containsKey("id") && !searchTerms.get("id").isEmpty() ? Long.valueOf(searchTerms.get("id")) : null;
        TipoCambio tipo = searchTerms.containsKey("tipo") && !searchTerms.get("tipo").isEmpty() ? TipoCambio.valueOf(searchTerms.get("tipo")) : null;
        StatusCambio status = searchTerms.containsKey("status") && !searchTerms.get("status").isEmpty() ? StatusCambio.valueOf(searchTerms.get("status")) : null;
        String marca = searchTerms.get("marca");
        String modelo = searchTerms.get("modelo");
        String numeroSerie = searchTerms.get("numeroSerie");
        String codigoFabricacao = searchTerms.get("codigoFabricacao");
        Integer numeroMarchas = searchTerms.containsKey("numeroMarchas") && !searchTerms.get("numeroMarchas").isEmpty() ? Integer.valueOf(searchTerms.get("numeroMarchas")) : null;
        Long onibusId = searchTerms.containsKey("onibusId") && !searchTerms.get("onibusId").isEmpty() ? Long.valueOf(searchTerms.get("onibusId")) : null;
        String tipoFluido = searchTerms.get("tipoFluido");

        return cambioRepository.searchCambio(id, tipo, status, marca, modelo, numeroSerie, codigoFabricacao, numeroMarchas, onibusId, tipoFluido);
    }
    
    @Override
    public Cambio enviarParaManutencao(Long cambioId) {
        Optional<Cambio> optionalCambio = buscarPorId(cambioId);
        if (optionalCambio.isEmpty()) {
            throw new RuntimeException("Câmbio não encontrado com ID: " + cambioId);
        }
        Cambio cambio = optionalCambio.get();
        
        if (cambio.getStatus() != StatusCambio.EM_USO && cambio.getStatus() != StatusCambio.DISPONIVEL) {
            throw new RuntimeException("Só é possível enviar para manutenção câmbios em uso ou disponíveis");
        }
        
        cambio.setStatus(StatusCambio.EM_MANUTENCAO);
        return cambioRepository.save(cambio);
    }

    @Override
    public Cambio retornarDeManutencao(Long cambioId) {
        Optional<Cambio> optionalCambio = buscarPorId(cambioId);
        if (optionalCambio.isEmpty()) {
            throw new RuntimeException("Câmbio não encontrado com ID: " + cambioId);
        }
        Cambio cambio = optionalCambio.get();
        
        if (cambio.getStatus() != StatusCambio.EM_MANUTENCAO) {
            throw new RuntimeException("Só é possível retornar da manutenção câmbios em manutenção");
        }
        
        cambio.setStatus(StatusCambio.DISPONIVEL);
        return cambioRepository.save(cambio);
    }

    @Override
    public Cambio trocarFluido(Long cambioId, String novoTipoFluido, Double novaQuantidade) {
        Optional<Cambio> optionalCambio = buscarPorId(cambioId);
        if (optionalCambio.isEmpty()) {
            throw new RuntimeException("Câmbio não encontrado com ID: " + cambioId);
        }
        Cambio cambio = optionalCambio.get();
        
        if (novaQuantidade <= 0) {
            throw new RuntimeException("Quantidade de fluido deve ser positiva");
        }
        
        cambio.setTipoFluido(novoTipoFluido);
        cambio.setQuantidadeFluido(novaQuantidade);
        cambio.setDataUltimaManutencao(LocalDate.now());
        return cambioRepository.save(cambio);
    }

    @Override
    public Cambio registrarRevisao(Long cambioId) {
        Optional<Cambio> optionalCambio = buscarPorId(cambioId);
        if (optionalCambio.isEmpty()) {
            throw new RuntimeException("Câmbio não encontrado com ID: " + cambioId);
        }
        Cambio cambio = optionalCambio.get();
        
        cambio.revisar();
        return cambioRepository.save(cambio);
    }

    @Override
    public boolean estaEmGarantia(Long cambioId) {
        Optional<Cambio> optionalCambio = buscarPorId(cambioId);
        if (optionalCambio.isEmpty()) {
            throw new RuntimeException("Câmbio não encontrado com ID: " + cambioId);
        }
        Cambio cambio = optionalCambio.get();
        
        return cambio.estaEmGarantia();
    }

    @Override
    public boolean existeNumeroSerie(String numeroSerie) {
        return cambioRepository.existsByNumeroSerie(numeroSerie);
    }

    @Override
    public boolean existeCodigoFabricacao(String codigoFabricacao) {
        return cambioRepository.existsByCodigoFabricacao(codigoFabricacao);
    }

    @Override
    public List<Cambio> buscarCambiosParaRevisao() {
        LocalDate seisMesesAtras = LocalDate.now().minusMonths(6);
        return cambioRepository.findCambiosPrecisandoRevisao(seisMesesAtras);
    }

    @Override
    public List<Cambio> buscarCambiosComGarantiaPrestesVencer() {
        LocalDate dataLimite = LocalDate.now().plusMonths(2).minusDays(30);
        return cambioRepository.findCambiosGarantiaPrestesVencer(dataLimite);
    }

    @Override
    public List<Object[]> countCambiosPorTipo() {
        return cambioRepository.countCambiosPorTipo();
    }

    @Override
    public List<Object[]> countCambiosPorMarchas() {
        return cambioRepository.countCambiosPorMarchas();
    }

    @Override
    public List<Object[]> countCambiosPorStatus() {
        return cambioRepository.countCambiosPorStatus();
    }
}