import React, { useState } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import SearchBar from '../components/SearchBar';
import BackButton from '../components/BackButton'; // ✅ Importando o botão de voltar
import { pneuService } from '../services/pneuService';
import useSearch from '../hooks/useSearch';
import { StatusPneu, PosicaoPneu } from '../constants/pneuEnums';

const PneuPage = () => {
    // ✅ Usando o hook de busca para gerenciar o estado dos pneus
    const { data: pneus, loading, error, onSearch, refetch } = useSearch(pneuService, {
        // Mapeamento dos campos de busca para os métodos do service
        status: pneuService.getByStatus,
        marca: pneuService.getByMarca,
        medida: pneuService.getByMedida,
        numeroSerie: pneuService.getByNumeroSerie,
        codigoFabricacao: pneuService.getByCodigoFabricacao
    });
    
    const [editing, setEditing] = useState(false);
    const [currentPneu, setCurrentPneu] = useState(null);
    const [showForm, setShowForm] = useState(false);

    // ✅ Colunas para a tabela
    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'marca', label: 'Marca' },
        { key: 'medida', label: 'Medida' },
        { key: 'numeroSerie', label: 'Número Série' },
        { key: 'status', label: 'Status' },
        { key: 'posicao', label: 'Posição' }
    ];

    // ✅ Campos de busca para o SearchBar
    const searchFields = [
        { name: 'marca', label: 'Buscar por Marca', type: 'text' },
        { name: 'medida', label: 'Buscar por Medida', type: 'text' },
        { name: 'numeroSerie', label: 'Buscar por Número de Série', type: 'text' },
        { 
            name: 'status', 
            label: 'Buscar por Status', 
            type: 'select', 
            options: Object.values(StatusPneu).map(s => ({ value: s, label: s }))
        },
        { 
            name: 'posicao', 
            label: 'Buscar por Posição', 
            type: 'select', 
            options: Object.values(PosicaoPneu).map(p => ({ value: p, label: p }))
        }
    ];

    // ✅ Campos do formulário com selects para enums
    const formFields = [
        { name: 'marca', label: 'Marca', type: 'text', required: true },
        { name: 'medida', label: 'Medida', type: 'text', required: true },
        { name: 'codigoFabricacao', label: 'Código Fabricação', type: 'text', required: true },
        { name: 'anoFabricacao', label: 'Ano Fabricação', type: 'number', required: true },
        { name: 'numeroSerie', label: 'Número Série', type: 'text', required: true },
        { name: 'dataCompra', label: 'Data de Compra', type: 'date', required: true },
        { name: 'periodoGarantiaMeses', label: 'Período Garantia (meses)', type: 'number', required: true },
        { 
            name: 'status', 
            label: 'Status', 
            type: 'select',
            options: Object.values(StatusPneu).map(s => ({ value: s, label: s })),
            required: true 
        },
        { name: 'kmRodados', label: 'KM Rodados', type: 'number' },
        { name: 'dataInstalacao', label: 'Data Instalação', type: 'date' },
        { 
            name: 'posicao', 
            label: 'Posição', 
            type: 'select', 
            options: [{ value: '', label: 'N/A' }, ...Object.values(PosicaoPneu).map(p => ({ value: p, label: p }))]
        }
    ];

    const handleCreate = () => {
        setCurrentPneu({
            status: 'NOVO',
            periodoGarantiaMeses: 24,
            kmRodados: 0
        });
        setEditing(false);
        setShowForm(true);
    };

    const handleEdit = (pneu) => {
        setCurrentPneu(pneu);
        setEditing(true);
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Tem certeza que deseja excluir este pneu?')) {
            try {
                await pneuService.delete(id);
                refetch();
            } catch (err) {
                console.error('Erro ao deletar:', err);
            }
        }
    };

    const handleSubmit = async (formData) => {
        try {
            if (editing) {
                await pneuService.update(currentPneu.id, formData);
            } else {
                await pneuService.create(formData);
            }
            setShowForm(false);
            refetch();
        } catch (err) {
            console.error('Erro ao salvar:', err);
        }
    };
    
    const handleCancel = () => {
        setShowForm(false);
        setCurrentPneu(null);
    };

    const handleInputChange = (e) => {
        const { name, value, type } = e.target;
        let processedValue = value;
        if (type === 'number') {
            processedValue = value === '' ? null : Number(value);
        } else if (type === 'date') {
            processedValue = value || null;
        }
        setCurrentPneu(prev => ({
            ...prev,
            [name]: processedValue
        }));
    };

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>🛞 Gerenciamento de Pneus</h2>
                <div className="btn-group">
                    <button className="btn btn-primary" onClick={handleCreate} disabled={loading}>
                        {loading ? '⏳' : '➕'} Novo Pneu
                    </button>
                    <BackButton />
                </div>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            {showForm ? (
                <CrudForm
                    formData={currentPneu}
                    fields={formFields}
                    onSubmit={handleSubmit}
                    onCancel={handleCancel}
                    onChange={handleInputChange}
                    title={editing ? '✏️ Editar Pneu' : '➕ Novo Pneu'}
                    loading={loading}
                />
            ) : (
                <>
                    <SearchBar fields={searchFields} onSearch={onSearch} />
                    <div className="mb-3">
                        <button className="btn btn-success" onClick={refetch} disabled={loading}>
                            {loading ? '⏳ Carregando...' : '🔄 Atualizar Lista'}
                        </button>
                    </div>
                    
                    {loading && <div className="text-center">⏳ Carregando pneus...</div>}
                    
                    {!loading && pneus.length === 0 ? (
                        <div className="alert alert-info">
                            📝 Nenhum pneu encontrado.
                        </div>
                    ) : (
                        <CrudTable
                            data={pneus}
                            columns={columns}
                            onEdit={handleEdit}
                            onDelete={handleDelete}
                        />
                    )}
                </>
            )}
        </div>
    );
};

export default PneuPage;