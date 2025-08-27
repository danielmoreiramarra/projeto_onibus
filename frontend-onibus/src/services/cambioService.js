// src/services/cambioService.js
import api from './api';

export const cambioService = {
  // Métodos CRUD básicos (já existiam)
  getAll: () => api.get('/cambios'),
  getById: (id) => api.get(`/cambios/${id}`),
  create: (cambio) => api.post('/cambios', cambio),
  update: (id, cambio) => api.put(`/cambios/${id}`, cambio),
  delete: (id) => api.delete(`/cambios/${id}`),

  // ✅ NOVOS MÉTODOS DE BUSCA E FILTRO
  getByStatus: (status) => api.get(`/cambios/status/${status}`),
  getByTipo: (tipo) => api.get(`/cambios/tipo/${tipo}`),
  getByMarca: (marca) => api.get(`/cambios/marca/${marca}`),
  getDisponiveis: () => api.get('/cambios/disponiveis'),
  getEmUso: () => api.get('/cambios/em-uso'),

  // ✅ NOVOS MÉTODOS DE RELATÓRIOS E GESTÃO
  getParaRevisao: () => api.get('/cambios/para-revisao'),
  getGarantiaPrestesVencer: () => api.get('/cambios/garantia-prestes-vencer'),
  trocarFluido: (id, novoTipoFluido, novaQuantidade) =>
    api.patch(`/cambios/${id}/trocar-fluido`, null, {
      params: { novoTipoFluido, novaQuantidade },
    }),
  registrarRevisao: (id) => api.patch(`/cambios/${id}/registrar-revisao`),
};