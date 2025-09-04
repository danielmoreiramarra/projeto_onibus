import api from './api';

// Objeto que encapsula todas as chamadas de API para a entidade 'Onibus'
export const onibusService = {
  
  // --- Métodos CRUD ---
  
  // Busca todos os ônibus
  getAll: () => api.get('/onibus'),
  
  // Busca um ônibus específico pelo seu ID
  getById: (id) => api.get(`/onibus/${id}`),
  
  // Cria um novo ônibus. Envia o DTO de criação no corpo da requisição.
  create: (onibusCreateDTO) => api.post('/onibus', onibusCreateDTO),
  
  // Atualiza as informações básicas de um ônibus. Envia o DTO de atualização.
  update: (id, onibusUpdateDTO) => api.put(`/onibus/${id}`, onibusUpdateDTO),
  
  // Exclui um ônibus (apenas se permitido pelas regras de negócio)
  delete: (id) => api.delete(`/onibus/${id}`),
  
  // --- Busca Combinada ---

  // Realiza uma busca com múltiplos critérios. 'terms' é um objeto (nosso DTO de busca).
  // Ex: { marca: 'Volvo', status: 'EM_OPERACAO' }
  search: (terms) => api.get('/onibus/search', { params: terms }),

  // --- Ações de Negócio e Ciclo de Vida ---

  // Coloca um ônibus em operação
  colocarEmOperacao: (onibusId) => api.patch(`/onibus/${onibusId}/colocar-em-operacao`),

  // Retira um ônibus de operação
  retirarDeOperacao: (onibusId) => api.patch(`/onibus/${onibusId}/retirar-de-operacao`),

  // Registra uma nova viagem, atualizando a quilometragem do ônibus e de todos os seus pneus
  registrarViagem: (onibusId, kmPercorridos) => api.post(`/onibus/${onibusId}/registrar-viagem`, null, { params: { kmPercorridos } }),

  // --- Gerenciamento de Componentes ---

  // Instala um motor em um ônibus
  instalarMotor: (onibusId, motorId) => api.post(`/onibus/${onibusId}/motor/${motorId}`),

  // Remove o motor de um ônibus
  removerMotor: (onibusId) => api.delete(`/onibus/${onibusId}/motor`),

  // Instala um câmbio em um ônibus
  instalarCambio: (onibusId, cambioId) => api.post(`/onibus/${onibusId}/cambio/${cambioId}`),

  // Remove o câmbio de um ônibus
  removerCambio: (onibusId) => api.delete(`/onibus/${onibusId}/cambio`),

  // Instala um pneu em uma posição específica de um ônibus
  instalarPneu: (onibusId, pneuId, posicao) => api.post(`/onibus/${onibusId}/pneu/${pneuId}`, null, { params: { posicao } }),

  // Remove um pneu de uma posição específica de um ônibus
  removerPneu: (onibusId, posicao) => api.delete(`/onibus/${onibusId}/pneu`, { params: { posicao } }),
};
