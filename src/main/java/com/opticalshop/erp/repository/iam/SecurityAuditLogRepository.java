package com.opticalshop.erp.repository.iam;

import com.opticalshop.erp.model.iam.SecurityAuditLog;
import com.opticalshop.erp.model.iam.SecurityEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, UUID> {
    List<SecurityAuditLog> findByTenantId(UUID tenantId);
    List<SecurityAuditLog> findByUserId(UUID userId);
    List<SecurityAuditLog> findByEventType(SecurityEventType eventType);
    List<SecurityAuditLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<SecurityAuditLog> findByTenantIdAndCreatedAtBetween(UUID tenantId, LocalDateTime startDate, LocalDateTime endDate);
}
