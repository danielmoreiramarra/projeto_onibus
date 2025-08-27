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
    
    // Buscar por número de série
    Optional<Cambio> findByNumeroSerie(String numeroSerie);
    
    // Buscar por código de fabricação
    Optional<Cambio> findByCodigoFabricacao(String codigoFabricacao);
    
    // Buscar por marca
    List<Cambio> findByMarca(String marca);
    
    // Buscar por modelo
    List<Cambio> findByModelo(String modelo);
    
    // Buscar por tipo
    List<Cambio> findByTipo(TipoCambio tipo);
    
    // Buscar por número de marchas
    List<Cambio> findByNumeroMarchas(Integer numeroMarchas);
    
    // Buscar por status
    List<Cambio> findByStatus(StatusCambio status);
    
    // Verificar se número de série existe
    boolean existsByNumeroSerie(String numeroSerie);
    
    // Verificar se código de fabricação existe
    boolean existsByCodigoFabricacao(String codigoFabricacao);

    // Buscar câmbios novos
    @Query("SELECT c FROM Cambio c WHERE c.onibus IS NULL AND c.status = 'NOVO'")
    List<Cambio> findCambiosNovos();
    
    // Buscar câmbios disponíveis (não instalados)
    @Query("SELECT c FROM Cambio c WHERE c.onibus IS NULL AND c.status = 'DISPONIVEL'")
    List<Cambio> findCambiosDisponiveis();
    
    // Buscar câmbios em uso
    @Query("SELECT c FROM Cambio c WHERE c.onibus IS NOT NULL AND c.status = 'EM_USO'")
    List<Cambio> findCambiosEmUso();
    
    // Buscar câmbios que precisam de revisão
    @Query("SELECT c FROM Cambio c WHERE c.dataUltimaRevisao IS NULL OR " +
           "c.dataUltimaRevisao <= :dataLimite")
    List<Cambio> findCambiosPrecisandoRevisao(@Param("dataLimite") LocalDate dataLimite);
    
    // Buscar câmbios com garantia prestes a vencer
    @Query("SELECT c FROM Cambio c WHERE c.dataCompra IS NOT NULL AND " +
           "c.dataCompra <= :dataLimite")
    List<Cambio> findCambiosGarantiaPrestesVencer(@Param("dataLimite") LocalDate dataLimite);
    
    // Buscar câmbios por um ônibus específico
    @Query("SELECT c FROM Cambio c WHERE c.onibus.id = :onibusId")
    Optional<Cambio> findByOnibusId(@Param("onibusId") Long onibusId);
    
    // Buscar câmbios por tipo de fluido
    @Query("SELECT c FROM Cambio c WHERE c.tipoFluido = :tipoFluido")
    List<Cambio> findByTipoFluido(@Param("tipoFluido") String tipoFluido);
    
    // Contar câmbios por tipo
    @Query("SELECT c.tipo, COUNT(c) FROM Cambio c GROUP BY c.tipo")
    List<Object[]> countCambiosPorTipo();
    
    // Contar câmbios por número de marchas
    @Query("SELECT c.numeroMarchas, COUNT(c) FROM Cambio c GROUP BY c.numeroMarchas")
    List<Object[]> countCambiosPorMarchas();
    
    // Contar câmbios por status
    @Query("SELECT c.status, COUNT(c) FROM Cambio c GROUP BY c.status")
    List<Object[]> countCambiosPorStatus();
    
}