package com.proj_db.onibus.service;

import java.time.LocalDate; // Importa todos os modelos necessários
import java.util.List; // Importa todos os repositórios
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proj_db.onibus.dto.CambioCreateDTO;
import com.proj_db.onibus.dto.CambioUpdateDTO;
import com.proj_db.onibus.model.Cambio;
import com.proj_db.onibus.model.Estoque;
import com.proj_db.onibus.model.OrdemServico;
import com.proj_db.onibus.model.OrdemServico.StatusOrdemServico;
import com.proj_db.onibus.model.OrdemServico.TipoOrdemServico;
import com.proj_db.onibus.model.Produto;
import com.proj_db.onibus.repository.CambioRepository;
import com.proj_db.onibus.repository.EstoqueRepository;
import com.proj_db.onibus.repository.OrdemServicoRepository;
import com.proj_db.onibus.repository.ProdutoRepository;

@Service
@Transactional
public class CambioImpl implements CambioService {

    // Injeção de todos os repositórios que vamos precisar
    @Autowired private CambioRepository cambioRepository;
    @Autowired private OrdemServicoRepository osRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private EstoqueRepository estoqueRepository;

    @Override
    public Cambio save(CambioCreateDTO cambio) {
        // Lógica de validação para evitar duplicatas
        cambioRepository.findByNumeroSerie(cambio.getNumeroSerie()).ifPresent(c -> {
            throw new IllegalArgumentException("Já existe um câmbio com este número de série.");
        });
        cambioRepository.findByCodigoFabricacao(cambio.getCodigoFabricacao()).ifPresent(c -> {
            throw new IllegalArgumentException("Já existe um câmbio com este código de fabricação.");
        });

        // Converte o DTO para a Entidade (lógica que estava no Controller)
        Cambio novoCambio = new Cambio();
        novoCambio.setMarca(cambio.getMarca());
        novoCambio.setModelo(cambio.getModelo());
        novoCambio.setTipo(cambio.getTipo());
        novoCambio.setNumeroMarchas(cambio.getNumeroMarchas());
        novoCambio.setCodigoFabricacao(cambio.getCodigoFabricacao());
        novoCambio.setNumeroSerie(cambio.getNumeroSerie());
        novoCambio.setAnoFabricacao(cambio.getAnoFabricacao());
        novoCambio.setCapacidadeFluido(cambio.getCapacidadeFluido());
        novoCambio.setTipoFluido(cambio.getTipoFluido());
        novoCambio.setDataCompra(cambio.getDataCompra());
        novoCambio.setPeriodoGarantiaMeses(cambio.getPeriodoGarantiaMeses());
        // Status e quantidade de fluido já são definidos com valores padrão no modelo

        return cambioRepository.save(novoCambio);
    }

    @Override
    public Cambio update(Long id, CambioUpdateDTO cambioDetails) {
        // 1. Encontra o câmbio existente no banco de dados.
        Cambio cambioExistente = findById(id)
            .orElseThrow(() -> new RuntimeException("Câmbio não encontrado com ID: " + id));

        // 2. Valida o Número de Série
        // Se o número de série foi alterado...
        if (!cambioExistente.getNumeroSerie().equals(cambioDetails.getNumeroSerie())) {
            // ...verifica se o novo número de série já existe em outro câmbio.
            cambioRepository.findByNumeroSerie(cambioDetails.getNumeroSerie()).ifPresent(outroCambio -> {
                throw new IllegalArgumentException("O número de série '" + cambioDetails.getNumeroSerie() + "' já está em uso por outro câmbio.");
            });
        }

        // 3. Valida o Código de Fabricação
        // Se o código de fabricação foi alterado...
        if (!cambioExistente.getCodigoFabricacao().equals(cambioDetails.getCodigoFabricacao())) {
            // ...verifica se o novo código já existe em outro câmbio.
            cambioRepository.findByCodigoFabricacao(cambioDetails.getCodigoFabricacao()).ifPresent(outroCambio -> {
                throw new IllegalArgumentException("O código de fabricação '" + cambioDetails.getCodigoFabricacao() + "' já está em uso por outro câmbio.");
            });
        }

        // 4. Atualiza todos os campos permitidos
        cambioExistente.setMarca(cambioDetails.getMarca());
        cambioExistente.setModelo(cambioDetails.getModelo());
        cambioExistente.setTipo(cambioDetails.getTipo());
        cambioExistente.setNumeroMarchas(cambioDetails.getNumeroMarchas());
        cambioExistente.setTipoFluido(cambioDetails.getTipoFluido());
        cambioExistente.setCapacidadeFluido(cambioDetails.getCapacidadeFluido());
        cambioExistente.setAnoFabricacao(cambioDetails.getAnoFabricacao());
        cambioExistente.setPeriodoGarantiaMeses(cambioDetails.getPeriodoGarantiaMeses());
        // Nota: Status, Onibus, Quantidade de Fluido e Datas são controlados por outros métodos.

        // 5. Salva e retorna o câmbio atualizado.
        return cambioRepository.save(cambioExistente);
    }

