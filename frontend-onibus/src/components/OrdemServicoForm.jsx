import React, { useState, useCallback, useEffect } from 'react';
import AutocompleteInput from './AutocompleteInput';
import { onibusService } from '../services/onibusService';
import { motorService } from '../services/motorService';
import { cambioService } from '../services/cambioService';
import { pneuService } from '../services/pneuService';
import { produtoService } from '../services/produtoService';
import { TipoOrdemServico } from '../constants/ordemServicoEnums';

/**
 * Formulário dedicado para criar e editar Ordens de Serviço.
 */
const OrdemServicoForm = ({ initialData, onSubmit, onCancel, isEditing }) => {
    const [formData, setFormData] = useState(initialData || { tipo: 'CORRETIVA', itens: [] });
    const [itemSearch, setItemSearch] = useState('');
    const [itemQty, setItemQty] = useState(1);
    
    // --- Lógica de Autocomplete para os Alvos ---
    const searchOnibus = useCallback(async (term) => (await onibusService.search({ placa: term })).data, []);
    const searchMotor = useCallback(async (term) => (await motorService.search({ numeroSerie: term })).data, []);
    const searchCambio = useCallback(async (term) => (await cambioService.search({ numeroSerie: term })).data, []);
    const searchPneu = useCallback(async (term) => (await pneuService.search({ numeroSerie: term })).data, []);
    const searchProduto = useCallback(async (term) => {
        const params = { nome: term };
        if (formData.tipo === 'PREVENTIVA') {
            params.categoria = 'FLUIDO'; // Filtra para OS Preventiva
        }
        return (await produtoService.search(params)).data;
    }, [formData.tipo]);

    const handleSelectAlvo = (alvo, tipo) => {
        setFormData(prev => ({ ...prev, [tipo]: alvo, [`${tipo}Id`]: alvo.id }));
    };

    // --- Lógica para Adicionar/Remover Itens ---
    const handleAddItem = (produto) => {
        if (!produto || itemQty <= 0) return;
        const novoItem = { produto, quantidade: itemQty, descricao: '' };
        setFormData(prev => ({ ...prev, itens: [...prev.itens, novoItem] }));
        setItemSearch('');
        setItemQty(1);
    };

    const handleRemoveItem = (index) => {
        setFormData(prev => ({ ...prev, itens: prev.itens.filter((_, i) => i !== index) }));
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <div className="card my-4">
            <div className="card-header">
                <h5>{isEditing ? 'Editar Ordem de Serviço' : 'Criar Nova Ordem de Serviço'}</h5>
            </div>
            <div className="card-body">
                <form onSubmit={handleSubmit}>
                    {/* Seção de Alvos */}
                    <div className="row g-3">
                        <div className="col-md-3">
                           <AutocompleteInput label="Ônibus (Alvo)" onSearch={searchOnibus} onItemSelected={item => handleSelectAlvo(item, 'onibus')} displayField="placa" value={formData.onibus?.placa || ''} onChange={val => setFormData(prev => ({...prev, onibus: {placa: val}}))} />
                        </div>
                        <div className="col-md-3">
                           <AutocompleteInput label="Motor (Alvo)" onSearch={searchMotor} onItemSelected={item => handleSelectAlvo(item, 'motor')} displayField="numeroSerie" value={formData.motor?.numeroSerie || ''} onChange={val => setFormData(prev => ({...prev, motor: {numeroSerie: val}}))} />
                        </div>
                        {/* ... Autocomplete para Cambio e Pneu ... */}
                    </div>
                    {/* Seção de Detalhes da OS */}
                    <div className="row g-3 mt-3">
                        <div className="col-md-4">
                            <label className="form-label">Tipo de OS</label>
                            <select name="tipo" className="form-select" value={formData.tipo} onChange={handleInputChange}>
                                {Object.values(TipoOrdemServico).map(t => <option key={t} value={t}>{t}</option>)}
                            </select>
                        </div>
                        {/* ... Campos para data de previsão, etc. ... */}
                    </div>
                    {/* Seção de Itens */}
                    <div className="mt-4">
                        <h6>Itens da Ordem de Serviço</h6>
                        <div className="input-group">
                            <AutocompleteInput onSearch={searchProduto} onItemSelected={handleAddItem} displayField="nome" value={itemSearch} onChange={setItemSearch} />
                            <input type="number" className="form-control" style={{ maxWidth: '100px' }} value={itemQty} onChange={e => setItemQty(Number(e.target.value))} min="1" />
                            <button className="btn btn-outline-primary" type="button" onClick={() => handleAddItem(null)}>Adicionar Manualmente</button>
                        </div>
                        <ul className="list-group mt-2">
                            {formData.itens.map((item, index) => (
                                <li key={index} className="list-group-item d-flex justify-content-between align-items-center">
                                    {item.produto.nome} - Qtd: {item.quantidade}
                                    <button type="button" className="btn btn-danger btn-sm" onClick={() => handleRemoveItem(index)}>X</button>
                                </li>
                            ))}
                        </ul>
                    </div>
                    
                    <div className="mt-4">
                        <button type="submit" className="btn btn-primary me-2">Salvar Ordem de Serviço</button>
                        <button type="button" className="btn btn-secondary" onClick={onCancel}>Cancelar</button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default OrdemServicoForm;
