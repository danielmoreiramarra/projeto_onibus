import React, { useState, useCallback } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import BackButton from '../components/BackButton';
import AutocompleteInput from '../components/AutocompleteInput'; // <<< Importa o novo componente
import { produtoService } from '../services/produtoService';
import useSearch from '../hooks/useSearch';
import { Categoria, UnidadeMedida, StatusProduto } from '../constants/produtoEnums';

const ProdutoPage = () => {
    const { data: produtos, loading, error, onSearch, refetch } = useSearch(produtoService);

    const [view, setView] = useState('LIST');
    const [currentProduto, setCurrentProduto] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [searchTerms, setSearchTerms] = useState({});

    // --- Lógica do Autocomplete ---
    const handleAutocompleteSearch = useCallback(async (term) => {
        // Busca rápida apenas por nome ou descrição para as sugestões
        const response = await produtoService.search({ nome: term });
        return response.data;
    }, []);

    const handleAutocompleteSelect = (produto) => {
        // Preenche todos os campos de busca quando um item é selecionado
        setSearchTerms({
            nome: produto.nome,
            marca: produto.marca,
            codigoInterno: produto.codigoInterno,
            categoria: produto.categoria,
            status: produto.status,
        });
    };

    // --- Handlers de Ações ---
    const handleCreate = () => { /* ... */ };
    const handleEdit = (produto) => { /* ... */ };
    const handleDelete = async (id) => {
        if (window.confirm('Tem certeza que deseja inativar este produto?')) {
            await produtoService.archive(id);
            refetch();
        }
    };
    const handleSubmit = async (formData) => { /* ... */ };
    const handleReturnToList = () => { setView('LIST'); };
    
    // --- Definições ---
    const columns = [/* ... seu array de colunas ... */];
    const formFields = [/* ... seu array de formFields ... */];

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>📦 Gerenciamento de Produtos</h2>
                {view === 'LIST' && <button className="btn btn-primary" onClick={handleCreate}>➕ Novo Produto</button>}
            </div>

            {view === 'FORM' ? (
                <CrudForm initialData={currentProduto} fields={formFields} onSubmit={handleSubmit} onCancel={handleReturnToList} title={isEditing ? 'Editar Produto' : 'Novo Produto'} />
            ) : (
                <>
                    {/* <<< SearchBar SUBSTITUÍDA PELO AUTOCOMPLETE >>> */}
                    <div className="card my-4">
                        <div className="card-header"><h5 className="mb-0">🔍 Busca Inteligente</h5></div>
                        <div className="card-body">
                            <div className="row g-3 align-items-end">
                                <div className="col-md-6">
                                    <AutocompleteInput
                                        label="Buscar por Nome ou Descrição"
                                        value={searchTerms.nome || ''}
                                        onChange={(value) => setSearchTerms(prev => ({ ...prev, nome: value }))}
                                        onSearch={handleAutocompleteSearch}
                                        onItemSelected={handleAutocompleteSelect}
                                        displayField="nome"
                                    />
                                </div>
                                <div className="col-md-4">
                                     <label className="form-label">Filtrar por Categoria</label>
                                     <select className="form-select" name="categoria" value={searchTerms.categoria || ''} onChange={(e) => setSearchTerms(prev => ({ ...prev, categoria: e.target.value }))}>
                                        <option value="">Todas</option>
                                        {Object.values(Categoria).map(c => <option key={c} value={c}>{c}</option>)}
                                     </select>
                                </div>
                                <div className="col-md-2">
                                     <button className="btn btn-primary w-100" onClick={() => onSearch(searchTerms)}>Buscar</button>
                                </div>
                            </div>
                        </div>
                    </div>

                    {loading ? <p>Carregando...</p> : (
                        <CrudTable data={produtos} columns={columns} onEdit={handleEdit} onDelete={handleDelete} />
                    )}
                </>
            )}
        </div>
    );
};

export default ProdutoPage;
