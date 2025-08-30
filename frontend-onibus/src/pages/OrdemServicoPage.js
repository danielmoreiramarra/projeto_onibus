import React, { useState, useEffect } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import SearchBar from '../components/SearchBar';
import BackButton from '../components/BackButton';
import { ordemServicoService } from '../services/ordemServicoService';
import { onibusService } from '../services/onibusService';
import { produtoService } from '../services/produtoService';
import { estoqueService } from '../services/estoqueService';
import { motorService } from '../services/motorService';
import { cambioService } from '../services/cambioService';
import useSearch from '../hooks/useSearch';
import { StatusOrdemServico, TipoOrdemServico } from '../constants/ordemServicoEnums';
import { useNavigate } from 'react-router-dom';
import { TipoMotor } from '../constants/motorEnums';
import { TipoCambio } from '../constants/cambioEnums';
import { StatusPneu } from '../constants/pneuEnums';


// Componente para a lógica de criação de OS
const NovaOrdemServicoForm = ({ onibusList, produtosList, onCancel, onSubmit, loading }) => {
    const [formData, setFormData] = useState({
        onibusId: '',
        tipo: 'CORRETIVA',
        descricao: '',
        itens: [],
        servicosPreventivos: {}
    });

    const [produtoSelecionado, setProdutoSelecionado] = useState('');
    const [quantidadeProduto, setQuantidadeProduto] = useState(1);

    const handleAddItem = () => {
        if (!produtoSelecionado || quantidadeProduto <= 0) return;
        
        const produto = produtosList.find(p => p.id === produtoSelecionado);
        if (!produto) return;

        const novoItem = {
            produto: { id: produto.id, nome: produto.nome },
            quantidade: quantidadeProduto
        };
        
        setFormData(prev => ({
            ...prev,
            itens: [...prev.itens, novoItem]
        }));
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleCheckboxChange = (e) => {
        const { name, checked } = e.target;
        setFormData(prev => ({ 
            ...prev,
            servicosPreventivos: { ...prev.servicosPreventivos, [name]: checked }
        }));
    };
    
    return (
        <div className="card my-4">
            <div className="card-header d-flex justify-content-between align-items-center">
                <h5 className="card-title mb-0">➕ Criar Nova Ordem de Serviço</h5>
                <button onClick={onCancel} className="btn btn-secondary btn-sm">Voltar</button>
            </div>
            <div className="card-body">
                <form onSubmit={(e) => { e.preventDefault(); onSubmit(formData); }}>
                    {/* Campos Principais da OS */}
                    <div className="row g-3 mb-4">
                        <div className="col-md-6">
                            <label className="form-label">Ônibus</label>
                            <select className="form-select" name="onibusId" value={formData.onibusId} onChange={handleInputChange} required>
                                <option value="">Selecione um ônibus</option>
                                {onibusList.map(o => (
                                    <option key={o.id} value={o.id}>{o.numeroFrota} - {o.modelo}</option>
                                ))}
                            </select>
                        </div>
                        <div className="col-md-6">
                            <label className="form-label">Tipo de OS</label>
                            <select className="form-select" name="tipo" value={formData.tipo} onChange={handleInputChange} required>
                                {Object.values(TipoOrdemServico).map(t => (
                                    <option key={t} value={t}>{t}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    {/* ✅ Se a OS for preventiva, mostra a seleção de serviços */}
                    {formData.tipo === TipoOrdemServico.PREVENTIVA && (
                        <div className="card mb-4">
                            <div className="card-header">Serviços Preventivos</div>
                            <div className="card-body">
                                {/* Checkboxes para os serviços */}
                                <div className="form-check">
                                    <input className="form-check-input" type="checkbox" name="servicoMotorOleo" id="motorOleo" checked={formData.servicosPreventivos.servicoMotorOleo || false} onChange={handleCheckboxChange} />
                                    <label className="form-check-label" htmlFor="motorOleo">Troca de Óleo do Motor</label>
                                </div>
                                {/* ... outros serviços ... */}
                            </div>
                        </div>
                    )}

                    {/* ✅ Seleção de Produtos do Estoque */}
                    <h5 className="mt-4">Itens de Estoque</h5>
                    <div className="input-group mb-3">
                        <select className="form-select" value={produtoSelecionado} onChange={e => setProdutoSelecionado(e.target.value)}>
                            <option value="">Selecione um produto</option>
                            {produtosList.map(p => (
                                <option key={p.id} value={p.id}>{p.nome} ({p.codigoInterno})</option>
                            ))}
                        </select>
                        <input type="number" className="form-control" value={quantidadeProduto} onChange={e => setQuantidadeProduto(Number(e.target.value))} min="1" />
                        <button className="btn btn-outline-primary" type="button" onClick={handleAddItem}>Adicionar</button>
                    </div>

                    {/* Tabela de itens da OS */}
                    <CrudTable
                        data={formData.itens}
                        columns={[{key: 'produto.nome', label: 'Produto'}, {key: 'quantidade', label: 'Qtd.'}]}
                    />
                    
                    {/* Botões de Ação */}
                    <div className="d-flex gap-2 mt-4">
                        <button type="submit" className="btn btn-primary" disabled={loading}>
                            {loading ? '⏳ Criando...' : '💾 Criar OS'}
                        </button>
                        <button type="button" className="btn btn-secondary" onClick={onCancel}>Cancelar</button>
                    </div>
                </form>
            </div>
        </div>
    );
};


const OrdemServicoPage = () => {
    const navigate = useNavigate();
    const { data: ordens, loading, error, onSearch, refetch } = useSearch(ordemServicoService);

    const [editing, setEditing] = useState(false);
    const [currentOrdem, setCurrentOrdem] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [onibusList, setOnibusList] = useState([]);
    const [produtosList, setProdutosList] = useState([]);
    const [stats, setStats] = useState([]);
    const [showDetalhes, setShowDetalhes] = useState(false);
    
    useEffect(() => {
        const loadOnibusAndStats = async () => {
            try {
                const onibusResponse = await onibusService.getAll();
                setOnibusList(onibusResponse.data);
                
                const produtosResponse = await produtoService.getAll();
                setProdutosList(produtosResponse.data);

                const statsResponse = await ordemServicoService.buscarEstatisticasPorStatus();
                setStats(statsResponse.data);
            } catch (err) {
                console.error("Erro ao carregar dados:", err);
            }
        };
        loadOnibusAndStats();
    }, []);

    const columns = [
        { key: 'numeroOS', label: 'Número OS' },
        { key: 'onibus.numeroFrota', label: 'Frota' },
        { key: 'tipo', label: 'Tipo' },
        { key: 'status', label: 'Status' },
        { key: 'dataAbertura', label: 'Data Abertura' },
    ];
    
    const searchFields = [
        { name: 'numeroOS', label: 'Buscar por Número OS', type: 'text' },
        { 
            name: 'status', 
            label: 'Buscar por Status', 
            type: 'select', 
            options: Object.values(StatusOrdemServico).map(s => ({ value: s, label: s }))
        },
        { 
            name: 'tipo', 
            label: 'Buscar por Tipo', 
            type: 'select', 
            options: Object.values(TipoOrdemServico).map(t => ({ value: t, label: t }))
        }
    ];

    const formFields = [
        { 
            name: 'onibusId', 
            label: 'Ônibus', 
            type: 'select',
            options: onibusList.map(o => ({ value: o.id, label: `${o.numeroFrota} - ${o.modelo}` })),
            required: true
        },
        { 
            name: 'tipo', 
            label: 'Tipo', 
            type: 'select',
            options: Object.values(TipoOrdemServico).map(t => ({ value: t, label: t })),
            required: true 
        },
        { name: 'descricao', label: 'Descrição', type: 'textarea' },
    ];

    const handleCreate = () => {
        setCurrentOrdem({ tipo: 'PREVENTIVA', onibusId: onibusList[0]?.id });
        setEditing(false);
        setShowForm(true);
    };

    const handleEdit = (ordem) => {
        setCurrentOrdem({
            ...ordem,
            onibusId: ordem.onibus?.id || '' 
        });
        setEditing(true);
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Tem certeza que deseja excluir esta ordem de serviço?')) {
            try {
                await ordemServicoService.delete(id);
                refetch();
            } catch (err) {
                console.error("Erro ao deletar:", err);
            }
        }
    };

    const handleView = (ordem) => {
        setCurrentOrdem(ordem);
        setShowDetalhes(true);
    };

    const handleAction = async (action, id) => {
        try {
            switch (action) {
                case 'iniciar':
                    await ordemServicoService.iniciarExecucao(id);
                    break;
                case 'finalizar':
                    await ordemServicoService.finalizar(id);
                    break;
                case 'cancelar':
                    if (window.confirm("Deseja realmente cancelar?")) {
                      await ordemServicoService.cancelar(id);
                    }
                    break;
                case 'excluir':
                    if (window.confirm("Deseja realmente excluir?")) {
                      await ordemServicoService.excluir(id);
                    }
                    break;
                default:
                    break;
            }
            refetch();
        } catch (err) {
            console.error("Erro na ação:", err);
        }
    };
    
    const handleSubmit = async (formData) => {
        try {
            const payload = {
                ...formData,
                onibus: { id: formData.onibusId }
            };

            if (editing) {
                await ordemServicoService.update(currentOrdem.id, payload);
            } else {
                await ordemServicoService.create(payload);
            }
            
            setShowForm(false);
            refetch();
        } catch (err) {
            console.error("Erro ao salvar:", err);
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setCurrentOrdem(null);
    };

    return (
        <div>
            <h2>🔧 Gerenciamento de Ordens de Serviço</h2>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <button className="btn btn-primary" onClick={handleCreate} disabled={loading}>
                    {loading ? '⏳' : '➕'} Nova OS
                </button>
                <BackButton />
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            {showForm ? (
                <CrudForm
                    formData={currentOrdem}
                    fields={formFields}
                    onSubmit={handleSubmit}
                    onCancel={handleCancel}
                    title={editing ? '✏️ Editar OS' : '➕ Nova OS'}
                    loading={loading}
                />
            ) : (
                <>
                    <SearchBar fields={searchFields} onSearch={onSearch} />
                    
                    {stats.length > 0 && (
                        <div className="card mb-4">
                            <div className="card-header">📊 Estatísticas por Status</div>
                            <ul className="list-group list-group-flush">
                                {stats.map(([status, count]) => (
                                    <li key={status} className="list-group-item">
                                        <strong>{status}:</strong> {count}
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}

                    <div className="mb-3">
                        <button className="btn btn-success" onClick={refetch} disabled={loading}>
                            {loading ? '⏳ Carregando...' : '🔄 Atualizar Lista'}
                        </button>
                    </div>
                    
                    {loading && <div className="text-center">⏳ Carregando ordens de serviço...</div>}
                    
                    {!loading && ordens.length === 0 ? (
                        <div className="alert alert-info">
                            📝 Nenhuma ordem de serviço encontrada.
                        </div>
                    ) : (
                        <CrudTable
                            data={ordens}
                            columns={columns}
                            onEdit={handleEdit}
                            onDelete={handleDelete}
                            onView={handleView}
                        />
                    )}
                </>
            )}
        </div>
    );
};

export default OrdemServicoPage;