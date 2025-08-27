package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.model.Motor.StatusMotor;
import com.proj_db.onibus.model.Motor.TipoMotor;
import com.proj_db.onibus.repository.MotorRepository;

@Service
@Transactional
public class MotorImpl implements MotorService {

    @Autowired
    private MotorRepository motorRepository;

    @Override
    public Motor criarMotor(Motor motor) {
        if (existeNumeroSerie(motor.getNumeroSerie())) {
            throw new RuntimeException("Já existe um motor com este número de série: " + motor.getNumeroSerie());
        }
        
        if (existeCodigoFabricacao(motor.getCodigoFabricacao())) {
            throw new RuntimeException("Já existe um motor com este código de fabricação: " + motor.getCodigoFabricacao());
        }
        
        if (motor.getStatus() == null) {
            motor.setStatus(StatusMotor.DISPONIVEL);
        }
        
        if (motor.getDataCompra() == null) {
            motor.setDataCompra(LocalDate.now());
        }
        
        return motorRepository.save(motor);
    }

    @Override
    public Motor atualizarMotor(Long id, Motor motorAtualizado) {
        Motor motorExistente = buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Motor não encontrado com ID: " + id));
        
        if (!motorExistente.getNumeroSerie().equals(motorAtualizado.getNumeroSerie()) && 
            existeNumeroSerie(motorAtualizado.getNumeroSerie())) {
            throw new RuntimeException("Já existe um motor com este número de série: " + motorAtualizado.getNumeroSerie());
        }
        
        if (!motorExistente.getCodigoFabricacao().equals(motorAtualizado.getCodigoFabricacao()) && 
            existeCodigoFabricacao(motorAtualizado.getCodigoFabricacao())) {
            throw new RuntimeException("Já existe um motor com este código de fabricação: " + motorAtualizado.getCodigoFabricacao());
        }
        
        // Atualizar campos permitidos
        motorExistente.setMarca(motorAtualizado.getMarca());
        motorExistente.setModelo(motorAtualizado.getModelo());
        motorExistente.setTipo(motorAtualizado.getTipo());
        motorExistente.setPotencia(motorAtualizado.getPotencia());
        motorExistente.setCilindrada(motorAtualizado.getCilindrada());
        motorExistente.setQuantidadeOleo(motorAtualizado.getQuantidadeOleo());
        
        return motorRepository.save(motorExistente);
    }

    @Override
    public void excluirMotor(Long id) {
        Motor motor = buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Motor não encontrado com ID: " + id));
        
        if (motor.getStatus() == StatusMotor.EM_USO) {
            throw new RuntimeException("Não é possível excluir motor em uso");
        }
        
        motorRepository.deleteById(id);
    }

    @Override
    public Optional<Motor> buscarPorId(Long id) {
        return motorRepository.findById(id);
    }

    @Override
    public List<Motor> buscarTodos() {
        return motorRepository.findAll();
    }

    @Override
    public Optional<Motor> buscarPorNumeroSerie(String numeroSerie) {
        return motorRepository.findByNumeroSerie(numeroSerie);
    }

    @Override
    public Optional<Motor> buscarPorCodigoFabricacao(String codigoFabricacao) {
        return motorRepository.findByCodigoFabricacao(codigoFabricacao);
    }

    @Override
    public List<Motor> buscarPorStatus(StatusMotor status) {
        return motorRepository.findByStatus(status);
    }

    @Override
    public List<Motor> buscarPorTipo(TipoMotor tipo) {
        return motorRepository.findByTipo(tipo);
    }

    @Override
    public List<Motor> buscarPorMarca(String marca) {
        return motorRepository.findByMarca(marca);
    }

    @Override
    public List<Motor> buscarDisponiveis() {
        return motorRepository.findMotoresDisponiveis();
    }

    @Override
    public List<Motor> buscarNovos() {
        return motorRepository.findMotoresNovos();
    }

    @Override
    public List<Motor> buscarEmUso() {
        return motorRepository.findMotoresEmUso();
    }

    @Override
    public Motor enviarParaManutencao(Long motorId) {
        Motor motor = buscarPorId(motorId)
            .orElseThrow(() -> new RuntimeException("Motor não encontrado com ID: " + motorId));
        
        if (motor.getStatus() != StatusMotor.EM_USO && motor.getStatus() != StatusMotor.DISPONIVEL) {
            throw new RuntimeException("Só é possível enviar para manutenção motores em uso ou disponíveis");
        }
        
        motor.setStatus(StatusMotor.EM_MANUTENCAO);
        return motorRepository.save(motor);
    }

    @Override
    public Motor retornarDeManutencao(Long motorId) {
        Motor motor = buscarPorId(motorId)
            .orElseThrow(() -> new RuntimeException("Motor não encontrado com ID: " + motorId));
        
        if (motor.getStatus() != StatusMotor.EM_MANUTENCAO) {
            throw new RuntimeException("Só é possível retornar da manutenção motores em manutenção");
        }
        
        motor.setStatus(StatusMotor.DISPONIVEL);
        return motorRepository.save(motor);
    }

    @Override
    public boolean registrarRevisao(Long motorId) {
        Motor motor = buscarPorId(motorId)
            .orElseThrow(() -> new RuntimeException("Motor não encontrado com ID: " + motorId));
        
        motor.revisar(); // Usando a lógica do modelo
        motorRepository.save(motor);
        
        return true;
    }

    @Override
    public boolean estaEmGarantia(Long motorId) {
        Motor motor = buscarPorId(motorId)
            .orElseThrow(() -> new RuntimeException("Motor não encontrado com ID: " + motorId));
        
        return motor.estaEmGarantia(); // Usando a lógica do modelo
    }

    @Override
    public boolean existeNumeroSerie(String numeroSerie) {
        return motorRepository.existsByNumeroSerie(numeroSerie);
    }

    @Override
    public boolean existeCodigoFabricacao(String codigoFabricacao) {
        return motorRepository.existsByCodigoFabricacao(codigoFabricacao);
    }

    @Override
    public List<Motor> buscarMotoresParaRevisao() {
        LocalDate seisMesesAtras = LocalDate.now().minusMonths(6);
        return motorRepository.findMotoresPrecisandoRevisao(seisMesesAtras);
    }

    @Override
    public List<Motor> buscarMotoresComGarantiaPrestesVencer() {
        LocalDate dataLimite = LocalDate.now().plusMonths(2).minusDays(30); // Lógica corrigida para os últimos 30 dias de garantia
        return motorRepository.findMotoresGarantiaPrestesVencer(dataLimite);
    }
}