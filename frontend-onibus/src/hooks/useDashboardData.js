// src/hooks/useDashboardData.js
import { useState, useEffect } from 'react';
import { onibusService } from '../services/onibusService';
import { ordemServicoService } from '../services/ordemServicoService';
import { estoqueService } from '../services/estoqueService';

const useDashboardData = () => {
    const [data, setData] = useState({
        totalOnibus: 0,
        onibusEmOperacao: 0,
        osEmExecucao: 0,
        itensAbaixoMinimo: 0,
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchData = async () => {
        setLoading(true);
        setError(null);
        try {
            // ✅ Nomes dos métodos corrigidos
            const onibusTotalResponse = await onibusService.getAll();
            const onibusEmOperacaoResponse = await onibusService.getByStatus('EM_OPERACAO');
            const osEmExecucaoResponse = await ordemServicoService.getEmExecucao();
            const estoqueAbaixoMinimoResponse = await estoqueService.getAbaixoMinimo();

            setData({
                totalOnibus: onibusTotalResponse.data.length,
                onibusEmOperacao: onibusEmOperacaoResponse.data.length,
                osEmExecucao: osEmExecucaoResponse.data.length,
                itensAbaixoMinimo: estoqueAbaixoMinimoResponse.data.length,
            });
        } catch (err) {
            console.error('❌ Erro ao buscar dados do dashboard:', err);
            setError('Não foi possível carregar os dados do painel.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    return { data, loading, error, refetch: fetchData };
};

export default useDashboardData;