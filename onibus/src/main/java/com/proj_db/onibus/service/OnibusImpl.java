package com.proj_db.onibus.service;

import java.time.LocalDate; // Importa todos os modelos
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired; // Importa todos os repositórios
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.dto.OnibusCreateDTO;
import com.proj_db.onibus.dto.OnibusUpdateDTO;
import com.proj_db.onibus.model.Cambio;
import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.model.OrdemServico.StatusOrdemServico;
import com.proj_db.onibus.model.OrdemServico.TipoOrdemServico;
import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.repository.CambioRepository;
import com.proj_db.onibus.repository.MotorRepository;
import com.proj_db.onibus.repository.OnibusRepository;
import com.proj_db.onibus.repository.OrdemServicoRepository;
import com.proj_db.onibus.repository.PneuRepository;

@Service
@Transactional
public class OnibusImpl implements OnibusService {

    @Autowired private OnibusRepository onibusRepository;
    @Autowired private MotorRepository motorRepository;
    @Autowired private CambioRepository cambioRepository;
    @Autowired private PneuRepository pneuRepository;
    @Autowired private OrdemServicoRepository osRepository;

    // --- CRUD Básico ---

    @Override
    public Onibus save(OnibusCreateDTO onibusDetails) {
        // 1. Validações para garantir que os identificadores são únicos
        onibusRepository.findByChassi(onibusDetails.getChassi()).ifPresent(o -> {
            throw new IllegalArgumentException("Já existe um ônibus com este chassi: " + onibusDetails.getChassi());
        });
        onibusRepository.findByPlaca(onibusDetails.getPlaca()).ifPresent(o -> {
            throw new IllegalArgumentException("Já existe um ônibus com esta placa: " + onibusDetails.getPlaca());
        });
        onibusRepository.findByNumeroFrota(onibusDetails.getNumeroFrota()).ifPresent(o -> {
            throw new IllegalArgumentException("Já existe um ônibus com este número de frota: " + onibusDetails.getNumeroFrota());
        });

        // 2. Converte o DTO para a Entidade Onibus
        Onibus novoOnibus = new Onibus();
        novoOnibus.setChassi(onibusDetails.getChassi());
        novoOnibus.setPlaca(onibusDetails.getPlaca());
        novoOnibus.setModelo(onibusDetails.getModelo());
        novoOnibus.setMarca(onibusDetails.getMarca());
        novoOnibus.setCodigoFabricacao(onibusDetails.getCodigoFabricacao());
        novoOnibus.setCapacidade(onibusDetails.getCapacidade());
        novoOnibus.setAnoFabricacao(onibusDetails.getAnoFabricacao());
        novoOnibus.setNumeroFrota(onibusDetails.getNumeroFrota());
        novoOnibus.setDataCompra(onibusDetails.getDataCompra());

        // Status e quilometragem já são definidos com valores padrão no modelo

        // 3. Salva a nova entidade no banco
        return onibusRepository.save(novoOnibus);
    }

