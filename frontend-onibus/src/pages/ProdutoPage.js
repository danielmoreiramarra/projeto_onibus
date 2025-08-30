import React, { useState } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import SearchBar from '../components/SearchBar';
import BackButton from '../components/BackButton';
import { produtoService } from '../services/produtoService';
import useSearch from '../hooks/useSearch';
import { Categoria, UnidadeMedida, StatusProduto } from '../constants/produtoEnums';
import { useNavigate } from 'react-router-dom';

const ProdutoPage = () => {
    const { data: produtos, loading, error, onSearch, refetch } = useSearch(produtoService);
    const navigate = useNavigate();

    const [editing, setEditing] = useState(false);
    const [currentProduto, setCurrentProduto] = useState(null);
    const [showForm, setShowForm] = useState(false);

    // ✅ Colunas ajustadas para exibir os dados do modelo 'Produto'
    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'nome', label: 'Nome' },
        { key: 'marca', label: 'Marca' },
        { key: 'codigoInterno', label: 'Código Interno' },
        { key: 'codigoBarras', label: 'Cód. Barras' },
        { key: 'descricao', label: 'Descrição' },
        { key: 'precoUnitario', label: 'Preço', format: (value) => `R$ ${value?.toFixed(2)}` },
        { key: 'estoqueMinimo', label: 'Estoque Mínimo' },
        { key: 'localizacao', label: 'Localização' },
        { key: 'categoria', label: 'Categoria' },
        { key: 'unidadeMedida', label: 'Unidade' },
        { key: 'status', label: 'Status' }
    ];

    // ✅ Campos de busca para o SearchBar
    const searchFields = [
        { name: 'id', label: 'ID do Produto', type: 'number' },
        { name: 'nome', label: 'Nome', type: 'text' },
        { name: 'marca', label: 'Marca', type: 'text' },
        { name: 'codigoInterno', label: 'Cód. Interno', type: 'text' },
        { name: 'codigoBarras', label: 'Cód. Barras', type: 'text' },
        { name: 'descricao', label: 'Descrição', type: 'text' },
        { name: 'precoUnitarioMin', label: 'Preço Mín.', type: 'number' },
        { name: 'precoUnitarioMax', label: 'Preço Máx.', type: 'number' },
        { name: 'localizacao', label: 'Localização', type: 'text' },
        { name: 'categoria', label: 'Categoria', type: 'select', 
          options: [{ value: '', label: 'Todos' }, ...Object.values(Categoria).map(c => ({ value: c, label: c }))] },
        { name: 'status', label: 'Status', type: 'select', 
          options: [{ value: '', label: 'Todos' }, ...Object.values(StatusProduto).map(s => ({ value: s, label: s }))] },
        { name: 'unidadeMedida', label: 'Unidade de Medida', type: 'select', 
          options: [{ value: '', label: 'Todos' }, ...Object.values(UnidadeMedida).map(u => ({ value: u, label: u }))] },
    ];

    // ✅ Campos de formulário com selects para enums
    const formFields = [
        { name: 'nome', label: 'Nome', type: 'text', required: true },
        { name: 'marca', label: 'Marca', type: 'text', required: true },
        { 
            name: 'categoria', 
            label: 'Categoria', 
            type: 'select',
            options: Object.values(Categoria).map(c => ({ value: c, label: c })),
            required: true 
        },
        { name: 'descricao', label: 'Descrição', type: 'textarea' },
        { 
            name: 'precoUnitario', 
            label: 'Preço Unitário (R$)', 
            type: 'number', 
            step: '0.01', 
            required: true 
        },
        { 
            name: 'unidadeMedida', 
            label: 'Unidade Medida', 
            type: 'select',
            options: Object.values(UnidadeMedida).map(u => ({ value: u, label: u })),
            required: true 
        },
        { 
            name: 'status', 
            label: 'Status', 
            type: 'select',
            options: Object.values(StatusProduto).map(s => ({ value: s, label: s })),
            required: true 
        },
        { name: 'codigoInterno', label: 'Código Interno', type: 'text', required: true },
        { name: 'codigoBarras', label: 'Código de Barras', type: 'text' },
        { name: 'estoqueMinimo', label: 'Estoque Mínimo', type: 'number', required: true }
    ];
    
    // Funções de manipulação
    const handleCreate = () => {
        setCurrentProduto({
            status: 'ATIVO',
            estoqueMinimo: 5
        });
        setEditing(false);
        setShowForm(true);
    };

    const handleEdit = (produto) => {
        setCurrentProduto(produto);
        setEditing(true);
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Tem certeza que deseja inativar este produto?')) {
            try {
                await produtoService.delete(id);
                refetch();
            } catch (err) {
                console.error('Erro ao inativar:', err);
            }
        }
    };

    const handleSubmit = async (formData) => {
        try {
            if (editing) {
                await produtoService.update(currentProduto.id, formData);
            } else {
                await produtoService.create(formData);
            }
            setShowForm(false);
            refetch();
        } catch (error) {
            console.error('Erro ao salvar:', error);
        }
    };
    
    const handleCancel = () => {
        setShowForm(false);
        setCurrentProduto(null);
    };

    const handleInputChange = (e) => {
        const { name, value, type } = e.target;
        let processedValue = value;
        if (type === 'number') {
            processedValue = value === '' ? null : Number(value);
        } else if (type === 'date') {
            processedValue = value || null;
        }
        setCurrentProduto(prev => ({
            ...prev,
            [name]: processedValue
        }));
    };

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>📦 Gerenciamento de Produtos</h2>
                <div className="btn-group">
                    <button className="btn btn-primary" onClick={handleCreate} disabled={loading}>
                        {loading ? '⏳' : '➕'} Novo Produto
                    </button>
                    <BackButton />
                </div>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            {showForm ? (
                <CrudForm
                    formData={currentProduto}
                    fields={formFields}
                    onSubmit={handleSubmit}
                    onCancel={handleCancel}
                    onChange={handleInputChange}
                    title={editing ? '✏️ Editar Produto' : '➕ Novo Produto'}
                    loading={loading}
                />
            ) : (
                <>
                    <SearchBar fields={searchFields} onSearch={onSearch} />
                    
                    {loading && <div className="text-center">⏳ Carregando produtos...</div>}
                    
                    {!loading && produtos.length === 0 ? (
                        <div className="alert alert-info">
                            📝 Nenhum produto encontrado.
                        </div>
                    ) : (
                        <CrudTable
                            data={produtos}
                            columns={columns}
                            onEdit={handleEdit}
                            onDelete={handleDelete}
                        />
                    )}
                </>
            )}
        </div>
    );
};

export default ProdutoPage;