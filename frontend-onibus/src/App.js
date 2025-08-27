// src/App.js
import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import HomePage from './pages/HomePage';
import CambioPage from './pages/CambioPage';
import MotorPage from './pages/MotorPage';
import PneuPage from './pages/PneuPage';
import OnibusPage from './pages/OnibusPage';
import OrdemServicoPage from './pages/OrdemServicoPage';
import EstoquePage from './pages/EstoquePage';
import ProdutoPage from './pages/ProdutoPage';
import HistoricoPage from './pages/HistoricoPage';
import RelatoriosPage from './pages/RelatoriosPage';
import './App.css';

function App() {
  return (
    <div className="App">
      <Navbar />
      <div className="container mt-4">
        <Routes>
          <Route path="/" element={<HomePage />} />
          
          <Route path="/cambios" element={<CambioPage />} />
          <Route path="/motores" element={<MotorPage />} />
          <Route path="/pneus" element={<PneuPage />} />
          <Route path="/onibus" element={<OnibusPage />} />
          
          <Route path="/ordens-servico" element={<OrdemServicoPage />} />
          <Route path="/historico-manutencao" element={<HistoricoPage />} />
          
          <Route path="/estoque" element={<EstoquePage />} />
          <Route path="/produtos" element={<ProdutoPage />} />
          <Route path="/relatorios" element={<RelatoriosPage />} />
          
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </div>
    </div>
  );
}

// Componente para página não encontrada (opcional)
const NotFoundPage = () => (
  <div className="text-center py-5">
    <h1>404 - Página Não Encontrada</h1>
    <p>A página que você está procurando não existe.</p>
    <a href="/" className="btn btn-primary">Voltar para Home</a>
  </div>
);

export default App;