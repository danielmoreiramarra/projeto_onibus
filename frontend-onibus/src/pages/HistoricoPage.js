// src/pages/HistoricoPage.js
import React from 'react';
import useSearch from '../hooks/useSearch';
import { ordemServicoService } from '../services/ordemServicoService';
import CrudTable from '../components/CrudTable';
import BackButton from '../components/BackButton';

const HistoricoPage = () => {
    const { data: ordens, loading, error } = useSearch(ordemServicoService);

    const columns = [
        { key: 'numeroOS', label: 'Número OS' },
        { key: 'onibus.numeroFrota', label: 'Frota' },
        { key: 'tipo', label: 'Tipo' },
        { key: 'status', label: 'Status' },
        { key: 'dataConclusao', label: 'Data Conclusão' },
        { key: 'valorTotal', label: 'Valor Total', format: (value) => `R$ ${value?.toFixed(2)}` }
    ];

    const historicoFiltrado = ordens.filter(
        (os) => os.status === 'FINALIZADA' || os.status === 'CANCELADA'
    );

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>📋 Histórico de Ordens de Serviço</h2>
                <BackButton />
            </div>
            
            {loading && <div className="text-center">⏳ Carregando histórico...</div>}
            {error && <div className="alert alert-danger">{error}</div>}
            
            {!loading && historicoFiltrado.length === 0 ? (
                <div className="alert alert-info">Nenhuma ordem de serviço no histórico.</div>
            ) : (
                <CrudTable data={historicoFiltrado} columns={columns} />
            )}
        </div>
    );
};

export default HistoricoPage;