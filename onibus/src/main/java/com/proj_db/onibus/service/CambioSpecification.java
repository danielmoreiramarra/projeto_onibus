package com.proj_db.onibus.service;

import org.springframework.data.jpa.domain.Specification;

import com.proj_db.onibus.model.Cambio;

public class CambioSpecification {

    public static Specification<Cambio> searchByCriteria(CambioService.CambioSearchDTO criteria) {
        return (root, query, builder) -> {
            // Inicia com uma condição sempre verdadeira
            var predicate = builder.conjunction(); 
            
            if (criteria.marca() != null && !criteria.marca().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("marca")), "%" + criteria.marca().toLowerCase() + "%"));
            }
            if (criteria.modelo() != null && !criteria.modelo().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("modelo")), "%" + criteria.modelo().toLowerCase() + "%"));
            }
            if (criteria.numeroSerie() != null && !criteria.numeroSerie().isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("numeroSerie"), "%" + criteria.numeroSerie() + "%"));
            }
            if (criteria.tipoFluido() != null && !criteria.tipoFluido().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("tipoFluido")), "%" + criteria.tipoFluido().toLowerCase() + "%"));
            }
            if (criteria.tipo() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("tipo"), criteria.tipo()));
            }
            if (criteria.status() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("status"), criteria.status()));
            }
            
            return predicate;
        };
    }
}