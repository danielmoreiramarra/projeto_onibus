package com.proj_db.onibus.service;

import org.springframework.data.jpa.domain.Specification;

import com.proj_db.onibus.model.Pneu;

public class PneuSpecification {

    public static Specification<Pneu> searchByCriteria(PneuService.PneuSearchDTO criteria) {
        return (root, query, builder) -> {
            var predicate = builder.conjunction(); 
            
            if (criteria.marca() != null && !criteria.marca().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("marca")), "%" + criteria.marca().toLowerCase() + "%"));
            }
            if (criteria.medida() != null && !criteria.medida().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("medida")), "%" + criteria.medida().toLowerCase() + "%"));
            }
            if (criteria.numeroSerie() != null && !criteria.numeroSerie().isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("numeroSerie"), "%" + criteria.numeroSerie() + "%"));
            }
            if (criteria.status() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("status"), criteria.status()));
            }
            if (criteria.kmRodadosMin() != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("kmRodados"), criteria.kmRodadosMin()));
            }
            if (criteria.kmRodadosMax() != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("kmRodados"), criteria.kmRodadosMax()));
            }
            if (criteria.onibusId() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("onibus").get("id"), criteria.onibusId()));
            }
            
            return predicate;
        };
    }
}