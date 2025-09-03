package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.dto.MotorCreateDTO;
import com.proj_db.onibus.dto.MotorUpdateDTO;
import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.model.OrdemServico.StatusOrdemServico;
import com.proj_db.onibus.model.OrdemServico.TipoOrdemServico;
import com.proj_db.onibus.repository.EstoqueRepository;
import com.proj_db.onibus.repository.MotorRepository;
import com.proj_db.onibus.repository.OrdemServicoRepository;
import com.proj_db.onibus.repository.ProdutoRepository;

@Service
@Transactional
public class MotorImpl implements MotorService {

    @Autowired private MotorRepository motorRepository;
    @Autowired private OrdemServicoRepository osRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private EstoqueRepository estoqueRepository;

    @Override
    public Motor save(MotorCreateDTO motorDetails) {
        // 1. Validações para garantir que os códigos são únicos
        motorRepository.findByNumeroSerie(motorDetails.getNumeroSerie()).ifPresent(m -> {
            throw new IllegalArgumentException("Já existe um motor com este número de série: " + motorDetails.getNumeroSerie());
        });
        motorRepository.findByCodigoFabricacao(motorDetails.getCodigoFabricacao()).ifPresent(m -> {
            throw new IllegalArgumentException("Já existe um motor com este código de fabricação: " + motorDetails.getCodigoFabricacao());
        });

        // 2. Converte o DTO para a Entidade Motor
        Motor novoMotor = new Motor();
        novoMotor.setMarca(motorDetails.getMarca());
        novoMotor.setModelo(motorDetails.getModelo());
        novoMotor.setTipo(motorDetails.getTipo());
        novoMotor.setPotencia(motorDetails.getPotencia());
        novoMotor.setCilindrada(motorDetails.getCilindrada());
        novoMotor.setCodigoFabricacao(motorDetails.getCodigoFabricacao());
        novoMotor.setNumeroSerie(motorDetails.getNumeroSerie());
        novoMotor.setAnoFabricacao(motorDetails.getAnoFabricacao());
        novoMotor.setCapacidadeOleo(motorDetails.getCapacidadeOleo());
        novoMotor.setTipoOleo(motorDetails.getTipoOleo());
        novoMotor.setDataCompra(motorDetails.getDataCompra());
        novoMotor.setPeriodoGarantiaMeses(motorDetails.getPeriodoGarantiaMeses());
        
        // Status e quantidade de óleo já são definidos com valores padrão no modelo

        // 3. Salva a nova entidade no banco
        return motorRepository.save(novoMotor);
    }

    @Override
    public Motor update(Long id, MotorUpdateDTO motorDetails) {
        Motor motorExistente = findById(id).orElseThrow(() -> new RuntimeException("Motor não encontrado"));
        
        // Validações de unicidade
        if (!motorExistente.getNumeroSerie().equals(motorDetails.getNumeroSerie())) {
            motorRepository.findByNumeroSerie(motorDetails.getNumeroSerie()).ifPresent(outro -> {
                throw new IllegalArgumentException("O número de série '" + motorDetails.getNumeroSerie() + "' já está em uso.");
            });
        }
        
        // Atualiza campos
        motorExistente.setMarca(motorDetails.getMarca());
        motorExistente.setModelo(motorDetails.getModelo());
        motorExistente.setTipo(motorDetails.getTipo());
        motorExistente.setPotencia(motorDetails.getPotencia());
        motorExistente.setCilindrada(motorDetails.getCilindrada());
        motorExistente.setTipoOleo(motorDetails.getTipoOleo());
        motorExistente.setCapacidadeOleo(motorDetails.getCapacidadeOleo());
        motorExistente.setAnoFabricacao(motorDetails.getAnoFabricacao());
        motorExistente.setPeriodoGarantiaMeses(motorDetails.getPeriodoGarantiaMeses());
        
        return motorRepository.save(motorExistente);
    }

    @Override
    public void deleteById(Long id) {
        Motor motor = findById(id).orElseThrow(() -> new RuntimeException("Motor não encontrado"));
        if (motor.getStatus() == Motor.StatusMotor.EM_USO) {
            throw new IllegalStateException("Não é possível excluir um motor que está em uso.");
        }
        motorRepository.delete(motor);
    }

    @Override @Transactional(readOnly = true)
    public Optional<Motor> findById(Long id) { return motorRepository.findById(id); }

    @Override @Transactional(readOnly = true)
    public List<Motor> findAll() { return motorRepository.findAll(); }

    @Override @Transactional(readOnly = true)
    public Optional<Motor> findByNumeroSerie(String numeroSerie) { return motorRepository.findByNumeroSerie(numeroSerie); }

    @Override @Transactional(readOnly = true)
    public List<Motor> search(MotorSearchDTO criteria) {
        return motorRepository.findAll(MotorSpecification.searchByCriteria(criteria));
    }

    // --- Lógica de Negócio ---

    @Override 
    public Motor enviarParaManutencao(Long motorId) {
        Motor motor = findById(motorId).orElseThrow(() -> new RuntimeException("Motor não encontrado"));
        motor.enviarParaManutencao();
        return motorRepository.save(motor);
    }

    @Override 
    public Motor retornarDaManutencao(Long motorId) {
        Motor motor = findById(motorId).orElseThrow(() -> new RuntimeException("Motor não encontrado"));
        motor.retornarDaManutencao();
        return motorRepository.save(motor);
    }

