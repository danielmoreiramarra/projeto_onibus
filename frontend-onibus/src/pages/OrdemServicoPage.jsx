import React, { useState, useCallback } from 'react';
import CrudTable from '../components/CrudTable';
import BackButton from '../components/BackButton';
import OrdemServicoForm from '../components/OrdemServicoForm';
import OrdemServicoDetailView from '../components/OrdemServicoDetailView';
import AutocompleteInput from '../components/AutocompleteInput';
import { ordemServicoService } from '../services/ordemServicoService';
import useSearch from '../hooks/useSearch';
import { StatusOrdemServico, TipoOrdemServico } from '../constants/ordemServicoEnums';

const OrdemServicoPage = () => {
  const { data: ordens, loading, error, onSearch, refetch } = useSearch(ordemServicoService);

  const [view, setView] = useState('LIST'); // CORRIGIDO: Iniciar com 'LIST'
  const [currentOrdem, setCurrentOrdem] = useState(null);
  const [searchTerms, setSearchTerms] = useState({});

  // --- Lógica de Busca ---
  const handleAutocompleteSearch = useCallback(async (term) => (await ordemServicoService.search({ numeroOS: term })).data, []);
  const handleAutocompleteSelect = (os) => onSearch({ numeroOS: os.numeroOS });
  const handleSearchChange = (e) => setSearchTerms(prev => ({ ...prev, [e.target.name]: e.target.value }));

  // --- Handlers de Ações ---
  const handleCreate = () => {
    setCurrentOrdem({
      tipo: 'CORRETIVA',
      itens: [],
      dataPrevisaoInicio: new Date().toISOString().split('T')[0],
      dataPrevisaoConclusao: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    });
    setView('FORM');
  };

  const handleView = (ordem) => {
    ordemServicoService.getById(ordem.id).then(response => {
      setCurrentOrdem(response.data);
      setView('DETAIL');
    });
  };

  const handleReturnToList = () => {
    setView('LIST');
    setCurrentOrdem(null);
    refetch();
  };

  const handleSubmit = async (formData) => {
    const dto = {
      numeroOS: formData.numeroOS || `OS-CORR-${Date.now()}`,
      tipo: formData.tipo,
      descricao: formData.descricao,
      dataPrevisaoInicio: formData.dataPrevisaoInicio,
      dataPrevisaoConclusao: formData.dataPrevisaoConclusao,
      onibusId: formData.onibusId,
      motorId: formData.motorId,
      cambioId: formData.cambioId,
      pneuId: formData.pneuId,
    };
    
    try {
      const osCriadaResponse = await ordemServicoService.create(dto);
      const osId = osCriadaResponse.data.id;

      for (const item of formData.itens) {
        const itemDTO = {
          produtoId: item.produto.id,
          quantidade: item.quantidade,
          descricao: item.descricao,
        };
        await ordemServicoService.addItem(osId, itemDTO);
      }
      
      alert('Ordem de Serviço criada com sucesso!');
      handleReturnToList();
    } catch (err) {
      console.error("Erro ao criar OS:", err);
      alert(`Falha ao criar OS: ${err.response?.data || err.message}`);
    }
  };

  const columns = [
    { key: 'numeroOS', label: 'Número OS' },
    { key: 'alvoDescricao', label: 'Alvo Principal' },
    { key: 'tipo', label: 'Tipo' },
    { key: 'status', label: 'Status' },
    { key: 'dataAbertura', label: 'Abertura' },
    { key: 'valorTotal', label: 'Valor Total', format: (val) => val != null ? `R$ ${val.toFixed(2)}` : 'R$ 0.00' },
  ];

  // --- Funções de Renderização ---
  const renderListView = () => (
    <>
      <div className="card my-4">
        <div className="card-header"><h5 className="mb-0">🔍 Busca de Ordens de Serviço</h5></div>
        <div className="card-body">
          <div className="row g-3 align-items-end">
            <div className="col-md-6">
              <AutocompleteInput 
                label="Buscar por Número da OS" 
                value={searchTerms.numeroOS || ''} 
                name="numeroOS" 
                onChange={handleSearchChange} 
                onSearch={handleAutocompleteSearch} 
                onItemSelected={handleAutocompleteSelect} 
                displayField="numeroOS" 
              />
            </div>
            <div className="col-md-4">
              <label className="form-label">Filtrar por Status</label>
              <select className="form-select" name="status" value={searchTerms.status || ''} onChange={handleSearchChange}>
                <option value="">Todos</option>
                {Object.values(StatusOrdemServico).map(s => <option key={s} value={s}>{s}</option>)}
              </select>
            </div>
            <div className="col-md-2">
              <button className="btn btn-primary w-100" onClick={() => onSearch(searchTerms)}>Buscar</button>
            </div>
          </div>
        </div>
      </div>
      
      <div className="d-flex justify-content-between align-items-center mb-2">
        <span className="text-muted">{ordens.length} ordens de serviço encontradas.</span>
        <button className="btn btn-sm btn-outline-secondary" onClick={() => onSearch(searchTerms)}>🔄 Atualizar</button>
      </div>
      
      <CrudTable data={ordens} columns={columns} onView={handleView} />
    </>
  );

  const renderContent = () => {
    if (loading && view === 'LIST') return <p className="text-center mt-5">Carregando...</p>;
    if (error) return <div className="alert alert-danger">{error}</div>;

    switch(view) {
      case 'FORM':
        return (
          <OrdemServicoForm 
            initialData={currentOrdem} 
            onSubmit={handleSubmit} 
            onCancel={handleReturnToList} 
            isEditing={false} 
          />
        );
      case 'DETAIL':
        return (
          <OrdemServicoDetailView 
            ordemServico={currentOrdem} 
            onReturn={handleReturnToList} 
            onUpdate={refetch} 
          />
        );
      default:
        return renderListView();
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>🔧 Gerenciamento de Ordens de Serviço</h2>
        {view === 'LIST' && (
          <div className="btn-group">
            <button className="btn btn-primary" onClick={handleCreate}>➕ Nova OS</button>
            <BackButton to="/" />
          </div>
        )}
        {view !== 'LIST' && (
          <div className="btn-group">
            <button className="btn btn-secondary" onClick={handleReturnToList}>← Voltar para Lista</button>
          </div>
        )}
      </div>
      {renderContent()}
    </div>
  );
};

export default OrdemServicoPage;