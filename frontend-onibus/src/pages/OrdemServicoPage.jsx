import React, { useState, useCallback } from 'react';
import CrudTable from '../components/CrudTable';
import BackButton from '../components/BackButton';
import OrdemServicoForm from '../components/OrdemServicoForm';
import OrdemServicoDetailView from '../components/OrdemServicoDetailView';
import AutocompleteInput from '../components/AutocompleteInput';
import { ordemServicoService } from '../services/ordemServicoService';
import useSearch from '../hooks/useSearch';
import { StatusOrdemServico } from '../constants/ordemServicoEnums';

const OrdemServicoPage = () => {
  const { data: ordens, loading, error, onSearch, refetch } = useSearch(ordemServicoService);

  const [view, setView] = useState('LIST');
  const [currentOrdem, setCurrentOrdem] = useState(null);

  const handleAutocompleteSearch = useCallback(async (term) => (await ordemServicoService.search({ numeroOS: term })).data, []);
  const handleAutocompleteSelect = (os) => onSearch({ numeroOS: os.numeroOS });

  const handleCreate = () => {
    setCurrentOrdem({ tipo: 'CORRETIVA', itens: [] });
    setView('FORM');
  };
  const handleView = (ordem) => {
    setCurrentOrdem(ordem);
    setView('DETAIL');
  };
  const handleReturnToList = () => {
    setView('LIST');
    setCurrentOrdem(null);
    refetch();
  };
  const handleSubmit = async (formData) => {
    // Lógica para extrair e formatar os dados para o DTO de criação
    const dto = {
      numeroOS: formData.numeroOS || `OS-CORR-${Date.now()}`,
      tipo: formData.tipo,
      descricao: formData.descricao,
      dataPrevisaoInicio: formData.dataPrevisaoInicio,
      dataPrevisaoConclusao: formData.dataPrevisaoConclusao,
      onibusId: formData.onibus?.id,
      motorId: formData.motor?.id,
      cambioId: formData.cambio?.id,
      pneuId: formData.pneu?.id,
    };
    const osCriada = await ordemServicoService.create(dto);
    // Adiciona os itens um por um
    for (const item of formData.itens) {
      await ordemServicoService.addItem(osCriada.data.id, item.produto.id, item.quantidade, item.descricao);
    }
    handleReturnToList();
  };

  const columns = [
    { key: 'numeroOS', label: 'Número OS' },
    { key: 'alvoDescricao', label: 'Alvo Principal' },
    { key: 'tipo', label: 'Tipo' },
    { key: 'status', label: 'Status' },
    { key: 'dataAbertura', label: 'Abertura' },
    { key: 'valorTotal', label: 'Valor Total', format: (val) => `R$ ${val?.toFixed(2)}` },
  ];

  const renderContent = () => {
    if (loading) return <p>Carregando...</p>;
    if (error) return <div className="alert alert-danger">{error}</div>;

    switch(view) {
      case 'FORM':
        return <OrdemServicoForm initialData={currentOrdem} onSubmit={handleSubmit} onCancel={handleReturnToList} isEditing={!!currentOrdem?.id} />;
      case 'DETAIL':
        return <OrdemServicoDetailView ordemServico={currentOrdem} onReturn={handleReturnToList} onUpdate={refetch} />;
      default:
        return (
          <>
            {/* ... Barra de Busca com Autocomplete ... */}
            <CrudTable data={ordens} columns={columns} onView={handleView} />
          </>
        );
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
      </div>
      {renderContent()}
    </div>
  );
};

export default OrdemServicoPage;
