import api from './api';

export const estoqueService = {
  // --- Consultas ---
  getAll: () => api.get('/estoque'),
  getById: (id) => api.get(`/estoque/${id}`),
  getByProdutoId: (produtoId) => api.get(`/estoque/produto/${produtoId}`),
  search: (terms) => api.get('/estoque/search', { params: terms }),

  // --- Ações de Negócio ---
  // Adiciona uma quantidade ao saldo de um item no estoque
  adicionar: (produtoId, quantidade) => api.post(`/estoque/produto/${produtoId}/adicionar`, { quantidade }),

  // Atualiza a localização física de um item no estoque
  updateLocation: (estoqueId, localizacaoFisica) => api.patch(`/estoque/${estoqueId}/localizacao`, { localizacaoFisica }),

  // --- Relatórios e Alertas ---
  getEstoqueAbaixoDoMinimo: () => api.get('/estoque/alertas/estoque-baixo'),
  getValorTotalInventario: () => api.get('/estoque/relatorios/valor-total-inventario'),
  getValorTotalPorCategoria: () => api.get('/estoque/relatorios/valor-por-categoria'),
};
