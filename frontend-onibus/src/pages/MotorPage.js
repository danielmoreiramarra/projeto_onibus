import React, { useState } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import SearchBar from '../components/SearchBar';
import BackButton from '../components/BackButton';
import { motorService } from '../services/motorService';
import useSearch from '../hooks/useSearch';
import { TipoMotor, StatusMotor } from '../constants/motorEnums'; // Certifique-se de que esses enums existem

const MotorPage = () => {
    const { data: motores, loading, error, onSearch, refetch } = useSearch(motorService, {
        tipo: motorService.getByTipo,
        marca: motorService.getByMarca,
        status: motorService.getByStatus
    });

    const [editing, setEditing] = useState(false);
    const [currentMotor, setCurrentMotor] = useState(null);
    const [showForm, setShowForm] = useState(false);

    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'tipo', label: 'Tipo' },
        { key: 'marca', label: 'Marca' },
        { key: 'modelo', label: 'Modelo' },
        { key: 'status', label: 'Status' }
    ];

    const searchFields = [
        { name: 'tipo', label: 'Buscar por Tipo', type: 'select', options: Object.values(TipoMotor).map(t => ({ value: t, label: t })) },
        { name: 'marca', label: 'Buscar por Marca', type: 'text' },
        { name: 'status', label: 'Buscar por Status', type: 'select', options: Object.values(StatusMotor).map(s => ({ value: s, label: s })) }
    ];

    const formFields = [
        {
            name: 'tipo',
            label: 'Tipo',
            type: 'select',
            options: Object.values(TipoMotor).map(t => ({ value: t, label: t })),
            required: true
        },
        {
            name: 'status',
            label: 'Status',
            type: 'select',
            options: Object.values(StatusMotor).map(s => ({ value: s, label: s })),
            required: true
        },
        { name: 'marca', label: 'Marca', type: 'text', required: true },
        { name: 'modelo', label: 'Modelo', type: 'text', required: true },
        { name: 'potencia', label: 'Potência (HP)', type: 'number' },
        { name: 'cilindrada', label: 'Cilindrada (cc)', type: 'number' },
        { name: 'combustivel', label: 'Combustível', type: 'text' },
        { name: 'codigoFabricacao', label: 'Código Fabricação', type: 'text', required: true },
        { name: 'numeroSerie', label: 'Número de Série', type: 'text', required: true },
        { name: 'dataCompra', label: 'Data de Compra', type: 'date', required: true },
        { name: 'periodoGarantiaMeses', label: 'Período Garantia (meses)', type: 'number', required: true }
    ];

    const handleCreate = () => {
        setCurrentMotor({
            status: 'NOVO',
            tipo: 'DIESEL',
            periodoGarantiaMeses: 24,
            dataCompra: new Date().toISOString().slice(0, 10)
        });
        setEditing(false);
        setShowForm(true);
    };

    const handleEdit = (motor) => {
        setCurrentMotor(motor);
        setEditing(true);
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Tem certeza que deseja excluir este motor?')) {
            try {
                await motorService.delete(id);
                refetch();
            } catch (error) {
                console.error("Erro ao deletar:", error);
            }
        }
    };

    const handleSubmit = async (formData) => {
        try {
            if (editing) {
                await motorService.update(currentMotor.id, formData);
            } else {
                await motorService.create(formData);
            }
            setShowForm(false);
            refetch();
        } catch (error) {
            console.error("Erro ao salvar:", error);
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setCurrentMotor(null);
    };

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Gerenciamento de Motores</h2>
                <div className="btn-group">
                    <button className="btn btn-primary" onClick={handleCreate} disabled={loading}>
                        {loading ? '⏳' : '➕'} Novo Motor
                    </button>
                    {/* ✅ Adicionando o botão de voltar */}
                    <BackButton />
                </div>
            </div>

            {error && (
                <div className="alert alert-danger">
                    <strong>Erro:</strong> {error}
                </div>
            )}

            {showForm ? (
                <CrudForm
                    formData={currentMotor}
                    fields={formFields}
                    onSubmit={handleSubmit}
                    onCancel={handleCancel}
                    title={editing ? '✏️ Editar Motor' : '➕ Novo Motor'}
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

                    {loading && <div className="text-center">⏳ Carregando motores...</div>}
                    
                    {!loading && motores.length === 0 ? (
                        <div className="alert alert-info">
                            📝 Nenhum motor encontrado. Clique em "Novo Motor" para adicionar.
                        </div>
                    ) : (
                        <CrudTable
                            data={motores}
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

export default MotorPage;