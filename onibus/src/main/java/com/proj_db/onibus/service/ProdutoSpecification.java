package com.proj_db.onibus.service;

import org.springframework.data.jpa.domain.Specification;

import com.proj_db.onibus.model.Produto;

public class ProdutoSpecification {

    public static Specification<Produto> searchByCriteria(ProdutoService.ProdutoSearchDTO criteria) {
        return (root, query, builder) -> {
            var predicate = builder.conjunction();
            
            if (criteria.nome() != null && !criteria.nome().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("nome")), "%" + criteria.nome().toLowerCase() + "%"));
            }
            if (criteria.marca() != null && !criteria.marca().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("marca")), "%" + criteria.marca().toLowerCase() + "%"));
            }
            if (criteria.codigoInterno() != null && !criteria.codigoInterno().isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("codigoInterno"), "%" + criteria.codigoInterno() + "%"));
            }
            if (criteria.categoria() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("categoria"), criteria.categoria()));
            }
            if (criteria.status() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("status"), criteria.status()));
            }
            
            return predicate;
        };
    }
}