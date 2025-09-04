import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Navbar = () => {
  const location = useLocation();
  const currentPath = location.pathname;

  // Função auxiliar para verificar se um link está ativo
  const isActive = (path) => currentPath.startsWith(path);

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm mb-4">
      <div className="container">
        <Link className="navbar-brand fw-bold" to="/">🚌 SGO - Sistema de Gerenciamento</Link>
        <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span className="navbar-toggler-icon"></span>
        </button>
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">
            {/* <<< MELHORIA: Lógica de 'active' adicionada */}
            <li className="nav-item">
              <Link className={`nav-link ${currentPath === '/' ? 'active fw-bold' : ''}`} to="/">🏠 Home</Link>
            </li>
            
            <li className="nav-item dropdown">
              <a className={`nav-link dropdown-toggle ${isActive('/onibus') || isActive('/motores') ? 'active fw-bold' : ''}`} href="#" role="button" data-bs-toggle="dropdown">
                🚗 Veículos
              </a>
              <ul className="dropdown-menu">
                <li><Link className="dropdown-item" to="/onibus">Ônibus</Link></li>
                <li><Link className="dropdown-item" to="/motores">Motores</Link></li>
                <li><Link className="dropdown-item" to="/cambios">Câmbios</Link></li>
                <li><Link className="dropdown-item" to="/pneus">Pneus</Link></li>
              </ul>
            </li>

            {/* Repetir a lógica de 'active' para os outros dropdowns */}
            <li className="nav-item dropdown">
              <a className={`nav-link dropdown-toggle ${isActive('/ordens-servico') ? 'active fw-bold' : ''}`} href="#" role="button" data-bs-toggle="dropdown">
                🔧 Manutenção
              </a>
              <ul className="dropdown-menu">
                <li><Link className="dropdown-item" to="/ordens-servico">Ordens de Serviço</Link></li>
              </ul>
            </li>
            
            <li className="nav-item dropdown">
              <a className={`nav-link dropdown-toggle ${isActive('/produtos') || isActive('/estoque') ? 'active fw-bold' : ''}`} href="#" role="button" data-bs-toggle="dropdown">
                📦 Estoque
              </a>
              <ul className="dropdown-menu">
                <li><Link className="dropdown-item" to="/produtos">Produtos</Link></li>
                <li><Link className="dropdown-item" to="/estoque">Controle de Estoque</Link></li>
              </ul>
            </li>
            
            <li className="nav-item">
              <Link className={`nav-link ${isActive('/relatorios') ? 'active fw-bold' : ''}`} to="/relatorios">📊 Relatórios</Link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
