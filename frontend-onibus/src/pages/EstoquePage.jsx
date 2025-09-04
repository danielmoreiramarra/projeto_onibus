import React, { useState, useCallback } from 'react';
import CrudTable from '../components/CrudTable';
import BackButton from '../components/BackButton';
import AutocompleteInput from '../components/AutocompleteInput';
import EstoqueDetailView from '../components/EstoqueDetailView';
import { estoqueService } from '../services/estoqueService';
import useSearch from '../hooks/useSearch';

const EstoquePage = () => {
  const { data: estoqueList, loading, error, onSearch, refetch } = useSearch(estoqueService);

  const [view, setView] = useState('LIST');
  const [currentItem, setCurrentItem] = useState(null);
  const [searchTerms, setSearchTerms] = useState({});

  // --- Lógica do Autocomplete ---
  const handleAutocompleteSearch = useCallback(async (term) => {
    const response = await estoqueService.search({ nomeProduto: term });
    return response.data;
  }, []);

  const handleAutocompleteSelect = (estoqueItem) => {
    setSearchTerms({
      nomeProduto: estoqueItem.produto.nome,
      marcaProduto: estoqueItem.produto.marca,
      localizacao: estoqueItem.localizacaoFisica,
    });
  };

  // --- Handlers ---
  const handleView = (item) => {
    setCurrentItem(item);
    setView('DETAIL');
  };

  const handleReturnToList = () => {
    setView('LIST');
    setCurrentItem(null);
    refetch(); // Atualiza a lista ao voltar
  };

  // --- Definições ---
  const columns = [
    { key: 'produto.codigoInterno', label: 'Cód. Interno' },
    { key: 'produto.nome', label: 'Produto' },
    { key: 'quantidadeDisponivel', label: 'Disponível', format: (val) => val.toFixed(2) },
    { key: 'quantidadeReservada', label: 'Reservada', format: (val) => val.toFixed(2) },
    { key: 'quantidadeAtual', label: 'Total', format: (val) => val.toFixed(2) },
    { key: 'localizacaoFisica', label: 'Localização' },
  ];

  const renderContent = () => {
    if (loading) return <p>Carregando...</p>;
    if (error) return <div className="alert alert-danger">{error}</div>;

    if (view === 'DETAIL') {
      return <EstoqueDetailView estoqueItem={currentItem} onReturn={handleReturnToList} onUpdate={refetch} />;
    }

    return (
      <>
        <div className="card my-4">
            <div className="card-header"><h5 className="mb-0">🔍 Busca Inteligente de Estoque</h5></div>
            <div className="card-body">
                <div className="row g-3 align-items-end">
                    <div className="col-md-8">
                        <AutocompleteInput
                            label="Buscar por Nome do Produto"
                            value={searchTerms.nomeProduto || ''}
                            onChange={(value) => setSearchTerms(prev => ({ ...prev, nomeProduto: value }))}
                            onSearch={handleAutocompleteSearch}
                            onItemSelected={handleAutocompleteSelect}
                            displayField="produto.nome" // Acessa o nome aninhado
                        />
                    </div>
                    <div className="col-md-4">
                         <button className="btn btn-primary w-100" onClick={() => onSearch(searchTerms)}>Buscar</button>
                    </div>
                </div>
            </div>
        </div>
        <CrudTable data={estoqueList} columns={columns} onView={handleView} />
      </>
    );
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>📦 Gerenciamento de Estoque</h2>
        <BackButton to="/" />
      </div>
      {renderContent()}
    </div>
  );
};

export default EstoquePage;
