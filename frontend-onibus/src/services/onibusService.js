// src/services/onibusService.js
import api from './api';

export const onibusService = {
  // Métodos CRUD básicos (já existiam)
  getAll: () => api.get('/onibus'),
  getById: (id) => api.get(`/onibus/${id}`),
  create: (onibus) => api.post('/onibus', onibus),
  update: (id, onibus) => api.put(`/onibus/${id}`, onibus),
  delete: (id) => api.delete(`/onibus/${id}`),
  
  // ✅ NOVOS MÉTODOS DE GESTÃO DE COMPONENTES
  instalarMotor: (onibusId, motorId) =>
    api.patch(`/onibus/${onibusId}/instalar/motor/${motorId}`),
  removerMotor: (onibusId, motorId) =>
    api.patch(`/onibus/${onibusId}/remover/motor/${motorId}`),
  instalarCambio: (onibusId, cambioId) =>
    api.patch(`/onibus/${onibusId}/instalar/cambio/${cambioId}`),
  removerCambio: (onibusId, cambioId) =>
    api.patch(`/onibus/${onibusId}/remover/cambio/${cambioId}`),
  instalarPneu: (onibusId, pneuId, posicao) =>
    api.patch(`/onibus/${onibusId}/instalar/pneu/${pneuId}`, null, {
      params: { posicao },
    }),
  removerPneu: (onibusId, pneuId) =>
    api.patch(`/onibus/${onibusId}/remover/pneu/${pneuId}`),

  // ✅ NOVOS MÉTODOS DE BUSCA
  getByStatus: (status) => api.get(`/onibus/status/${status}`),
  getByChassi: (chassi) => api.get(`/onibus/chassi/${chassi}`),
  getByPlaca: (placa) => api.get(`/onibus/placa/${placa}`),
  getByModelo: (modelo) => api.get(`/onibus/modelo/${modelo}`),
};