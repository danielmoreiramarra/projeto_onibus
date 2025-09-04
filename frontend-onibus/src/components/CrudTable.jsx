import React from 'react';

const getNestedValue = (obj, path) => path.split('.').reduce((acc, part) => acc && acc[part], obj);

const CrudTable = ({ data, columns, onEdit, onDelete, onView }) => {
  return (
    <div className="table-responsive shadow-sm rounded">
      <table className="table table-striped table-hover table-bordered mb-0">
        <thead className="table-dark">
          <tr>
            {columns.map((column) => (
              <th key={column.key} scope="col" className="text-center">{column.label}</th>
            ))}
            <th scope="col" className="text-center" style={{ width: '150px' }}>Ações</th>
          </tr>
        </thead>
        <tbody>
          {data.map((item) => (
            <tr key={item.id}>
              {columns.map((column) => {
                const value = getNestedValue(item, column.key);
                const displayValue = column.format ? column.format(value, item) : value;
                // <<< MELHORIA: Adiciona classe para centralizar
                return <td key={column.key} className="text-center align-middle">{displayValue ?? 'N/A'}</td>;
              })}
              <td className="text-center align-middle">
                <div className="btn-group w-100">
                  {onView && <button className="btn btn-info btn-sm" onClick={() => onView(item)}>Ver</button>}
                  {onEdit && <button className="btn btn-warning btn-sm" onClick={() => onEdit(item)}>Editar</button>}
                  {onDelete && <button className="btn btn-danger btn-sm" onClick={() => onDelete(item.id)}>Excluir</button>}
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