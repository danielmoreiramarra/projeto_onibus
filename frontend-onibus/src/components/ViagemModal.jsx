import React, { useState } from 'react';
import { onibusService } from '../services/onibusService';

/**
 * Modal para registrar uma nova quilometragem (viagem) para um ônibus.
 * @param {object} props
 * @param {object} props.onibus - O objeto do ônibus que terá a viagem registrada.
 * @param {boolean} props.show - Controla a visibilidade do modal.
 * @param {function} props.onClose - Função para fechar o modal.
 * @param {function} props.onSuccess - Função chamada após o registro bem-sucedido.
 */
const ViagemModal = ({ onibus, show, onClose, onSuccess }) => {
    // Estado para a nova quilometragem informada pelo usuário
    const [novaQuilometragem, setNovaQuilometragem] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        
        const kmAtual = onibus.quilometragem || 0;
        const kmNova = parseFloat(novaQuilometragem);

        // --- Validação no Frontend ---
        if (isNaN(kmNova)) {
            setError('Por favor, insira um valor numérico.');
            return;
        }

        if (kmNova < kmAtual) {
            setError(`A nova quilometragem (${kmNova} km) não pode ser menor que a atual (${kmAtual} km).`);
            return;
        }
        
        if (kmNova === kmAtual) {
            setError('A nova quilometragem deve ser maior que a atual para registrar uma viagem.');
            return;
        }

        const kmPercorridos = kmNova - kmAtual;
        setLoading(true);

        try {
            await onibusService.registrarViagem(onibus.id, kmPercorridos);
            alert('Viagem registrada com sucesso!');
            onSuccess(); // Chama a função de sucesso do componente pai (ex: para atualizar dados)
        } catch (err) {
            setError(err.message || 'Ocorreu um erro ao registrar a viagem.');
        } finally {
            setLoading(false);
        }
    };
    
    // Não renderiza nada se não for para mostrar
    if (!show) {
        return null;
    }

    return (
        <div className="modal" tabIndex="-1" style={{ display: 'block', backgroundColor: 'rgba(0,0,0,0.5)' }}>
            <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content">
                    <div className="modal-header">
                        <h5 className="modal-title">Registrar Viagem para o Ônibus {onibus.placa}</h5>
                        <button type="button" className="btn-close" onClick={onClose}></button>
                    </div>
                    <form onSubmit={handleSubmit}>
                        <div className="modal-body">
                            <p>Informe a quilometragem **atual** exibida no odômetro do veículo.</p>
                            
                            <div className="mb-3">
                                <label className="form-label">Quilometragem Anterior</label>
                                <input 
                                    type="text" 
                                    className="form-control" 
                                    value={`${onibus.quilometragem || 0} km`} 
                                    disabled 
                                />
                            </div>

                            <div className="mb-3">
                                <label htmlFor="novaQuilometragem" className="form-label">Nova Quilometragem (Odômetro)</label>
                                <input
                                    type="number"
                                    className={`form-control ${error ? 'is-invalid' : ''}`}
                                    id="novaQuilometragem"
                                    value={novaQuilometragem}
                                    onChange={(e) => setNovaQuilometragem(e.target.value)}
                                    step="0.1"
                                    required
                                />
                                {error && <div className="invalid-feedback">{error}</div>}
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={loading}>
                                Cancelar
                            </button>
                            <button type="submit" className="btn btn-primary" disabled={loading}>
                                {loading ? 'Registrando...' : 'Registrar Viagem'}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ViagemModal;