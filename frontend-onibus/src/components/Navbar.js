// src/components/Navbar.js
import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = () => {
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary">
      <div className="container">
        <Link className="navbar-brand fw-bold" to="/">
          🚌 SGO - Sistema de Gerenciamento de Ônibus
        </Link>
        
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
          <span className="navbar-toggler-icon"></span>
        </button>
        
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav me-auto">
            <li className="nav-item">
              <Link className="nav-link" to="/">
                🏠 Home
              </Link>
            </li>
            
            <li className="nav-item dropdown">
              <button className="nav-link dropdown-toggle" id="navbarDropdownVeiculos" data-bs-toggle="dropdown" aria-expanded="false">
                🚗 Veículos
              </button>
              <ul className="dropdown-menu" aria-labelledby="navbarDropdownVeiculos">
                <li><Link className="dropdown-item" to="/onibus">Ônibus</Link></li>
                <li><Link className="dropdown-item" to="/motores">Motores</Link></li>
                <li><Link className="dropdown-item" to="/cambios">Câmbios</Link></li>
                <li><Link className="dropdown-item" to="/pneus">Pneus</Link></li>
              </ul>
            </li>
            
            <li className="nav-item dropdown">
              <button className="nav-link dropdown-toggle" id="navbarDropdownManutencao" data-bs-toggle="dropdown" aria-expanded="false">
                🔧 Manutenção
              </button>
              <ul className="dropdown-menu" aria-labelledby="navbarDropdownManutencao">
                <li><Link className="dropdown-item" to="/ordens-servico">Ordens de Serviço</Link></li>
                <li><Link className="dropdown-item" to="/historico-manutencao">Histórico de OS</Link></li>
              </ul>
            </li>
            
            <li className="nav-item dropdown">
              <button className="nav-link dropdown-toggle" id="navbarDropdownEstoque" data-bs-toggle="dropdown" aria-expanded="false">
                📦 Estoque
              </button>
              <ul className="dropdown-menu" aria-labelledby="navbarDropdownEstoque">
                <li><Link className="dropdown-item" to="/produtos">Produtos</Link></li>
                <li><Link className="dropdown-item" to="/estoque">Controle de Estoque</Link></li>
              </ul>
            </li>
            
            <li className="nav-item">
              <Link className="nav-link" to="/relatorios">
                📊 Relatórios
              </Link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;