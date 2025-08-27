// src/components/CrudTable.js
import React from 'react';

const CrudTable = ({ data, columns, onEdit, onDelete, onView }) => {
  return (
    <div className="table-responsive">
      <table className="table table-striped table-hover">
        <thead className="table-dark">
          <tr>
            {columns.map((column) => (
              <th key={column.key}>{column.label}</th>
            ))}
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {data.map((item) => (
            <tr key={item.id}>
              {columns.map((column) => (
                <td key={column.key}>{item[column.key]}</td>
              ))}
              <td>
                <div className="btn-group">
                  {onView && (
                    <button
                      className="btn btn-info btn-sm"
                      onClick={() => onView(item)}
                    >
                      Ver
                    </button>
                  )}
                  <button
                    className="btn btn-warning btn-sm"
                    onClick={() => onEdit(item)}
                  >
                    Editar
                  </button>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => onDelete(item.id)}
                  >
                    Excluir
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default CrudTable;