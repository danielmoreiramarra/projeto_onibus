import React, { useState, useCallback, useEffect } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import BackButton from '../components/BackButton';
import MotorDetailView from '../components/MotorDetailView';
import AutocompleteInput from '../components/AutocompleteInput';
import { motorService } from '../services/motorService';
import { onibusService } from '../services/onibusService';
import useSearch from '../hooks/useSearch';
import { TipoMotor, StatusMotor } from '../constants/motorEnums';

const MotorPage = () => {
  const { data: motores, loading, error, onSearch, refetch } = useSearch(motorService);
  const [view, setView] = useState('LIST');
  const [currentMotor, setCurrentMotor] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [searchTerms, setSearchTerms] = useState({});

  // --- Lógica do Autocomplete ---
  const handleAutocompleteSearch = useCallback(async (searchField, term) => {
    // Se a busca for por ônibus, usamos o onibusService
    if (searchField === 'onibus') {
        const response = await onibusService.search({ placa: term });
        // Mapeia para um formato que o AutocompleteInput entenda (id e um campo de exibição)
        return response.data.map(o => ({ id: o.id, display: `${o.placa} - ${o.numeroFrota}` }));
    }
    // Para outros campos, usa o motorService
    const response = await motorService.search({ [searchField]: term });
    return response.data;
  }, []);

  const handleAutocompleteSelect = (item, fieldName) => {
    // Se um ônibus foi selecionado, apenas preenchemos o onibusId para a busca
    if (fieldName === 'onibus') {
        setSearchTerms(prev => ({ ...prev, onibusId: item.id, onibusDisplay: item.display }));
    } else { // Se um motor foi selecionado, preenchemos os outros campos
        setSearchTerms({
            modelo: item.modelo,
            marca: item.marca,
            numeroSerie: item.numeroSerie,
        });
    }
  };

  // Handler genérico para os inputs da busca
  const handleSearchChange = (e) => {
    const {name, value} = e.target;
    setSearchTerms(prev => ({ ...prev, [name]: value }))
  }

  // --- Handlers de Ações ---
  const handleCreate = () => {
    setCurrentMotor({ dataCompra: new Date().toISOString().slice(0, 10) });
    setIsEditing(false);
    setView('FORM');
  };
  const handleEdit = (motor) => {
    // Busca os dados completos antes de editar para garantir que o formulário esteja preenchido
    motorService.getById(motor.id).then(response => {
        setCurrentMotor(response.data);
        setIsEditing(true);
        setView('FORM');
    });
  };
  const handleDelete = async (id) => {
    if (window.confirm('Tem certeza que deseja inativar este motor? Esta ação não pode ser desfeita.')) {
      await motorService.archive(id);
      refetch();
    }
  };
  const handleSubmit = async (formData) => {
    if (isEditing) {
      await motorService.update(currentMotor.id, formData);
    } else {
      await motorService.create(formData);
    }
    handleReturnToList();
  };
  const handleView = (motor) => {
    // Busca a versão mais recente e completa dos dados para a tela de detalhes
    motorService.getById(motor.id).then(response => {
      setCurrentMotor(response.data);
      setView('DETAIL');
    });
  };
  const handleReturnToList = () => {
    setView('LIST');
    setCurrentMotor(null);
    refetch();
  };
  
  // --- Definições de Colunas e Campos ---
  const columns = [
    { key: 'marca', label: 'Marca' },
    { key: 'modelo', label: 'Modelo' },
    { key: 'numeroSerie', label: 'Nº de Série' },
    { key: 'status', label: 'Status' },
    { key: 'potencia', label: 'Potência (CV)' },
    { key: 'anoFabricacao', label: 'Ano' },
    { key: 'dataCompra', label: 'Data da Compra' },
    { key: 'onibus', label: 'Ônibus Instalado', format: (onibus) => onibus ? `${onibus.placa} (${onibus.numeroFrota})` : 'N/A' }
  ];

  const formFields = [
    { name: 'marca', label: 'Marca', type: 'text', required: true },
    { name: 'modelo', label: 'Modelo', type: 'text', required: true },
    { name: 'numeroSerie', label: 'Número de Série', type: 'text', required: true },
    { name: 'codigoFabricacao', label: 'Código Fabricação', type: 'text', required: true },
    { name: 'tipo', label: 'Tipo', type: 'select', options: Object.values(TipoMotor).map(t => ({ value: t, label: t })), required: true },
    { name: 'potencia', label: 'Potência (CV)', type: 'number', required: true },
    { name: 'cilindrada', label: 'Cilindrada (cc)', type: 'number' },
    { name: 'anoFabricacao', label: 'Ano Fabricação', type: 'number', required: true },
    { name: 'dataCompra', label: 'Data de Compra', type: 'date', required: true },
    { name: 'periodoGarantiaMeses', label: 'Garantia (meses)', type: 'number', defaultValue: 24, required: true },
    { name: 'tipoOleo', label: 'Tipo de Óleo', type: 'text', required: true },
    { name: 'capacidadeOleo', label: 'Capacidade Óleo (L)', type: 'number', step: '0.1', required: true },
  ];

  const renderListView = () => (
    <>
      <div className="card my-4">
        <div className="card-header"><h5 className="mb-0">🔍 Busca Inteligente de Motores</h5></div>
        <div className="card-body">
            <div className="row g-3">
                 <div className="col-md-3">
                    <AutocompleteInput label="Modelo" name="modelo" value={searchTerms.modelo || ''} onChange={handleSearchChange} onSearch={(term) => handleAutocompleteSearch('modelo', term)} onItemSelected={handleAutocompleteSelect} displayField="modelo"/>
                 </div>
                 <div className="col-md-3">
                    <AutocompleteInput label="Nº de Série" name="numeroSerie" value={searchTerms.numeroSerie || ''} onChange={handleSearchChange} onSearch={(term) => handleAutocompleteSearch('numeroSerie', term)} onItemSelected={handleAutocompleteSelect} displayField="numeroSerie"/>
                 </div>
                 <div className="col-md-3">
                    <AutocompleteInput label="Ônibus (Placa)" name="onibusDisplay" value={searchTerms.onibusDisplay || ''} onChange={(e) => handleSearchChange({target: {name: 'onibusDisplay', value: e.target.value}})} onSearch={(term) => handleAutocompleteSearch('onibus', term)} onItemSelected={(item) => handleAutocompleteSelect(item, 'onibus')} displayField="display"/>
                 </div>
                 <div className="col-md-3">
                    <label className="form-label">Status</label>
                    <select className="form-select" name="status" value={searchTerms.status || ''} onChange={handleSearchChange}>
                        <option value="">Todos</option>
                        {Object.values(StatusMotor).map(s => <option key={s} value={s}>{s}</option>)}
                    </select>
                 </div>
            </div>
            <div className="mt-3 d-flex justify-content-end">
                 <button className="btn btn-primary" onClick={() => onSearch(searchTerms)}>Buscar</button>
            </div>
        </div>
      </div>

      <div className="d-flex justify-content-between align-items-center mb-2">
        <span className="text-muted">{motores.length} motores encontrados.</span>
        <button className="btn btn-sm btn-outline-secondary" onClick={() => onSearch(searchTerms)}>🔄 Atualizar Lista</button>
      </div>
      <CrudTable data={motores} columns={columns} onEdit={handleEdit} onDelete={handleDelete} onView={handleView} />
    </>
  );

  const renderContent = () => {
    if (loading && !currentMotor) return <p className="text-center mt-5">Carregando...</p>;
    if (error) return <div className="alert alert-danger">{error}</div>;

    switch(view) {
        case 'FORM':
            return <CrudForm initialData={currentMotor} fields={formFields} onSubmit={handleSubmit} onCancel={handleReturnToList} title={isEditing ? '✏️ Editar Motor' : '➕ Novo Motor'} />;
        case 'DETAIL':
            return <MotorDetailView motor={currentMotor} onReturn={handleReturnToList} onUpdate={refetch} />;
        default: // LIST
            return renderListView();
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Gerenciamento de Motores</h2>
        {view === 'LIST' && (
          <div className="btn-group">
            <button className="btn btn-primary" onClick={handleCreate}>➕ Novo Motor</button>
            <BackButton to="/" />
          </div>
        )}
      </div>
      {renderContent()}
    </div>
  );
};
export default MotorPage;

