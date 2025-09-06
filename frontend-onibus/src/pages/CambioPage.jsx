import React, { useState, useCallback } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import BackButton from '../components/BackButton';
import CambioDetailView from '../components/CambioDetailView';
import AutocompleteInput from '../components/AutocompleteInput';
import { cambioService } from '../services/cambioService';
import { onibusService } from '../services/onibusService';
import useSearch from '../hooks/useSearch';
import { TipoCambio, StatusCambio } from '../constants/cambioEnums';

const CambioPage = () => {
  const { data: cambios, loading, error, onSearch, refetch } = useSearch(cambioService);

  const [view, setView] = useState('LIST');
  const [currentCambio, setCurrentCambio] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [searchTerms, setSearchTerms] = useState({});

  // --- Lógica do Autocomplete ---
  const handleAutocompleteSearch = useCallback(async (searchField, term) => {
    // Se a busca for por ônibus, usamos o onibusService
    if (searchField === 'onibus') {
      const response = await onibusService.search({ numeroFrota: term, comCambio: true });
      return response.data.map(o => ({ id: o.id, display: `${o.numeroFrota} - ${o.placa}` }));
    }
    // Para outros campos, usa o cambioService
    const response = await cambioService.search({ [searchField]: term });
    return response.data;
  }, []);

  const handleAutocompleteSelect = (item, fieldName) => {
    if (fieldName === 'onibus') {
      setSearchTerms(prev => ({ ...prev, onibusId: item.id, onibusDisplay: item.display }));
    } else {
      setSearchTerms({
        modelo: item.modelo,
        marca: item.marca,
        numeroSerie: item.numeroSerie,
      });
    }
  };

  const handleSearchChange = (e) => {
    const {name, value} = e.target;
    setSearchTerms(prev => ({ ...prev, [name]: value }))
  };

  // --- Handlers de Ações ---
  const handleCreate = () => {
    setCurrentCambio({ dataCompra: new Date().toISOString().slice(0, 10) });
    setIsEditing(false);
    setView('FORM');
  };
  const handleEdit = (cambio) => {
    cambioService.getById(cambio.id).then(response => {
        setCurrentCambio(response.data);
        setIsEditing(true);
        setView('FORM');
    });
  };
  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este câmbio? Esta ação não pode ser desfeita.')) {
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
    handleReturnToList();
  };
  const handleView = (cambio) => {
    cambioService.getById(cambio.id).then(response => {
      setCurrentCambio(response.data);
      setView('DETAIL');
    });
  };
  const handleReturnToList = () => {
    setView('LIST');
    setCurrentCambio(null);
    refetch();
  };
  
  // --- Definições de Colunas e Campos ---
  const columns = [
    { key: 'marca', label: 'Marca' },
    { key: 'modelo', label: 'Modelo' },
    { key: 'numeroSerie', label: 'Nº Série' },
    { key: 'status', label: 'Status' },
    { key: 'numeroMarchas', label: 'Marchas' },
    { key: 'onibus', label: 'Ônibus (Frota)', format: (onibus) => onibus ? onibus.numeroFrota : 'N/A' }
  ];

  const formFields = [
    { name: 'marca', label: 'Marca', type: 'text', required: true },
    { name: 'modelo', label: 'Modelo', type: 'text', required: true },
    { name: 'numeroSerie', label: 'Número de Série', type: 'text', required: true },
    { name: 'codigoFabricacao', label: 'Código Fabricação', type: 'text', required: true },
    { name: 'tipo', label: 'Tipo', type: 'select', options: Object.values(TipoCambio).map(t => ({ value: t, label: t })), required: true },
    { name: 'numeroMarchas', label: 'Nº de Marchas', type: 'number', required: true },
    { name: 'anoFabricacao', label: 'Ano Fabricação', type: 'number', required: true },
    { name: 'dataCompra', label: 'Data de Compra', type: 'date', required: true },
    { name: 'periodoGarantiaMeses', label: 'Garantia (meses)', type: 'number', defaultValue: 24, required: true },
    { name: 'tipoFluido', label: 'Tipo de Fluido', type: 'text', required: true },
    { name: 'capacidadeFluido', label: 'Capacidade Fluido (L)', type: 'number', step: '0.1', required: true },
  ];

  const renderListView = () => (
    <>
      <div className="card my-4">
        <div className="card-header"><h5 className="mb-0">🔍 Busca Avançada de Câmbios</h5></div>
        <div className="card-body">
            <div className="row g-3">
                 <div className="col-md-3">
                    <AutocompleteInput label="Modelo" name="modelo" value={searchTerms.modelo || ''} onChange={handleSearchChange} onSearch={(term) => handleAutocompleteSearch('modelo', term)} onItemSelected={handleAutocompleteSelect} displayField="modelo"/>
                 </div>
                 <div className="col-md-3">
                    <AutocompleteInput label="Nº de Série" name="numeroSerie" value={searchTerms.numeroSerie || ''} onChange={handleSearchChange} onSearch={(term) => handleAutocompleteSearch('numeroSerie', term)} onItemSelected={handleAutocompleteSelect} displayField="numeroSerie"/>
                 </div>
                 <div className="col-md-3">
                    <AutocompleteInput label="Ônibus (Frota)" name="onibusDisplay" value={searchTerms.onibusDisplay || ''} onChange={(e) => handleSearchChange({target: {name: 'onibusDisplay', value: e.target.value}})} onSearch={(term) => handleAutocompleteSearch('onibus', term)} onItemSelected={(item) => handleAutocompleteSelect(item, 'onibus')} displayField="display"/>
                 </div>
                 <div className="col-md-3">
                    <label className="form-label">Status</label>
                    <select className="form-select" name="status" value={searchTerms.status || ''} onChange={handleSearchChange}>
                        <option value="">Todos</option>
                        {Object.values(StatusCambio).map(s => <option key={s} value={s}>{s}</option>)}
                    </select>
                 </div>
            </div>
            <div className="mt-3 d-flex justify-content-end">
                 <button className="btn btn-primary" onClick={() => onSearch(searchTerms)}>Buscar</button>
            </div>
        </div>
      </div>

      <div className="d-flex justify-content-between align-items-center mb-2">
        <span className="text-muted">{cambios.length} câmbios encontrados.</span>
        <button className="btn btn-sm btn-outline-secondary" onClick={() => onSearch(searchTerms)}>🔄 Atualizar</button>
      </div>
      <CrudTable data={cambios} columns={columns} onEdit={handleEdit} onDelete={handleDelete} onView={handleView} />
    </>
  );

  const renderContent = () => {
    if (loading && !currentCambio) return <p className="text-center mt-5">Carregando...</p>;
    if (error) return <div className="alert alert-danger">{error}</div>;

    switch(view) {
        case 'FORM':
            return <CrudForm initialData={currentCambio} fields={formFields} onSubmit={handleSubmit} onCancel={handleReturnToList} title={isEditing ? 'Editar Câmbio' : 'Novo Câmbio'} />;
        case 'DETAIL':
            return <CambioDetailView cambio={currentCambio} onReturn={handleReturnToList} onUpdate={refetch} />;
        default:
            return renderListView();
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

