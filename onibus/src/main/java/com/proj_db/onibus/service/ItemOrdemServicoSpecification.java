package com.proj_db.onibus.service;

import org.springframework.data.jpa.domain.Specification;

import com.proj_db.onibus.model.ItemOrdemServico;

public class ItemOrdemServicoSpecification {
    public static Specification<ItemOrdemServico> searchByCriteria(ItemOrdemServicoService.ItemSearchDTO criteria) {
        return (root, query, builder) -> {
            var predicate = builder.conjunction();
            
            if (criteria.ordemServicoId() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("ordemServico").get("id"), criteria.ordemServicoId()));
            }
            if (criteria.produtoId() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("produto").get("id"), criteria.produtoId()));
            }
            if (criteria.descricao() != null && !criteria.descricao().isEmpty()) {
                predicate = builder.and(predicate, builder.like(builder.lower(root.get("descricao")), "%" + criteria.descricao().toLowerCase() + "%"));
            }
            
            return predicate;
        };
    }
}