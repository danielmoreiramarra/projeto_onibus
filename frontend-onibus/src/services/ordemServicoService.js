import api from './api';

export const ordemServicoService = {
  // --- CRUD e Ciclo de Vida da OS ---
  create: (osCreateDTO) => api.post('/ordens-servico', osCreateDTO),
  updateInfo: (id, osUpdateDTO) => api.put(`/ordens-servico/${id}`, osUpdateDTO),
  delete: (id) => api.delete(`/ordens-servico/${id}`),
  
  // --- Consultas ---
  getAll: () => api.get('/ordens-servico'),
  getById: (id) => api.get(`/ordens-servico/${id}`),
  search: (terms) => api.get('/ordens-servico/search', { params: terms }),

  // --- Ações de Fluxo de Trabalho ---
  startExecution: (id) => api.patch(`/ordens-servico/${id}/start`),
  finishExecution: (id) => api.patch(`/ordens-servico/${id}/finish`),
  cancel: (id) => api.patch(`/ordens-servico/${id}/cancel`),

  // --- Gerenciamento de Itens (através da OS) ---
  addItem: (osId, itemDTO) => api.post(`/ordens-servico/${osId}/itens`, itemDTO),
  removeItem: (osId, itemId) => api.delete(`/ordens-servico/${osId}/itens/${itemId}`),
  updateItemQuantity: (osId, itemId, itemDTO) => api.put(`/ordens-servico/${osId}/itens/${itemId}`, itemDTO),
};

