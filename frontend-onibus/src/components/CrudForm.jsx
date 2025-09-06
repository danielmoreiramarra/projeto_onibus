import React, { useState, useEffect } from 'react';

/**
 * Formulário genérico e independente para Criar e Atualizar.
 * Agora ele gerencia seu próprio estado interno.
 */
const CrudForm = ({ initialData, fields, onSubmit, onCancel, title, loading = false }) => {
  const [formData, setFormData] = useState({});

  // Efeito para preencher o formulário quando os dados iniciais mudam
  useEffect(() => {
    const initialFormState = fields.reduce((acc, field) => {
        // Usa os dados iniciais, ou um valor padrão do campo, ou uma string vazia
        const initialValue = initialData?.[field.name];
        if (field.type === 'date' && initialValue) {
            // Garante que a data esteja no formato correto (YYYY-MM-DD) para o input
            acc[field.name] = new Date(initialValue).toISOString().split('T')[0];
        } else {
            acc[field.name] = initialValue ?? field.defaultValue ?? '';
        }
        return acc;
    }, {});
    setFormData(initialFormState);
  }, [initialData, fields]);

  // Handler de mudança interno. Não precisa mais vir do pai.
  const handleChange = (e) => {
    const { name, value, type } = e.target;
    let processedValue = value;
    if (type === 'number') {
      processedValue = value === '' ? null : Number(value);
    }
    setFormData(prev => ({ ...prev, [name]: processedValue }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Envia o estado interno do formulário para o pai
    onSubmit(formData);
  };

  // Função interna para renderizar o campo de formulário correto
  const renderField = (field) => {
    const commonProps = {
        className: "form-select",
        id: field.name,
        name: field.name,
        value: formData[field.name] || '',
        onChange: handleChange,
        required: field.required,
        disabled: loading
    };

    if (field.type === 'select') {
        return (
            <select {...commonProps}>
                <option value="">Selecione...</option>
                {field.options.map(option => (
                    <option key={option.value} value={option.value}>
                        {option.label}
                    </option>
                ))}
            </select>
        );
    }

    // Para todos os outros tipos, usamos um input
    return (
        <input
            {...commonProps}
            type={field.type || 'text'}
            className="form-control" // Sobrescreve o className para inputs
            step={field.step}
        />
    );
  };

  return (
    <div className="card my-4 shadow-sm">
      <div className="card-header">
        <h5 className="card-title mb-0">{title}</h5>
      </div>
      <div className="card-body">
        <form onSubmit={handleSubmit}>
          <div className="row g-3">
            {fields.map((field) => (
              <div key={field.name} className="col-md-6 col-lg-4">
                <label htmlFor={field.name} className="form-label">
                  {field.label} {field.required && <span className="text-danger">*</span>}
                </label>
                {/* Chama a função renderField para criar o input/select */}
                {renderField(field)}
              </div>
            ))}
          </div>
          <div className="d-flex gap-2 mt-4">
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Salvando...' : 'Salvar'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={onCancel} disabled={loading}>
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CrudForm;

