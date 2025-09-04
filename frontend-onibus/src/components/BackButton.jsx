import React from 'react';
import { useNavigate } from 'react-router-dom';

/**
 * Botão de "Voltar" aprimorado.
 * Aceita uma propriedade 'to' para navegação hierárquica.
 * Se 'to' não for fornecido, volta para a página anterior no histórico.
 * @param {{ to?: string }} props
 */
const BackButton = ({ to }) => {
    const navigate = useNavigate();

    const handleBack = () => {
        if (to) {
            navigate(to); // Navega para o destino explícito, respeitando a hierarquia
        } else {
            navigate(-1); // Comportamento padrão de fallback
        }
    };

    return (
        <button onClick={handleBack} className="btn btn-secondary">
            🔙 Voltar
        </button>
    );
};

export default BackButton;
