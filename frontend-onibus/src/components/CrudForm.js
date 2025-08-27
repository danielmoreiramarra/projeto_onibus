// src/components/CrudForm.js
import React from 'react';

const CrudForm = ({ 
  formData, 
  fields, 
  onSubmit, 
  onCancel, 
  onChange, 
  title,
  loading = false
}) => {
  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit();
  };

  const renderField = (field) => {
    switch (field.type) {
      case 'select':
        return (
          <select
            className="form-select"
            id={field.name}
            name={field.name}
            value={formData[field.name] || ''}
            onChange={onChange}
            required={field.required}
            disabled={loading}
          >
            <option value="">Selecione...</option>
            {field.options.map(option => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        );
      
      case 'date':
        return (
          <input
            type="date"
            className="form-control"
            id={field.name}
            name={field.name}
            value={formData[field.name] || ''}
            onChange={onChange}
            required={field.required}
            disabled={loading}
          />
        );
      
      default:
        return (
          <input
            type={field.type || 'text'}
            className="form-control"
            id={field.name}
            name={field.name}
            value={formData[field.name] || ''}
            onChange={onChange}
            required={field.required}
            disabled={loading}
          />
        );
    }
  };

  return (
    <div className="card">
      <div className="card-header">
        <h5 className="card-title">{title}</h5>
      </div>
      <div className="card-body">
        <form onSubmit={handleSubmit}>
          {fields.map((field) => (
            <div key={field.name} className="mb-3">
              <label htmlFor={field.name} className="form-label">
                {field.label} {field.required && <span className="text-danger">*</span>}
              </label>
              {renderField(field)}
            </div>
          ))}
          <div className="d-flex gap-2">
            <button 
              type="submit" 
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? '⏳ Salvando...' : '💾 Salvar'}
            </button>
            <button 
              type="button" 
              className="btn btn-secondary" 
              onClick={onCancel}
              disabled={loading}
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CrudForm;