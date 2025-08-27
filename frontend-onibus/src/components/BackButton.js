// src/components/BackButton.js
import React from 'react';
import { useNavigate } from 'react-router-dom';

const BackButton = () => {
    const navigate = useNavigate();
    const handleBack = () => {
        navigate(-1); // Navega para a página anterior
    };

    return (
        <button onClick={handleBack} className="btn btn-secondary">
            🔙 Voltar
        </button>
    );
};

export default BackButton;