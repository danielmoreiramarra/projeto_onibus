import React from 'react';
import { ordemServicoService } from '../services/ordemServicoService';

// Componente auxiliar para padronizar a exibição de detalhes
const DetailRow = ({ label, value, unit = '' }) => (
    <div className="col-md-4 mb-3">
        <strong className="d-block text-muted">{label}</strong>
        <span>{value != null ? `${value} ${unit}`.trim() : 'N/A'}</span>
    </div>
);

const PneuDetailView = ({ pneu, onReturn, onUpdate }) => {
  if (!pneu) return null;

  const handleCreateOsReforma = async () => {
    if (!window.confirm(`Criar uma OS Corretiva para reformar o pneu ${pneu.modelo}?`)) return;
    try {
      const numeroOS = `OS-CORR-${Date.now()}`;
      const osDTO = {
        numeroOS,
        tipo: 'CORRETIVA',
        descricao: `Serviço de reforma para o pneu ${pneu.marca} ${pneu.modelo}, N/S: ${pneu.numeroSerie}.`,
        dataPrevisaoInicio: new Date().toISOString().split('T')[0],
        dataPrevisaoConclusao: new Date(Date.now() + 5 * 24 * 60 * 60 * 1000).toISOString().split('T')[0], // 5 dias de prazo
        pneuId: pneu.id,
        onibusId: pneu.onibus?.id || null,
      };
      await ordemServicoService.create(osDTO);
      alert('Ordem de Serviço para reforma criada com sucesso!');
      onUpdate(); // Atualiza a lista
    } catch (err) {
      alert(`Falha ao criar OS: ${err.response?.data || err.message}`);
    }
  };

  return (
    <div className="card my-4">
      <div className="card-header d-flex justify-content-between align-items-center">
        <h5 className="mb-0">Detalhes do Pneu: {pneu.modelo} (ID: {pneu.id})</h5>
        <button onClick={onReturn} className="btn btn-secondary">Voltar</button>
      </div>
      <div className="card-body">
        <h6 className="card-subtitle mb-3 text-muted">Dados de Identificação</h6>
        <div className="row border-bottom pb-3 mb-3">
            <DetailRow label="Marca" value={pneu.marca} />
            <DetailRow label="Modelo" value={pneu.modelo} />
            <DetailRow label="Medida" value={pneu.medida} />
            <DetailRow label="Nº de Série" value={pneu.numeroSerie} />
            <DetailRow label="Código Fabricação" value={pneu.codigoFabricacao} />
            <DetailRow label="Ano de Fabricação" value={pneu.anoFabricacao} />
            <DetailRow label="Status" value={pneu.status} />
        </div>
        
        <h6 className="card-subtitle mb-3 text-muted">Dados de Uso e Desgaste</h6>
        <div className="row border-bottom pb-3 mb-3">
            <DetailRow label="KM Rodados (desde a última reforma)" value={pneu.kmRodados?.toFixed(1)} unit="km" />
            <DetailRow label="KM Restantes para Manutenção" value={pneu.kmRestantesManutencao?.toFixed(1)} unit="km" />
            <DetailRow label="KM Restantes para Reforma" value={pneu.kmRestantesReforma?.toFixed(1)} unit="km" />
        </div>

        <h6 className="card-subtitle mb-3 text-muted">Datas e Garantia</h6>
        <div className="row border-bottom pb-3 mb-3">
             <DetailRow label="Data da Compra" value={pneu.dataCompra} />
             <DetailRow label="Período de Garantia" value={pneu.periodoGarantiaMeses} unit="meses" />
             <DetailRow label="Garantia Restante" value={`${pneu.diasRestantesGarantia} dias`} />
             <DetailRow label="Última Manutenção" value={pneu.dataUltimaManutencao} />
             <DetailRow label="Próxima Manutenção em" value={`${pneu.diasRestantesManutencao} dias`} />
             <DetailRow label="Última Reforma" value={pneu.dataUltimaReforma} />
             <DetailRow label="Próxima Reforma em" value={`${pneu.diasRestantesReforma} dias`} />
        </div>
        
        {pneu.onibus && (
          <div className="alert alert-info">
            Instalado no Ônibus: <strong>{pneu.onibus.placa}</strong> (Frota: {pneu.onibus.numeroFrota}) na posição <strong>{pneu.posicao}</strong>
          </div>
        )}

        <div className="mt-4">
          <h6>Ações</h6>
          <button 
            className="btn btn-warning" 
            onClick={handleCreateOsReforma}
            disabled={pneu.status === 'EM_MANUTENCAO' || pneu.status === 'EM_REFORMA'}
          >
            SENDO REFORMADO Enviar para Reforma (Criar OS)
          </button>
        </div>
      </div>
    </div>
  );
};
export default PneuDetailView;
