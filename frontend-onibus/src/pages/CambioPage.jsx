import React, { useState, useCallback } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import BackButton from '../components/BackButton';
import CambioDetailView from '../components/CambioDetailView';
import AutocompleteInput from '../components/AutocompleteInput';
import { cambioService } from '../services/cambioService';
import useSearch from '../hooks/useSearch';
import { TipoCambio, StatusCambio } from '../constants/cambioEnums';

const CambioPage = () => {
  const { data: cambios, loading, error, onSearch, refetch } = useSearch(cambioService);

  const [view, setView] = useState('LIST');
  const [currentCambio, setCurrentCambio] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [searchTerms, setSearchTerms] = useState({});

  // --- Lógica do Autocomplete ---
  const handleAutocompleteSearch = useCallback(async (term) => {
    // Busca rápida por modelo para as sugestões
    const response = await cambioService.search({ modelo: term });
    return response.data;
  }, []);

  const handleAutocompleteSelect = (cambio) => {
    // Preenche os campos de busca quando um item é selecionado
    setSearchTerms({
        modelo: cambio.modelo,
        marca: cambio.marca,
        status: cambio.status,
    });
  };

  // --- Handlers de Ações ---
  const handleCreate = () => {
      setCurrentCambio({ dataCompra: new Date().toISOString().slice(0, 10) });
      setIsEditing(false);
      setView('FORM');
  };

  const handleEdit = (cambio) => {
      setCurrentCambio(cambio);
      setIsEditing(true);
      setView('FORM');
  };

  const handleView = (cambio) => {
      setCurrentCambio(cambio);
      setView('DETAIL');
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza?')) {
      await cambioService.delete(id);
      refetch();
    }
  };

  const handleSubmit = async (formData) => {
    if (isEditing) {
      await cambioService.update(currentCambio.id, formData);
    } else {
      await cambioService.create(formData);
    }
    setView('LIST');
    refetch();
  };
 
  const handleReturnToList = () => {
      setView('LIST');
      setCurrentCambio(null);
      refetch();
  }

  // --- Definições ---
  const columns = [
    { key: 'id', label: 'ID' },
    { key: 'marca', label: 'Marca' },
    { key: 'modelo', label: 'Modelo' },
    { key: 'status', label: 'Status' },
    { 
      key: 'onibus', 
      label: 'Ônibus Instalado',
      format: (onibus) => {
        if (!onibus) return 'N/A';
        return `${onibus.modelo} - ${onibus.placa} (Frota: ${onibus.numeroFrota})`;
      }
    }
  ];

  const formFields = [
    { name: 'marca', label: 'Marca', type: 'text', required: true },
    { name: 'modelo', label: 'Modelo', type: 'text', required: true },
    { name: 'numeroSerie', label: 'Número de Série', type: 'text', required: true },
    { name: 'codigoFabricacao', label: 'Código Fabricação', type: 'text', required: true },
    { name: 'tipo', label: 'Tipo', type: 'select', options: Object.values(TipoCambio).map(t => ({ value: t, label: t })), required: true },
    { name: 'numeroMarchas', label: 'Número de Marchas', type: 'number', required: true },
    { name: 'anoFabricacao', label: 'Ano Fabricação', type: 'number', required: true },
    { name: 'dataCompra', label: 'Data de Compra', type: 'date', required: true },
    { name: 'periodoGarantiaMeses', label: 'Garantia (meses)', type: 'number', defaultValue: 24, required: true },
    { name: 'tipoFluido', label: 'Tipo de Fluido', type: 'text', required: true },
    { name: 'capacidadeFluido', label: 'Capacidade Fluido (L)', type: 'number', step: '0.1', required: true },
  ];

  const renderContent = () => {
      if (loading) return <p>Carregando...</p>;
      if (error) return <div className="alert alert-danger">{error}</div>;

      switch(view) {
          case 'FORM':
              return <CrudForm initialData={currentCambio} fields={formFields} onSubmit={handleSubmit} onCancel={handleReturnToList} title={isEditing ? 'Editar Câmbio' : 'Novo Câmbio'} />;
          case 'DETAIL':
              return <CambioDetailView cambio={currentCambio} onReturn={handleReturnToList} onUpdate={refetch} />;
          default:
              return (
                  <>
                      <div className="card my-4">
                          <div className="card-header"><h5 className="mb-0">🔍 Busca Inteligente de Câmbios</h5></div>
                          <div className="card-body">
                              <div className="row g-3 align-items-end">
                                  <div className="col-md-6">
                                      <AutocompleteInput
                                          label="Buscar por Modelo"
                                          value={searchTerms.modelo || ''}
                                          onChange={(value) => setSearchTerms(prev => ({ ...prev, modelo: value }))}
                                          onSearch={handleAutocompleteSearch}
                                          onItemSelected={handleAutocompleteSelect}
                                          displayField="modelo"
                                      />
                                  </div>
                                  <div className="col-md-4">
                                      <label className="form-label">Filtrar por Status</label>
                                      <select className="form-select" name="status" value={searchTerms.status || ''} onChange={(e) => setSearchTerms(prev => ({ ...prev, status: e.target.value }))}>
                                          <option value="">Todos</option>
                                          {Object.values(StatusCambio).map(s => <option key={s} value={s}>{s}</option>)}
                                      </select>
                                  </div>
                                  <div className="col-md-2">
                                      <button className="btn btn-primary w-100" onClick={() => onSearch(searchTerms)}>Buscar</button>
                                  </div>
                              </div>
                          </div>
                      </div>
                      <CrudTable data={cambios} columns={columns} onEdit={handleEdit} onDelete={handleDelete} onView={handleView} />
                  </>
              );
      }
  };

  return (
      <div>
          <div className="d-flex justify-content-between align-items-center mb-3">
              <h2>Gerenciamento de Câmbios</h2>
              {view === 'LIST' && (
                  <div className="btn-group">
                      <button className="btn btn-primary" onClick={handleCreate}>➕ Novo Câmbio</button>
                      <BackButton to="/" />
                  </div>
              )}
          </div>
          {renderContent()}
      </div>
  );
};

export default CambioPage;
