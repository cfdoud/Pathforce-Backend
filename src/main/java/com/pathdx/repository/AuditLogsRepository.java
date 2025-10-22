package com.pathdx.repository;

import com.pathdx.model.AuditLogModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogsRepository extends JpaRepository<AuditLogModel, Long> {
    List<AuditLogModel> findAll(Specification<AuditLogModel> auditLogModelSpecification);
}
