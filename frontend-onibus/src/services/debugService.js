// src/services/debugService.js
import api from './api';

export const debugService = {
  testConnection: async () => {
    try {
      console.log('🧪 Testando conexão com a API...');
      // ✅ Use um endpoint que existe, não "/"
      const response = await api.get('/cambios');
      console.log('✅ Conexão bem-sucedida:', response.status);
      return true;
    } catch (error) {
      console.error('❌ Falha na conexão:', error);
      return false;
    }
  },

  testAuth: async () => {
    try {
      console.log('🔐 Testando autenticação...');
      const response = await api.get('/cambios');
      console.log('✅ Autenticação bem-sucedida:', response.status);
      return true;
    } catch (error) {
      console.error('❌ Falha na autenticação:', error);
      return false;
    }
  }
};