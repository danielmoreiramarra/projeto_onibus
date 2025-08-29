package com.proj_db.onibus.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.model.ItemOrdemServico;
import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.model.OrdemServico.StatusOrdemServico;
import com.proj_db.onibus.model.OrdemServico.TipoOrdemServico;
import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.repository.ItemOrdemServicoRepository;
import com.proj_db.onibus.repository.OnibusRepository;
import com.proj_db.onibus.repository.OrdemServicoRepository;
import com.proj_db.onibus.repository.ProdutoRepository;

@Service
@Transactional
public class OrdemServicoImpl implements OrdemServicoService {
    
    @Autowired private OrdemServicoRepository ordemServicoRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private EstoqueService estoqueService;
    @Autowired private ItemOrdemServicoRepository itemOrdemServicoRepository;
    @Autowired private OnibusRepository onibusRepository;

    @Override
    public OrdemServico criarOrdemServico(OrdemServico ordemServico) {
        if (ordemServico.getNumeroOS() == null || ordemServico.getNumeroOS().isEmpty()) {
            ordemServico.setNumeroOS(gerarProximoNumeroOS());
        }
        
        if (ordemServicoRepository.existsByNumeroOS(ordemServico.getNumeroOS())) {
            throw new RuntimeException("Já existe uma OS com este número: " + ordemServico.getNumeroOS());
        }

        Onibus onibus = onibusRepository.findById(ordemServico.getOnibus().getId())
            .orElseThrow(() -> new RuntimeException("Ônibus não encontrado com ID: " + ordemServico.getOnibus().getId()));
        ordemServico.setOnibus(onibus);

        if (ordemServico.getDataAbertura() == null) {
            ordemServico.setDataAbertura(LocalDate.now());
        }
        
        if (ordemServico.getStatus() == null) {
            ordemServico.setStatus(StatusOrdemServico.ABERTA);
        }
        
        if (ordemServico.getTipo() == TipoOrdemServico.PREVENTIVA) {
            // Lógica de reserva de estoque para serviços preventivos (implementaremos no frontend)
        }
        
        return ordemServicoRepository.save(ordemServico);
    }
    
    @Override
    public OrdemServico atualizarOrdemServico(Long id, OrdemServico ordemServicoAtualizada) {
        OrdemServico osExistente = buscarPorId(id);
        
        if (!osExistente.getNumeroOS().equals(ordemServicoAtualizada.getNumeroOS()) && 
            ordemServicoRepository.existsByNumeroOS(ordemServicoAtualizada.getNumeroOS())) {
            throw new RuntimeException("Já existe uma OS com este número: " + ordemServicoAtualizada.getNumeroOS());
        }
        
        osExistente.setNumeroOS(ordemServicoAtualizada.getNumeroOS());
        osExistente.setTipo(ordemServicoAtualizada.getTipo());
        osExistente.setDescricao(ordemServicoAtualizada.getDescricao());
        osExistente.setDataPrevisaoConclusao(ordemServicoAtualizada.getDataPrevisaoConclusao());
        
        return ordemServicoRepository.save(osExistente);
    }

    @Override
    public OrdemServico cancelarOrdemServico(Long id) {
        return cancelarOrdemServico(id, "Cancelado pelo usuário");
    }

    @Override
    public OrdemServico iniciarExecucao(Long ordemServicoId) {
        OrdemServico os = buscarPorId(ordemServicoId);
        if (os.getStatus() != StatusOrdemServico.ABERTA) {
            throw new RuntimeException("Só é possível iniciar OSs com status ABERTA");
        }
        
        os.setStatus(StatusOrdemServico.EM_EXECUCAO);
        return ordemServicoRepository.save(os);
    }

    @Override
    public OrdemServico finalizarOrdemServico(Long ordemServicoId) {
        OrdemServico os = buscarPorId(ordemServicoId);
        if (os.getStatus() != StatusOrdemServico.EM_EXECUCAO) {
            throw new RuntimeException("Só é possível finalizar OSs com status EM_EXECUCAO");
        }
        
        os.setStatus(StatusOrdemServico.FINALIZADA);
        os.setDataConclusao(LocalDate.now());
        return ordemServicoRepository.save(os);
    }

