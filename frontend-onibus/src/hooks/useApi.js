// src/hooks/useApi.js
import { useState, useCallback } from 'react';

const useApi = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const callApi = useCallback(async (apiCall, successMessage = null) => {
    setLoading(true);
    setError(null);
    
    try {
      const response = await apiCall();
      if (successMessage) {
        console.log('✅', successMessage);
      }
      return response;
    } catch (err) {
      console.error('❌ Erro na API:', err);
      setError(err.response?.data?.message || err.message || 'Erro desconhecido');
      throw err;
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