import React, { useState, useCallback } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import BackButton from '../components/BackButton';
import PneuDetailView from '../components/PneuDetailView';
import AutocompleteInput from '../components/AutocompleteInput';
import { pneuService } from '../services/pneuService';
import { onibusService } from '../services/onibusService';
import useSearch from '../hooks/useSearch';
import { StatusPneu } from '../constants/pneuEnums';

const PneuPage = () => {
  const { data: pneus, loading, error, onSearch, refetch } = useSearch(pneuService);

  const [view, setView] = useState('LIST');
  const [currentPneu, setCurrentPneu] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [searchTerms, setSearchTerms] = useState({});

  // --- Lógica do Autocomplete ---
  const handleAutocompleteSearch = useCallback(async (searchField, term) => {
    // Se a busca for por ônibus, usamos o onibusService
    if (searchField === 'onibus') {
      const response = await onibusService.search({ numeroFrota: term, comPneu: true }); // Adapte o DTO de busca se necessário
      return response.data.map(o => ({ id: o.id, display: `${o.numeroFrota} - ${o.placa}` }));
    }
    // Para outros campos, usa o pneuService
    const response = await pneuService.search({ [searchField]: term });
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
    setCurrentPneu({ dataCompra: new Date().toISOString().slice(0, 10) });
    setIsEditing(false);
    setView('FORM');
  };
  const handleEdit = (pneu) => {
    pneuService.getById(pneu.id).then(response => {
        setCurrentPneu(response.data);
        setIsEditing(true);
        setView('FORM');
    });
  };
  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir este pneu? Esta ação não pode ser desfeita.')) {
      await pneuService.delete(id);
      refetch();
    }
  };
  const handleSubmit = async (formData) => {
    if (isEditing) {
      await pneuService.update(currentPneu.id, formData);
    } else {
      await pneuService.create(formData);
    }
    handleReturnToList();
  };
  const handleView = (pneu) => {
    pneuService.getById(pneu.id).then(response => {
      setCurrentPneu(response.data);
      setView('DETAIL');
    });
  };
  const handleReturnToList = () => {
    setView('LIST');
    setCurrentPneu(null);
    refetch();
  };
  
  // --- Definições de Colunas e Campos ---
  const columns = [
    { key: 'marca', label: 'Marca' },
    { key: 'modelo', label: 'Modelo' },
    { key: 'numeroSerie', label: 'Nº Série' },
    { key: 'status', label: 'Status' },
    { key: 'kmRodados', label: 'KM Rodados', format: (km) => km ? km.toFixed(1) : '0.0' },
    { key: 'posicao', label: 'Posição' },
    { key: 'onibus', label: 'Ônibus (Frota)', format: (onibus) => onibus ? onibus.numeroFrota : 'N/A' }
  ];

  const formFields = [
    { name: 'marca', label: 'Marca', type: 'text', required: true },
    { name: 'modelo', label: 'Modelo', type: 'text', required: true },
    { name: 'medida', label: 'Medida (Ex: 295/80R22.5)', type: 'text', required: true },
    { name: 'numeroSerie', label: 'Número de Série', type: 'text', required: true },
    { name: 'codigoFabricacao', label: 'Código Fabricação', type: 'text', required: true },
    { name: 'anoFabricacao', label: 'Ano Fabricação', type: 'number', required: true },
    { name: 'dataCompra', label: 'Data de Compra', type: 'date', required: true },
    { name: 'periodoGarantiaMeses', label: 'Garantia (meses)', type: 'number', defaultValue: 12, required: true },
  ];

  const renderListView = () => (
    <>
      <div className="card my-4">
        <div className="card-header"><h5 className="mb-0">🔍 Busca Avançada de Pneus</h5></div>
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
                        {Object.values(StatusPneu).map(s => <option key={s} value={s}>{s}</option>)}
                    </select>
                 </div>
            </div>
            <div className="mt-3 d-flex justify-content-end">
                 <button className="btn btn-primary" onClick={() => onSearch(searchTerms)}>Buscar</button>
            </div>
        </div>
      </div>

      <div className="d-flex justify-content-between align-items-center mb-2">
        <span className="text-muted">{pneus.length} pneus encontrados.</span>
        <button className="btn btn-sm btn-outline-secondary" onClick={() => onSearch(searchTerms)}>🔄 Atualizar</button>
      </div>
      <CrudTable data={pneus} columns={columns} onEdit={handleEdit} onDelete={handleDelete} onView={handleView} />
    </>
  );

  const renderContent = () => {
    if (loading && !currentPneu) return <p className="text-center mt-5">Carregando...</p>;
    if (error) return <div className="alert alert-danger">{error}</div>;

    switch(view) {
        case 'FORM':
            return <CrudForm initialData={currentPneu} fields={formFields} onSubmit={handleSubmit} onCancel={handleReturnToList} title={isEditing ? 'Editar Pneu' : 'Novo Pneu'} />;
        case 'DETAIL':
            return <PneuDetailView pneu={currentPneu} onReturn={handleReturnToList} onUpdate={refetch} />;
        default:
            return renderListView();
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>🛞 Gerenciamento de Pneus</h2>
        {view === 'LIST' && (
          <div className="btn-group">
            <button className="btn btn-primary" onClick={handleCreate}>➕ Novo Pneu</button>
            <BackButton to="/" />
          </div>
        )}
      </div>
      {renderContent()}
    </div>
  );
};

export default PneuPage;

