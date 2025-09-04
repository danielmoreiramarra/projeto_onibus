import React from 'react';
import { ordemServicoService } from '../services/ordemServicoService';

const DetailRow = ({ label, value, unit = '' }) => (
    <div className="col-md-4 mb-3">
        <strong className="d-block text-muted">{label}</strong>
        <span>{value ?? 'N/A'} {unit}</span>
    </div>
);

const MotorDetailView = ({ motor, onReturn, onUpdate }) => {
  if (!motor) return null;

  const handleCreateOsRevisao = async () => {
    if (!window.confirm(`Criar uma OS Corretiva para revisar o motor ${motor.modelo}?`)) return;
    try {
      const numeroOS = `OS-CORR-${Date.now()}`;
      const osDTO = {
        numeroOS,
        tipo: 'CORRETIVA',
        descricao: `Revisão corretiva para o motor ${motor.marca} ${motor.modelo}, N/S: ${motor.numeroSerie}.`,
        dataPrevisaoInicio: new Date().toISOString().split('T')[0],
        dataPrevisaoConclusao: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
        motorId: motor.id,
        onibusId: motor.onibus?.id || null,
      };
      await ordemServicoService.create(osDTO);
      alert('Ordem de Serviço criada com sucesso!');
      onUpdate();
    } catch (err) {
      alert(`Falha ao criar OS: ${err.response?.data || err.message}`);
    }
  };

  return (
    <div className="card my-4">
      <div className="card-header d-flex justify-content-between align-items-center">
        <h5 className="mb-0">Detalhes do Motor: {motor.modelo}</h5>
        <button onClick={onReturn} className="btn btn-secondary">Voltar</button>
      </div>
      <div className="card-body">
        {/* <<< VISUALIZAÇÃO COMPLETA DOS ATRIBUTOS >>> */}
        <div className="row border-bottom pb-3 mb-3">
            <DetailRow label="Marca" value={motor.marca} />
            <DetailRow label="Modelo" value={motor.modelo} />
            <DetailRow label="Nº de Série" value={motor.numeroSerie} />
            <DetailRow label="Status" value={motor.status} />
            <DetailRow label="Potência" value={motor.potencia} unit="CV" />
            <DetailRow label="Cilindrada" value={motor.cilindrada} unit="cc" />
            <DetailRow label="Ano de Fabricação" value={motor.anoFabricacao} />
            <DetailRow label="Data da Compra" value={motor.dataCompra} />
            <DetailRow label="Garantia" value={`${motor.periodoGarantiaMeses} meses`} />
        </div>
        <div className="row border-bottom pb-3 mb-3">
            <DetailRow label="Tipo de Óleo" value={motor.tipoOleo} />
            <DetailRow label="Capacidade de Óleo" value={motor.capacidadeOleo} unit="L" />
            <DetailRow label="Quantidade Atual de Óleo" value={motor.quantidadeOleo} unit="L" />
        </div>
        <div className="row border-bottom pb-3 mb-3">
             <DetailRow label="Última Manutenção" value={motor.dataUltimaManutencao} />
             <DetailRow label="Próxima Manutenção em" value={`${motor.diasRestantesManutencao} dias`} />
             <DetailRow label="Última Revisão" value={motor.dataUltimaRevisao} />
             <DetailRow label="Próxima Revisão em" value={`${motor.diasRestantesRevisao} dias`} />
             <DetailRow label="Garantia Restante" value={`${motor.diasRestantesGarantia} dias`} />
        </div>
        
        {motor.onibus && (
          <div className="alert alert-info">
            Instalado no Ônibus: <strong>{motor.onibus.placa}</strong> (Frota: {motor.onibus.numeroFrota})
          </div>
        )}
        
        <div className="mt-4">
          <h6>Ações</h6>
          <button 
            className="btn btn-warning" 
            onClick={handleCreateOsRevisao}
            disabled={motor.status === 'EM_MANUTENCAO' || motor.status === 'EM_REVISAO'}
          >
            🔧 Enviar para Revisão (Criar OS)
          </button>
        </div>
      </div>
    </div>
  );
};

export default MotorDetailView;
