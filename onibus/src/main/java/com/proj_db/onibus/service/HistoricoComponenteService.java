package com.proj_db.onibus.service;

import java.util.List;

import com.proj_db.onibus.model.HistoricoComponente;

public interface HistoricoComponenteService {
    List<HistoricoComponente> findByOnibusId(Long onibusId);
    List<HistoricoComponente> findByMotorId(Long motorId);
    List<HistoricoComponente> findByCambioId(Long cambioId);
    List<HistoricoComponente> findByPneuId(Long pneuId);
}