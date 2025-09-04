import React from 'react';
import { ordemServicoService } from '../services/ordemServicoService';

const PneuDetailView = ({ pneu, onReturn, onUpdate }) => {

  const handleCreateOsReforma = async () => {
    if (!window.confirm(`Tem certeza que deseja criar uma OS Corretiva para enviar o pneu ${pneu.modelo} (ID: ${pneu.id}) para reforma?`)) {
      return;
    }
    try {
      const numeroOS = `OS-CORR-${Date.now()}`;
      
      const osDTO = {
        numeroOS: numeroOS,
        tipo: 'CORRETIVA',
        descricao: `Reforma corretiva para o pneu ${pneu.marca} ${pneu.modelo}, N/S: ${pneu.numeroSerie}.`,
        dataPrevisaoInicio: new Date().toISOString().split('T')[0],
        dataPrevisaoConclusao: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000).toISOString().split('T')[0], // 5 dias
        pneuId: pneu.id,
        onibusId: pneu.onibus?.id || null
      };

      await ordemServicoService.create(osDTO);
      alert('Ordem de Serviço para reforma criada com sucesso!');
      onUpdate(); // Atualiza a lista para refletir a mudança de status do pneu
    } catch (err) {
      console.error("Erro ao criar Ordem de Serviço:", err);
      alert(`Falha ao criar OS: ${err.response?.data || err.message}`);
    }
  };

  return (
    <div className="card my-4">
      <div className="card-header d-flex justify-content-between align-items-center">
        <h5 className="card-title mb-0">Detalhes do Pneu: {pneu.modelo} (Série: {pneu.numeroSerie})</h5>
        <button onClick={onReturn} className="btn btn-secondary">Voltar</button>
      </div>
      <div className="card-body">
        <div className="row">
          <div className="col-md-6">
            <p><strong>Marca:</strong> {pneu.marca}</p>
            <p><strong>Medida:</strong> {pneu.medida}</p>
            <p><strong>Status:</strong> {pneu.status}</p>
          </div>
          <div className="col-md-6">
            <p><strong>KM Rodados:</strong> {pneu.kmRodados.toFixed(2)} km</p>
            <p><strong>Posição:</strong> {pneu.posicao || 'Não instalado'}</p>
          </div>
        </div>
        
        {pneu.onibus && (
          <div className="alert alert-info mt-3">
            Instalado no Ônibus: <strong>{pneu.onibus.placa}</strong> (Frota: {pneu.onibus.numeroFrota})
          </div>
        )}

        <div className="mt-4">
          <h6>Ações</h6>
          <button 
            className="btn btn-warning" 
            onClick={handleCreateOsReforma}
            disabled={pneu.status === 'EM_MANUTENCAO' || pneu.status === 'EM_REFORMA'}
          >
             reformar Pneu (Criar OS)
          </button>
        </div>
      </div>
    </div>
  );
};

export default PneuDetailView;
