import React, { useState } from 'react';

const SearchBar = ({ fields, onSearch }) => {
  const [searchTerms, setSearchTerms] = useState({});

  const handleChange = (e) => {
    const { name, value } = e.target;
    // Remove o campo se o valor for vazio (ex: "Todos")
    const newTerms = { ...searchTerms };
    if (value) {
      newTerms[name] = value;
    } else {
      delete newTerms[name];
    }
    setSearchTerms(newTerms);
  };

  const handleSearch = () => {
    onSearch(searchTerms);
  };

  const handleClear = () => {
    setSearchTerms({});
    onSearch({});
  };

  // Função para renderizar o campo correto (input ou select)
  const renderField = (field) => {
    // <<< MELHORIA: Adiciona suporte para <select>
    if (field.type === 'select') {
      return (
        <select
          className="form-select"
          id={`search-${field.name}`}
          name={field.name}
          value={searchTerms[field.name] || ''}
          onChange={handleChange}
        >
          {field.options.map(opt => <option key={opt.value} value={opt.value}>{opt.label}</option>)}
        </select>
      );
    }
    
    return (
      <input
        type={field.type || 'text'}
        className="form-control"
        id={`search-${field.name}`}
        name={field.name}
        value={searchTerms[field.name] || ''}
        onChange={handleChange}
      />
    );
  };

  return (
    <div className="card my-4">
      <div className="card-header d-flex justify-content-between align-items-center">
        <h5 className="card-title mb-0">🔍 Filtros de Busca</h5>
        <div>
          <button className="btn btn-sm btn-primary me-2" onClick={handleSearch}>Buscar</button>
          <button className="btn btn-sm btn-secondary" onClick={handleClear}>Limpar</button>
        </div>
      </div>
      <div className="card-body">
        <div className="row g-3">
          {fields.map((field) => (
            <div className="col-md-4" key={field.name}>
              <label htmlFor={`search-${field.name}`} className="form-label">{field.label}</label>
              {renderField(field)}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default SearchBar;
