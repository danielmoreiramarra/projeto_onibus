// src/hooks/useSearch.js
import { useState, useEffect } from 'react';

const useSearch = (service, searchMapping) => {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [searchTerms, setSearchTerms] = useState({});
    const [shouldFetch, setShouldFetch] = useState(true);

    const fetchApi = async () => {
        setLoading(true);
        setError(null);
        try {
            let response;
            const terms = Object.fromEntries(
                Object.entries(searchTerms).filter(([_, v]) => v != null && v !== '')
            );
            
            if (Object.keys(terms).length > 0) {
                // ✅ Agora o hook chama o método search do service com todos os termos
                response = await service.search(terms);
            } else {
                response = await service.getAll();
            }

            const responseData = response.data;
            if (Array.isArray(responseData)) {
                setData(responseData);
            } else if (responseData) {
                setData([responseData]);
            } else {
                setData([]);
            }
        } catch (err) {
            setError(err.response?.data?.message || err.message || 'Erro ao buscar dados.');
            setData([]);
        } finally {
            setLoading(false);
        }
        setShouldFetch(false);
    };

    useEffect(() => {
        if (shouldFetch) {
            fetchApi();
        }
    }, [shouldFetch, searchTerms, service]);

    const onSearch = (terms) => {
        setSearchTerms(terms);
        setShouldFetch(true);
    };
    
    const refetch = () => {
        setShouldFetch(true);
    };

    return { data, loading, error, onSearch, refetch };
};

export default useSearch;