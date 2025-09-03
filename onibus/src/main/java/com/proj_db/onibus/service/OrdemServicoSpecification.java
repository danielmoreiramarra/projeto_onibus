package com.proj_db.onibus.service;

import org.springframework.data.jpa.domain.Specification;

import com.proj_db.onibus.dto.OrdemServicoSearchDTO;
import com.proj_db.onibus.model.OrdemServico;

import jakarta.persistence.criteria.JoinType;

public class OrdemServicoSpecification {

    public static Specification<OrdemServico> searchByCriteria(OrdemServicoSearchDTO c) {
        return (root, query, builder) -> {
            query.distinct(true);
            var predicate = builder.conjunction();
            
            if (c.getNumeroOS() != null) {
                predicate = builder.and(predicate, builder.like(root.get("numeroOS"), "%" + c.getNumeroOS() + "%"));
            }
            if (c.getTipo() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("tipo"), c.getTipo()));
            }
            if (c.getStatus() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("status"), c.getStatus()));
            }
            if (c.getOnibusId() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("onibus").get("id"), c.getOnibusId()));
            }
            if (c.getMotorId() != null) {
                predicate = builder.and(predicate, builder.equal(root.get("motor").get("id"), c.getMotorId()));
            }
            // ... (lógica similar para cambioId e pneuId) ...
            if (c.getProdutoId() != null) {
                // Join para buscar OS que contenham um determinado produto
                predicate = builder.and(predicate, builder.equal(root.join("itens", JoinType.LEFT).get("produto").get("id"), c.getProdutoId()));
            }
            if (c.getDataAberturaInicio() != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("dataAbertura"), c.getDataAberturaInicio()));
            }
            if (c.getDataAberturaFim() != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("dataAbertura"), c.getDataAberturaFim()));
            }
            
            return predicate;
        };
    }
}