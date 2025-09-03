package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.dto.PneuCreateDTO;
import com.proj_db.onibus.dto.PneuUpdateDTO;
import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.model.OrdemServico.StatusOrdemServico;
import com.proj_db.onibus.model.OrdemServico.TipoOrdemServico;
import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.repository.OrdemServicoRepository;
import com.proj_db.onibus.repository.PneuRepository;

@Service
@Transactional
public class PneuImpl implements PneuService {

    @Autowired private PneuRepository pneuRepository;
    @Autowired private OrdemServicoRepository osRepository;

    @Override
    public Pneu save(PneuCreateDTO pneuDetails) {
        // 1. Validações para garantir que os códigos são únicos
        pneuRepository.findByNumeroSerie(pneuDetails.getNumeroSerie()).ifPresent(p -> {
            throw new IllegalArgumentException("Já existe um pneu com este número de série: " + pneuDetails.getNumeroSerie());
        });
        pneuRepository.findByCodigoFabricacao(pneuDetails.getCodigoFabricacao()).ifPresent(p -> {
            throw new IllegalArgumentException("Já existe um pneu com este código de fabricação: " + pneuDetails.getCodigoFabricacao());
        });

        // 2. Converte o DTO para a Entidade Pneu
        Pneu novoPneu = new Pneu();
        novoPneu.setMarca(pneuDetails.getMarca());
        novoPneu.setModelo(pneuDetails.getModelo());
        novoPneu.setMedida(pneuDetails.getMedida());
        novoPneu.setCodigoFabricacao(pneuDetails.getCodigoFabricacao());
        novoPneu.setNumeroSerie(pneuDetails.getNumeroSerie());
        novoPneu.setAnoFabricacao(pneuDetails.getAnoFabricacao());
        novoPneu.setDataCompra(pneuDetails.getDataCompra());
        novoPneu.setPeriodoGarantiaMeses(pneuDetails.getPeriodoGarantiaMeses());

        // Status e kmRodados já são definidos com valores padrão no modelo

        // 3. Salva a nova entidade no banco
        return pneuRepository.save(novoPneu);
    }

    @Override
    public Pneu update(Long id, PneuUpdateDTO pneuDetails) {
        Pneu pneuExistente = findById(id).orElseThrow(() -> new RuntimeException("Pneu não encontrado"));
        
        if (!pneuExistente.getNumeroSerie().equals(pneuDetails.getNumeroSerie())) {
            pneuRepository.findByNumeroSerie(pneuDetails.getNumeroSerie()).ifPresent(outro -> {
                throw new IllegalArgumentException("O número de série '" + pneuDetails.getNumeroSerie() + "' já está em uso.");
            });
        }
        
        pneuExistente.setMarca(pneuDetails.getMarca());
        pneuExistente.setMedida(pneuDetails.getMedida());
        pneuExistente.setModelo(pneuDetails.getModelo());
        pneuExistente.setAnoFabricacao(pneuDetails.getAnoFabricacao());
        pneuExistente.setPeriodoGarantiaMeses(pneuDetails.getPeriodoGarantiaMeses());
        
        return pneuRepository.save(pneuExistente);
    }

    @Override
    public void deleteById(Long id) {
        Pneu pneu = findById(id).orElseThrow(() -> new RuntimeException("Pneu não encontrado"));
        if (pneu.getStatus() == Pneu.StatusPneu.EM_USO) {
            throw new IllegalStateException("Não é possível excluir um pneu que está em uso.");
        }
        pneuRepository.delete(pneu);
    }

    @Override @Transactional(readOnly = true)
    public Optional<Pneu> findById(Long id) { return pneuRepository.findById(id); }

    @Override @Transactional(readOnly = true)
    public List<Pneu> findAll() { return pneuRepository.findAll(); }

    @Override @Transactional(readOnly = true)
    public Optional<Pneu> findByNumeroSerie(String numeroSerie) { return pneuRepository.findByNumeroSerie(numeroSerie); }

    @Override @Transactional(readOnly = true)
    public List<Pneu> search(PneuSearchDTO criteria) {
        return pneuRepository.findAll(PneuSpecification.searchByCriteria(criteria));
    }

    // --- Lógica de Negócio ---

    @Override public Pneu enviarParaManutencao(Long pneuId) {
        Pneu pneu = findById(pneuId).orElseThrow(() -> new RuntimeException("Pneu não encontrado"));
        pneu.enviarParaManutencao();
        return pneuRepository.save(pneu);
    }

    @Override public Pneu retornarDeManutencao(Long pneuId) {
        Pneu pneu = findById(pneuId).orElseThrow(() -> new RuntimeException("Pneu não encontrado"));
        pneu.retornarDaManutencao();
        return pneuRepository.save(pneu);
    }
    
