package com.proj_db.onibus.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Cambio;
import com.proj_db.onibus.model.Cambio.StatusCambio;
import com.proj_db.onibus.model.Cambio.TipoCambio;

public interface CambioRepository extends JpaRepository<Cambio, Long> {
    
    // ✅ MÉTODOS DE BUSCA INDIVIDUAIS
    Optional<Cambio> findByNumeroSerie(String numeroSerie);
    Optional<Cambio> findByCodigoFabricacao(String codigoFabricacao);
    List<Cambio> findByMarca(String marca);
    List<Cambio> findByModelo(String modelo);
    List<Cambio> findByTipo(TipoCambio tipo);
    List<Cambio> findByNumeroMarchas(Integer numeroMarchas);
    List<Cambio> findByStatus(StatusCambio status);
    List<Cambio> findCambiosGarantiaPrestesVencer(LocalDate dataLimite);
    List<Cambio> findCambiosPrecisandoRevisao(LocalDate seisMesesAtras);
    List<Cambio> findCambiosParaRevisao(LocalDate seisMesesAtras);
    boolean existsByNumeroSerie(String numeroSerie);
    boolean existsByCodigoFabricacao(String codigoFabricacao);

    // ✅ NOVA CONSULTA COMBINADA PARA TODOS OS CAMPOS
    @Query("SELECT c FROM Cambio c WHERE " +
           "(:id IS NULL OR c.id = :id) AND " +
           "(:tipo IS NULL OR c.tipo = :tipo) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:marca IS NULL OR c.marca LIKE %:marca%) AND " +
           "(:modelo IS NULL OR c.modelo LIKE %:modelo%) AND " +
           "(:numeroSerie IS NULL OR c.numeroSerie LIKE %:numeroSerie%) AND " +
           "(:codigoFabricacao IS NULL OR c.codigoFabricacao LIKE %:codigoFabricacao%) AND " +
           "(:numeroMarchas IS NULL OR c.numeroMarchas = :numeroMarchas) AND " +
           "(:onibusId IS NULL OR c.onibus.id = :onibusId) AND " +
           "(:tipoFluido IS NULL OR c.tipoFluido = : tipoFluido)")
    List<Cambio> searchCambio(
        @Param("id") Long id,
        @Param("tipo") TipoCambio tipo,
        @Param("status") StatusCambio status,
        @Param("marca") String marca,
        @Param("modelo") String modelo,
        @Param("numeroSerie") String numeroSerie,
        @Param("codigoFabricacao") String codigoFabricacao,
        @Param("numeroMarchas") Integer numeroMarchas,
        @Param("onibusId") Long onibusId,
        @Param("tipoFluido") String tipoFluido
    );

    // ✅ MÉTODOS DE RELATÓRIO
    @Query("SELECT c.tipo, COUNT(c) FROM Cambio c GROUP BY c.tipo")
    List<Object[]> countCambiosPorTipo();
    
    @Query("SELECT c.numeroMarchas, COUNT(c) FROM Cambio c GROUP BY c.numeroMarchas")
    List<Object[]> countCambiosPorMarchas();
    
    @Query("SELECT c.status, COUNT(c) FROM Cambio c GROUP BY c.status")
    List<Object[]> countCambiosPorStatus();

}