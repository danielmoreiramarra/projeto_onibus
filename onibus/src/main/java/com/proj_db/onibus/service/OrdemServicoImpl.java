package com.proj_db.onibus.service;

import java.util.List; // Importa todos os modelos
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired; // Importa todos os repositórios
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.dto.OrdemServicoCreateDTO;
import com.proj_db.onibus.dto.OrdemServicoSearchDTO;
import com.proj_db.onibus.dto.OrdemServicoUpdateDTO;
import com.proj_db.onibus.model.Cambio;
import com.proj_db.onibus.model.ItemOrdemServico;
import com.proj_db.onibus.model.Motor;
import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.model.OrdemServico.StatusOrdemServico;
import com.proj_db.onibus.model.Pneu;
import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.repository.CambioRepository;
import com.proj_db.onibus.repository.MotorRepository;
import com.proj_db.onibus.repository.OnibusRepository;
import com.proj_db.onibus.repository.OrdemServicoRepository;
import com.proj_db.onibus.repository.PneuRepository;
import com.proj_db.onibus.repository.ProdutoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class OrdemServicoImpl implements OrdemServicoService {

    // --- DEPENDÊNCIAS ---
    @Autowired private OrdemServicoRepository osRepository;
    @Autowired private OnibusRepository onibusRepository;
    @Autowired private MotorRepository motorRepository;
    @Autowired private CambioRepository cambioRepository;
    @Autowired private PneuRepository pneuRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private EstoqueService estoqueService;

    // --- CRUD BÁSICO ---

    @Override
    public OrdemServico create(OrdemServicoCreateDTO dto) {
        osRepository.findByNumeroOS(dto.getNumeroOS()).ifPresent(os -> {
            throw new IllegalArgumentException("O número de OS '" + dto.getNumeroOS() + "' já existe.");
        });

        OrdemServico os = new OrdemServico(
            dto.getNumeroOS(), dto.getTipo(), dto.getDescricao(),
            dto.getDataPrevisaoInicio(), dto.getDataPrevisaoConclusao()
        );

        // Associa os alvos (ônibus, motor, etc.) buscando-os pelo ID
        if (dto.getOnibusId() != null) os.setOnibus(onibusRepository.findById(dto.getOnibusId()).orElse(null));
        if (dto.getMotorId() != null) os.setMotor(motorRepository.findById(dto.getMotorId()).orElse(null));
        if (dto.getCambioId() != null) os.setCambio(cambioRepository.findById(dto.getCambioId()).orElse(null));
        if (dto.getPneuId() != null) os.setPneu(pneuRepository.findById(dto.getPneuId()).orElse(null));

        if (os.getAlvo() == null) {
            throw new IllegalArgumentException("Uma Ordem de Serviço deve ter pelo menos um alvo.");
        }
        
        return osRepository.save(os);
    }

    @Override
    public OrdemServico updateInfo(Long osId, OrdemServicoUpdateDTO dto) {
        OrdemServico os = findById(osId)
            .orElseThrow(() -> new EntityNotFoundException("OS não encontrada com ID: " + osId));
            
        if (os.getStatus() != OrdemServico.StatusOrdemServico.ABERTA) {
            throw new IllegalStateException("Apenas informações de uma OS ABERTA podem ser atualizadas.");
        }
        
        os.setDescricao(dto.getDescricao());
        os.setDataPrevisaoInicio(dto.getDataPrevisaoInicio());
        os.setDataPrevisaoConclusao(dto.getDataPrevisaoConclusao());
        
        return osRepository.save(os);
    }

    @Override
    public void delete(Long osId) {
        OrdemServico os = findById(osId).orElseThrow(() -> new EntityNotFoundException("OS não encontrada."));
        if (os.getStatus() != StatusOrdemServico.CANCELADA) {
            throw new IllegalStateException("Apenas Ordens de Serviço CANCELADAS podem ser excluídas.");
        }
        osRepository.delete(os);
    }

    // --- MÉTODOS DE BUSCA ---

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdemServico> findById(Long id) {
        return osRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> findAll() {
        return osRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdemServico> search(OrdemServicoSearchDTO criteria) {
        return osRepository.findAll(OrdemServicoSpecification.searchByCriteria(criteria));
    }

    // --- AÇÕES PRINCIPAIS DO FLUXO DE TRABALHO ---

    @Override
    public OrdemServico startExecution(Long osId) {
        OrdemServico os = findById(osId).orElseThrow(() -> new EntityNotFoundException("OS não encontrada."));
        
        // 1. Reserva todos os itens no estoque
        for (ItemOrdemServico item : os.getItens()) {
            boolean sucesso = estoqueService.reservar(item.getProduto().getId(), item.getQuantidade());
            if (!sucesso) {
                throw new IllegalStateException("Estoque insuficiente para o produto: " + item.getProduto().getNome());
            }
        }

        // 2. Muda o status do alvo principal (se aplicável) e do ônibus pai
        Object alvo = os.getAlvo();
        if (alvo instanceof Motor motor) {
            if (motor.getOnibus() != null) motor.getOnibus().enviarParaManutencao();
            motor.enviarParaManutencao();
        } else if (alvo instanceof Cambio cambio) {
            if (cambio.getOnibus() != null) cambio.getOnibus().enviarParaManutencao();
            cambio.enviarParaManutencao();
        } else if (alvo instanceof Pneu pneu) {
            if (pneu.getOnibus() != null) pneu.getOnibus().enviarParaManutencao();
            pneu.enviarParaManutencao();
        } else if (alvo instanceof Onibus onibus) {
            onibus.enviarParaManutencao();
        }
        
        // 3. Muda o status da OS
        os.iniciarExecucao();
        return osRepository.save(os);
    }

    @Override
    public OrdemServico finishExecution(Long osId) {
        OrdemServico os = findById(osId).orElseThrow(() -> new EntityNotFoundException("OS não encontrada."));
        
        // 1. Consome os itens que foram reservados
        for (ItemOrdemServico item : os.getItens()) {
            estoqueService.confirmarConsumoDeReserva(item.getProduto().getId(), item.getQuantidade());
        }

        // 2. Muda o status do alvo principal de volta para DISPONÍVEL
        Object alvo = os.getAlvo();
        if (alvo instanceof Motor motor) motor.retornarDaManutencao();
        if (alvo instanceof Cambio cambio) cambio.retornarDaManutencao();
        if (alvo instanceof Pneu pneu) pneu.retornarDaManutencao();
        if (alvo instanceof Onibus onibus) onibus.retornarDaManutencao();
        
        // 3. Muda o status da OS
        os.finalizar();
        return osRepository.save(os);
    }

    @Override
    public OrdemServico cancel(Long osId) {
        OrdemServico os = findById(osId).orElseThrow(() -> new EntityNotFoundException("OS não encontrada."));
        
        // Libera os itens que foram reservados, apenas se a OS já estava em execução
        if (os.getStatus() == StatusOrdemServico.EM_EXECUCAO) {
            for (ItemOrdemServico item : os.getItens()) {
                estoqueService.liberarReserva(item.getProduto().getId(), item.getQuantidade());
            }
        }
        
        os.cancelar();
        return osRepository.save(os);
    }

    // --- GERENCIAMENTO DE ITENS ---

    @Override
    public OrdemServico addItem(Long osId, Long produtoId, Double quantidade, String descricao) {
        OrdemServico os = findById(osId).orElseThrow(() -> new EntityNotFoundException("OS não encontrada."));
        Produto produto = produtoRepository.findById(produtoId).orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));
        
        os.adicionarItem(produto, quantidade, descricao);
        return osRepository.save(os);
    }

    @Override
    public OrdemServico removeItem(Long osId, Long itemId) {
        OrdemServico os = findById(osId).orElseThrow(() -> new EntityNotFoundException("OS não encontrada."));
        ItemOrdemServico item = os.getItens().stream().filter(i -> i.getId().equals(itemId)).findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Item não encontrado nesta OS."));
        
        // Lógica de remover o item (o orphanRemoval=true cuidará da exclusão)
        os.getItens().remove(item);
        
        return osRepository.save(os);
    }

    @Override
    public OrdemServico updateItemQuantity(Long osId, Long itemId, Double novaQuantidade) {
        OrdemServico os = findById(osId).orElseThrow(() -> new EntityNotFoundException("OS não encontrada."));
        
        if (os.getStatus() != StatusOrdemServico.ABERTA) {
            throw new IllegalStateException("A quantidade de itens só pode ser alterada em uma OS ABERTA.");
        }
        
        ItemOrdemServico item = os.getItens().stream().filter(i -> i.getId().equals(itemId)).findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Item não encontrado nesta OS."));
        
        item.setQuantidade(novaQuantidade);
        
        return osRepository.save(os);
    }
}