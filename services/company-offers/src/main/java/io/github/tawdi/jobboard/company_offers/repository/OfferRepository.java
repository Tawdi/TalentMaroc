package io.github.tawdi.jobboard.company_offers.repository;

import io.github.tawdi.jobboard.company_offers.entity.ContractType;
import io.github.tawdi.jobboard.company_offers.entity.Offer;
import io.github.tawdi.jobboard.company_offers.entity.OfferStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long>, JpaSpecificationExecutor<Offer> {

    List<Offer> findByCompanyId(Long companyId);

    Page<Offer> findByCompanyId(Long companyId, Pageable pageable);

    List<Offer> findByCompanyUserId(String userId);

    Page<Offer> findByCompanyUserId(String userId, Pageable pageable);

    /** Public active offers from approved companies only */
    @Query("""
        SELECT o FROM Offer o
        JOIN o.company c
        WHERE o.status = 'ACTIVE'
          AND c.status = 'APPROVED'
          AND (o.expiresAt IS NULL OR o.expiresAt >= CURRENT_DATE)
        ORDER BY o.createdAt DESC
    """)
    Page<Offer> findAllActiveOffers(Pageable pageable);

    /** Search active offers by keyword in title or description */
    @Query("""
        SELECT o FROM Offer o
        JOIN o.company c
        WHERE o.status = 'ACTIVE'
          AND c.status = 'APPROVED'
          AND (o.expiresAt IS NULL OR o.expiresAt >= CURRENT_DATE)
          AND (LOWER(o.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(o.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY o.createdAt DESC
    """)
    Page<Offer> searchActiveOffers(@Param("keyword") String keyword, Pageable pageable);

    long countByCompanyIdAndStatus(Long companyId, OfferStatus status);
}





