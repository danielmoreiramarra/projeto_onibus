// src/services/pneuService.js
import api from './api';

export const pneuService = {
  // Métodos CRUD básicos (já existiam)
  getAll: () => api.get('/pneus'),
  getById: (id) => api.get(`/pneus/${id}`),
  create: (pneu) => api.post('/pneus', pneu),
  update: (id, pneu) => api.put(`/pneus/${id}`, pneu),
  delete: (id) => api.delete(`/pneus/${id}`),

  // ✅ NOVOS MÉTODOS DE BUSCA E FILTRO
  getByStatus: (status) => api.get(`/pneus/status/${status}`),
  getByMarca: (marca) => api.get(`/pneus/marca/${marca}`),
  getByMedida: (medida) => api.get(`/pneus/medida/${medida}`),
  getDisponiveis: () => api.get('/pneus/disponiveis'),
  getEmUso: () => api.get('/pneus/em-uso'),

  // ✅ NOVOS MÉTODOS DE GESTÃO E RELATÓRIOS
  registrarKm: (id, kmAdicionais) =>
    api.patch(`/pneus/${id}/registrar-km`, null, {
      params: { kmAdicionais },
    }),
  precisaTroca: (id) => api.get(`/pneus/${id}/precisa-troca`),
  getParaTroca: () => api.get('/pneus/para-troca'),
  getGarantiaPrestesVencer: () => api.get('/pneus/garantia-prestes-vencer'),
  getEstatisticasStatus: () => api.get('/pneus/estatisticas-status'),
  getEstatisticasMarca: () => api.get('/pneus/estatisticas-marca'),
};