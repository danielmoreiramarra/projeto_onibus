// src/pages/ProdutoPage.js
import React, { useState } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import SearchBar from '../components/SearchBar';
import BackButton from '../components/BackButton'; // ✅ Importando o botão de voltar
import { produtoService } from '../services/produtoService';
import useSearch from '../hooks/useSearch';
import { Categoria, UnidadeMedida, StatusProduto } from '../constants/produtoEnums';

const ProdutoPage = () => {
    // ✅ Usando o hook de busca para gerenciar o estado dos produtos
    const { data: produtos, loading, error, onSearch, refetch } = useSearch(produtoService, {
        // Mapeamento dos campos de busca para os métodos do service
        nome: produtoService.getByNome,
        marca: produtoService.getByMarca,
        categoria: produtoService.getByCategoria
    });

    const [editing, setEditing] = useState(false);
    const [currentProduto, setCurrentProduto] = useState(null);
    const [showForm, setShowForm] = useState(false);

    // ✅ Colunas ajustadas para exibir os dados do modelo 'Produto'
    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'nome', label: 'Nome' },
        { key: 'codigoInterno', label: 'Código Interno' },
        { key: 'categoria', label: 'Categoria' },
        { key: 'precoUnitario', label: 'Preço', format: (value) => `R$ ${value?.toFixed(2)}` },
        { key: 'unidadeMedida', label: 'Unidade' },
        { key: 'status', label: 'Status' }
    ];

    // ✅ Campos de busca para o SearchBar
    const searchFields = [
        { name: 'nome', label: 'Buscar por Nome', type: 'text' },
        { name: 'marca', label: 'Buscar por Marca', type: 'text' },
        { 
            name: 'categoria', 
            label: 'Buscar por Categoria', 
            type: 'select', 
            options: Object.values(Categoria).map(c => ({ value: c, label: c })) 
        },
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
        } catch (err) {
            console.error('Erro ao salvar:', err);
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
                    <BackButton /> {/* ✅ Posição do botão de voltar */}
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