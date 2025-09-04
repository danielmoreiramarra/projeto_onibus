import React, { useState, useEffect, useCallback } from 'react';

const AutocompleteInput = ({ label, value, name, onChange, onSearch, onItemSelected, displayField, placeholder }) => {
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);

  useEffect(() => {
    if (value.length < 2) {
      setSuggestions([]);
      return;
    }
    const handler = setTimeout(async () => {
      try {
        const results = await onSearch(value);
        const uniqueResults = [...new Map(results.map(item => [item[displayField], item])).values()];
        setSuggestions(uniqueResults.slice(0, 7));
        setShowSuggestions(true);
      } catch (error) {
        setSuggestions([]);
      }
    }, 300);

    return () => clearTimeout(handler);
  }, [value, onSearch, displayField]);

  const handleSelect = (item) => {
    onItemSelected(item);
    setShowSuggestions(false);
  };

  const handleChange = (e) => {
    onChange(e);
    if (e.target.value.length >= 2) {
        setShowSuggestions(true);
    }
  }

  return (
    <div className="position-relative">
      <label className="form-label">{label}</label>
      <input
        type="text"
        className="form-control"
        value={value}
        name={name}
        onChange={handleChange}
        onBlur={() => setTimeout(() => setShowSuggestions(false), 200)}
        autoComplete="off"
        placeholder={placeholder || 'Digite para buscar...'}
      />
      {showSuggestions && suggestions.length > 0 && (
        <ul className="list-group position-absolute w-100 shadow-lg" style={{ zIndex: 1000 }}>
          {suggestions.map(item => (
            <li
              key={item.id}
              className="list-group-item list-group-item-action"
              onMouseDown={() => handleSelect(item)}
              style={{ cursor: 'pointer' }}
            >
              {item[displayField]}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default AutocompleteInput;