    @Override 
    public Motor enviarParaRevisao(Long motorId) {
        Motor motor = findById(motorId).orElseThrow(() -> new RuntimeException("Motor não encontrado"));
        motor.enviarParaRevisao();
        return motorRepository.save(motor);
    }

    @Override
    public Motor retornarDaRevisao(Long motorId) {
        Motor motor = findById(motorId).orElseThrow(() -> new RuntimeException("Motor não encontrado"));
        motor.retornarDaRevisao();
        return motorRepository.save(motor);
    }

    public OrdemServico descartarMotorViaOS(Long motorId) {
        Motor motor = findById(motorId)
            .orElseThrow(() -> new RuntimeException("Motor não encontrado para descarte."));

        if (motor.getStatus() != Motor.StatusMotor.DISPONIVEL && motor.getStatus() != Motor.StatusMotor.NOVO) {
            throw new IllegalStateException("Apenas motores com status DISPONÍVEL ou NOVO podem ser descartados.");
        }

        // 1. Cria uma OS Corretiva específica para o descarte
        
        String prefix = "OS-CORR-DESC-";
        int prefixLength = prefix.length() + 1;
        Integer maxNum = osRepository.findMaxNumeroByPrefix(prefix + "%", prefixLength);
        String numeroOS = String.format("%s%06d", prefix, maxNum + 1);

        String desc = "Ordem de Serviço para esgotamento de fluido e preparação para descarte do Motor " + motor.getModelo();
        LocalDate hoje = LocalDate.now();
        
        OrdemServico os = new OrdemServico(numeroOS, OrdemServico.TipoOrdemServico.CORRETIVA, desc, hoje, hoje.plusDays(7));
        os.setMotor(motor); // Define o alvo

        // 2. Salva a OS no banco. O processo está formalmente iniciado.
        return osRepository.save(os);
        // O próximo passo seria o usuário "iniciar", "finalizar" esta OS,
        // o que chamaria outros métodos de serviço para esgotar o fluido e, finalmente, descartar o Motor.
    }

    // --- Geração Automática de OS Preventiva ---
    @Override
    public void verificarEGerarOsPreventivas() {
        List<Motor> todosOsMotoresAtivos = motorRepository.findAll();
        for (Motor motor : todosOsMotoresAtivos) {
            gerarOsPreventivaSeNecessario(motor);
        }
    }

    private void gerarOsPreventivaSeNecessario(Motor motor) {
        if (osRepository.existsByMotorAndStatusIn(motor, List.of(StatusOrdemServico.ABERTA, StatusOrdemServico.EM_EXECUCAO))) {
            return; // Já existe OS ativa
        }

        LocalDate ultimaTrocaOleo = motor.getDataUltimaTrocaOleo();
        boolean trocaRecente = ultimaTrocaOleo != null && ultimaTrocaOleo.isAfter(LocalDate.now().minusDays(45));

        if (motor.revisaoPrestesVencer()) {
            criarOsPreventiva(motor, "Revisão Preventiva (30 dias) para Motor " + motor.getModelo(), "REVISAO", !trocaRecente);
            return;
        }

        if (motor.manutencaoPrestesVencer()) {
            if (trocaRecente) return;
            criarOsPreventiva(motor, "Manutenção Preventiva (30 dias) para Motor " + motor.getModelo(), "MANUTENCAO", true);
        }
    }

    private void criarOsPreventiva(Motor motor, String desc, String tipoServico, boolean incluirItemOleo) {
        String prefix = "OS-PREV-";
        Integer maxNum = osRepository.findMaxNumeroByPrefix(prefix + "%", prefix.length() + 1);
        String numeroOS = String.format("%s%03d", prefix, maxNum + 1);

        LocalDate dataPrevisaoInicio = "REVISAO".equals(tipoServico) ? 
            LocalDate.now().plusDays(motor.getDiasRestantesRevisao()) : 
            LocalDate.now().plusDays(motor.getDiasRestantesManutencao());

        OrdemServico os = new OrdemServico(numeroOS, TipoOrdemServico.PREVENTIVA, desc, dataPrevisaoInicio, dataPrevisaoInicio.plusDays(7));
        os.setMotor(motor);
        if (motor.getOnibus() != null) os.setOnibus(motor.getOnibus());

        if (incluirItemOleo) {
            produtoRepository.findProdutoParaPreventiva(motor.getTipoOleo()).ifPresent(oleo -> {
                Double quantidade = "REVISAO".equals(tipoServico) ? motor.getCapacidadeOleo() : (motor.getCapacidadeOleo() - motor.getQuantidadeOleo());
                if (quantidade > 0) {
                    os.adicionarItem(oleo, quantidade, "Troca/complemento de óleo para serviço preventivo.");
                    estoqueRepository.findByProduto(oleo).ifPresent(estoque -> estoque.reservarEstoque(quantidade));
                }
            });
        }
        osRepository.save(os);
        System.out.println("LOG: OS Preventiva " + numeroOS + " criada para motor " + motor.getId());
    }

    // --- Métodos de Relatório ---

    @Override @Transactional(readOnly = true)
    public List<Object[]> countByTipo() { return motorRepository.countByTipo(); }

    @Override @Transactional(readOnly = true)
    public List<Object[]> countByStatus() { return motorRepository.countByStatus(); }

}