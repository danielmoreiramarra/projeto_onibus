package com.proj_db.onibus.service;

import com.proj_db.onibus.model.HistoricoComponente;
import com.proj_db.onibus.repository.HistoricoComponenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // Apenas leitura, otimiza a performance
public class HistoricoComponenteServiceImpl implements HistoricoComponenteService {

    @Autowired
    private HistoricoComponenteRepository historicoRepository;

    @Override
    public List<HistoricoComponente> findByOnibusId(Long onibusId) {
        return historicoRepository.findByOnibusIdOrderByDataEventoDesc(onibusId);
    }

    @Override
    public List<HistoricoComponente> findByMotorId(Long motorId) {
        return historicoRepository.findByMotorIdOrderByDataEventoDesc(motorId);
    }

    @Override
    public List<HistoricoComponente> findByCambioId(Long cambioId) {
        return historicoRepository.findByCambioIdOrderByDataEventoDesc(cambioId);
    }

    @Override
    public List<HistoricoComponente> findByPneuId(Long pneuId) {
        return historicoRepository.findByPneuIdOrderByDataEventoDesc(pneuId);
    }
}