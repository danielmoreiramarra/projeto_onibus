import { useState, useEffect, useCallback } from 'react';

/**
 * Hook genérico para gerenciar a busca e listagem de dados de um serviço.
 * @param {object} service - O objeto de serviço com os métodos `getAll` e `search`.
 */
const useSearch = (service) => {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true); // Inicia como true para o carregamento inicial
    const [error, setError] = useState(null);
    const [searchTerms, setSearchTerms] = useState({});

    // <<< MELHORIA: A lógica de busca é memoizada com useCallback
    const fetchData = useCallback(async () => {
        setLoading(true);
        setError(null);
        try {
            // Filtra termos de busca vazios ou nulos
            const activeTerms = Object.fromEntries(
                Object.entries(searchTerms).filter(([_, v]) => v != null && v !== '')
            );

            const response = Object.keys(activeTerms).length > 0
                ? await service.search(activeTerms)
                : await service.getAll();
            
            // A API sempre retorna um array, então podemos atribuir diretamente
            setData(response.data || []);

        } catch (err) {
            const errorMessage = err.response?.data?.message || err.message || 'Erro ao buscar dados.';
            setError(errorMessage);
            setData([]);
        } finally {
            setLoading(false);
        }
    }, [searchTerms, service]); // Recria a função apenas se os termos ou o serviço mudarem

    // <<< MELHORIA: useEffect é acionado pela mudança na função fetchData
    useEffect(() => {
        fetchData();
    }, [fetchData]);

    // Função que as páginas usarão para iniciar uma nova busca
    const onSearch = (terms) => {
        setSearchTerms(terms);
    };

    // A função de refetch é a mesma que a de busca, garantindo consistência
    const refetch = fetchData;

    return { data, loading, error, onSearch, refetch };
};

export default useSearch;
