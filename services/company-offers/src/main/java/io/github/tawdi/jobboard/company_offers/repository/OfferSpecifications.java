package io.github.tawdi.jobboard.company_offers.repository;

import io.github.tawdi.jobboard.company_offers.entity.*;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * JPA Specifications for type-safe, composable Offer queries.
 * Avoids JPQL issues with nullable enum parameters.
 */
public final class OfferSpecifications {

    private OfferSpecifications() {}

    public static Specification<Offer> isActive() {
        return (root, query, cb) -> cb.equal(root.get("status"), OfferStatus.ACTIVE);
    }

    public static Specification<Offer> companyApproved() {
        return (root, query, cb) -> {
            Join<Offer, Company> company = root.join("company");
            return cb.equal(company.get("status"), CompanyStatus.APPROVED);
        };
    }

    public static Specification<Offer> notExpired() {
        return (root, query, cb) -> cb.or(
                cb.isNull(root.get("expiresAt")),
                cb.greaterThanOrEqualTo(root.get("expiresAt"), LocalDate.now())
        );
    }

    public static Specification<Offer> locationContains(String location) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<Offer> hasContractType(ContractType contractType) {
        return (root, query, cb) -> cb.equal(root.get("contractType"), contractType);
    }

    public static Specification<Offer> keywordInTitleOrDescription(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    /**
     * Build a composite specification for filtering active offers.
     */
    public static Specification<Offer> activeOffersWithFilters(
            String keyword, String location, ContractType contractType) {

        Specification<Offer> spec = Specification
                .where(isActive())
                .and(companyApproved())
                .and(notExpired());

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(keywordInTitleOrDescription(keyword));
        }
        if (location != null && !location.isBlank()) {
            spec = spec.and(locationContains(location));
        }
        if (contractType != null) {
            spec = spec.and(hasContractType(contractType));
        }

        return spec;
    }
}