    @Override
    public OrdemServico cancelarOrdemServico(Long ordemServicoId, String motivo) {
        OrdemServico os = buscarPorId(ordemServicoId);
        if (os.getStatus() != StatusOrdemServico.ABERTA && os.getStatus() != StatusOrdemServico.EM_EXECUCAO) {
            throw new RuntimeException("Só é possível cancelar OSs com status ABERTA ou EM_EXECUCAO");
        }
        
        os.setStatus(StatusOrdemServico.CANCELADA);
        os.setDataCancelamento(LocalDate.now());
        
        return ordemServicoRepository.save(os);
    }

    @Override
    public void excluirOrdemServico(Long id) {
        OrdemServico os = buscarPorId(id);
        if (os.getStatus() != StatusOrdemServico.CANCELADA) {
            throw new RuntimeException("Só é possível excluir OSs com status CANCELADA");
        }
        ordemServicoRepository.deleteById(id);
    }
    
    @Override
    public OrdemServico buscarPorId(Long id) {
        return ordemServicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada com ID: " + id));
    }

    @Override
    public List<OrdemServico> buscarTodas() {
        return ordemServicoRepository.findAll();
    }

    @Override
    public List<OrdemServico> searchOrdemServico(Map<String, String> searchTerms) {
        Long osId = searchTerms.containsKey("osId") && !searchTerms.get("osId").isEmpty() ? Long.parseLong(searchTerms.get("osId")) : null;
        String numeroOS = searchTerms.get("numeroOS");
        TipoOrdemServico tipo = searchTerms.containsKey("tipo") && !searchTerms.get("tipo").isEmpty() ? TipoOrdemServico.valueOf(searchTerms.get("tipo")) : null;
        StatusOrdemServico status = searchTerms.containsKey("status") && !searchTerms.get("status").isEmpty() ? StatusOrdemServico.valueOf(searchTerms.get("status")) : null;
        Long onibusId = searchTerms.containsKey("onibusId") && !searchTerms.get("onibusId").isEmpty() ? Long.parseLong(searchTerms.get("onibusId")) : null;
        LocalDate dataAberturaInicio = searchTerms.containsKey("dataAberturaInicio") && !searchTerms.get("dataAberturaInicio").isEmpty() ? LocalDate.parse(searchTerms.get("dataAberturaInicio")) : null;
        LocalDate dataAberturaFim = searchTerms.containsKey("dataAberturaFim") && !searchTerms.get("dataAberturaFim").isEmpty() ? LocalDate.parse(searchTerms.get("dataAberturaFim")) : null;
        LocalDate dataConclusaoInicio = searchTerms.containsKey("dataConclusaoInicio") && !searchTerms.get("dataConclusaoInicio").isEmpty() ? LocalDate.parse(searchTerms.get("dataConclusaoInicio")) : null;
        LocalDate dataConclusaoFim = searchTerms.containsKey("dataConclusaoFim") && !searchTerms.get("dataConclusaoFim").isEmpty() ? LocalDate.parse(searchTerms.get("dataConclusaoFim")) : null;
        Double valorTotalMin = searchTerms.containsKey("valorTotalMin") && !searchTerms.get("valorTotalMin").isEmpty() ? Double.parseDouble(searchTerms.get("valorTotalMin")) : null;
        Double valorTotalMax = searchTerms.containsKey("valorTotalMax") && !searchTerms.get("valorTotalMax").isEmpty() ? Double.parseDouble(searchTerms.get("valorTotalMax")) : null;
        Long produtoId = searchTerms.containsKey("produtoId") && !searchTerms.get("produtoId").isEmpty() ? Long.parseLong(searchTerms.get("produtoId")) : null;
        
        return ordemServicoRepository.searchOrdemServico(
            osId, numeroOS, tipo, status, onibusId, 
            dataAberturaInicio, dataAberturaFim, 
            dataConclusaoInicio, dataConclusaoFim, 
            valorTotalMin, valorTotalMax, 
            produtoId);
    }

    @Override
    public Optional<OrdemServico> buscarPorNumeroOS(String numeroOS) {
        return ordemServicoRepository.findByNumeroOS(numeroOS);
    }

    @Override
    public List<OrdemServico> buscarPorStatus(StatusOrdemServico status) {
        return ordemServicoRepository.findByStatus(status);
    }

    @Override
    public List<OrdemServico> buscarPorTipo(TipoOrdemServico tipo) {
        return ordemServicoRepository.findByTipo(tipo);
    }

