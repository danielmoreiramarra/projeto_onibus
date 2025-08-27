package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.model.Pneu.StatusPneu;
import com.proj_db.onibus.repository.PneuRepository;

@Service
@Transactional
public class PneuImpl implements PneuService {

    @Autowired
    private PneuRepository pneuRepository;

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

    @Override
    public List<Pneu> buscarPorStatus(StatusPneu status) {
        return pneuRepository.findByStatus(status);
    }

    @Override
    public List<Pneu> buscarPorMarca(String marca) {
        return pneuRepository.findByMarca(marca);
    }

    @Override
    public List<Pneu> buscarPorMedida(String medida) {
        return pneuRepository.findByMedida(medida);
    }

    @Override
    public List<Pneu> buscarPorMarcaEMedida(String marca, String medida) {
        return pneuRepository.findByMarcaAndMedida(marca, medida);
    }

    @Override
    public List<Pneu> buscarDisponiveis() {
        return pneuRepository.findPneusDisponiveis();
    }

    @Override
    public List<Pneu> buscarEmUso() {
        return pneuRepository.findPneusEmUso();
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
        
        pneu.descartar(); // Usando a lógica do modelo
        return pneuRepository.save(pneu);
    }

    @Override
    public boolean registrarKmRodados(Long pneuId, Integer kmAdicionais) {
        Pneu pneu = buscarPneuPorId(pneuId);
        
        if (kmAdicionais <= 0) {
            throw new RuntimeException("Quilometragem deve ser positiva");
        }
        
        pneu.setKmRodados(pneu.getKmRodados() + kmAdicionais);
        pneuRepository.save(pneu);
        
        return true;
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

    private Pneu buscarPneuPorId(Long id) {
        return pneuRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pneu não encontrado com ID: " + id));
    }
}