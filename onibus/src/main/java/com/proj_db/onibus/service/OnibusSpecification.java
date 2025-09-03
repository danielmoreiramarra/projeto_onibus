package com.proj_db.onibus.service;

import org.springframework.data.jpa.domain.Specification;

import com.proj_db.onibus.model.Onibus;

import jakarta.persistence.criteria.JoinType;

public class OnibusSpecification {

    public static Specification<Onibus> searchByCriteria(OnibusService.OnibusSearchDTO criteria) {
        return (root, query, builder) -> {
            query.distinct(true); // Evita resultados duplicados por causa dos joins
            var predicate = builder.conjunction();
            
            if (criteria.chassi() != null) {
                predicate = builder.and(predicate, builder.like(root.get("chassi"), "%" + criteria.chassi() + "%"));
            }
            if (criteria.placa() != null) {
                predicate = builder.and(predicate, builder.like(root.get("placa"), "%" + criteria.placa() + "%"));
            }
            if (criteria.numeroFrota() != null) {
                predicate = builder.and(predicate, builder.like(root.get("numeroFrota"), "%" + criteria.numeroFrota() + "%"));
            }
            if (criteria.marca() != null) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("marca")), "%" + criteria.marca().toLowerCase() + "%"));
            }
            if (criteria.modelo() != null) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("modelo")), "%" + criteria.modelo().toLowerCase() + "%"));
            }
            if (criteria.status() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("status"), criteria.status()));
            }
            if (criteria.motorId() != null) {
                predicate = builder.and(predicate, builder.equal(root.join("motor", JoinType.LEFT).get("id"), criteria.motorId()));
            }
            if (criteria.cambioId() != null) {
                predicate = builder.and(predicate, builder.equal(root.join("cambio", JoinType.LEFT).get("id"), criteria.cambioId()));
            }
            if (criteria.pneuId() != null) {
                predicate = builder.and(predicate, builder.equal(root.join("pneus", JoinType.LEFT).get("id"), criteria.pneuId()));
            }
            
            return predicate;
        };
    }
}