    @Override
    public List<OrdemServico> buscarPorOnibus(Long onibusId) {
        return ordemServicoRepository.findByOnibusId(onibusId);
    }

    @Override
    public List<OrdemServico> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return ordemServicoRepository.findByDataAberturaBetween(dataInicio, dataFim);
    }

    @Override
    public boolean adicionarItem(Long ordemServicoId, Long produtoId, Integer quantidade) {
        OrdemServico os = buscarPorId(ordemServicoId);
        
        if (os.getStatus() != StatusOrdemServico.ABERTA) {
            throw new RuntimeException("Só é possível adicionar itens em OSs com status ABERTA");
        }

        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Optional<ItemOrdemServico> itemExistente = os.getItens().stream()
            .filter(item -> item.getProduto().getId().equals(produtoId))
            .findFirst();
        
        if (itemExistente.isPresent()) {
            ItemOrdemServico item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + quantidade);
        } else {
            ItemOrdemServico item = new ItemOrdemServico();
            item.setOrdemServico(os);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPrecoUnitario());
            os.getItens().add(item);
        }
        
        ordemServicoRepository.save(os);
        os.setValorTotal(calcularValorTotal(os.getId()));
        
        return true;
    }

    @Override
    public boolean removerItem(Long ordemServicoId, Long produtoId) {
        OrdemServico os = buscarPorId(ordemServicoId);
        
        if (os.getStatus() != StatusOrdemServico.ABERTA) {
            throw new RuntimeException("Só é possível remover itens em OSs com status ABERTA");
        }
        
        Optional<ItemOrdemServico> itemParaRemover = os.getItens().stream()
            .filter(item -> item.getProduto().getId().equals(produtoId))
            .findFirst();
        
        if (itemParaRemover.isPresent()) {
            os.getItens().remove(itemParaRemover.get());
            ordemServicoRepository.save(os);
            
            os.setValorTotal(calcularValorTotal(os.getId()));
            
            return true;
        }
        
        throw new RuntimeException("Item não encontrado na OS");
    }

    @Override
    public boolean verificarEstoqueSuficiente(Long ordemServicoId) {
        OrdemServico os = buscarPorId(ordemServicoId);
        
        for (ItemOrdemServico item : os.getItens()) {
            if (!estoqueService.verificarDisponibilidade(item.getProduto().getId(), item.getQuantidade())) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public Double calcularValorTotal(Long ordemServicoId) {
        OrdemServico os = buscarPorId(ordemServicoId);
        return os.getItens().stream()
            .mapToDouble(item -> item.getPrecoUnitario() * item.getQuantidade())
            .sum();
    }

    @Override
    public List<OrdemServico> buscarOrdensEmAberto() {
        return ordemServicoRepository.findOrdensEmAberto();
    }

    @Override
    public List<OrdemServico> buscarOrdensEmExecucao() {
        return ordemServicoRepository.findOrdensEmExecucao();
    }

    @Override
    public List<OrdemServico> buscarOrdensFinalizadasNoPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return ordemServicoRepository.findOrdensFinalizadasNoPeriodo(dataInicio, dataFim);
    }

    @Override
    public List<OrdemServico> buscarOrdensComPrevisaoVencida() {
        return ordemServicoRepository.findOrdensComPrevisaoVencida();
    }

    @Override
    public String gerarProximoNumeroOS() {
        Integer proximoNumero = ordemServicoRepository.findProximoNumeroOS();
        return "OS-" + String.format("%04d", proximoNumero);
    }

    @Override
    public List<Object[]> buscarEstatisticasPorStatus() {
        return ordemServicoRepository.countOrdensPorStatus();
    }

    @Override
    public List<Object[]> buscarEstatisticasPorTipo() {
        return ordemServicoRepository.countOrdensPorTipo();
    }

    @Override
    public Double calcularFaturamentoPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return ordemServicoRepository.calcularValorTotalFinalizadoNoPeriodo(dataInicio, dataFim);
    }

    private void liberarReservas(OrdemServico os) {
        for (ItemOrdemServico item : os.getItens()) {
            try {
                estoqueService.liberarReserva(
                    item.getProduto().getId(), 
                    item.getQuantidade()
                );
            } catch (Exception e) {
                System.err.println("Erro ao liberar reserva do produto " + item.getProduto().getNome() + ": " + e.getMessage());
            }
        }
    }
    
}