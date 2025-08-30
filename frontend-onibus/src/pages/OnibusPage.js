import React, { useState, useEffect } from 'react';
import CrudTable from '../components/CrudTable';
import CrudForm from '../components/CrudForm';
import SearchBar from '../components/SearchBar';
import BackButton from '../components/BackButton';
import { onibusService } from '../services/onibusService';
import { motorService } from '../services/motorService';
import { cambioService } from '../services/cambioService';
import { pneuService } from '../services/pneuService';
import useSearch from '../hooks/useSearch';
import { StatusOnibus } from '../constants/onibusEnums';
import { PosicaoPneu } from '../constants/pneuEnums';
import { useNavigate } from 'react-router-dom';

const OnibusComponentes = ({ onibus, onCancel, motores, cambios, pneus, onAction }) => {
  const [motorId, setMotorId] = useState(onibus.motor?.id || '');
  const [cambioId, setCambioId] = useState(onibus.cambio?.id || '');
  const [pneuId, setPneuId] = useState('');
  const [posicaoPneu, setPosicaoPneu] = useState('');
  
  const pneusInstalados = onibus.pneus || [];
  const posicoesOcupadas = pneusInstalados.map(p => p.posicao);

  const posicoesDisponiveis = Object.values(PosicaoPneu).filter(
    posicao => !posicoesOcupadas.includes(posicao)
  );

  return (
    <div className="card my-4">
      <div className="card-header d-flex justify-content-between align-items-center">
        <h5 className="card-title mb-0">🛠️ Gerenciar Componentes do Ônibus: {onibus.numeroFrota}</h5>
        <button onClick={onCancel} className="btn btn-secondary btn-sm">Voltar</button>
      </div>
      <div className="card-body">
        <div className="row g-3">
          {/* Instalar/Remover Motor */}
          <div className="col-md-4">
            <label className="form-label">Motor</label>
            <div className="input-group">
              <select className="form-select" value={motorId} onChange={e => setMotorId(e.target.value)} disabled={!!onibus.motor}>
                <option value="">Selecione um motor</option>
                {motores.map(m => <option key={m.id} value={m.id}>{m.modelo} (ID: {m.id})</option>)}
              </select>
              {onibus.motor ? (
                <button className="btn btn-danger" onClick={() => onAction('removerMotor', onibus.id, onibus.motor.id)}>Remover</button>
              ) : (
                <button className="btn btn-primary" onClick={() => onAction('instalarMotor', onibus.id, motorId)}>Instalar</button>
              )}
            </div>
          </div>
          {/* Instalar/Remover Câmbio */}
          <div className="col-md-4">
            <label className="form-label">Câmbio</label>
            <div className="input-group">
              <select className="form-select" value={cambioId} onChange={e => setCambioId(e.target.value)} disabled={!!onibus.cambio}>
                <option value="">Selecione um câmbio</option>
                {cambios.map(c => <option key={c.id} value={c.id}>{c.modelo} (ID: {c.id})</option>)}
              </select>
              {onibus.cambio ? (
                <button className="btn btn-danger" onClick={() => onAction('removerCambio', onibus.id, onibus.cambio.id)}>Remover</button>
              ) : (
                <button className="btn btn-primary" onClick={() => onAction('instalarCambio', onibus.id, cambioId)}>Instalar</button>
              )}
            </div>
          </div>
          {/* Instalar/Remover Pneu */}
          <div className="col-md-4">
            <label className="form-label">Pneu</label>
            <div className="input-group">
              <select className="form-select" value={pneuId} onChange={e => setPneuId(e.target.value)}>
                <option value="">Selecione um pneu</option>
                {pneus.map(p => <option key={p.id} value={p.id}>{p.numeroSerie} (ID: {p.id})</option>)}
              </select>
              <select className="form-select" value={posicaoPneu} onChange={e => setPosicaoPneu(e.target.value)}>
                <option value="">Posição</option>
                {posicoesDisponiveis.map(p => <option key={p} value={p}>{p}</option>)}
              </select>
              <button className="btn btn-primary" onClick={() => onAction('instalarPneu', onibus.id, pneuId, posicaoPneu)}>Instalar</button>
              {pneusInstalados.length > 0 && <button className="btn btn-danger" onClick={() => onAction('removerPneu', onibus.id, pneusInstalados[0].id)}>Remover</button>}
            </div>
          </div>
        </div>
        <h6 className="mt-4">Pneus Instalados</h6>
        <CrudTable
          data={onibus.pneus || []}
          columns={[{key: 'numeroSerie', label: 'Número de Série'}, {key: 'posicao', label: 'Posição'}]}
        />
      </div>
    </div>
  );
};


