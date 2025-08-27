// src/pages/OrdemServicoPage.js
import React, { useState, useEffect } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import SearchBar from '../components/SearchBar';
import BackButton from '../components/BackButton';
import { ordemServicoService } from '../services/ordemServicoService';
import { onibusService } from '../services/onibusService';
import useSearch from '../hooks/useSearch';
import { StatusOrdemServico, TipoOrdemServico } from '../constants/ordemServicoEnums';
import { useNavigate } from 'react-router-dom';

const OrdemServicoPage = () => {
    const navigate = useNavigate();
    const { data: ordens, loading, error, onSearch, refetch } = useSearch(ordemServicoService, {
        numeroOS: ordemServicoService.getByNumeroOS,
        status: ordemServicoService.getByStatus,
        tipo: ordemServicoService.getByTipo
    });

    const [editing, setEditing] = useState(false);
    const [currentOrdem, setCurrentOrdem] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [onibusList, setOnibusList] = useState([]);
    const [stats, setStats] = useState([]);
    const [showDetalhes, setShowDetalhes] = useState(false);

    useEffect(() => {
        const loadOnibusAndStats = async () => {
            try {
                const onibusResponse = await onibusService.getAll();
                setOnibusList(onibusResponse.data);
                
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
                await ordemServicoService.excluir(id);
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