    @Override
    public void deleteById(Long id) {
        Cambio cambio = findById(id).orElseThrow(() -> new RuntimeException("Câmbio não encontrado"));
        if (cambio.getStatus() == Cambio.StatusCambio.EM_USO) {
            throw new IllegalStateException("Não é possível excluir um câmbio que está em uso.");
        }
        cambioRepository.delete(cambio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cambio> findById(Long id) {
        return cambioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cambio> findAll() {
        return cambioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cambio> findByNumeroSerie(String numeroSerie) {
        return cambioRepository.findByNumeroSerie(numeroSerie);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cambio> findByCodigoFabricacao(String codigoFabricacao) {
        return cambioRepository.findByCodigoFabricacao(codigoFabricacao);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cambio> search(CambioSearchDTO criteria) {
        // Usa a classe Specification para criar a busca dinâmica
        return cambioRepository.findAll(CambioSpecification.searchByCriteria(criteria));
    }

    // --- Lógica de Negócio ---

    @Override
    public Cambio enviarParaManutencao(Long cambioId) {
        Cambio cambio = findById(cambioId).orElseThrow(() -> new RuntimeException("Câmbio não encontrado"));
        cambio.enviarParaManutencao(); // <<< CHAMA O MÉTODO DO MODELO
        return cambioRepository.save(cambio);
    }

    @Override
    public Cambio retornarDeManutencao(Long cambioId) {
        Cambio cambio = findById(cambioId).orElseThrow(() -> new RuntimeException("Câmbio não encontrado"));
        cambio.retornarDaManutencao(); // <<< CHAMA O MÉTODO DO MODELO
        return cambioRepository.save(cambio);
    }

    @Override
    public Cambio enviarParaRevisao(Long cambioId) {
        Cambio cambio = findById(cambioId).orElseThrow(() -> new RuntimeException("Câmbio não encontrado"));
        cambio.enviarParaRevisao(); // <<< CHAMA O MÉTODO DO MODELO
        return cambioRepository.save(cambio);
    }

    @Override
    public Cambio retornarDaRevisao(Long cambioId) {
        Cambio cambio = findById(cambioId).orElseThrow(() -> new RuntimeException("Câmbio não encontrado"));
        cambio.retornarDaRevisao(); // <<< CHAMA O MÉTODO DO MODELO
        return cambioRepository.save(cambio);
    }

    public OrdemServico descartarCambioViaOS(Long cambioId) {
        Cambio cambio = findById(cambioId)
            .orElseThrow(() -> new RuntimeException("Câmbio não encontrado para descarte."));

        if (cambio.getStatus() != Cambio.StatusCambio.DISPONIVEL && cambio.getStatus() != Cambio.StatusCambio.NOVO) {
            throw new IllegalStateException("Apenas câmbios com status DISPONÍVEL ou NOVO podem ser descartados.");
        }

        // 1. Cria uma OS Corretiva específica para o descarte
        
        String prefix = "OS-CORR-DESC-";
        int prefixLength = prefix.length() + 1;
        Integer maxNum = osRepository.findMaxNumeroByPrefix(prefix + "%", prefixLength);
        String numeroOS = String.format("%s%06d", prefix, maxNum + 1);

        String desc = "Ordem de Serviço para esgotamento de fluido e preparação para descarte do câmbio " + cambio.getModelo();
        LocalDate hoje = LocalDate.now();
        
        OrdemServico os = new OrdemServico(numeroOS, OrdemServico.TipoOrdemServico.CORRETIVA, desc, hoje, hoje.plusDays(7));
        os.setCambio(cambio); // Define o alvo

        // 2. Salva a OS no banco. O processo está formalmente iniciado.
        return osRepository.save(os);
        // O próximo passo seria o usuário "iniciar", "finalizar" esta OS,
        // o que chamaria outros métodos de serviço para esgotar o fluido e, finalmente, descartar o câmbio.
    }


    // --- Geração Automática de OS Preventiva ---

    @Override
    public void verificarEGerarOsPreventivas() {
        List<Cambio> todosOsCambiosAtivos = cambioRepository.findAll();
        for (Cambio cambio : todosOsCambiosAtivos) {
            // A lógica de verificação agora está centralizada em um único método
            gerarOsPreventivaSeNecessario(cambio);
        }
    }

    private void gerarOsPreventivaSeNecessario(Cambio cambio) {
        // Verifica se já existe uma OS Preventiva ativa para este câmbio
        if (osRepository.existsByCambioAndStatusIn(cambio, List.of(StatusOrdemServico.ABERTA, StatusOrdemServico.EM_EXECUCAO))) {
            System.out.println("LOG: Já existe uma OS ativa para o câmbio " + cambio.getId());
            return;
        }

        LocalDate ultimaTrocaFluido = cambio.getDataUltimaTrocaFluido();
        boolean trocaRecente = ultimaTrocaFluido != null && ultimaTrocaFluido.isAfter(LocalDate.now().minusDays(45)); // Ex: 45 dias

        // LÓGICA PARA REVISÃO (Prioridade maior)
        if (cambio.revisaoPrestesVencer()) {
            String desc = "Revisão Preventiva (30 dias) para Câmbio " + cambio.getModelo();
            // A revisão é necessária, mas a troca de fluido pode não ser.
            // O item de fluido só será adicionado se não houver troca recente.
            criarOsPreventiva(cambio, desc, "REVISAO", !trocaRecente); 
            return; // Sai após criar a OS de revisão
        }

        // LÓGICA PARA MANUTENÇÃO
        if (cambio.manutencaoPrestesVencer()) {
            // Manutenção preventiva (completar fluido) só é criada se NÃO houve troca recente.
            if (trocaRecente) {
                System.out.println("LOG: Manutenção preventiva para câmbio " + cambio.getId() + " adiada devido a troca de fluido recente.");
                return;
            }
            String desc = "Manutenção Preventiva (30 dias) para Câmbio " + cambio.getModelo();
            criarOsPreventiva(cambio, desc, "MANUTENCAO", true);
        }
    }

    private void criarOsPreventiva(Cambio cambio, String desc, String tipoServico, boolean incluirItemFluido) {
        // --- Geração do novo número da OS ---
        String prefix = "OS-PREV-";
        int prefixLength = prefix.length() + 1;
        Integer maxNum = osRepository.findMaxNumeroByPrefix(prefix + "%", prefixLength);
        String numeroOS = String.format("%s%06d", prefix, maxNum + 1);

        // --- Cálculo de Datas ---
        LocalDate dataPrevisaoInicio;
        if ("REVISAO".equals(tipoServico)) {
            dataPrevisaoInicio = LocalDate.now().plusDays(cambio.getDiasRestantesRevisao());
        } else {
            dataPrevisaoInicio = LocalDate.now().plusDays(cambio.getDiasRestantesManutencao());
        }
        
        OrdemServico os = new OrdemServico(numeroOS, TipoOrdemServico.PREVENTIVA, desc, dataPrevisaoInicio, dataPrevisaoInicio.plusDays(7));
        os.setCambio(cambio);
        if (cambio.getOnibus() != null) {
            os.setOnibus(cambio.getOnibus());
        }

        // --- Adição condicional do item de fluido ---
        if (incluirItemFluido) {
            Produto fluido = produtoRepository.findProdutoParaPreventiva(cambio.getTipoFluido()).orElse(null);
            if (fluido != null) {
                Double quantidade = "REVISAO".equals(tipoServico) ? cambio.getCapacidadeFluido() : (cambio.getCapacidadeFluido() - cambio.getQuantidadeFluido());
                if (quantidade > 0) {
                    os.adicionarItem(fluido, quantidade, "Troca/complemento de fluido para serviço preventivo.");
                    
                    // Lógica de reserva de estoque
                    Estoque estoque = estoqueRepository.findByProduto(fluido).orElse(null);
                    if (estoque != null && estoque.reservarEstoque(quantidade)) {
                         osRepository.save(os);
                         System.out.println("LOG: OS Preventiva " + numeroOS + " criada para câmbio " + cambio.getId());
                    } else {
                        System.out.println("ALERTA: OS " + numeroOS + " para câmbio " + cambio.getId() + " criada, mas estoque de fluido insuficiente para reserva.");
                        osRepository.save(os); // Salva mesmo sem estoque, para controle
                    }
                }
            } else {
                 System.out.println("ALERTA: Produto de fluido não encontrado para câmbio " + cambio.getId());
                 osRepository.save(os); // Salva a OS mesmo sem o item
            }
        } else {
            // Salva a OS sem o item de fluido, mas com a descrição do serviço principal
            osRepository.save(os);
            System.out.println("LOG: OS Preventiva " + numeroOS + " criada para câmbio " + cambio.getId() + " (sem troca de fluido).");
        }
    }

    // --- Métodos de Relatório ---

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> countByTipo() {
        return cambioRepository.countByTipo();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> countByStatus() {
        return cambioRepository.countByStatus();
    }
}