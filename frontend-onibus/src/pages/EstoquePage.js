import React, { useState, useEffect } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import SearchBar from '../components/SearchBar';
import BackButton from '../components/BackButton';
import { estoqueService } from '../services/estoqueService';
import { produtoService } from '../services/produtoService';
import useSearch from '../hooks/useSearch';
import { Categoria } from '../constants/produtoEnums';

const EstoquePage = () => {
    const { data: estoque, loading, error, onSearch, refetch } = useSearch(estoqueService, {
        localizacaoFisica: estoqueService.getByLocalizacao,
        categoria: estoqueService.getByCategoriaProduto,
    });
    
    const [produtos, setProdutos] = useState([]);
    const [editing, setEditing] = useState(false);
    const [currentItem, setCurrentItem] = useState(null);
    const [showForm, setShowForm] = useState(false);
    
    useEffect(() => {
        const loadProdutos = async () => {
            try {
                const response = await produtoService.getAll();
                setProdutos(response.data);
            } catch (err) {
                console.error("Erro ao carregar produtos:", err);
            }
        };
        loadProdutos();
    }, []);

    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'produto.nome', label: 'Produto' },
        { key: 'produto.codigoInterno', label: 'Cód. Interno' },
        { key: 'quantidadeAtual', label: 'Qtd. Atual' },
        { key: 'quantidadeReservada', label: 'Qtd. Reservada' },
        { key: 'produto.estoqueMinimo', label: 'Qtd. Mínima' },
        { key: 'localizacaoFisica', label: 'Localização' }
    ];

    const searchFields = [
        { name: 'localizacaoFisica', label: 'Buscar por Localização', type: 'text' },
        { 
            name: 'categoria', 
            label: 'Buscar por Categoria', 
            type: 'select', 
            options: Object.values(Categoria).map(c => ({ value: c, label: c }))
        }
    ];

    const formFields = [
        { 
            name: 'produtoId', 
            label: 'Produto', 
            type: 'select',
            options: produtos.map(p => ({ value: p.id, label: `${p.nome} (${p.codigoInterno})` })),
            required: true
        },
        { name: 'quantidadeAtual', label: 'Quantidade Atual', type: 'number', required: true },
        { name: 'quantidadeReservada', label: 'Quantidade Reservada', type: 'number' },
        { name: 'localizacaoFisica', label: 'Localização Física', type: 'text' },
    ];
    
    const handleCreate = () => {
        setCurrentItem({
            quantidadeAtual: 0,
            quantidadeReservada: 0,
            produtoId: ''
        });
        setEditing(false);
        setShowForm(true);
    };

    const handleEdit = (item) => {
        setCurrentItem({
            ...item,
            produtoId: item.produto ? item.produto.id : ''
        });
        setEditing(true);
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Tem certeza que deseja excluir este registro de estoque?')) {
            try {
                await estoqueService.delete(id);
                refetch();
            } catch (err) {
                console.error("Erro ao deletar:", err);
            }
        }
    };
    
    const handleSubmit = async (formData) => {
        try {
            const payload = {
                ...formData,
                produto: { id: formData.produtoId }
            };

            if (editing) {
                await estoqueService.update(currentItem.id, payload);
            } else {
                await estoqueService.create(payload);
            }
            
            setShowForm(false);
            refetch();
        } catch (err) {
            console.error("Erro ao salvar:", err);
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setCurrentItem(null);
    };

    const handleInputChange = (e) => {
        const { name, value, type } = e.target;
        let processedValue = value;
        if (type === 'number') {
            processedValue = value === '' ? null : Number(value);
        }
        setCurrentItem(prev => ({
            ...prev,
            [name]: processedValue
        }));
    };
    
    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>📦 Gerenciamento de Estoque</h2>
                <BackButton /> {/* ✅ Posição do botão de voltar */}
            </div>
            
            <div className="mb-3 d-flex justify-content-between align-items-center">
                <button className="btn btn-primary" onClick={handleCreate}>
                    ➕ Novo Registro
                </button>
                <div className="btn-group">
                    <button className="btn btn-success" onClick={refetch} disabled={loading}>
                        {loading ? '⏳ Atualizando...' : '🔄 Atualizar Lista'}
                    </button>
                    <button className="btn btn-warning" onClick={() => alert('Em breve: Relatório de alertas!')}>
                        ⚠️ Alertas de Estoque
                    </button>
                </div>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            {showForm ? (
                <CrudForm
                    formData={currentItem}
                    fields={formFields}
                    onSubmit={handleSubmit}
                    onCancel={handleCancel}
                    onChange={handleInputChange}
                    title={editing ? '✏️ Editar Registro' : '➕ Novo Registro'}
                    loading={loading}
                />
            ) : (
                <>
                    <SearchBar fields={searchFields} onSearch={onSearch} />
                    
                    {loading && <div className="text-center">⏳ Carregando estoque...</div>}
                    
                    {!loading && estoque.length === 0 ? (
                        <div className="alert alert-info">Nenhum registro de estoque encontrado.</div>
                    ) : (
                        <CrudTable
                            data={estoque}
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

export default EstoquePage;