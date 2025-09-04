import api from './api';

export const produtoService = {
  // --- Métodos CRUD ---
  create: (produtoCreateDTO) => api.post('/produtos', produtoCreateDTO),
  update: (id, produtoUpdateDTO) => api.put(`/produtos/${id}`, produtoUpdateDTO),
  updatePrice: (id, novoPreco) => api.patch(`/produtos/${id}/price`, { novoPreco }),
  archive: (id) => api.delete(`/produtos/${id}`), // Soft delete
  
  // --- Consultas ---
  getAll: () => api.get('/produtos'),
  getById: (id) => api.get(`/produtos/${id}`),
  search: (terms) => api.get('/produtos/search', { params: terms }),

  // --- Relatórios e Auxiliares ---
  getNextCode: () => api.get('/produtos/proximo-codigo'),
  getEstoqueBaixo: () => api.get('/produtos/alertas/estoque-baixo'),
  getMaisUtilizados: () => api.get('/produtos/relatorios/mais-utilizados'),
};
