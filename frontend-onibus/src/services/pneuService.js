import api from './api';

export const pneuService = {
  getAll: () => api.get('/pneus'),
  getById: (id) => api.get(`/pneus/${id}`),
  create: (pneu) => api.post('/pneus', pneu),
  update: (id, pneu) => api.put(`/pneus/${id}`, pneu),
  delete: (id) => api.delete(`/pneus/${id}`),
  
  // ✅ NOVO MÉTODO: Busca combinada
  search: (terms) => api.get('/pneus/search', { params: terms }),

  // Métodos de busca individual (mantidos por compatibilidade)
  getByStatus: (status) => api.get(`/pneus/status/${status}`),
  getByMarca: (marca) => api.get(`/pneus/marca/${marca}`),
  getByMedida: (medida) => api.get(`/pneus/medida/${medida}`),
  
  // Métodos de relatórios (sem alteração)
  getParaTroca: () => api.get('/pneus/para-troca'),
  getGarantiaPrestesVencer: () => api.get('/pneus/garantia-prestes-vencer'),
};