// src/services/motorService.js
import api from './api';

export const motorService = {
  // Métodos CRUD básicos (já existiam)
  getAll: () => api.get('/motores'),
  getById: (id) => api.get(`/motores/${id}`),
  create: (motor) => api.post('/motores', motor),
  update: (id, motor) => api.put(`/motores/${id}`, motor),
  delete: (id) => api.delete(`/motores/${id}`),

  // ✅ NOVOS MÉTODOS DE BUSCA E FILTRO
  getByStatus: (status) => api.get(`/motores/status/${status}`),
  getByTipo: (tipo) => api.get(`/motores/tipo/${tipo}`),
  getByMarca: (marca) => api.get(`/motores/marca/${marca}`),
  getDisponiveis: () => api.get('/motores/disponiveis'),
  getNovos: () => api.get('/motores/novos'),
  getEmUso: () => api.get('/motores/em-uso'),
  
  // ✅ NOVOS MÉTODOS DE RELATÓRIOS E GESTÃO
  getParaRevisao: () => api.get('/motores/para-revisao'),
  getGarantiaPrestesVencer: () => api.get('/motores/garantia-prestes-vencer'),
  registrarRevisao: (id) => api.patch(`/motores/${id}/registrar-revisao`),
};