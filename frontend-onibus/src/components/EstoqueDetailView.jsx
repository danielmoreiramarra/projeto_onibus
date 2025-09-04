import React, { useState } from 'react';
import AdicionarEstoqueModal from './AdicionarEstoqueModal'; // Importaremos o modal que vamos criar

const EstoqueDetailView = ({ estoqueItem, onReturn, onUpdate }) => {
  const [showAddModal, setShowAddModal] = useState(false);

  if (!estoqueItem) return null;

  const { produto } = estoqueItem;

  return (
    <>
      <div className="card my-4">
        <div className="card-header d-flex justify-content-between align-items-center">
          <h5 className="card-title mb-0">Detalhes do Estoque: {produto.nome}</h5>
          <button onClick={onReturn} className="btn btn-secondary">Voltar</button>
        </div>
        <div className="card-body">
          <div className="row">
            <div className="col-md-6">
              <h6>Informações do Produto</h6>
              <p><strong>Cód. Interno:</strong> {produto.codigoInterno}</p>
              <p><strong>Marca:</strong> {produto.marca}</p>
              <p><strong>Preço Atual:</strong> R$ {produto.precoUnitarioAtual?.toFixed(2)}</p>
            </div>
            <div className="col-md-6">
              <h6>Informações de Estoque</h6>
              <p><strong>Localização:</strong> {estoqueItem.localizacaoFisica}</p>
              <p><strong>Quantidade Atual:</strong> {estoqueItem.quantidadeAtual}</p>
              <p><strong>Quantidade Reservada:</strong> {estoqueItem.quantidadeReservada}</p>
              <p className="fw-bold"><strong>Quantidade Disponível:</strong> {estoqueItem.quantidadeDisponivel}</p>
            </div>
          </div>
          <div className="mt-4">
            <h6>Ações</h6>
            <button className="btn btn-success me-2" onClick={() => setShowAddModal(true)}>
              ➕ Dar Entrada no Estoque
            </button>
            {/* TODO: Implementar botão/modal para editar localização */}
          </div>
        </div>
      </div>

      {/* Renderiza o modal de adição de estoque */}
      <AdicionarEstoqueModal
        show={showAddModal}
        onClose={() => setShowAddModal(false)}
        produto={produto}
        onSuccess={() => {
          setShowAddModal(false);
          onUpdate(); // Atualiza os dados na tela principal
        }}
      />
    </>
  );
};

export default EstoqueDetailView;
