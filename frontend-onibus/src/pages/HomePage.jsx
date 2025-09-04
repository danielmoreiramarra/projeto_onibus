import React from 'react';
import { Link } from 'react-router-dom';
import useDashboardData from '../hooks/useDashboardData';

const HomePage = () => {
    // Hook personalizado para buscar os dados do painel dinamicamente
    const { data, loading, error } = useDashboardData();

    // Componente de Card de Módulo para evitar repetição
    const ModuleCard = ({ title, description, links, icon }) => (
        <div className="col-md-4 mb-4">
            <div className="card h-100 shadow-sm">
                <div className="card-body text-center d-flex flex-column">
                    <h3 className="card-title">{icon} {title}</h3>
                    <p className="card-text">{description}</p>
                    <div className="d-grid gap-2 mt-auto">
                        {links.map(link => (
                            <Link key={link.to} to={link.to} className={`btn ${link.className}`}>{link.label}</Link>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );

    // Componente de Card de Estatística
    const StatCard = ({ value, label, icon }) => (
         <div className="col-md-3">
            <div className="card text-center p-3">
                <div className="card-body">
                    <h2 className="display-5 fw-bold text-primary">{icon} {loading ? '...' : value}</h2>
                    <p className="mb-0 text-muted">{label}</p>
                </div>
            </div>
        </div>
    );

    return (
        <div>
            <div className="text-center bg-light p-5 rounded-3 mb-5">
                <h1 className="display-4 fw-bold">🚌 SGO - Sistema de Gerenciamento</h1>
                <p className="lead">Sua plataforma central para gestão de frota, manutenção e estoque.</p>
            </div>

            {/* Cards de Módulos */}
            <div className="row">
                <ModuleCard 
                    icon="🚗" title="Gestão de Veículos"
                    description="Gerencie ônibus e todos os seus componentes."
                    links={[
                        { to: "/onibus", label: "Ônibus", className: "btn-outline-primary" },
                        { to: "/motores", label: "Motores", className: "btn-outline-primary" },
                        { to: "/cambios", label: "Câmbios", className: "btn-outline-primary" },
                        { to: "/pneus", label: "Pneus", className: "btn-outline-primary" },
                    ]}
                />
                <ModuleCard 
                    icon="🔧" title="Manutenção"
                    description="Controle de ordens de serviço e manutenções."
                    links={[
                        { to: "/ordens-servico", label: "Ordens de Serviço", className: "btn-outline-success" },
                    ]}
                />
                <ModuleCard 
                    icon="📦" title="Estoque"
                    description="Gestão de produtos e controle de inventário."
                    links={[
                        { to: "/produtos", label: "Produtos", className: "btn-outline-info" },
                        { to: "/estoque", label: "Controle de Estoque", className: "btn-outline-info" },
                    ]}
                />
            </div>

            {/* Estatísticas Dinâmicas */}
            <div className="row mt-4">
                <div className="col-12">
                    <h4 className="mb-3 text-center">📊 Visão Geral do Sistema</h4>
                    {error && <div className="alert alert-danger">{error}</div>}
                    <div className="row g-4">
                        <StatCard value={data.totalOnibus} label="Total de Ônibus" icon="🚌" />
                        <StatCard value={data.osEmExecucao} label="OS em Andamento" icon="🛠️" />
                        <StatCard value={data.itensAbaixoMinimo} label="Itens c/ Estoque Crítico" icon="⚠️" />
                        <StatCard value={data.onibusEmOperacao} label="Ônibus em Operação" icon="✅" />
                    </div>
                </div>
            </div>

            {/* Ações Rápidas */}
            <div className="row mt-5">
                <div className="col-12">
                    <div className="card">
                        <div className="card-header bg-light">
                            <h4 className="mb-0">⚡ Ações Rápidas</h4>
                        </div>
                        <div className="card-body">
                            <div className="d-flex justify-content-center gap-3 flex-wrap">
                                {/* Links corrigidos para levar à página de gerenciamento */}
                                <Link to="/ordens-servico" className="btn btn-lg btn-success">➕ Nova OS</Link>
                                <Link to="/onibus" className="btn btn-lg btn-primary">🚌 Cadastrar Ônibus</Link>
                                <Link to="/produtos" className="btn btn-lg btn-info">📦 Cadastrar Produto</Link>
                                <Link to="/relatorios" className="btn btn-lg btn-secondary">📋 Gerar Relatório</Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default HomePage;
