import { useState, useCallback } from 'react';

/**
 * Hook genérico para encapsular o estado de loading e error de uma chamada de API.
 */
const useApi = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const callApi = useCallback(async (apiCall) => {
    setLoading(true);
    setError(null);
    try {
      const response = await apiCall();
      return response;
    } catch (err) {
      const errorMessage = err.response?.data?.message || err.message || 'Ocorreu um erro desconhecido.';
      setError(errorMessage);
      throw err; // Re-lança o erro para que o chamador possa tratá-lo se necessário
    } finally {
      setLoading(false);
    }
  }, []);

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return { loading, error, callApi, clearError };
};

export default useApi;
