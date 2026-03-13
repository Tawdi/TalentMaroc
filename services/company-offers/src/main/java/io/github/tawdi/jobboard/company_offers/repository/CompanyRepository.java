package io.github.tawdi.jobboard.company_offers.repository;

import io.github.tawdi.jobboard.company_offers.entity.Company;
import io.github.tawdi.jobboard.company_offers.entity.CompanyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUserId(String userId);

    boolean existsByUserId(String userId);

    List<Company> findByStatus(CompanyStatus status);

    Page<Company> findByStatus(CompanyStatus status, Pageable pageable);

    Page<Company> findByStatusAndSectorContainingIgnoreCase(
            CompanyStatus status, String sector, Pageable pageable);
}

