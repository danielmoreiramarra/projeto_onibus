// src/services/produtoService.js
import api from './api';

export const produtoService = {
  // Métodos CRUD básicos (já existiam)
  getAll: () => api.get('/produtos'),
  getById: (id) => api.get(`/produtos/${id}`),
  create: (produto) => api.post('/produtos', produto),
  update: (id, produto) => api.put(`/produtos/${id}`, produto),
  delete: (id) => api.delete(`/produtos/${id}`),

  // ✅ NOVOS MÉTODOS DE BUSCA E FILTRO
  getByStatus: (status) => api.get(`/produtos/status/${status}`),
  getByMarca: (marca) => api.get(`/produtos/marca/${marca}`),
  getByCategoria: (categoria) => api.get(`/produtos/categoria/${categoria}`),
  getByNome: (nome) => api.get(`/produtos/nome/${nome}`),
  getByIntervaloPreco: (precoMinimo, precoMaximo) =>
    api.get('/produtos/preco', {
      params: { precoMinimo, precoMaximo },
    }),

  // ✅ NOVOS MÉTODOS DE RELATÓRIOS E GESTÃO
  getAtivos: () => api.get('/produtos/ativos'),
  getComEstoqueAbaixoMinimo: () => api.get('/produtos/estoque-abaixo-minimo'),
  getNuncaUtilizados: () => api.get('/produtos/nunca-utilizados'),
  getSemMovimento: () => api.get('/produtos/sem-movimento'),
  getMaisUtilizados: () => api.get('/produtos/mais-utilizados'),
  getPorGiro: () => api.get('/produtos/por-giro'),
  getEstatisticasCategoria: () => api.get('/produtos/estatisticas-categoria'),
};