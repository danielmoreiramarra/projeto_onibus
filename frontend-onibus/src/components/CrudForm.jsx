import React, { useState, useEffect } from 'react';

const CrudForm = ({ initialData, fields, onSubmit, onCancel, title, loading = false }) => {
  const [formData, setFormData] = useState({});
  const [errors, setErrors] = useState({}); // <<< NOVO: Estado para erros de validação

  useEffect(() => {
    // Garante que o formulário seja preenchido com todos os campos esperados
    const initialFormState = fields.reduce((acc, field) => {
        acc[field.name] = initialData?.[field.name] ?? field.defaultValue ?? '';
        return acc;
    }, {});
    setFormData(initialFormState);
  }, [initialData, fields]);

  const handleChange = (e) => {
    const { name, value, type } = e.target;
    let processedValue = value;
    if (type === 'number') processedValue = value === '' ? null : Number(value);
    setFormData(prev => ({ ...prev, [name]: processedValue }));
    // Limpa o erro do campo ao ser modificado
    if (errors[name]) {
        setErrors(prev => ({...prev, [name]: null}));
    }
  };
  
  // <<< NOVO: Função de validação no frontend
  const validateForm = () => {
    const newErrors = {};
    const today = new Date().toISOString().split('T')[0];

    for (const field of fields) {
        const value = formData[field.name];
        if (field.required && (!value || value.toString().trim() === '')) {
            newErrors[field.name] = `${field.label} é obrigatório.`;
        }
        if (field.type === 'date' && value > today) {
            newErrors[field.name] = `${field.label} não pode ser uma data futura.`;
        }
        if (field.name === 'dataCompra' && formData.anoFabricacao) {
            const dataCompraAno = new Date(formData.dataCompra).getFullYear();
            if (dataCompraAno < formData.anoFabricacao) {
                newErrors.dataCompra = 'A data de compra não pode ser anterior ao ano de fabricação.';
            }
        }
        if (field.type === 'number' && value < 0) {
            newErrors[field.name] = `${field.label} não pode ser negativo.`;
        }
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }

  const handleSubmit = (e) => {
    e.preventDefault();
    if (validateForm()) {
        onSubmit(formData);
    }
  };

  const renderField = (field) => {
    // ... (lógica de renderField, com uma pequena adição para mostrar o erro)
    const commonProps = { /* ... */ };
    const fieldWithError = (
        <>
            {/* Lógica de renderização (input, select, etc) */}
            {errors[field.name] && <div className="invalid-feedback d-block">{errors[field.name]}</div>}
        </>
    );
    return fieldWithError;
  };

  return (
    <div className="card">
        {/* ... (resto do JSX do formulário) */}
    </div>
  );
};

export default CrudForm;