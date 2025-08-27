// src/services/estoqueService.js
import api from './api';

export const estoqueService = {
  // Métodos CRUD básicos (já existiam)
  getAll: () => api.get('/estoque'),
  getByProdutoId: (produtoId) => api.get(`/estoque/produto/${produtoId}`),
  create: (estoque) => api.post('/estoque', estoque),
  update: (id, estoque) => api.put(`/estoque/${id}`, estoque),

  // ✅ NOVOS MÉTODOS DE BUSCA E RELATÓRIOS
  getAbaixoMinimo: () => api.get('/estoque/abaixo-minimo'),
  getCritico: () => api.get('/estoque/critico'),
  getParaReabastecer: () => api.get('/estoque/para-reabastecer'),
  getAlertas: () => api.get('/estoque/alertas'),
  
  // ✅ NOVOS MÉTODOS DE GESTÃO
  adicionar: (produtoId, quantidade) =>
    api.patch(`/estoque/produto/${produtoId}/adicionar`, null, {
      params: { quantidade },
    }),
  reservar: (produtoId, quantidade) =>
    api.patch(`/estoque/produto/${produtoId}/reservar`, null, {
      params: { quantidade },
    }),
  consumir: (produtoId, quantidade) =>
    api.patch(`/estoque/produto/${produtoId}/consumir`, null, {
      params: { quantidade },
    }),
  liberarReserva: (produtoId, quantidade) =>
    api.patch(`/estoque/produto/${produtoId}/liberar-reserva`, null, {
      params: { quantidade },
    }),

  // ✅ NOVOS MÉTODOS DE CONSULTA
  getQuantidadeDisponivel: (produtoId) =>
    api.get(`/estoque/produto/${produtoId}/quantidade-disponivel`),
  getValorTotal: () => api.get('/estoque/valor-total'),
  getValorTotalPorCategoria: () => api.get('/estoque/valor-total-por-categoria'),
};