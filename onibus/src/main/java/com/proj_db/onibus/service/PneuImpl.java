package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired; // Adicionado para a busca
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.model.Pneu.PosicaoPneu;
import com.proj_db.onibus.model.Pneu.StatusPneu;
import com.proj_db.onibus.repository.OnibusRepository;
import com.proj_db.onibus.repository.PneuRepository;

@Service
@Transactional
public class PneuImpl implements PneuService {

    @Autowired
    private PneuRepository pneuRepository;
    
    @Autowired
    private OnibusRepository onibusRepository; // ✅ Adicionado para a busca

    @Override
    public Pneu criarPneu(Pneu pneu) {
        if (existeNumeroSerie(pneu.getNumeroSerie())) {
            throw new RuntimeException("Já existe um pneu com este número de série: " + pneu.getNumeroSerie());
        }
        
        if (existeCodigoFabricacao(pneu.getCodigoFabricacao())) {
            throw new RuntimeException("Já existe um pneu com este código de fabricação: " + pneu.getCodigoFabricacao());
        }
        
        if (pneu.getStatus() == null) {
            pneu.setStatus(StatusPneu.NOVO);
        }
        
        if (pneu.getDataInstalacao() == null) {
            pneu.setDataInstalacao(LocalDate.now());
        }
        
        return pneuRepository.save(pneu);
    }

    @Override
    public Pneu atualizarPneu(Long id, Pneu pneuAtualizado) {
        Pneu pneuExistente = buscarPneuPorId(id);
        
        if (!pneuExistente.getNumeroSerie().equals(pneuAtualizado.getNumeroSerie()) && 
            existeNumeroSerie(pneuAtualizado.getNumeroSerie())) {
            throw new RuntimeException("Já existe um pneu com este número de série: " + pneuAtualizado.getNumeroSerie());
        }
        
        if (!pneuExistente.getCodigoFabricacao().equals(pneuAtualizado.getCodigoFabricacao()) && 
            existeCodigoFabricacao(pneuAtualizado.getCodigoFabricacao())) {
            throw new RuntimeException("Já existe um pneu com este código de fabricação: " + pneuAtualizado.getCodigoFabricacao());
        }
        
        // Atualizar campos permitidos
        pneuExistente.setMarca(pneuAtualizado.getMarca());
        pneuExistente.setMedida(pneuAtualizado.getMedida());
        pneuExistente.setKmRodados(pneuAtualizado.getKmRodados());
        pneuExistente.setPosicao(pneuAtualizado.getPosicao());
        
        return pneuRepository.save(pneuExistente);
    }

    @Override
    public void excluirPneu(Long id) {
        Pneu pneu = buscarPneuPorId(id);
        
        if (pneu.getStatus() == StatusPneu.EM_USO) {
            throw new RuntimeException("Não é possível excluir pneu em uso");
        }
        
        pneuRepository.deleteById(id);
    }

    @Override
    public Optional<Pneu> buscarPorId(Long id) {
        return pneuRepository.findById(id);
    }

    @Override
    public List<Pneu> buscarTodos() {
        return pneuRepository.findAll();
    }

    @Override
    public Optional<Pneu> buscarPorNumeroSerie(String numeroSerie) {
        return pneuRepository.findByNumeroSerie(numeroSerie);
    }

    @Override
    public Optional<Pneu> buscarPorCodigoFabricacao(String codigoFabricacao) {
        return pneuRepository.findByCodigoFabricacao(codigoFabricacao);
    }

    // ✅ IMPLEMENTAÇÃO DO NOVO MÉTODO DE BUSCA COMBINADA
    @Override
    public List<Pneu> searchPneu(Map<String, String> searchTerms) {
        Long id = searchTerms.containsKey("id") && !searchTerms.get("id").isEmpty() ? Long.parseLong(searchTerms.get("id")) : null;
        PosicaoPneu posicao = searchTerms.containsKey("posicao") && !searchTerms.get("posicao").isEmpty() ? PosicaoPneu.valueOf(searchTerms.get("posicao")) : null;
        StatusPneu status = searchTerms.containsKey("status") && !searchTerms.get("status").isEmpty() ? StatusPneu.valueOf(searchTerms.get("status")) : null;
        Long onibusId = searchTerms.containsKey("onibusId") && !searchTerms.get("onibusId").isEmpty() ? Long.parseLong(searchTerms.get("onibusId")) : null;
        String medida = searchTerms.get("medida");
        String marca = searchTerms.get("marca");
        String numeroSerie = searchTerms.get("numeroSerie");
        String codigoFabricacao = searchTerms.get("codigoFabricacao");
        Integer kmRodadosMin = searchTerms.containsKey("kmRodadosMin") && !searchTerms.get("kmRodadosMin").isEmpty() ? Integer.valueOf(searchTerms.get("kmRodadosMin")) : null;
        Integer kmRodadosMax = searchTerms.containsKey("kmRodadosMax") && !searchTerms.get("kmRodadosMax").isEmpty() ? Integer.valueOf(searchTerms.get("kmRodadosMax")) : null;
        LocalDate dataInstalacaoMin = searchTerms.containsKey("dataInstalacaoMin") && !searchTerms.get("dataInstalacaoMin").isEmpty() ? LocalDate.parse(searchTerms.get("dataInstalacaoMin")) : null;
        LocalDate dataInstalacaoMax = searchTerms.containsKey("dataInstalacaoMax") && !searchTerms.get("dataInstalacaoMax").isEmpty() ? LocalDate.parse(searchTerms.get("dataInstalacaoMax")) : null;
        LocalDate dataCompraMin = searchTerms.containsKey("dataCompraMin") && !searchTerms.get("dataCompraMin").isEmpty() ? LocalDate.parse(searchTerms.get("dataCompraMin")) : null;
        LocalDate dataCompraMax = searchTerms.containsKey("dataCompraMax") && !searchTerms.get("dataCompraMax").isEmpty() ? LocalDate.parse(searchTerms.get("dataCompraMax")) : null;
        LocalDate dataUltimaReformaMin = searchTerms.containsKey("dataUltimaReformaMin") && !searchTerms.get("dataUltimaReformaMin").isEmpty() ? LocalDate.parse(searchTerms.get("dataUltimaReformaMin")) : null;
        LocalDate dataUltimaReformaMax = searchTerms.containsKey("dataUltimaReformaMax") && !searchTerms.get("dataUltimaReformaMax").isEmpty() ? LocalDate.parse(searchTerms.get("dataUltimaReformaMax")) : null;

        return pneuRepository.searchPneu(id, posicao, status, onibusId, medida, marca, numeroSerie, codigoFabricacao, kmRodadosMin, kmRodadosMax, dataInstalacaoMin, dataInstalacaoMax, dataCompraMin, dataCompraMax, dataUltimaReformaMin, dataUltimaReformaMax);
    }
    
