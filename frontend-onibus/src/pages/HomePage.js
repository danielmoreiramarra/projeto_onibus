// src/pages/HomePage.js
import React from 'react';
import { Link } from 'react-router-dom';
import useDashboardData from '../hooks/useDashboardData';

const HomePage = () => {
    // ✅ Usando o hook para buscar os dados dinamicamente
    const { data, loading, error } = useDashboardData();

    if (loading) {
        return <div className="text-center mt-5">⏳ Carregando dados do painel...</div>;
    }

    if (error) {
        return <div className="alert alert-danger mt-5">{error}</div>;
    }

    return (
        <div className="text-center">
            <div className="bg-primary text-white py-5 rounded">
                <h1 className="display-4 fw-bold">🚌 SGO - Sistema de Gerenciamento de Ônibus</h1>
                <p className="lead">Sistema completo para gestão de frota, manutenção e estoque</p>
            </div>

            {/* ✅ Cards de Módulos (Sem alteração) */}
            <div className="row mt-5">
                <div className="col-md-4 mb-4">
                    <div className="card h-100 shadow-sm">
                        <div className="card-body text-center">
                            <h3 className="card-title">🏎️ Gestão de Veículos</h3>
                            <p className="card-text">Gerencie ônibus, motores, câmbios e pneus da frota</p>
                            <div className="d-grid gap-2">
                                <Link to="/onibus" className="btn btn-outline-primary">Ônibus</Link>
                                <Link to="/motores" className="btn btn-outline-primary">Motores</Link>
                                <Link to="/cambios" className="btn btn-outline-primary">Câmbios</Link>
                                <Link to="/pneus" className="btn btn-outline-primary">Pneus</Link>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 mb-4">
                    <div className="card h-100 shadow-sm">
                        <div className="card-body text-center">
                            <h3 className="card-title">🔧 Manutenção</h3>
                            <p className="card-text">Controle de ordens de serviço e manutenções</p>
                            <div className="d-grid gap-2">
                                <Link to="/ordens-servico" className="btn btn-outline-success">Ordens de Serviço</Link>
                                <Link to="/historico-manutencao" className="btn btn-outline-success">Histórico</Link>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4 mb-4">
                    <div className="card h-100 shadow-sm">
                        <div className="card-body text-center">
                            <h3 className="card-title">📦 Estoque</h3>
                            <p className="card-text">Gestão de produtos e controle de inventário</p>
                            <div className="d-grid gap-2">
                                <Link to="/produtos" className="btn btn-outline-info">Produtos</Link>
                                <Link to="/estoque" className="btn btn-outline-info">Estoque</Link>
                                <Link to="/fornecedores" className="btn btn-outline-info">Fornecedores</Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* ✅ Estatísticas Dinâmicas */}
            <div className="row mt-5">
                <div className="col-12">
                    <div className="card">
                        <div className="card-header bg-light">
                            <h4 className="mb-0">📊 Visão Geral do Sistema</h4>
                        </div>
                        <div className="card-body">
                            <div className="row text-center">
                                <div className="col-md-3">
                                    <div className="border rounded p-3">
                                        <h2 className="text-primary">{data.totalOnibus}</h2>
                                        <p className="mb-0">Total de Ônibus</p>
                                    </div>
                                </div>
                                <div className="col-md-3">
                                    <div className="border rounded p-3">
                                        <h2 className="text-success">{data.osEmExecucao}</h2>
                                        <p className="mb-0">OS em Andamento</p>
                                    </div>
                                </div>
                                <div className="col-md-3">
                                    <div className="border rounded p-3">
                                        <h2 className="text-warning">{data.itensAbaixoMinimo}</h2>
                                        <p className="mb-0">Itens c/ Estoque Crítico</p>
                                    </div>
                                </div>
                                <div className="col-md-3">
                                    <div className="border rounded p-3">
                                        <h2 className="text-info">{data.onibusEmOperacao}</h2>
                                        <p className="mb-0">Ônibus em Operação</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* ✅ Quick Actions (Sem alteração) */}
            <div className="row mt-5">
                <div className="col-12">
                    <div className="card">
                        <div className="card-header bg-light">
                            <h4 className="mb-0">⚡ Ações Rápidas</h4>
                        </div>
                        <div className="card-body">
                            <div className="d-flex justify-content-center gap-3 flex-wrap">
                                <Link to="/ordens-servico/create" className="btn btn-success">
                                    ➕ Nova OS
                                </Link>
                                <Link to="/onibus/create" className="btn btn-primary">
                                    🚌 Cadastrar Ônibus
                                </Link>
                                <Link to="/estoque/create" className="btn btn-info">
                                    📦 Entrada Estoque
                                </Link>
                                <Link to="/relatorios" className="btn btn-secondary">
                                    📋 Gerar Relatório
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* Footer */}
            <footer className="mt-5 py-4 text-muted">
                <div className="container">
                    <p className="mb-0">
                        🚍 <strong>Sistema de Gerenciamento de Ônibus</strong> - v1.0.0
                    </p>
                    <small>
                        Desenvolvido para otimizar a gestão de frotas e manutenção veicular
                    </small>
                </div>
            </footer>
        </div>
    );
};

export default HomePage;