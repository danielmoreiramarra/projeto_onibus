// src/main/java/com/proj_db/onibus/repository/OnibusRepository.java
package com.proj_db.onibus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proj_db.onibus.model.Onibus;
import com.proj_db.onibus.model.Onibus.StatusOnibus;

public interface OnibusRepository extends JpaRepository<Onibus, Long> {

    // ✅ Consulta combinada para todos os campos (substitui todas as buscas individuais)
    @Query("SELECT DISTINCT o FROM Onibus o " +
           "LEFT JOIN o.motor m " +
           "LEFT JOIN o.cambio c " +
           "LEFT JOIN o.pneus p " +
           "WHERE " +
           "(:chassi IS NULL OR o.chassi LIKE %:chassi%) AND " +
           "(:numeroFrota IS NULL OR o.numeroFrota LIKE %:numeroFrota%) AND " +
           "(:modelo IS NULL OR o.modelo LIKE %:modelo%) AND " +
           "(:marca IS NULL OR o.marca LIKE %:marca%) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:codigoFabricacao IS NULL OR o.codigoFabricacao LIKE %:codigoFabricacao%) AND " +
           "(:motorId IS NULL OR m.id = :motorId) AND " +
           "(:cambioId IS NULL OR c.id = :cambioId) AND " +
           "(:pneuId IS NULL OR p.id = :pneuId) AND " +
           "(:capacidadeMinima IS NULL OR o.capacidade >= :capacidadeMinima)")
    List<Onibus> searchOnibus(
        @Param("chassi") String chassi,
        @Param("numeroFrota") String numeroFrota,
        @Param("modelo") String modelo,
        @Param("marca") String marca,
        @Param("status") StatusOnibus status,
        @Param("codigoFabricacao") String codigoFabricacao,
        @Param("motorId") Long motorId,
        @Param("cambioId") Long cambioId,
        @Param("pneuId") Long pneuId,
        @Param("capacidadeMinima") Integer capacidadeMinima
    );
    
    // ✅ Métodos de busca por atributos diretos (já estavam corretos)
    Optional<Onibus> findByChassi(String chassi);
    Optional<Onibus> findByCodigoFabricacao(String codigoFabricacao);
    Optional<Onibus> findByNumeroFrota(String numeroFrota);
    List<Onibus> findByStatus(StatusOnibus status);
    List<Onibus> findByMarca(String marca);
    List<Onibus> findByModelo(String modelo);
    List<Onibus> findByAnoFabricacao(Integer anoFabricacao);
    List<Onibus> findByCapacidadeGreaterThanEqual(Integer capacidade);
    boolean existsByChassi(String chassi);
    boolean existsByCodigoFabricacao(String codigoFabricacao);
    boolean existsByNumeroFrota(String numeroFrota);
    
    // ✅ QUERIES DE RELATÓRIOS E ESTATÍSTICAS
    @Query("SELECT o.status, COUNT(o) FROM Onibus o GROUP BY o.status")
    List<Object[]> countOnibusByStatus();
    
    @Query("SELECT o.marca, COUNT(o) FROM Onibus o GROUP BY o.marca")
    List<Object[]> countOnibusByMarca();
    
    @Query("SELECT o.anoFabricacao, COUNT(o) FROM Onibus o GROUP BY o.anoFabricacao")
    List<Object[]> countOnibusByAnoFabricacao();
}