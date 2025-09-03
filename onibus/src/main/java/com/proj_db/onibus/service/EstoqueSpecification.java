package com.proj_db.onibus.service;

import org.springframework.data.jpa.domain.Specification;

import com.proj_db.onibus.model.Estoque;
import com.proj_db.onibus.model.Produto;

import jakarta.persistence.criteria.Join;

public class EstoqueSpecification {

    public static Specification<Estoque> searchByCriteria(EstoqueService.EstoqueSearchDTO criteria) {
        return (root, query, builder) -> {
            var predicate = builder.conjunction();
            
            // Join com a entidade Produto para permitir a busca por seus atributos
            Join<Estoque, Produto> produtoJoin = root.join("produto");

            if (criteria.produtoId() != null) {
                predicate = builder.and(predicate, builder.equal(produtoJoin.get("id"), criteria.produtoId()));
            }
            if (criteria.nomeProduto() != null && !criteria.nomeProduto().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(produtoJoin.get("nome")), "%" + criteria.nomeProduto().toLowerCase() + "%"));
            }
            if (criteria.marcaProduto() != null && !criteria.marcaProduto().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(produtoJoin.get("marca")), "%" + criteria.marcaProduto().toLowerCase() + "%"));
            }
            if (criteria.categoriaProduto() != null) {
                predicate = builder.and(predicate, builder.equal(produtoJoin.get("categoria"), criteria.categoriaProduto()));
            }
            if (criteria.localizacao() != null && !criteria.localizacao().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("localizacaoFisica")), "%" + criteria.localizacao().toLowerCase() + "%"));
            }
            
            return predicate;
        };
    }
}