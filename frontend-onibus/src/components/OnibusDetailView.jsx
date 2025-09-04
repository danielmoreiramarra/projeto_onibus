import React, { useState } from 'react';
import { onibusService } from '../services/onibusService';
import { PosicaoPneu } from '../constants/pneuEnums';
import ViagemModal from './ViagemModal';

const OnibusDetailView = ({ onibus, availableComponents, onReturn, onUpdate }) => {
    // Estados locais para controlar os inputs dos seletores
    const [motorId, setMotorId] = useState('');
    const [cambioId, setCambioId] = useState('');
    const [pneuId, setPneuId] = useState('');
    const [posicaoPneu, setPosicaoPneu] = useState('');
    const [showViagemModal, setShowViagemModal] = useState(false);
  
    const posicoesOcupadas = onibus.pneus?.map(p => p.posicao) || [];
    const posicoesDisponiveis = Object.values(PosicaoPneu).filter(p => !posicoesOcupadas.includes(p));

    // Função genérica para chamar o serviço e atualizar a lista na página pai
    const handleAction = async (action, ...params) => {
        // Exibe uma confirmação mais amigável
        const actionText = action.replace(/([A-Z])/g, ' $1').toLowerCase();
        if (!window.confirm(`Tem certeza que deseja ${actionText}?`)) {
            return;
        }
        try {
            await onibusService[action](...params);
            onUpdate(); // Chama a função refetch do pai para atualizar os dados
        } catch (err) {
            console.error(`Erro ao executar a ação ${action}:`, err);
            alert(`Falha ao executar: ${err.response?.data || err.message}`); // Mostra um alerta mais informativo
        }
    };

    return (
        // <<< AJUSTE: Usa um Fragment <> para encapsular o Card e o Modal
        <>
            <div className="card my-4">
                <div className="card-header d-flex justify-content-between align-items-center">
                    <h5 className="card-title mb-0">Detalhes do Ônibus: {onibus.placa} ({onibus.numeroFrota})</h5>
                    <button onClick={onReturn} className="btn btn-secondary">Voltar para a Lista</button>
                </div>
                
                <div className="card-body">
                    {/* <<< SEÇÃO DE AÇÕES CORRIGIDA E COMPLETA >>> */}
                    <div className="border p-3 rounded mb-4">
                        <h6 className="mb-3">Ações de Operação</h6>
                        <div className="d-flex gap-2">
                            {(onibus.status === 'DISPONIVEL' || onibus.status === 'NOVO') && (
                                <button 
                                    className="btn btn-success"
                                    onClick={() => handleAction('colocarEmOperacao', onibus.id)}
                                >
                                    ✅ Colocar em Operação
                                </button>
                            )}

                            {onibus.status === 'EM_OPERACAO' && (
                                <>
                                    <button 
                                        className="btn btn-warning"
                                        onClick={() => handleAction('retirarDeOperacao', onibus.id)}
                                    >
                                        ⏸️ Retirar de Operação
                                    </button>
                                    <button
                                        className="btn btn-info"
                                        onClick={() => setShowViagemModal(true)}
                                    >
                                        🚌 Registrar Viagem
                                    </button>
                                </>
                            )}
                        </div>
                    </div>

                    {/* --- Seção de Gerenciamento de Componentes --- */}
                    <div className="border p-3 rounded">
                        <h6 className="mb-3">Gerenciar Componentes</h6>
                        <p className="text-muted small">O ônibus deve estar com status 'DISPONÍVEL' ou 'NOVO' para alterar componentes.</p>
                        
                        <div className="row g-3">
                            {/* Instalar/Remover Motor */}
                            <div className="col-md-6">
                                <label className="form-label">Motor</label>
                                {onibus.motor ? (
                                    <div className="input-group">
                                        <input type="text" className="form-control" disabled value={`Modelo: ${onibus.motor.modelo} (ID: ${onibus.motor.id})`} />
                                        <button className="btn btn-danger" onClick={() => handleAction('removerMotor', onibus.id)} disabled={onibus.status !== 'DISPONIVEL' && onibus.status !== 'NOVO'}>Remover</button>
                                    </div>
                                ) : (
                                    <div className="input-group">
                                        <select className="form-select" value={motorId} onChange={e => setMotorId(e.target.value)} disabled={onibus.status !== 'DISPONIVEL' && onibus.status !== 'NOVO'}>
                                            <option value="">Selecione um motor</option>
                                            {availableComponents.motores.map(m => <option key={m.id} value={m.id}>{m.modelo} (ID: {m.id})</option>)}
                                        </select>
                                        <button className="btn btn-primary" onClick={() => handleAction('instalarMotor', onibus.id, motorId)} disabled={!motorId || (onibus.status !== 'DISPONIVEL' && onibus.status !== 'NOVO')}>Instalar</button>
                                    </div>
                                )}
                            </div>

                            {/* Instalar/Remover Câmbio (lógica preenchida) */}
                            <div className="col-md-6">
                                <label className="form-label">Câmbio</label>
                                {onibus.cambio ? (
                                    <div className="input-group">
                                        <input type="text" className="form-control" disabled value={`Modelo: ${onibus.cambio.modelo} (ID: ${onibus.cambio.id})`} />
                                        <button className="btn btn-danger" onClick={() => handleAction('removerCambio', onibus.id)} disabled={onibus.status !== 'DISPONIVEL' && onibus.status !== 'NOVO'}>Remover</button>
                                    </div>
                                ) : (
                                    <div className="input-group">
                                        <select className="form-select" value={cambioId} onChange={e => setCambioId(e.target.value)} disabled={onibus.status !== 'DISPONIVEL' && onibus.status !== 'NOVO'}>
                                            <option value="">Selecione um câmbio</option>
                                            {availableComponents.cambios.map(c => <option key={c.id} value={c.id}>{c.modelo} (ID: {c.id})</option>)}
                                        </select>
                                        <button className="btn btn-primary" onClick={() => handleAction('instalarCambio', onibus.id, cambioId)} disabled={!cambioId || (onibus.status !== 'DISPONIVEL' && onibus.status !== 'NOVO')}>Instalar</button>
                                    </div>
                                )}
                            </div>

                            {/* Instalar Pneu */}
                            <div className="col-12 mt-4">
                                <label className="form-label">Instalar Novo Pneu</label>
                                <div className="input-group">
                                    <select className="form-select" value={pneuId} onChange={e => setPneuId(e.target.value)} disabled={onibus.status !== 'DISPONIVEL' && onibus.status !== 'NOVO'}>
                                        <option value="">Selecione um pneu disponível</option>
                                        {availableComponents.pneus.map(p => <option key={p.id} value={p.id}>{p.modelo} (Série: {p.numeroSerie})</option>)}
                                    </select>
                                    <select className="form-select" value={posicaoPneu} onChange={e => setPosicaoPneu(e.target.value)} disabled={onibus.status !== 'DISPONIVEL' && onibus.status !== 'NOVO'}>
                                        <option value="">Selecione uma posição livre</option>
                                        {posicoesDisponiveis.map(p => <option key={p} value={p}>{p}</option>)}
                                    </select>
                                    <button className="btn btn-primary" onClick={() => handleAction('instalarPneu', onibus.id, pneuId, posicaoPneu)} disabled={!pneuId || !posicaoPneu || (onibus.status !== 'DISPONIVEL' && onibus.status !== 'NOVO')}>Instalar Pneu</button>
                                </div>
                            </div>

                            {/* Tabela de Pneus Instalados */}
                            <div className="col-12 mt-4">
                                <h6 className="mt-4">Pneus Instalados ({onibus.pneus?.length || 0}/6)</h6>
                                <table className="table table-sm table-bordered">
                                    <thead className="table-light">
                                        <tr><th>ID</th><th>Modelo</th><th>Posição</th><th>Ação</th></tr>
                                    </thead>
                                    <tbody>
                                        {onibus.pneus?.map(pneu => (
                                            <tr key={pneu.id}>
                                                <td>{pneu.id}</td>
                                                <td>{pneu.modelo}</td>
                                                <td>{pneu.posicao}</td>
                                                <td>
                                                    <button 
                                                        className="btn btn-danger btn-sm"
                                                        onClick={() => handleAction('removerPneu', onibus.id, pneu.posicao)}
                                                        disabled={onibus.status !== 'DISPONIVEL' && onibus.status !== 'NOVO'}
                                                    >
                                                        Remover
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* <<< O MODAL É RENDERIZADO AQUI FORA, COMO "IRMÃO" DO CARD >>> */}
            <ViagemModal
                onibus={onibus}
                show={showViagemModal}
                onClose={() => setShowViagemModal(false)}
                onSuccess={() => {
                    setShowViagemModal(false);
                    onUpdate(); // Atualiza os dados do ônibus na tela principal
                }}
            />
        </>
    );
};

export default OnibusDetailView;