// src/pages/OnibusPage.js
import React, { useState, useEffect } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import SearchBar from '../components/SearchBar';
import BackButton from '../components/BackButton'; // ✅ Importação corrigida
import { onibusService } from '../services/onibusService';
import { motorService } from '../services/motorService';
import { cambioService } from '../services/cambioService';
import { pneuService } from '../services/pneuService';
import useSearch from '../hooks/useSearch';
import { StatusOnibus } from '../constants/onibusEnums';
import { PosicaoPneu } from '../constants/pneuEnums';

const OnibusPage = () => {
    const { data: onibusList, loading, error, onSearch, refetch } = useSearch(onibusService, {
        chassi: onibusService.getByChassi,
        numeroFrota: onibusService.getByNumeroFrota,
        modelo: onibusService.getByModelo,
        marca: onibusService.getByMarca,
        status: onibusService.getByStatus,
    });
    
    const [editing, setEditing] = useState(false);
    const [currentOnibus, setCurrentOnibus] = useState(null);
    const [showForm, setShowForm] = useState(false);

    // Estados para os dados dos componentes (para os selects)
    const [motoresDisponiveis, setMotoresDisponiveis] = useState([]);
    const [cambiosDisponiveis, setCambiosDisponiveis] = useState([]);
    const [pneusDisponiveis, setPneusDisponiveis] = useState([]);

    useEffect(() => {
        // ✅ Carrega as listas de componentes disponíveis
        const loadComponentes = async () => {
            try {
                const motores = await motorService.getDisponiveis();
                setMotoresDisponiveis(motores.data);
                const cambios = await cambioService.getDisponiveis();
                setCambiosDisponiveis(cambios.data);
                const pneus = await pneuService.getDisponiveis();
                setPneusDisponiveis(pneus.data);
            } catch (err) {
                console.error("Erro ao carregar componentes disponíveis:", err);
            }
        };
        loadComponentes();
    }, [showForm]);

    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'chassi', label: 'Chassi' },
        { key: 'modelo', label: 'Modelo' },
        { key: 'marca', label: 'Marca' },
        { key: 'numeroFrota', label: 'Número Frota' },
        { key: 'status', label: 'Status' },
        { key: 'motor.modelo', label: 'Motor' },
        { key: 'cambio.modelo', label: 'Câmbio' },
    ];
    
    const searchFields = [
        { name: 'chassi', label: 'Buscar por Chassi', type: 'text' },
        { name: 'numeroFrota', label: 'Buscar por Número de Frota', type: 'text' },
        { name: 'modelo', label: 'Buscar por Modelo', type: 'text' },
        { name: 'marca', label: 'Buscar por Marca', type: 'text' },
        { name: 'status', label: 'Buscar por Status', type: 'select', 
          options: Object.values(StatusOnibus).map(s => ({ value: s, label: s })) }
    ];

    const formFields = [
        { name: 'chassi', label: 'Chassi', type: 'text', required: true },
        { name: 'modelo', label: 'Modelo', type: 'text', required: true },
        { name: 'marca', label: 'Marca', type: 'text', required: true },
        { name: 'codigoFabricacao', label: 'Código Fabricação', type: 'text', required: true },
        { name: 'capacidade', label: 'Capacidade', type: 'number', required: true },
        { name: 'anoFabricacao', label: 'Ano Fabricação', type: 'number', required: true },
        { name: 'numeroFrota', label: 'Número Frota', type: 'text', required: true },
        { name: 'dataUltimaReforma', label: 'Data Última Reforma', type: 'date' },
        { name: 'status', label: 'Status', type: 'select', required: true,
          options: Object.values(StatusOnibus).map(s => ({ value: s, label: s })) }
    ];
    
    const handleCreate = () => {
        setCurrentOnibus({
            status: 'NOVO'
        });
        setEditing(false);
        setShowForm(true);
    };

    const handleEdit = (onibus) => {
        setCurrentOnibus(onibus);
        setEditing(true);
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Tem certeza que deseja excluir este ônibus?')) {
            try {
                await onibusService.delete(id);
                refetch();
            } catch (err) {
                console.error("Erro ao deletar:", err);
            }
        }
    };
    
    const handleSubmit = async (formData) => {
        try {
            if (editing) {
                await onibusService.update(currentOnibus.id, formData);
            } else {
                await onibusService.create(formData);
            }
            setShowForm(false);
            refetch();
        } catch (err) {
            console.error("Erro ao salvar:", err);
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setCurrentOnibus(null);
    };

    const handleInputChange = (e) => {
        const { name, value, type } = e.target;
        let processedValue = value;
        if (type === 'number') {
            processedValue = value === '' ? null : Number(value);
        } else if (type === 'date') {
            processedValue = value || null;
        }
        setCurrentOnibus(prev => ({
            ...prev,
            [name]: processedValue
        }));
    };
    
    const handleInstalarMotor = async (onibusId, motorId) => {
      try {
        await onibusService.instalarMotor(onibusId, motorId);
        refetch();
      } catch (err) {
        console.error("Erro ao instalar motor:", err);
      }
    };

    const handleRemoverMotor = async (onibusId, motorId) => {
        if (!window.confirm("Deseja realmente remover este motor?")) return;
        try {
            await onibusService.removerMotor(onibusId, motorId);
            refetch();
        } catch (err) {
            console.error("Erro ao remover motor:", err);
        }
    };

    const handleInstalarCambio = async (onibusId, cambioId) => {
      try {
        await onibusService.instalarCambio(onibusId, cambioId);
        refetch();
      } catch (err) {
        console.error("Erro ao instalar cambio:", err);
      }
    };

    const handleRemoverCambio = async (onibusId, cambioId) => {
        if (!window.confirm("Deseja realmente remover este cambio?")) return;
        try {
            await onibusService.removerCambio(onibusId, cambioId);
            refetch();
        } catch (err) {
            console.error("Erro ao remover cambio:", err);
        }
    };

    const handleInstalarPneu = async (onibusId, pneuId, posicao) => {
      try {
        await onibusService.instalarPneu(onibusId, pneuId, posicao);
        refetch();
      } catch (err) {
        console.error("Erro ao instalar pneu:", err);
      }
    };

    const handleRemoverPneu = async (onibusId, pneuId) => {
        if (!window.confirm("Deseja realmente remover este pneu?")) return;
        try {
            await onibusService.removerPneu(onibusId, pneuId);
            refetch();
        } catch (err) {
            console.error("Erro ao remover pneu:", err);
        }
    };

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>🚌 Gerenciamento de Ônibus</h2>
                <div className="btn-group">
                    <button className="btn btn-primary" onClick={handleCreate} disabled={loading}>
                        {loading ? '⏳' : '➕'} Novo Ônibus
                    </button>
                    <BackButton /> {/* ✅ Posição do botão de voltar */}
                </div>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            {showForm ? (
                <CrudForm
                    formData={currentOnibus}
                    fields={formFields}
                    onSubmit={handleSubmit}
                    onCancel={handleCancel}
                    onChange={handleInputChange}
                    title={editing ? '✏️ Editar Ônibus' : '➕ Novo Ônibus'}
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
                    
                    {loading && <div className="text-center">⏳ Carregando ônibus...</div>}
                    
                    {!loading && onibusList.length === 0 ? (
                        <div className="alert alert-info">
                            📝 Nenhum ônibus encontrado.
                        </div>
                    ) : (
                        <CrudTable
                            data={onibusList}
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

export default OnibusPage;