const OnibusPage = () => {
    const { data: onibusList, loading, error, onSearch, refetch } = useSearch(onibusService);
    const navigate = useNavigate();
    
    const [editing, setEditing] = useState(false);
    const [currentOnibus, setCurrentOnibus] = useState(null);
    const [showForm, setShowForm] = useState(false);
    const [showManageComponents, setShowManageComponents] = useState(false);

    const [motoresDisponiveis, setMotoresDisponiveis] = useState([]);
    const [cambiosDisponiveis, setCambiosDisponiveis] = useState([]);
    const [pneusDisponiveis, setPneusDisponiveis] = useState([]);

    useEffect(() => {
        const loadComponentes = async () => {
            try {
                const motores = await motorService.getDisponiveis();
                setMotoresDisponiveis(motores.data);
                const cambios = await cambioService.getDisponiveis();
                setCambiosDisponiveis(cambios.data);
                const pneus = await pneuService.getDisponiveis();
                setPneusDisponiveis(pneus.data);
            } catch (err) {
                console.error("Erro ao carregar componentes disponíveis:", err);
            }
        };
        loadComponentes();
    }, [showForm, showManageComponents]);

    const columns = [
        { key: 'id', label: 'ID' },
        { key: 'chassi', label: 'Chassi' },
        { key: 'placa', label: 'Placa' },
        { key: 'modelo', 'label': 'Modelo' },
        { key: 'marca', label: 'Marca' },
        { key: 'numeroFrota', label: 'Frota' },
        { key: 'status', label: 'Status' },
        { key: 'motor.id', label: 'Motor ID' },
        { key: 'cambio.id', label: 'Câmbio ID' },
        { key: 'pneus', label: 'Pneus', format: (pneus) => pneus?.map(p => p.id).join(', ') || 'N/A' },
        { key: 'dataUltimaReforma', label: 'Última Reforma' },
        { key: 'codigoFabricacao', label: 'Código Fab.' },
        { key: 'capacidade', label: 'Capacidade' },
        { key: 'anoFabricacao', label: 'Ano Fab.' },
    ];
    
    const searchFields = [
        { name: 'chassi', label: 'Chassi', type: 'text' },
        { name: 'numeroFrota', label: 'Número de Frota', type: 'text' },
        { name: 'modelo', label: 'Modelo', type: 'text' },
        { name: 'marca', label: 'Marca', type: 'text' },
        { name: 'status', label: 'Status', type: 'select', 
          options: [{ value: '', label: 'Todos' }, ...Object.values(StatusOnibus).map(s => ({ value: s, label: s }))] },
        { name: 'codigoFabricacao', label: 'Código Fab.', type: 'text' },
        { name: 'motorId', label: 'ID Motor', type: 'number' },
        { name: 'cambioId', label: 'ID Câmbio', type: 'number' },
        { name: 'pneuId', label: 'ID Pneu', type: 'number' },
        { name: 'capacidadeMinima', label: 'Capacidade Mínima', type: 'number' }
    ];

    const formFields = [
        { name: 'chassi', label: 'Chassi', type: 'text', required: true },
        { name: 'placa', label: 'Placa', type: 'text', required: true },
        { name: 'modelo', label: 'Modelo', type: 'text', required: true },
        { name: 'marca', label: 'Marca', type: 'text', required: true },
        { name: 'codigoFabricacao', label: 'Código Fabricação', type: 'text', required: true },
        { name: 'capacidade', label: 'Capacidade', type: 'number', required: true },
        { name: 'anoFabricacao', label: 'Ano Fabricação', type: 'number', required: true },
        { name: 'numeroFrota', label: 'Número Frota', type: 'text', required: true },
        { name: 'dataUltimaReforma', label: 'Data Última Reforma', type: 'date' },
        { name: 'status', label: 'Status', type: 'select', required: true,
          options: Object.values(StatusOnibus).map(s => ({ value: s, label: s })) }
    ];
    
    const handleCreate = () => {
        setCurrentOnibus({ status: 'NOVO' });
        setEditing(false);
        setShowForm(true);
    };

    const handleEdit = (onibus) => {
        setCurrentOnibus(onibus);
        setEditing(true);
        setShowForm(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Tem certeza que deseja excluir este ônibus?')) {
            try {
                await onibusService.delete(id);
                refetch();
            } catch (err) {
                console.error("Erro ao deletar:", err);
            }
        }
    };
    
    const handleManageComponents = (onibus) => {
        setCurrentOnibus(onibus);
        setShowManageComponents(true);
    };
    
    const handleSubmit = async (formData) => {
        try {
            if (editing) {
                await onibusService.update(currentOnibus.id, formData);
            } else {
                await onibusService.create(formData);
            }
            setShowForm(false);
            refetch();
        } catch (err) {
            console.error("Erro ao salvar:", err);
        }
    };

    const handleCancel = () => {
        setShowForm(false);
        setCurrentOnibus(null);
    };

    const handleCancelManage = () => {
        setShowManageComponents(false);
        setCurrentOnibus(null);
    };

    const handleInputChange = (e) => {
        const { name, value, type } = e.target;
        let processedValue = value;
        if (type === 'number') {
            processedValue = value === '' ? null : Number(value);
        } else if (type === 'date') {
            processedValue = value || null;
        }
        setCurrentOnibus(prev => ({
            ...prev,
            [name]: processedValue
        }));
    };
    
    const handleComponentAction = async (action, onibusId, componentId, posicao) => {
        try {
            switch (action) {
                case 'instalarMotor':
                    await onibusService.instalarMotor(onibusId, componentId);
                    break;
                case 'removerMotor':
                    await onibusService.removerMotor(onibusId, componentId);
                    break;
                case 'instalarCambio':
                    await onibusService.instalarCambio(onibusId, componentId);
                    break;
                case 'removerCambio':
                    await onibusService.removerCambio(onibusId, componentId);
                    break;
                case 'instalarPneu':
                    await onibusService.instalarPneu(onibusId, componentId, posicao);
                    break;
                case 'removerPneu':
                    await onibusService.removerPneu(onibusId, componentId);
                    break;
                default:
                    break;
            }
            refetch();
        } catch (err) {
            console.error("Erro na ação:", err);
        }
    };

    return (
        <div>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>🚌 Gerenciamento de Ônibus</h2>
                <div className="btn-group">
                    <button className="btn btn-primary" onClick={handleCreate} disabled={loading}>
                        {loading ? '⏳' : '➕'} Novo Ônibus
                    </button>
                    <BackButton />
                </div>
            </div>

            {error && <div className="alert alert-danger">{error}</div>}

            {showForm ? (
                <CrudForm
                    formData={currentOnibus}
                    fields={formFields}
                    onSubmit={handleSubmit}
                    onCancel={handleCancel}
                    onChange={handleInputChange}
                    title={editing ? '✏️ Editar Ônibus' : '➕ Novo Ônibus'}
                    loading={loading}
                />
            ) : showManageComponents ? (
                <OnibusComponentes 
                    onibus={currentOnibus}
                    onCancel={handleCancelManage}
                    motores={motoresDisponiveis}
                    cambios={cambiosDisponiveis}
                    pneus={pneusDisponiveis}
                    onAction={handleComponentAction}
                />
            ) : (
                <>
                    <SearchBar fields={searchFields} onSearch={onSearch} />
                    
                    <div className="mb-3">
                        <button className="btn btn-success" onClick={refetch} disabled={loading}>
                            {loading ? '⏳ Carregando...' : '🔄 Atualizar Lista'}
                        </button>
                    </div>
                    
                    {loading && <div className="text-center">⏳ Carregando ônibus...</div>}
                    
                    {!loading && onibusList.length === 0 ? (
                        <div className="alert alert-info">
                            📝 Nenhum ônibus encontrado.
                        </div>
                    ) : (
                        <CrudTable
                            data={onibusList}
                            columns={columns}
                            onEdit={handleEdit}
                            onDelete={handleDelete}
                            onView={handleManageComponents} // ✅ Usando o onView para abrir a gestão de componentes
                        />
                    )}
                </>
            )}
        </div>
    );
};

export default OnibusPage;