    @Override
    public Onibus update(Long id, OnibusUpdateDTO onibusDetails) {
        Onibus onibus = findById(id).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        
        onibusRepository.findByPlaca(onibusDetails.getPlaca()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) throw new IllegalArgumentException("Placa já cadastrada em outro ônibus.");
        });
        
        onibus.setMarca(onibusDetails.getMarca());
        onibus.setModelo(onibusDetails.getModelo());
        onibus.setPlaca(onibusDetails.getPlaca());
        onibus.setCapacidade(onibusDetails.getCapacidade());
        onibus.setAnoFabricacao(onibusDetails.getAnoFabricacao());
        onibus.setNumeroFrota(onibusDetails.getNumeroFrota());
        
        return onibusRepository.save(onibus);
    }

    @Override
    public void deleteById(Long id) {
        Onibus onibus = findById(id).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        if (onibus.getStatus() == Onibus.StatusOnibus.EM_OPERACAO) {
            throw new IllegalStateException("Não é possível excluir um ônibus que está em operação.");
        }
        onibusRepository.delete(onibus);
    }

    // --- Buscas ---

    @Override
    @Transactional(readOnly = true)
    public Optional<Onibus> findById(Long id) {
        return onibusRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Onibus> findAll() {
        return onibusRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Onibus> findByChassi(String chassi) {
        return onibusRepository.findByChassi(chassi);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Onibus> search(OnibusSearchDTO criteria) {
        return onibusRepository.findAll(OnibusSpecification.searchByCriteria(criteria));
    }

    // --- Lógica de Negócio (Ciclo de Vida) ---

    @Override
    public Onibus colocarEmOperacao(Long onibusId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.colocarEmOperacao();
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus retirarDeOperacao(Long onibusId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.retirarDeOperacao();
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus enviarParaManutencao(Long onibusId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.enviarParaManutencao();
        return onibusRepository.save(onibus);
    }
    
    @Override
    public Onibus retornarDaManutencao(Long onibusId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.retornarDaManutencao();
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus enviarParaReforma(Long onibusId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.enviarParaReforma();
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus retornarDaReforma(Long onibusId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.retornarDaReforma();
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus aposentar(Long onibusId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.aposentar();
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus vender(Long onibusId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.vender();
        return onibusRepository.save(onibus);
    }

    // --- Lógica de Operação ---

    @Override
    public Onibus registrarViagem(Long onibusId, Double kmPercorridos) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.registrarViagem(kmPercorridos);
        return onibusRepository.save(onibus);
    }

    // --- Gerenciamento de Componentes ---

    @Override
    public Onibus instalarMotor(Long onibusId, Long motorId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        Motor motor = motorRepository.findById(motorId).orElseThrow(() -> new RuntimeException("Motor não encontrado"));
        onibus.instalarMotor(motor);
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus removerMotor(Long onibusId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.removerMotor();
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus instalarCambio(Long onibusId, Long cambioId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        Cambio cambio = cambioRepository.findById(cambioId).orElseThrow(() -> new RuntimeException("Câmbio não encontrado"));
        onibus.instalarCambio(cambio);
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus removerCambio(Long onibusId) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.removerCambio();
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus instalarPneu(Long onibusId, Long pneuId, Pneu.PosicaoPneu posicao) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        Pneu pneu = pneuRepository.findById(pneuId).orElseThrow(() -> new RuntimeException("Pneu não encontrado"));
        onibus.instalarPneu(pneu, posicao);
        return onibusRepository.save(onibus);
    }

    @Override
    public Onibus removerPneu(Long onibusId, Pneu.PosicaoPneu posicao) {
        Onibus onibus = findById(onibusId).orElseThrow(() -> new RuntimeException("Ônibus não encontrado"));
        onibus.removerPneu(posicao);
        return onibusRepository.save(onibus);
    }

    public OrdemServico aposentarOnibusViaOS(Long onibusId) {
        Onibus onibus = findById(onibusId)
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado para aposentadoria."));

        // Valida se o ônibus pode ser aposentado
        if (onibus.getStatus() == Onibus.StatusOnibus.EM_OPERACAO) {
            throw new IllegalStateException("O ônibus precisa ser retirado de operação antes de iniciar o processo de aposentadoria.");
        }
        if (onibus.getStatus() == Onibus.StatusOnibus.APOSENTADO || onibus.getStatus() == Onibus.StatusOnibus.VENDIDO) {
            throw new IllegalStateException("Este ônibus já foi aposentado ou vendido.");
        }

        // 1. Gera um número único para a OS de aposentadoria
        String prefix = "OS-APOSENT-";
        int prefixLength = prefix.length() + 1;
        Integer maxNum = osRepository.findMaxNumeroByPrefix(prefix + "%", prefixLength);
        String numeroOS = String.format("%s%06d", prefix, maxNum + 1);

        String desc = "Ordem de Serviço para descomissionamento completo e preparação para aposentadoria do Ônibus " + onibus.getPlaca();
        LocalDate hoje = LocalDate.now();
        
        // 2. Cria a OS Corretiva com um prazo maior, pois o trabalho é mais complexo
        OrdemServico os = new OrdemServico(numeroOS, OrdemServico.TipoOrdemServico.CORRETIVA, desc, hoje, hoje.plusDays(14));
        os.setOnibus(onibus); // Define o ônibus como o alvo da OS

        // 3. Salva a OS, formalizando o início do processo
        return osRepository.save(os);
    }

    // --- Lógica de OS Preventiva ---
    
    @Override
    public void verificarEGerarOsPreventivas() {
        List<Onibus> onibusAtivos = onibusRepository.findAll(); 
        for (Onibus onibus : onibusAtivos) {
            // Verifica se o próprio ônibus precisa de reforma
            if (onibus.reformaPrestesVencer()) {
                if (!osRepository.existsByOnibusAndStatusIn(onibus, List.of(StatusOrdemServico.ABERTA, StatusOrdemServico.EM_EXECUCAO))) {
                    String prefix = "OS-PREV-";
                    Integer maxNum = osRepository.findMaxNumeroByPrefix(prefix + "%", prefix.length() + 1);
                    String numeroOS = String.format("%s%03d", prefix, maxNum + 1);
                    
                    String desc = "Reforma Preventiva (30 dias) para Ônibus " + onibus.getPlaca();
                    LocalDate dataPrevisaoInicio = LocalDate.now().plusDays(onibus.getDiasRestantesReforma());
                    
                    OrdemServico os = new OrdemServico(numeroOS, TipoOrdemServico.PREVENTIVA, desc, dataPrevisaoInicio, dataPrevisaoInicio.plusDays(30));
                    os.setOnibus(onibus);
                    osRepository.save(os);
                    System.out.println("LOG: OS Preventiva " + numeroOS + " criada para o ônibus " + onibus.getId());
                }
            }
        }
    }

    // --- Métodos de Relatório ---

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> countByStatus() {
        return onibusRepository.countByStatus();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> countByMarca() {
        return onibusRepository.countByMarca();
    }
}