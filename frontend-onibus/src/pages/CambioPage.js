// src/pages/CambioPage.js
import React, { useState } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import SearchBar from '../components/SearchBar';
import BackButton from '../components/BackButton';
import { cambioService } from '../services/cambioService';
import useSearch from '../hooks/useSearch';
import { TipoCambio, StatusCambio } from '../constants/cambioEnums';
import { useNavigate } from 'react-router-dom';

const CambioPage = () => {
  const { data: cambios, loading, error, onSearch, refetch } = useSearch(cambioService);
  const navigate = useNavigate();

  const [editing, setEditing] = useState(false);
  const [currentCambio, setCurrentCambio] = useState(null);
  const [showForm, setShowForm] = useState(false);

  // ✅ Colunas para a tabela (adicionando os novos atributos)
  const columns = [
    { key: 'id', label: 'ID' },
    { key: 'tipo', label: 'Tipo' },
    { key: 'numeroMarchas', label: 'Marchas' },
    { key: 'marca', label: 'Marca' },
    { key: 'modelo', label: 'Modelo' },
    { key: 'status', label: 'Status' },
    { key: 'onibus.id', label: 'Ônibus ID' },
    { key: 'tipoFluido', label: 'Tipo Fluido' },
    { key: 'quantidadeFluido', label: 'Qtd. Fluido' }
  ];

  // ✅ Campos de busca corrigidos para usar os enums diretamente
  const searchFields = [
    { name: 'tipo', label: 'Buscar por Tipo', type: 'select', options: [{ value: '', label: 'Todos' }, ...Object.values(TipoCambio).map(t => ({ value: t, label: t }))] },
    { name: 'marca', label: 'Buscar por Marca', type: 'text' },
    { name: 'modelo', label: 'Buscar por Modelo', type: 'text' },
    { name: 'status', label: 'Buscar por Status', type: 'select', options: [{ value: '', label: 'Todos' }, ...Object.values(StatusCambio).map(s => ({ value: s, label: s }))] },
    { name: 'numeroSerie', label: 'Número de Série', type: 'text' },
    { name: 'codigoFabricacao', label: 'Código de Fabricação', type: 'text' },
    { name: 'numeroMarchas', label: 'Número de Marchas', type: 'number' },
    { name: 'onibusId', label: 'ID do Ônibus', type: 'number' },
    { name: 'id', label: 'ID do Câmbio', type: 'number' },
  ];

  // ✅ Campos de formulário corrigidos para usar os enums diretamente
  const formFields = [
    { 
      name: 'tipo', 
      label: 'Tipo', 
      type: 'select',
      options: Object.values(TipoCambio).map(t => ({ value: t, label: t })),
      required: true 
    },
    { 
      name: 'status', 
      label: 'Status', 
      type: 'select',
      options: Object.values(StatusCambio).map(s => ({ value: s, label: s })),
      required: true 
    },
    { name: 'numeroMarchas', label: 'Número de Marchas', type: 'number', required: true },
    { name: 'marca', label: 'Marca', type: 'text', required: true },
    { name: 'modelo', label: 'Modelo', type: 'text', required: true },
    { name: 'anoFabricacao', label: 'Ano Fabricação', type: 'number', required: true },
    { name: 'dataCompra', label: 'Data de Compra', type: 'date', required: true },
    { name: 'periodoGarantiaMeses', label: 'Período Garantia (meses)', type: 'number', required: true },
    { name: 'tipoFluido', label: 'Tipo de Fluido', type: 'text' },
    { name: 'quantidadeFluido', label: 'Quantidade Fluido (L)', type: 'number', step: '0.1' },
    { name: 'codigoFabricacao', label: 'Código Fabricação', type: 'text', required: true },
    { name: 'numeroSerie', label: 'Número Série', type: 'text', required: true },
    { name: 'dataUltimaRevisao', label: 'Data Última Revisão', type: 'date' },
    { name: 'dataUltimaManutencao', label: 'Data Última Manutenção', type: 'date' }
  ];

  const handleCreate = () => {
    setCurrentCambio({
      status: 'NOVO',
      periodoGarantiaMeses: 24,
      tipo: 'MANUAL'
    });
    setEditing(false);
    setShowForm(true);
  };

  const handleEdit = (cambio) => {
    setCurrentCambio(cambio);
    setEditing(true);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este câmbio?')) {
      try {
        await cambioService.delete(id);
        refetch();
      } catch (error) {
        console.error("Erro ao deletar:", error);
      }
    }
  };

  const handleSubmit = async (formData) => {
    try {
      if (editing) {
        await cambioService.update(currentCambio.id, formData);
      } else {
        await cambioService.create(formData);
      }
      setShowForm(false);
      refetch();
    } catch (error) {
      console.error("Erro ao salvar:", error);
    }
  };

  const handleCancel = () => {
    setShowForm(false);
    setCurrentCambio(null);
  };
  
  const handleInputChange = (e) => {
    const { name, value, type } = e.target;
    let processedValue = value;
    if (type === 'number') {
      processedValue = value === '' ? null : Number(value);
    } else if (type === 'date') {
      processedValue = value || null;
    }
    setCurrentCambio(prev => ({
      ...prev,
      [name]: processedValue
    }));
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Gerenciamento de Câmbios</h2>
        <div className="btn-group">
          <button className="btn btn-primary" onClick={handleCreate} disabled={loading}>
            {loading ? '⏳' : '➕'} Novo Câmbio
          </button>
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
          formData={currentCambio}
          fields={formFields}
          onSubmit={handleSubmit}
          onCancel={handleCancel}
          onChange={handleInputChange}
          title={editing ? '✏️ Editar Câmbio' : '➕ Novo Câmbio'}
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
          
          {loading && <div className="text-center">⏳ Carregando câmbios...</div>}
          
          {!loading && cambios.length === 0 ? (
            <div className="alert alert-info">
              📝 Nenhum câmbio encontrado. Clique em "Novo Câmbio" para adicionar.
            </div>
          ) : (
            <CrudTable
              data={cambios}
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

export default CambioPage;