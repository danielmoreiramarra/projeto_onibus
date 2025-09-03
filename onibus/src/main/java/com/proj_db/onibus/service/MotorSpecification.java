package com.proj_db.onibus.service;

import org.springframework.data.jpa.domain.Specification;

import com.proj_db.onibus.model.Motor;

public class MotorSpecification {

    public static Specification<Motor> searchByCriteria(MotorService.MotorSearchDTO criteria) {
        return (root, query, builder) -> {
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
            if (criteria.tipoOleo() != null && !criteria.tipoOleo().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("tipoOleo")), "%" + criteria.tipoOleo().toLowerCase() + "%"));
            }
            if (criteria.tipo() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("tipo"), criteria.tipo()));
            }
            if (criteria.status() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("status"), criteria.status()));
            }
            if (criteria.potenciaMin() != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("potencia"), criteria.potenciaMin()));
            }
            if (criteria.potenciaMax() != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("potencia"), criteria.potenciaMax()));
            }
            
            return predicate;
        };
    }
}