import React from 'react';
import { ordemServicoService } from '../services/ordemServicoService';

const CambioDetailView = ({ cambio, onReturn, onUpdate }) => {

  const handleCreateOsRevisao = async () => {
    if (!window.confirm(`Tem certeza que deseja criar uma OS Corretiva para enviar o câmbio ${cambio.modelo} (ID: ${cambio.id}) para revisão?`)) {
      return;
    }
    try {
      // Gera um número de OS único no frontend para enviar ao backend
      const numeroOS = `OS-CORR-${Date.now()}`;
      
      const osDTO = {
        numeroOS: numeroOS,
        tipo: 'CORRETIVA',
        descricao: `Revisão corretiva para o câmbio ${cambio.marca} ${cambio.modelo}, N/S: ${cambio.numeroSerie}.`,
        dataPrevisaoInicio: new Date().toISOString().split('T')[0], // Hoje
        dataPrevisaoConclusao: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0], // 7 dias a partir de hoje
        cambioId: cambio.id, // Define o câmbio como o alvo da OS
        onibusId: cambio.onibus?.id || null // Associa o ônibus se o câmbio estiver instalado
      };

      await ordemServicoService.create(osDTO);
      alert('Ordem de Serviço para revisão criada com sucesso!');
      onUpdate(); // Atualiza a lista para refletir a mudança de status do câmbio
    } catch (err) {
      console.error("Erro ao criar Ordem de Serviço:", err);
      alert(`Falha ao criar OS: ${err.response?.data || err.message}`);
    }
  };

  return (
    <div className="card my-4">
      <div className="card-header d-flex justify-content-between align-items-center">
        <h5 className="card-title mb-0">Detalhes do Câmbio: {cambio.modelo}</h5>
        <button onClick={onReturn} className="btn btn-secondary">Voltar para a Lista</button>
      </div>
      <div className="card-body">
        {/* Detalhes do Câmbio */}
        <div className="row">
            <div className="col-md-6">
                <p><strong>Marca:</strong> {cambio.marca}</p>
                <p><strong>Modelo:</strong> {cambio.modelo}</p>
                <p><strong>Tipo:</strong> {cambio.tipo}</p>
            </div>
            <div className="col-md-6">
                <p><strong>Status:</strong> {cambio.status}</p>
                <p><strong>Número de Marchas:</strong> {cambio.numeroMarchas}</p>
            </div>
        </div>
        
        {/* Detalhes do Ônibus (se instalado) */}
        {cambio.onibus && (
          <div className="alert alert-info mt-3">
            Instalado no Ônibus: <strong>{cambio.onibus.placa}</strong> (Frota: {cambio.onibus.numeroFrota})
          </div>
        )}

        {/* Ações */}
        <div className="mt-4">
          <h6>Ações</h6>
          <button 
            className="btn btn-warning" 
            onClick={handleCreateOsRevisao}
            disabled={cambio.status === 'EM_MANUTENCAO' || cambio.status === 'EM_REVISAO'}
          >
            🔧 Enviar para Revisão (Criar OS)
          </button>
        </div>
      </div>
    </div>
  );
};

export default CambioDetailView;
