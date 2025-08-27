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
      const activeTerms = Object.keys(searchTerms).filter(key => searchTerms[key]);
      
      if (activeTerms.length > 0) {
        // Encontra o método de busca correspondente no 'searchMapping'
        const term = activeTerms[0];
        const searchValue = searchTerms[term];
        const searchFunction = searchMapping[term];
        if (searchFunction) {
          response = await searchFunction(searchValue);
        } else {
          // Se não houver mapeamento, volta a buscar todos
          response = await service.getAll();
        }
      } else {
        response = await service.getAll();
      }
      setData(response.data);
    } catch (err) {
      setError(err.response?.data || err.message || 'Erro ao buscar dados.');
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
  }, [shouldFetch, searchTerms, service, searchMapping]);

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