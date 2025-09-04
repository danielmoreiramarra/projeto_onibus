import React from 'react';
import { ordemServicoService } from '../services/ordemServicoService';

const OrdemServicoDetailView = ({ ordemServico, onReturn, onUpdate }) => {
  const handleAction = async (action, osId) => {
    const actionText = action.replace('Execution', '').toLowerCase();
    if (!window.confirm(`Tem certeza que deseja ${actionText} esta OS?`)) return;
    try {
      await ordemServicoService[action](osId);
      onUpdate();
    } catch (err) {
      alert(`Falha ao executar ação: ${err.response?.data || err.message}`);
    }
  };

  return (
    <div className="card my-4">
      <div className="card-header d-flex justify-content-between align-items-center">
        <h5 className="mb-0">Detalhes da OS: {ordemServico.numeroOS}</h5>
        <button onClick={onReturn} className="btn btn-secondary">Voltar</button>
      </div>
      <div className="card-body">
        <p><strong>Status:</strong> <span className={`badge bg-${ordemServico.status === 'ABERTA' ? 'primary' : 'success'}`}>{ordemServico.status}</span></p>
        <p><strong>Alvo Principal:</strong> {ordemServico.alvoDescricao}</p>
        {/* ... outros detalhes da OS ... */}

        <h6>Itens da OS</h6>
        <ul className="list-group">
          {ordemServico.itens.map(item => (
            <li key={item.id} className="list-group-item">{item.produtoNome} (Qtd: {item.quantidade})</li>
          ))}
        </ul>
        
        <div className="mt-4">
            <h6>Ações</h6>
            {ordemServico.status === 'ABERTA' && (
                <button className="btn btn-info me-2" onClick={() => handleAction('startExecution', ordemServico.id)}>▶️ Iniciar Execução</button>
            )}
            {ordemServico.status === 'EM_EXECUCAO' && (
                <button className="btn btn-success me-2" onClick={() => handleAction('finishExecution', ordemServico.id)}>✅ Finalizar OS</button>
            )}
            {(ordemServico.status === 'ABERTA' || ordemServico.status === 'EM_EXECUCAO') && (
                <button className="btn btn-warning me-2" onClick={() => handleAction('cancel', ordemServico.id)}>❌ Cancelar OS</button>
            )}
        </div>
      </div>
    </div>
  );
};

export default OrdemServicoDetailView;
