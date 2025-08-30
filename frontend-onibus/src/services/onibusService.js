import api from './api';

export const onibusService = {
  getAll: () => api.get('/onibus'),
  getById: (id) => api.get(`/onibus/${id}`),
  create: (onibus) => api.post('/onibus', onibus),
  update: (id, onibus) => api.put(`/onibus/${id}`, onibus),
  delete: (id) => api.delete(`/onibus/${id}`),
  
  // ✅ NOVO MÉTODO: Busca combinada
  search: (terms) => api.get('/onibus/search', { params: terms }),

  // Métodos de gestão de componentes (sem alteração)
  instalarMotor: (onibusId, motorId) => api.patch(`/onibus/${onibusId}/instalar/motor/${motorId}`),
  removerMotor: (onibusId, motorId) => api.patch(`/onibus/${onibusId}/remover/motor/${motorId}`),
  instalarCambio: (onibusId, cambioId) => api.patch(`/onibus/${onibusId}/instalar/cambio/${cambioId}`),
  removerCambio: (onibusId, cambioId) => api.patch(`/onibus/${onibusId}/remover/cambio/${cambioId}`),
  instalarPneu: (onibusId, pneuId, posicao) => api.patch(`/onibus/${onibusId}/instalar/pneu/${pneuId}`, null, { params: { posicao } }),
  removerPneu: (onibusId, pneuId) => api.patch(`/onibus/${onibusId}/remover/pneu/${pneuId}`),
  
  // Métodos de busca individual (mantidos por compatibilidade)
  getByStatus: (status) => api.get(`/onibus/status/${status}`),
  getByChassi: (chassi) => api.get(`/onibus/chassi/${chassi}`),
  getByNumeroFrota: (numeroFrota) => api.get(`/onibus/frota/${numeroFrota}`),
  getByModelo: (modelo) => api.get(`/onibus/modelo/${modelo}`),
  getByMarca: (marca) => api.get(`/onibus/marca/${marca}`),
};