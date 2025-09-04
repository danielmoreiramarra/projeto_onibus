import api from './api';

/**
 * Objeto que encapsula todas as chamadas de API para a entidade 'Motor'.
 */
export const motorService = {
  
  // --- Métodos CRUD ---
  create: (motorCreateDTO) => api.post('/motores', motorCreateDTO),
  update: (id, motorUpdateDTO) => api.put(`/motores/${id}`, motorUpdateDTO),
  delete: (id) => api.delete(`/motores/${id}`),
  
  // --- Consultas ---
  getAll: () => api.get('/motores'),
  getById: (id) => api.get(`/motores/${id}`),
  search: (terms) => api.get('/motores/search', { params: terms }),

  // --- Ações de Negócio (Ciclo de Vida) ---
  enviarParaManutencao: (motorId) => api.patch(`/motores/${motorId}/enviar-manutencao`),
  retornarDeManutencao: (motorId) => api.patch(`/motores/${motorId}/retornar-manutencao`),
  enviarParaRevisao: (motorId) => api.patch(`/motores/${motorId}/enviar-revisao`),
  retornarDaRevisao: (motorId) => api.patch(`/motores/${motorId}/retornar-revisao`),
};
