import React, { useState, useCallback } from 'react';
import AutocompleteInput from '../components/AutocompleteInput';
import { onibusService } from '../services/onibusService';
import { motorService } from '../services/motorService';
import { cambioService } from '../services/cambioService';
import { pneuService } from '../services/pneuService';
import { produtoService } from '../services/produtoService';
import { TipoOrdemServico } from '../constants/ordemServicoEnums';

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
      params.categoria = 'FLUIDO';
    }
    return (await produtoService.search(params)).data;
  }, [formData.tipo]);

  const handleSelectAlvo = (alvo, tipo) => {
    setFormData(prev => ({ 
      ...prev, 
      [tipo]: alvo,
      [`${tipo}Id`]: alvo.id
    }));
  };
  
  const handleAlvoInputChange = (e, name, displayField) => {
    const { value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: {
        [displayField]: value
      },
      [`${name}Id`]: null
    }));
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

  const handleFormSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <div className="card my-4">
      <div className="card-header">
        <h5 className="card-title mb-0">{isEditing ? '✏️ Editar Ordem de Serviço' : '➕ Criar Nova Ordem de Serviço'}</h5>
      </div>
      <div className="card-body">
        <form onSubmit={handleFormSubmit}>
          <h6 className="card-subtitle mb-3 text-muted">1. Selecione o Alvo Principal do Serviço</h6>
          <div className="row g-3">
            <div className="col-md-3">
              <AutocompleteInput 
                label="Ônibus" 
                name="onibus" 
                value={formData.onibus?.placa || ''} 
                onChange={(e) => handleAlvoInputChange(e, 'onibus', 'placa')} 
                onSearch={searchOnibus} 
                onItemSelected={item => handleSelectAlvo(item, 'onibus')} 
                displayField="placa" 
                placeholder="Digite a placa..."
              />
            </div>
            <div className="col-md-3">
              <AutocompleteInput 
                label="Motor" 
                name="motor" 
                value={formData.motor?.numeroSerie || ''} 
                onChange={(e) => handleAlvoInputChange(e, 'motor', 'numeroSerie')} 
                onSearch={searchMotor} 
                onItemSelected={item => handleSelectAlvo(item, 'motor')} 
                displayField="numeroSerie" 
                placeholder="Digite o nº de série..."
              />
            </div>
            <div className="col-md-3">
              <AutocompleteInput 
                label="Câmbio" 
                name="cambio" 
                value={formData.cambio?.numeroSerie || ''} 
                onChange={(e) => handleAlvoInputChange(e, 'cambio', 'numeroSerie')} 
                onSearch={searchCambio} 
                onItemSelected={item => handleSelectAlvo(item, 'cambio')} 
                displayField="numeroSerie" 
                placeholder="Digite o nº de série..."
              />
            </div>
            <div className="col-md-3">
              <AutocompleteInput 
                label="Pneu" 
                name="pneu" 
                value={formData.pneu?.numeroSerie || ''} 
                onChange={(e) => handleAlvoInputChange(e, 'pneu', 'numeroSerie')} 
                onSearch={searchPneu} 
                onItemSelected={item => handleSelectAlvo(item, 'pneu')} 
                displayField="numeroSerie" 
                placeholder="Digite o nº de série..."
              />
            </div>
          </div>
          
          <h6 className="card-subtitle mt-4 mb-3 text-muted">2. Detalhes da Ordem de Serviço</h6>
          <div className="row g-3">
            <div className="col-md-3">
              <label className="form-label">Tipo de OS</label>
              <select name="tipo" className="form-select" value={formData.tipo} onChange={handleInputChange}>
                {Object.values(TipoOrdemServico).map(t => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
            <div className="col-md-3">
              <label className="form-label">Data Prev. Início</label>
              <input type="date" name="dataPrevisaoInicio" className="form-control" value={formData.dataPrevisaoInicio || ''} onChange={handleInputChange} required />
            </div>
            <div className="col-md-3">
              <label className="form-label">Data Prev. Conclusão</label>
              <input type="date" name="dataPrevisaoConclusao" className="form-control" value={formData.dataPrevisaoConclusao || ''} onChange={handleInputChange} required />
            </div>
            <div className="col-md-3">
              <label className="form-label">Nº da OS</label>
              <input type="text" name="numeroOS" className="form-control" value={formData.numeroOS || ''} onChange={handleInputChange} placeholder="Ex: OS-CORR-001" required />
            </div>
            <div className="col-12">
              <label className="form-label">Descrição Geral do Serviço</label>
              <textarea name="descricao" className="form-control" value={formData.descricao || ''} onChange={handleInputChange} rows="2" required></textarea>
            </div>
          </div>

          <h6 className="card-subtitle mt-4 mb-3 text-muted">3. Adicionar Itens de Estoque</h6>
          <div className="input-group">
            <div className="flex-grow-1">
              <AutocompleteInput 
                label="" 
                name="itemSearch" 
                value={itemSearch} 
                onChange={(e) => setItemSearch(e.target.value)} 
                onSearch={searchProduto} 
                onItemSelected={handleAddItem} 
                displayField="nome" 
                placeholder="Busque por nome do produto..."
              />
            </div>
            <input type="number" className="form-control" style={{ maxWidth: '100px' }} value={itemQty} onChange={e => setItemQty(Number(e.target.value))} min="1" />
            <span className="input-group-text">Qtd.</span>
          </div>

          <ul className="list-group mt-2">
            {formData.itens.map((item, index) => (
              <li key={index} className="list-group-item d-flex justify-content-between align-items-center">
                {item.produto.nome} (Qtd: {item.quantidade})
                <button type="button" className="btn-close" onClick={() => handleRemoveItem(index)}></button>
              </li>
            ))}
          </ul>
          
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