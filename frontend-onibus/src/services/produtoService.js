import api from './api';

export const produtoService = {
  getAll: () => api.get('/produtos'),
  getById: (id) => api.get(`/produtos/${id}`),
  create: (produto) => api.post('/produtos', produto),
  update: (id, produto) => api.put(`/produtos/${id}`, produto),
  delete: (id) => api.delete(`/produtos/${id}`),
  
  // ✅ NOVO MÉTODO: Busca combinada
  search: (terms) => api.get('/produtos/search', { params: terms }),

  // Métodos de busca individual (mantidos por compatibilidade)
  getByStatus: (status) => api.get(`/produtos/status/${status}`),
  getByMarca: (marca) => api.get(`/produtos/marca/${marca}`),
  getByCategoria: (categoria) => api.get(`/produtos/categoria/${categoria}`),
  getByNome: (nome) => api.get(`/produtos/nome/${nome}`),
  getByIntervaloPreco: (precoMinimo, precoMaximo) => api.get('/produtos/preco', { params: { precoMinimo, precoMaximo } }),
  
  // Métodos de relatórios (sem alteração)
  getComEstoqueAbaixoMinimo: () => api.get('/produtos/estoque-abaixo-minimo'),
  getNuncaUtilizados: () => api.get('/produtos/nunca-utilizados'),
  getSemMovimento: () => api.get('/produtos/sem-movimento'),
  getMaisUtilizados: () => api.get('/produtos/mais-utilizados'),
  getPorGiro: () => api.get('/produtos/por-giro'),
  getEstatisticasCategoria: () => api.get('/produtos/estatisticas-categoria'),
};