    @Override
    public Pneu enviarParaManutencao(Long pneuId) {
        Pneu pneu = buscarPneuPorId(pneuId);
        
        if (pneu.getStatus() != StatusPneu.EM_USO && pneu.getStatus() != StatusPneu.DISPONIVEL) {
            throw new RuntimeException("Só é possível enviar para manutenção pneus em uso ou disponíveis");
        }
        
        pneu.setStatus(StatusPneu.EM_MANUTENCAO);
        return pneuRepository.save(pneu);
    }

    @Override
    public Pneu retornarDeManutencao(Long pneuId) {
        Pneu pneu = buscarPneuPorId(pneuId);
        
        if (pneu.getStatus() != StatusPneu.EM_MANUTENCAO) {
            throw new RuntimeException("Só é possível retornar da manutenção pneus em manutenção");
        }
        
        pneu.setStatus(StatusPneu.DISPONIVEL);
        return pneuRepository.save(pneu);
    }

    @Override
    public Pneu descartarPneu(Long pneuId) {
        Pneu pneu = buscarPneuPorId(pneuId);
        
        pneu.descartar();
        return pneuRepository.save(pneu);
    }

    // ✅ Tipo de retorno alterado para Pneu
    @Override
    public Pneu registrarKmRodados(Long pneuId, Integer kmAdicionais) {
        Pneu pneu = buscarPneuPorId(pneuId);
        
        if (kmAdicionais <= 0) {
            throw new RuntimeException("Quilometragem deve ser positiva");
        }
        
        pneu.setKmRodados(pneu.getKmRodados() + kmAdicionais);
        return pneuRepository.save(pneu);
    }

    @Override
    public boolean precisaTroca(Long pneuId) {
        Pneu pneu = buscarPneuPorId(pneuId);
        return pneu.getKmRodados() >= 80000;
    }

    @Override
    public boolean existeNumeroSerie(String numeroSerie) {
        return pneuRepository.existsByNumeroSerie(numeroSerie);
    }

    @Override
    public boolean existeCodigoFabricacao(String codigoFabricacao) {
        return pneuRepository.existsByCodigoFabricacao(codigoFabricacao);
    }

    @Override
    public List<Pneu> buscarPneusParaTroca() {
        return pneuRepository.findByKmRodadosGreaterThanEqual(80000);
    }

    @Override
    public List<Pneu> buscarPneusComGarantiaPrestesVencer() {
        LocalDate trintaDiasNoFuturo = LocalDate.now().plusDays(30);
        return pneuRepository.findPneusGarantiaPrestesVencer(trintaDiasNoFuturo);
    }

    @Override
    public List<Object[]> estatisticasPorStatus() {
        return pneuRepository.countPneusPorStatus();
    }

    @Override
    public List<Object[]> estatisticasPorMarca() {
        return pneuRepository.avgKmPorMarca();
    }
    
    // ✅ IMPLEMENTAÇÃO DOS NOVOS MÉTODOS DE RELATÓRIO
    @Override
    public List<Object[]> avgKmPorPosicaoNoPeriodo(LocalDate startDate, LocalDate endDate) {
        return pneuRepository.avgKmPorPosicaoNoPeriodo(startDate, endDate);
    }
    
    @Override
    public List<Object[]> avgKmPorMarcaNoPeriodo(LocalDate startDate, LocalDate endDate) {
        return pneuRepository.avgKmPorMarcaNoPeriodo(startDate, endDate);
    }

    private Pneu buscarPneuPorId(Long id) {
        return pneuRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pneu não encontrado com ID: " + id));
    }
}