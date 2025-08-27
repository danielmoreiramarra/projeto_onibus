// src/components/SearchBar.js
import React, { useState } from 'react';

const SearchBar = ({ fields, onSearch }) => {
  // Estado para armazenar os valores dos campos de busca
  const [searchTerms, setSearchTerms] = useState({});

  // Atualiza o estado quando o usuário digita em um campo
  const handleChange = (e) => {
    const { name, value } = e.target;
    setSearchTerms(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  // Executa a função de busca com os termos atuais
  const handleSearch = () => {
    onSearch(searchTerms);
  };

  // Limpa todos os campos de busca
  const handleClear = () => {
    setSearchTerms({});
    onSearch({}); // Chama a busca com um objeto vazio para resetar
  };

  return (
    <div className="card my-4">
      <div className="card-header d-flex justify-content-between align-items-center">
        <h5 className="card-title mb-0">🔍 Buscar</h5>
        <div>
          <button className="btn btn-sm btn-primary me-2" onClick={handleSearch}>
            Buscar
          </button>
          <button className="btn btn-sm btn-secondary" onClick={handleClear}>
            Limpar
          </button>
        </div>
      </div>
      <div className="card-body">
        <div className="row g-3">
          {fields.map((field) => (
            <div className="col-md-4" key={field.name}>
              <label htmlFor={`search-${field.name}`} className="form-label">
                {field.label}
              </label>
              <input
                type={field.type || 'text'}
                className="form-control"
                id={`search-${field.name}`}
                name={field.name}
                value={searchTerms[field.name] || ''}
                onChange={handleChange}
              />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default SearchBar;