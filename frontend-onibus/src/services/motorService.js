import api from './api';

export const motorService = {
  getAll: () => api.get('/motores'),
  getById: (id) => api.get(`/motores/${id}`),
  create: (motor) => api.post('/motores', motor),
  update: (id, motor) => api.put(`/motores/${id}`, motor),
  delete: (id) => api.delete(`/motores/${id}`),
  
  // ✅ NOVO MÉTODO: Busca combinada
  search: (terms) => api.get('/motores/search', { params: terms }),

  // Métodos de busca individual (mantidos por compatibilidade)
  getByStatus: (status) => api.get(`/motores/status/${status}`),
  getByTipo: (tipo) => api.get(`/motores/tipo/${tipo}`),
  getByMarca: (marca) => api.get(`/motores/marca/${marca}`),
  
  // Métodos de relatórios (sem alteração)
  getParaRevisao: () => api.get('/motores/para-revisao'),
  getGarantiaPrestesVencer: () => api.get('/motores/garantia-prestes-vencer'),
};