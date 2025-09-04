import api from './api';

/**
 * Objeto que encapsula todas as chamadas de API para a entidade 'Pneu'.
 */
export const pneuService = {
  
  // --- Métodos CRUD ---
  create: (pneuCreateDTO) => api.post('/pneus', pneuCreateDTO),
  update: (id, pneuUpdateDTO) => api.put(`/pneus/${id}`, pneuUpdateDTO),
  delete: (id) => api.delete(`/pneus/${id}`),
  
  // --- Consultas ---
  getAll: () => api.get('/pneus'),
  getById: (id) => api.get(`/pneus/${id}`),
  search: (terms) => api.get('/pneus/search', { params: terms }),

  // --- Ações de Negócio (Ciclo de Vida) ---
  enviarParaManutencao: (pneuId) => api.patch(`/pneus/${pneuId}/enviar-manutencao`),
  retornarDeManutencao: (pneuId) => api.patch(`/pneus/${pneuId}/retornar-manutencao`),
  enviarParaReforma: (pneuId) => api.patch(`/pneus/${pneuId}/enviar-reforma`),
  retornarDeReforma: (pneuId) => api.patch(`/pneus/${pneuId}/retornar-reforma`),
};
