// src/services/api.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Criar instância do axios com configurações corretas
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // ✅ IMPORTANTE: permite enviar credenciais CORS
});

// Interceptor para adicionar autenticação básica manualmente
api.interceptors.request.use(
  (config) => {
    console.log(`🔄 Fazendo request para: ${config.method?.toUpperCase()} ${config.url}`);
    console.log('📦 Dados enviados:', config.data);
    
    // ✅ Adiciona autenticação básica manualmente no header
    const username = 'admin';
    const password = '123456';
    const authToken = btoa(`${username}:${password}`);
    config.headers.Authorization = `Basic ${authToken}`;
    
    // ✅ Headers importantes para CORS
    config.headers['Access-Control-Allow-Credentials'] = 'true';
    
    return config;
  },
  (error) => {
    console.error('❌ Erro no request:', error);
    return Promise.reject(error);
  }
);

// Interceptor para tratamento de responses
api.interceptors.response.use(
  (response) => {
    console.log(`✅ Response de: ${response.config.url}`, response.status);
    return response;
  },
  (error) => {
    console.error('❌ Erro na response:', {
      url: error.config?.url,
      method: error.config?.method,
      status: error.response?.status,
      data: error.response?.data,
      message: error.message
    });
    
    if (error.response?.status === 401) {
      alert('🔐 Erro de autenticação. Verifique se o usuário e senha estão corretos.');
    } else if (error.response?.status === 403) {
      alert('🚫 Acesso negado. Verifique as permissões.');
    }
    
    return Promise.reject(error);
  }
);

export default api;