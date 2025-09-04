import React, { useState } from 'react';
import { estoqueService } from '../services/estoqueService';

const AdicionarEstoqueModal = ({ show, onClose, onSuccess, produto }) => {
  const [quantidade, setQuantidade] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await estoqueService.adicionar(produto.id, parseFloat(quantidade));
      alert('Estoque adicionado com sucesso!');
      onSuccess();
    } catch (err) {
      setError(err.response?.data || err.message || 'Erro ao adicionar estoque.');
    } finally {
      setLoading(false);
    }
  };

  if (!show) return null;

  return (
    <div className="modal" style={{ display: 'block', backgroundColor: 'rgba(0,0,0,0.5)' }}>
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">Adicionar Estoque para: {produto.nome}</h5>
            <button type="button" className="btn-close" onClick={onClose}></button>
          </div>
          <form onSubmit={handleSubmit}>
            <div className="modal-body">
              <p>Informe a quantidade de itens que está entrando no estoque.</p>
              <div className="mb-3">
                <label htmlFor="quantidade" className="form-label">Quantidade a Adicionar</label>
                <input
                  type="number"
                  className={`form-control ${error ? 'is-invalid' : ''}`}
                  id="quantidade"
                  value={quantidade}
                  onChange={(e) => setQuantidade(e.target.value)}
                  step="0.1"
                  required
                />
                {error && <div className="invalid-feedback">{error}</div>}
              </div>
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancelar</button>
              <button type="submit" className="btn btn-primary" disabled={loading}>
                {loading ? 'Adicionando...' : 'Confirmar Entrada'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AdicionarEstoqueModal;
