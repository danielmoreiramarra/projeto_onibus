import React, { useState, useCallback, useEffect } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import BackButton from '../components/BackButton';
import OnibusDetailView from '../components/OnibusDetailView';
import AutocompleteInput from '../components/AutocompleteInput';
import { onibusService } from '../services/onibusService';
import { motorService } from '../services/motorService';
import { cambioService } from '../services/cambioService';
import { pneuService } from '../services/pneuService';
import useSearch from '../hooks/useSearch';
import { StatusOnibus } from '../constants/onibusEnums';

const OnibusPage = () => {
  const { data: onibusList, loading, error, onSearch, refetch } = useSearch(onibusService);

  const [view, setView] = useState('LIST');
  const [currentOnibus, setCurrentOnibus] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [searchTerms, setSearchTerms] = useState({});
  const [availableComponents, setAvailableComponents] = useState({ motores: [], cambios: [], pneus: [] });

  // Carrega componentes disponíveis quando a view de detalhes é aberta
  useEffect(() => {
    if (view === 'DETAIL') {
      const loadAvailableComponents = async () => {
        try {
          const motoresRes = await motorService.search({ status: 'DISPONIVEL' });
          const cambiosRes = await cambioService.search({ status: 'DISPONIVEL' });
          const pneusRes = await pneuService.search({ status: 'DISPONIVEL' });
          setAvailableComponents({ motores: motoresRes.data, cambios: cambiosRes.data, pneus: pneusRes.data });
        } catch (err) {
          console.error("Erro ao carregar componentes disponíveis:", err);
        }
      };
      loadAvailableComponents();
    }
  }, [view]);

  // --- Lógica do Autocomplete ---
  const handleAutocompleteSearch = useCallback(async (term) => {
    const response = await onibusService.search({ placa: term });
    return response.data;
  }, []);

  const handleAutocompleteSelect = (onibus) => {
    setSearchTerms({
      placa: onibus.placa,
      numeroFrota: onibus.numeroFrota,
      status: onibus.status,
    });
  };
  
  const handleSearchChange = (e) => {
    const {name, value} = e.target;
    setSearchTerms(prev => ({ ...prev, [name]: value }))
  };

  // --- Handlers de Ações ---
  const handleCreate = () => {
    setCurrentOnibus({ dataCompra: new Date().toISOString().slice(0, 10) });
    setIsEditing(false);
    setView('FORM');
  };

  const handleEdit = (onibus) => {
    onibusService.getById(onibus.id).then(response => {
        setCurrentOnibus(response.data);
        setIsEditing(true);
        setView('FORM');
    });
  };
  
  const handleView = (onibus) => {
      onibusService.getById(onibus.id).then(response => {
        setCurrentOnibus(response.data);
        setView('DETAIL');
      });
  };

  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja excluir esse ônibus? Esta ação não pode ser desfeita.')) {
      await onibusService.delete(id); 
      refetch();
    }
  };

  const handleSubmit = async (formData) => {
    try {
      if (isEditing) {
        await onibusService.update(currentOnibus.id, formData);
      } else {
        await onibusService.create(formData);
      }
      handleReturnToList();
    } catch (err) {
      alert(`Falha ao salvar: ${err.response?.data || err.message}`);
    }
  };
  
  const handleReturnToList = () => {
      setView('LIST');
      setCurrentOnibus(null);
      refetch();
  };
  
  // --- Definições de Colunas e Campos ---
  const columns = [
    { key: 'placa', label: 'Placa' },
    { key: 'numeroFrota', label: 'Frota' },
    { key: 'marca', label: 'Marca' },
    { key: 'modelo', label: 'Modelo' },
    { key: 'status', label: 'Status' },
    { key: 'quilometragem', label: 'KM', format: (km) => km ? km.toFixed(1) : '0.0' },
  ];

  const formFields = [
    { name: 'placa', label: 'Placa', type: 'text', required: true },
    { name: 'chassi', label: 'Chassi', type: 'text', required: true },
    { name: 'numeroFrota', label: 'Número Frota', type: 'text', required: true },
    { name: 'marca', label: 'Marca', type: 'text', required: true },
    { name: 'modelo', label: 'Modelo', type: 'text', required: true },
    { name: 'codigoFabricacao', label: 'Código Fabricação', type: 'text', required: true },
    { name: 'anoFabricacao', label: 'Ano Fabricação', type: 'number', required: true },
    { name: 'capacidade', label: 'Capacidade', type: 'number', required: true },
    { name: 'dataCompra', label: 'Data de Compra', type: 'date', required: true },
  ];
  
  // --- Funções de Renderização ---
  const renderListView = () => (
    <>
      <div className="card my-4">
        <div className="card-header"><h5 className="mb-0">🔍 Busca Inteligente de Ônibus</h5></div>
        <div className="card-body">
            <div className="row g-3 align-items-end">
                <div className="col-md-6">
                    <AutocompleteInput
                        label="Buscar por Placa"
                        value={searchTerms.placa || ''}
                        name="placa"
                        onChange={handleSearchChange}
                        onSearch={handleAutocompleteSearch}
                        onItemSelected={handleAutocompleteSelect}
                        displayField="placa"
                    />
                </div>
                <div className="col-md-4">
                      <label className="form-label">Filtrar por Status</label>
                      <select className="form-select" name="status" value={searchTerms.status || ''} onChange={handleSearchChange}>
                        <option value="">Todos</option>
                        {Object.values(StatusOnibus).map(s => <option key={s} value={s}>{s}</option>)}
                      </select>
                </div>
                <div className="col-md-2">
                      <button className="btn btn-primary w-100" onClick={() => onSearch(searchTerms)}>Buscar</button>
                </div>
            </div>
        </div>
      </div>
      <CrudTable data={onibusList} columns={columns} onEdit={handleEdit} onDelete={handleDelete} onView={handleView} />
    </>
  );

  const renderContent = () => {
    if (loading && !currentOnibus) return <p className="text-center mt-5">Carregando...</p>;
    if (error) return <div className="alert alert-danger">{error}</div>;

    switch(view) {
        case 'FORM':
            return <CrudForm initialData={currentOnibus} fields={formFields} onSubmit={handleSubmit} onCancel={handleReturnToList} title={isEditing ? '✏️ Editar Ônibus' : '➕ Novo Ônibus'} />;
        case 'DETAIL':
            return <OnibusDetailView onibus={currentOnibus} availableComponents={availableComponents} onReturn={handleReturnToList} onUpdate={refetch} />;
        default: // LIST
            return renderListView();
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>🚌 Gerenciamento de Ônibus</h2>
        {view === 'LIST' && (
          <div className="btn-group">
            <button className="btn btn-primary" onClick={handleCreate}>➕ Novo Ônibus</button>
            <BackButton to="/" />
          </div>
        )}
      </div>
      
      {renderContent()}
    </div>
  );
};

export default OnibusPage;

