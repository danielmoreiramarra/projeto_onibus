import React, { useState, useCallback } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import BackButton from '../components/BackButton';
import PneuDetailView from '../components/PneuDetailView';
import AutocompleteInput from '../components/AutocompleteInput';
import { pneuService } from '../services/pneuService';
import useSearch from '../hooks/useSearch';
import { StatusPneu } from '../constants/pneuEnums';

const PneuPage = () => {
  const { data: pneus, loading, error, onSearch, refetch } = useSearch(pneuService);

  const [view, setView] = useState('LIST');
  const [currentPneu, setCurrentPneu] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [searchTerms, setSearchTerms] = useState({});

  // --- Lógica do Autocomplete ---
  const handleAutocompleteSearch = useCallback(async (term) => {
    // Busca rápida por número de série para as sugestões
    const response = await pneuService.search({ numeroSerie: term });
    return response.data;
  }, []);

  const handleAutocompleteSelect = (pneu) => {
    // Preenche os campos de busca quando um item é selecionado
    setSearchTerms({
        numeroSerie: pneu.numeroSerie,
        marca: pneu.marca,
        status: pneu.status,
    });
  };

  // --- Handlers de Ações ---
  const handleCreate = () => {
    setCurrentPneu({ dataCompra: new Date().toISOString().slice(0, 10) });
    setIsEditing(false);
    setView('FORM');
  };

  const handleEdit = (pneu) => {
    setCurrentPneu(pneu);
    setIsEditing(true);
    setView('FORM');
  };
  
  const handleView = (pneu) => {
    setCurrentPneu(pneu);
    setView('DETAIL');
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza?')) {
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
    setView('LIST');
    refetch();
  };
  
  const handleReturnToList = () => {
    setView('LIST');
    setCurrentPneu(null);
  }

  // --- Definições ---
  const columns = [
    { key: 'id', label: 'ID' },
    { key: 'marca', label: 'Marca' },
    { key: 'modelo', label: 'Modelo' },
    { key: 'status', label: 'Status' },
    { key: 'kmRodados', label: 'KM Rodados', format: (km) => km.toFixed(1) },
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
    { name: 'medida', label: 'Medida', type: 'text', required: true },
    { name: 'codigoFabricacao', label: 'Código Fabricação', type: 'text', required: true },
    { name: 'numeroSerie', label: 'Número de Série', type: 'text', required: true },
    { name: 'anoFabricacao', label: 'Ano Fabricação', type: 'number', required: true },
    { name: 'dataCompra', label: 'Data de Compra', type: 'date', required: true },
    { name: 'periodoGarantiaMeses', label: 'Garantia (meses)', type: 'number', defaultValue: 12, required: true },
  ];

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

      {loading && <p>Carregando...</p>}
      {error && <div className="alert alert-danger">{error}</div>}

      {!loading && !error && (
        <>
          {view === 'FORM' && (
            <CrudForm initialData={currentPneu} fields={formFields} onSubmit={handleSubmit} onCancel={handleReturnToList} title={isEditing ? 'Editar Pneu' : 'Novo Pneu'} />
          )}
          {view === 'DETAIL' && (
            <PneuDetailView pneu={currentPneu} onReturn={handleReturnToList} onUpdate={refetch} />
          )}
          {view === 'LIST' && (
            <>
              <div className="card my-4">
                  <div className="card-header"><h5 className="mb-0">🔍 Busca Inteligente de Pneus</h5></div>
                  <div className="card-body">
                      <div className="row g-3 align-items-end">
                          <div className="col-md-6">
                              <AutocompleteInput
                                  label="Buscar por Número de Série"
                                  value={searchTerms.numeroSerie || ''}
                                  onChange={(value) => setSearchTerms(prev => ({ ...prev, numeroSerie: value }))}
                                  onSearch={handleAutocompleteSearch}
                                  onItemSelected={handleAutocompleteSelect}
                                  displayField="numeroSerie"
                              />
                          </div>
                          <div className="col-md-4">
                               <label className="form-label">Filtrar por Status</label>
                               <select className="form-select" name="status" value={searchTerms.status || ''} onChange={(e) => setSearchTerms(prev => ({ ...prev, status: e.target.value }))}>
                                  <option value="">Todos</option>
                                  {Object.values(StatusPneu).map(s => <option key={s} value={s}>{s}</option>)}
                               </select>
                          </div>
                          <div className="col-md-2">
                               <button className="btn btn-primary w-100" onClick={() => onSearch(searchTerms)}>Buscar</button>
                          </div>
                      </div>
                  </div>
              </div>
              <CrudTable data={pneus} columns={columns} onEdit={handleEdit} onDelete={handleDelete} onView={handleView} />
            </>
          )}
        </>
      )}
    </div>
  );
};

export default PneuPage;

