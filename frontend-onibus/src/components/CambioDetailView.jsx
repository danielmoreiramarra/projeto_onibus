import React from 'react';
import { ordemServicoService } from '../services/ordemServicoService';

const DetailRow = ({ label, value, unit = '' }) => (
    <div className="col-md-4 mb-3">
        <strong className="d-block text-muted">{label}</strong>
        <span>{value ?? 'N/A'} {unit}</span>
    </div>
);

const CambioDetailView = ({ cambio, onReturn, onUpdate }) => {
  if (!cambio) return null;

  const handleCreateOsRevisao = async () => {
    if (!window.confirm(`Criar uma OS Corretiva para revisar o câmbio ${cambio.modelo}?`)) return;
    try {
      const numeroOS = `OS-CORR-${Date.now()}`;
      const osDTO = {
        numeroOS,
        tipo: 'CORRETIVA',
        descricao: `Revisão corretiva para o câmbio ${cambio.marca} ${cambio.modelo}, N/S: ${cambio.numeroSerie}.`,
        dataPrevisaoInicio: new Date().toISOString().split('T')[0],
        dataPrevisaoConclusao: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
        cambioId: cambio.id,
        onibusId: cambio.onibus?.id || null,
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
        <h5 className="mb-0">Detalhes do Câmbio: {cambio.modelo} (ID: {cambio.id})</h5>
        <button onClick={onReturn} className="btn btn-secondary">Voltar</button>
      </div>
      <div className="card-body">
        <h6 className="card-subtitle mb-3 text-muted">Dados de Identificação</h6>
        <div className="row border-bottom pb-3 mb-3">
            <DetailRow label="Marca" value={cambio.marca} />
            <DetailRow label="Modelo" value={cambio.modelo} />
            <DetailRow label="Nº de Série" value={cambio.numeroSerie} />
            <DetailRow label="Código Fabricação" value={cambio.codigoFabricacao} />
            <DetailRow label="Ano de Fabricação" value={cambio.anoFabricacao} />
            <DetailRow label="Status" value={cambio.status} />
        </div>
        <h6 className="card-subtitle mb-3 text-muted">Dados Técnicos</h6>
        <div className="row border-bottom pb-3 mb-3">
            <DetailRow label="Tipo de Câmbio" value={cambio.tipo} />
            <DetailRow label="Nº de Marchas" value={cambio.numeroMarchas} />
            <DetailRow label="Tipo de Fluido" value={cambio.tipoFluido} />
            <DetailRow label="Capacidade de Fluido" value={cambio.capacidadeFluido} unit="L" />
            <DetailRow label="Qtd. Atual de Fluido" value={cambio.quantidadeFluido} unit="L" />
        </div>
        <h6 className="card-subtitle mb-3 text-muted">Datas e Garantia</h6>
        <div className="row border-bottom pb-3 mb-3">
             <DetailRow label="Data da Compra" value={cambio.dataCompra} />
             <DetailRow label="Período de Garantia" value={cambio.periodoGarantiaMeses} unit="meses" />
             <DetailRow label="Garantia Restante" value={`${cambio.diasRestantesGarantia} dias`} />
             <DetailRow label="Última Manutenção" value={cambio.dataUltimaManutencao} />
             <DetailRow label="Próxima Manutenção em" value={`${cambio.diasRestantesManutencao} dias`} />
             <DetailRow label="Última Revisão" value={cambio.dataUltimaRevisao} />
             <DetailRow label="Próxima Revisão em" value={`${cambio.diasRestantesRevisao} dias`} />
        </div>
        {cambio.onibus && (
          <div className="alert alert-info">
            Instalado no Ônibus: <strong>{cambio.onibus.placa}</strong> (Frota: {cambio.onibus.numeroFrota})
          </div>
        )}
        <div className="mt-4">
          <h6>Ações</h6>
          <button className="btn btn-warning" onClick={handleCreateOsRevisao} disabled={cambio.status === 'EM_MANUTENCAO' || cambio.status === 'EM_REVISAO'}>
            🔧 Enviar para Revisão (Criar OS)
          </button>
        </div>
      </div>
    </div>
  );
};
export default CambioDetailView;
