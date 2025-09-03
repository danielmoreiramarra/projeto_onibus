package com.proj_db.onibus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proj_db.onibus.model.HistoricoComponente;

public interface HistoricoComponenteRepository extends JpaRepository<HistoricoComponente, Long> {
    List<HistoricoComponente> findByOnibusIdOrderByDataEventoDesc(Long onibusId);
    List<HistoricoComponente> findByMotorIdOrderByDataEventoDesc(Long motorId);
    List<HistoricoComponente> findByCambioIdOrderByDataEventoDesc(Long cambioId);
    List<HistoricoComponente> findByPneuIdOrderByDataEventoDesc(Long pneuId);
}