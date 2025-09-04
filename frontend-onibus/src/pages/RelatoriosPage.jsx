// // src/pages/RelatoriosPage.js
// import React, { useState, useEffect } from 'react';
// import { onibusService } from '../services/onibusService';
// import { estoqueService } from '../services/estoqueService';
// import BackButton from '../components/BackButton';

// const RelatoriosPage = () => {
//     const [statsOnibus, setStatsOnibus] = useState([]);
//     const [statsEstoque, setStatsEstoque] = useState([]);
//     const [loading, setLoading] = useState(true);
//     const [error, setError] = useState(null);

//     useEffect(() => {
//         const loadStats = async () => {
//             try {
//                 const onibusData = await onibusService.estatisticasPorStatus();
//                 setStatsOnibus(onibusData.data);
//                 const estoqueData = await estoqueService.getEstatisticasCategoria();
//                 setStatsEstoque(estoqueData.data);
//             } catch (err) {
//                 setError("Erro ao carregar os relatórios.");
//             } finally {
//                 setLoading(false);
//             }
//         };
//         loadStats();
//     }, []);

//     if (loading) return <div className="text-center">⏳ Carregando relatórios...</div>;
//     if (error) return <div className="alert alert-danger">{error}</div>;

//     return (
//         <div>
//             <div className="d-flex justify-content-between align-items-center mb-3">
//                 <h2>📊 Relatórios do Sistema</h2>
//                 <BackButton />
//             </div>

//             <div className="card mb-4">
//                 <div className="card-header">Estatísticas de Ônibus por Status</div>
//                 <ul className="list-group list-group-flush">
//                     {statsOnibus.map(([status, count]) => (
//                         <li key={status} className="list-group-item">
//                             <strong>{status}:</strong> {count}
//                         </li>
//                     ))}
//                 </ul>
//             </div>

//             <div className="card">
//                 <div className="card-header">Valor Total do Estoque por Categoria</div>
//                 <ul className="list-group list-group-flush">
//                     {statsEstoque.map(([categoria, valor]) => (
//                         <li key={categoria} className="list-group-item">
//                             <strong>{categoria}:</strong> {valor.toFixed(2)}
//                         </li>
//                     ))}
//                 </ul>
//             </div>
//         </div>
//     );
// };

// export default RelatoriosPage;