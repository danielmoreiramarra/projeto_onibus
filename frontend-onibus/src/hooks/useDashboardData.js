import { useState, useEffect, useCallback } from 'react';
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

    const fetchData = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            // <<< MELHORIA: Executa todas as chamadas em paralelo
            const [
                onibusTotalResponse,
                onibusEmOperacaoResponse,
                osEmExecucaoResponse,
                estoqueAbaixoMinimoResponse,
            ] = await Promise.all([
                onibusService.getAll(),
                onibusService.search({ status: 'EM_OPERACAO' }),
                ordemServicoService.search({ status: 'EM_EXECUCAO' }),
                estoqueService.getEstoqueAbaixoDoMinimo(),
            ]);

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
    }, []); // useCallback com array vazio, pois a função não depende de props

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    return { data, loading, error, refetch: fetchData };
};

export default useDashboardData;
