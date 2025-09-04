import api from './api';

/**
 * Objeto que encapsula todas as chamadas de API para a entidade 'Cambio'.
 */
export const cambioService = {
  
  // --- Métodos CRUD ---
  
  /**
   * Busca todos os registros de câmbio.
   * @returns {Promise} A promessa da chamada da API.
   */
  getAll: () => api.get('/cambios'),
  
  /**
   * Busca um câmbio específico pelo seu ID.
   * @param {number} id - O ID do câmbio.
   * @returns {Promise}
   */
  getById: (id) => api.get(`/cambios/${id}`),
  
  /**
   * Cria um novo câmbio.
   * @param {object} cambioCreateDTO - O DTO com os dados para a criação.
   * @returns {Promise}
   */
  create: (cambioCreateDTO) => api.post('/cambios', cambioCreateDTO),
  
  /**
   * Atualiza as informações de um câmbio existente.
   * @param {number} id - O ID do câmbio a ser atualizado.
   * @param {object} cambioUpdateDTO - O DTO com os dados a serem atualizados.
   * @returns {Promise}
   */
  update: (id, cambioUpdateDTO) => api.put(`/cambios/${id}`, cambioUpdateDTO),
  
  /**
   * Exclui um câmbio.
   * @param {number} id - O ID do câmbio a ser excluído.
   * @returns {Promise}
   */
  delete: (id) => api.delete(`/cambios/${id}`),
  
  // --- Busca Combinada ---

  /**
   * Realiza uma busca com múltiplos critérios.
   * @param {object} terms - Um objeto com os termos da busca (ex: { marca: 'ZF', status: 'DISPONIVEL' }).
   * @returns {Promise}
   */
  search: (terms) => api.get('/cambios/search', { params: terms }),

  // --- Ações de Negócio (Ciclo de Vida) ---

  /**
   * Envia um câmbio para o status de manutenção.
   * @param {number} cambioId - O ID do câmbio.
   * @returns {Promise}
   */
  enviarParaManutencao: (cambioId) => api.patch(`/cambios/${cambioId}/enviar-manutencao`),

  /**
   * Retorna um câmbio do status de manutenção para disponível.
   * @param {number} cambioId - O ID do câmbio.
   * @returns {Promise}
   */
  retornarDeManutencao: (cambioId) => api.patch(`/cambios/${cambioId}/retornar-manutencao`),

  /**
   * Envia um câmbio para o status de revisão.
   * @param {number} cambioId - O ID do câmbio.
   * @returns {Promise}
   */
  enviarParaRevisao: (cambioId) => api.patch(`/cambios/${cambioId}/enviar-revisao`),

  /**
   * Retorna um câmbio do status de revisão para disponível.
   * @param {number} cambioId - O ID do câmbio.
   * @returns {Promise}
   */
  retornarDaRevisao: (cambioId) => api.patch(`/cambios/${cambioId}/retornar-revisao`),
};

