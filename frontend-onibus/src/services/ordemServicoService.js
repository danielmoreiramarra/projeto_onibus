// src/services/ordemServicoService.js
import api from './api';

export const ordemServicoService = {
  // Métodos CRUD básicos (já existiam)
  getAll: () => api.get('/ordens-servico'),
  getById: (id) => api.get(`/ordens-servico/${id}`),
  create: (ordemServico) => api.post('/ordens-servico', ordemServico),
  update: (id, ordemServico) => api.put(`/ordens-servico/${id}`, ordemServico),
  delete: (id) => api.delete(`/ordens-servico/${id}`),

  // ✅ NOVOS MÉTODOS DE BUSCA E GESTÃO
  getByStatus: (status) => api.get(`/ordens-servico/status/${status}`),
  getEmAberto: () => api.get('/ordens-servico/em-aberto'),
  getEmExecucao: () => api.get('/ordens-servico/em-execucao'),
  getFinalizadasNoPeriodo: (dataInicio, dataFim) =>
    api.get(`/ordens-servico/finalizadas-periodo`, {
      params: { dataInicio, dataFim },
    }),
  getComPrevisaoVencida: () => api.get('/ordens-servico/previsao-vencida'),
  iniciarExecucao: (id) => api.patch(`/ordens-servico/${id}/iniciar-execucao`),
  finalizar: (id) => api.patch(`/ordens-servico/${id}/finalizar`),
  cancelar: (id) => api.patch(`/ordens-servico/${id}/cancelar`),
};