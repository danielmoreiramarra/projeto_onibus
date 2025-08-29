package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.Cambio;
import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.Onibus.StatusOnibus;
import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.repository.CambioRepository;
import com.proj_db.onibus.repository.MotorRepository;
import com.proj_db.onibus.repository.OnibusRepository;
import com.proj_db.onibus.repository.PneuRepository;

@Service
@Transactional
public class OnibusImpl implements OnibusService {

    @Autowired
    private OnibusRepository onibusRepository;
    @Autowired
    private MotorRepository motorRepository;
    @Autowired
    private CambioRepository cambioRepository;
    @Autowired
    private PneuRepository pneuRepository;

    @Override
    public Onibus criarOnibus(Onibus onibus) {
        if (existeChassi(onibus.getChassi())) {
            throw new RuntimeException("Já existe um ônibus com este chassi: " + onibus.getChassi());
        }
        
        if (existeCodigoFabricacao(onibus.getCodigoFabricacao())) {
            throw new RuntimeException("Já existe um ônibus com este código de fabricação: " + onibus.getCodigoFabricacao());
        }
        
        if (existeNumeroFrota(onibus.getNumeroFrota())) {
            throw new RuntimeException("Já existe um ônibus com este número de frota: " + onibus.getNumeroFrota());
        }
        
        if (onibus.getStatus() == null) {
            onibus.setStatus(StatusOnibus.NOVO);
        }
        
        if (onibus.getAnoFabricacao() > LocalDate.now().getYear()) {
            throw new RuntimeException("Ano de fabricação não pode ser futuro");
        }
        
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus atualizarOnibus(Long id, Onibus onibusAtualizado) {
        Onibus onibusExistente = onibusRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado com ID: " + id));
        
        if (!onibusExistente.getChassi().equals(onibusAtualizado.getChassi()) && 
            existeChassi(onibusAtualizado.getChassi())) {
            throw new RuntimeException("Já existe um ônibus com este chassi: " + onibusAtualizado.getChassi());
        }
        
        if (!onibusExistente.getCodigoFabricacao().equals(onibusAtualizado.getCodigoFabricacao()) && 
            existeCodigoFabricacao(onibusAtualizado.getCodigoFabricacao())) {
            throw new RuntimeException("Já existe um ônibus com este código de fabricação: " + onibusAtualizado.getCodigoFabricacao());
        }
        
        if (!onibusExistente.getNumeroFrota().equals(onibusAtualizado.getNumeroFrota()) && 
            existeNumeroFrota(onibusAtualizado.getNumeroFrota())) {
            throw new RuntimeException("Já existe um ônibus com este número de frota: " + onibusAtualizado.getNumeroFrota());
        }
        
        onibusExistente.setChassi(onibusAtualizado.getChassi());
        onibusExistente.setModelo(onibusAtualizado.getModelo());
        onibusExistente.setMarca(onibusAtualizado.getMarca());
        onibusExistente.setCodigoFabricacao(onibusAtualizado.getCodigoFabricacao());
        onibusExistente.setCapacidade(onibusAtualizado.getCapacidade());
        onibusExistente.setAnoFabricacao(onibusAtualizado.getAnoFabricacao());
        onibusExistente.setNumeroFrota(onibusAtualizado.getNumeroFrota());
        
        return onibusRepository.save(onibusExistente);
    }

    @Override
    public void excluirOnibus(Long id) {
        Onibus onibus = onibusRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado com ID: " + id));
        
        if (onibus.getStatus() == StatusOnibus.EM_OPERACAO || 
            onibus.getStatus() == StatusOnibus.EM_MANUTENCAO) {
            throw new RuntimeException("Não é possível excluir ônibus em operação ou manutenção");
        }
        
        onibusRepository.deleteById(id);
    }

    @Override
    public Optional<Onibus> buscarPorId(Long id) {
        return onibusRepository.findById(id);
    }

    @Override
    public List<Onibus> buscarTodos() {
        return onibusRepository.findAll();
    }

    // ✅ IMPLEMENTAÇÃO DO NOVO MÉTODO DE BUSCA COMBINADA
    @Override
    public List<Onibus> searchOnibus(Map<String, String> searchTerms) {
        String chassi = searchTerms.get("chassi");
        String numeroFrota = searchTerms.get("numeroFrota");
        String modelo = searchTerms.get("modelo");
        String marca = searchTerms.get("marca");
        StatusOnibus status = searchTerms.containsKey("status") && !searchTerms.get("status").isEmpty() ? StatusOnibus.valueOf(searchTerms.get("status")) : null;
        String codigoFabricacao = searchTerms.get("codigoFabricacao");
        Long motorId = searchTerms.containsKey("motorId") && !searchTerms.get("motorId").isEmpty() ? Long.parseLong(searchTerms.get("motorId")) : null;
        Long cambioId = searchTerms.containsKey("cambioId") && !searchTerms.get("cambioId").isEmpty() ? Long.parseLong(searchTerms.get("cambioId")) : null;
        Long pneuId = searchTerms.containsKey("pneuId") && !searchTerms.get("pneuId").isEmpty() ? Long.parseLong(searchTerms.get("pneuId")) : null;
        Integer capacidadeMinima = searchTerms.containsKey("capacidadeMinima") && !searchTerms.get("capacidadeMinima").isEmpty() ? Integer.parseInt(searchTerms.get("capacidadeMinima")) : null;

        return onibusRepository.searchOnibus(chassi, numeroFrota, modelo, marca, status, codigoFabricacao, motorId, cambioId, pneuId, capacidadeMinima);
    }

    @Override
    public Optional<Onibus> buscarPorChassi(String chassi) {
        return onibusRepository.findByChassi(chassi);
    }

    @Override
    public Optional<Onibus> buscarPorCodigoFabricacao(String codigoFabricacao) {
        return onibusRepository.findByCodigoFabricacao(codigoFabricacao);
    }

    @Override
    public Optional<Onibus> buscarPorNumeroFrota(String numeroFrota) {
        return onibusRepository.findByNumeroFrota(numeroFrota);
    }

    @Override
    public List<Onibus> buscarPorStatus(StatusOnibus status) {
        return onibusRepository.findByStatus(status);
    }

    @Override
    public List<Onibus> buscarDisponiveis() {
        return onibusRepository.findByStatus(StatusOnibus.DISPONIVEL);
    }

    @Override
    public List<Onibus> buscarNovos() {
        return onibusRepository.findByStatus(StatusOnibus.NOVO);
    }

    @Override
    public List<Onibus> buscarEmManutencao() {
        return onibusRepository.findByStatus(StatusOnibus.EM_MANUTENCAO);
    }

    @Override
    public List<Onibus> buscarPorMarca(String marca) {
        return onibusRepository.findByMarca(marca);
    }

    @Override
    public List<Onibus> buscarPorModelo(String modelo) {
        return onibusRepository.findByModelo(modelo);
    }

    @Override
    public List<Onibus> buscarPorAnoFabricacao(Integer anoFabricacao) {
        return onibusRepository.findByAnoFabricacao(anoFabricacao);
    }

    @Override
    public List<Onibus> buscarPorCapacidadeMinima(Integer capacidadeMinima) {
        return onibusRepository.findByCapacidadeGreaterThanEqual(capacidadeMinima);
    }

    @Override
    public Onibus colocarEmManutencao(Long id) {
        Onibus onibus = onibusRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado com ID: " + id));
        
        if (onibus.getStatus() != StatusOnibus.EM_OPERACAO) {
            throw new RuntimeException("Só é possível colocar em manutenção ônibus em operação");
        }
        
        onibus.setStatus(StatusOnibus.EM_MANUTENCAO);
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus retirarDeManutencao(Long id) {
        Onibus onibus = onibusRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado com ID: " + id));
        
        if (onibus.getStatus() != StatusOnibus.EM_MANUTENCAO) {
            throw new RuntimeException("Só é possível retirar da manutenção ônibus em manutenção");
        }
        
        onibus.setStatus(StatusOnibus.EM_OPERACAO);
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus aposentarOnibus(Long id) {
        Onibus onibus = onibusRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado com ID: " + id));
        
        if (onibus.getStatus() == StatusOnibus.EM_OPERACAO) {
            throw new RuntimeException("Não é possível aposentar ônibus em operação");
        }
        
        onibus.setStatus(StatusOnibus.APOSENTADO);
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus venderOnibus(Long id) {
        Onibus onibus = onibusRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado com ID: " + id));
        
        if (onibus.getStatus() != StatusOnibus.DISPONIVEL && 
            onibus.getStatus() != StatusOnibus.APOSENTADO) {
            throw new RuntimeException("Só é possível vender ônibus disponível ou aposentado");
        }
        
        onibus.setStatus(StatusOnibus.VENDIDO);
        return onibusRepository.save(onibus);
    }

    @Override
    public boolean verificarDisponibilidade(Long onibusId, LocalDate dataInicio, LocalDate dataFim) {
        Onibus onibus = onibusRepository.findById(onibusId)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado com ID: " + onibusId));

        return onibus.getStatus() == StatusOnibus.DISPONIVEL;
    }

    @Override
    public boolean existeChassi(String chassi) {
        return onibusRepository.existsByChassi(chassi);
    }

    @Override
    public boolean existeCodigoFabricacao(String codigoFabricacao) {
        return onibusRepository.existsByCodigoFabricacao(codigoFabricacao);
    }

    @Override
    public boolean existeNumeroFrota(String numeroFrota) {
        return onibusRepository.existsByNumeroFrota(numeroFrota);
    }

    @Override
    public List<Object[]> estatisticasPorStatus() {
        return onibusRepository.countOnibusByStatus();
    }

    @Override
    public List<Object[]> estatisticasPorMarca() {
        return onibusRepository.countOnibusByMarca();
    }

    @Override
    public List<Object[]> estatisticasPorAno() {
        return onibusRepository.countOnibusByAnoFabricacao();
    }

    @Override
    public Onibus instalarMotor(Long onibusId, Long motorId) {
        Onibus onibus = onibusRepository.findById(onibusId)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        Motor motor = motorRepository.findById(motorId)
            .orElseThrow(() -> new RuntimeException("Motor não encontrado"));
        
        if (onibus.getMotor() != null) {
            throw new RuntimeException("Ônibus já possui um motor instalado.");
        }
        if (motor.getOnibus() != null) {
            throw new RuntimeException("Motor já está instalado em outro ônibus.");
        }
        
        onibus.setMotor(motor);
        motor.setOnibus(onibus);
        motor.instalar(); 
        
        motorRepository.save(motor);
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus removerMotor(Long onibusId, Long motorId) {
        Onibus onibus = onibusRepository.findById(onibusId)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        Motor motor = motorRepository.findById(motorId)
            .orElseThrow(() -> new RuntimeException("Motor não encontrado"));
        
        if (onibus.getMotor() == null || !onibus.getMotor().getId().equals(motor.getId())) {
            throw new RuntimeException("Motor não está instalado neste ônibus.");
        }
        
        onibus.setMotor(null);
        motor.remover();
        
        motorRepository.save(motor);
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus instalarCambio(Long onibusId, Long cambioId) {
        Onibus onibus = onibusRepository.findById(onibusId)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        Cambio cambio = cambioRepository.findById(cambioId)
            .orElseThrow(() -> new RuntimeException("Câmbio não encontrado"));
        
        if (onibus.getCambio() != null) {
            throw new RuntimeException("Ônibus já possui um câmbio instalado.");
        }
        if (cambio.getOnibus() != null) {
            throw new RuntimeException("Câmbio já está instalado em outro ônibus.");
        }
        
        onibus.setCambio(cambio);
        cambio.setOnibus(onibus);
        cambio.instalar(); 
        
        cambioRepository.save(cambio);
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus removerCambio(Long onibusId, Long cambioId) {
        Onibus onibus = onibusRepository.findById(onibusId)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        Cambio cambio = cambioRepository.findById(cambioId)
            .orElseThrow(() -> new RuntimeException("Câmbio não encontrado"));
        
        if (onibus.getCambio() == null || !onibus.getCambio().getId().equals(cambio.getId())) {
            throw new RuntimeException("Câmbio não está instalado neste ônibus.");
        }
        
        onibus.setCambio(null);
        cambio.remover();
        
        cambioRepository.save(cambio);
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus instalarPneu(Long onibusId, Long pneuId, Pneu.PosicaoPneu posicao) {
        Onibus onibus = onibusRepository.findById(onibusId)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        Pneu pneu = pneuRepository.findById(pneuId)
            .orElseThrow(() -> new RuntimeException("Pneu não encontrado"));
        
        if (pneuRepository.findByOnibusAndPosicao(onibus, posicao).isPresent()) {
            throw new RuntimeException("Já existe um pneu nesta posição.");
        }
        if (pneu.getOnibus() != null) {
            throw new RuntimeException("Pneu já está instalado em um ônibus.");
        }
        
        pneu.setOnibus(onibus);
        pneu.setPosicao(posicao);
        pneu.instalar();
        
        pneuRepository.save(pneu);
        return onibusRepository.save(onibus);
    }
    
    @Override
    public Onibus removerPneu(Long onibusId, Long pneuId) {
        Onibus onibus = onibusRepository.findById(onibusId)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        Pneu pneu = pneuRepository.findById(pneuId)
            .orElseThrow(() -> new RuntimeException("Pneu não encontrado"));
        
        if (pneu.getOnibus() == null || !pneu.getOnibus().getId().equals(onibus.getId())) {
            throw new RuntimeException("Pneu não está instalado neste ônibus.");
        }
        
        pneu.setOnibus(null);
        pneu.setPosicao(null);
        pneu.remover();
        
        pneuRepository.save(pneu);
        return onibusRepository.save(onibus);
    }
}