    @Override public Pneu enviarParaReforma(Long pneuId) {
        Pneu pneu = findById(pneuId).orElseThrow(() -> new RuntimeException("Pneu não encontrado"));
        pneu.enviarParaReforma();
        return pneuRepository.save(pneu);
    }

    @Override public Pneu retornarDeReforma(Long pneuId) {
        Pneu pneu = findById(pneuId).orElseThrow(() -> new RuntimeException("Pneu não encontrado"));
        pneu.retornarDaReforma();
        return pneuRepository.save(pneu);
    }
    
    @Override public void descartarPneu(Long pneuId) {
        Pneu pneu = findById(pneuId).orElseThrow(() -> new RuntimeException("Pneu não encontrado"));
        pneu.descartar();
        pneuRepository.save(pneu);
    }

    public OrdemServico descartarPneuViaOS(Long pneuId) {
        Pneu pneu = findById(pneuId)
            .orElseThrow(() -> new RuntimeException("Pneu não encontrado para descarte."));

        // Valida se o pneu está em uma condição que permite o descarte
        if (pneu.getStatus() != Pneu.StatusPneu.DISPONIVEL && pneu.getStatus() != Pneu.StatusPneu.NOVO) {
            throw new IllegalStateException("Apenas pneus com status DISPONÍVEL ou NOVO podem ser descartados.");
        }

        // 1. Gera um número único para a OS de descarte
        String prefix = "OS-DESC-PNEU-";
        int prefixLength = prefix.length() + 1;
        Integer maxNum = osRepository.findMaxNumeroByPrefix(prefix + "%", prefixLength);
        String numeroOS = String.format("%s%06d", prefix, maxNum + 1);

        String desc = "Ordem de Serviço para inspeção final e preparação para descarte do Pneu " + pneu.getModelo() + " (Série: " + pneu.getNumeroSerie() + ")";
        LocalDate hoje = LocalDate.now();
        
        // 2. Cria a OS Corretiva
        OrdemServico os = new OrdemServico(numeroOS, OrdemServico.TipoOrdemServico.CORRETIVA, desc, hoje, hoje.plusDays(2)); // Prazo curto para descarte
        os.setPneu(pneu); // Define o pneu como o alvo da OS

        // 3. Salva a OS, formalizando o início do processo
        return osRepository.save(os);
    }

    // --- Geração Automática de OS Preventiva ---

    @Override
    public void verificarEGerarOsPreventivas() {
        // Busca apenas pneus que estão EM_USO para verificar a necessidade de manutenção.
        List<Pneu> pneusEmUso = pneuRepository.findByStatus(Pneu.StatusPneu.EM_USO);
        for (Pneu pneu : pneusEmUso) {
            gerarOsPreventivaSeNecessario(pneu);
        }
    }

    private void gerarOsPreventivaSeNecessario(Pneu pneu) {
        if (osRepository.existsByPneuAndStatusIn(pneu, List.of(StatusOrdemServico.ABERTA, StatusOrdemServico.EM_EXECUCAO))) {
            return; // Já existe OS ativa
        }

        String desc = null;
        LocalDate dataPrevisaoInicio = null;

        if (pneu.reformaPrestesVencer()) {
            desc = "Reforma Preventiva (30 dias / 2000km) para Pneu " + pneu.getModelo();
            dataPrevisaoInicio = LocalDate.now().plusDays(pneu.getDiasRestantesReforma());
        } else if (pneu.manutencaoPrestesVencer()) {
            desc = "Manutenção Preventiva (30 dias / 500km) para Pneu " + pneu.getModelo();
            dataPrevisaoInicio = LocalDate.now().plusDays(pneu.getDiasRestantesManutencao());
        }

        if (desc != null) {
            String prefix = "OS-PREV-";
            Integer maxNum = osRepository.findMaxNumeroByPrefix(prefix + "%", prefix.length() + 1);
            String numeroOS = String.format("%s%03d", prefix, maxNum + 1);

            OrdemServico os = new OrdemServico(numeroOS, TipoOrdemServico.PREVENTIVA, desc, dataPrevisaoInicio, dataPrevisaoInicio.plusDays(3));
            os.setPneu(pneu);
            if (pneu.getOnibus() != null) os.setOnibus(pneu.getOnibus());

            // OS de pneu geralmente não adiciona itens automaticamente, apenas agenda o serviço.
            // Itens como "mão de obra" podem ser adicionados manualmente depois.
            osRepository.save(os);
            System.out.println("LOG: OS Preventiva " + numeroOS + " criada para pneu " + pneu.getId());
        }
    }

    // --- Métodos de Relatório ---
    @Override @Transactional(readOnly = true)
    public List<Object[]> countByStatus() { return pneuRepository.countByStatus(); }
    
    @Override @Transactional(readOnly = true)
    public List<Object[]> avgKmPorMarca() { return pneuRepository.avgKmPorMarca(); }
}