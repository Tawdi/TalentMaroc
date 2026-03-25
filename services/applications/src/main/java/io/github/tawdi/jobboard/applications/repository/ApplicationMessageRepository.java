package io.github.tawdi.jobboard.applications.repository;

import io.github.tawdi.jobboard.applications.entity.ApplicationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationMessageRepository extends JpaRepository<ApplicationMessage, Long> {

    List<ApplicationMessage> findByApplicationIdOrderBySentAtAsc(Long applicationId);
}

