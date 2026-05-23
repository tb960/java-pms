package com.opticalshop.erp.repository.iam;

import com.opticalshop.erp.model.iam.Tenant;
import com.opticalshop.erp.model.iam.TenantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByTenantCode(String tenantCode);
    List<Tenant> findByStatus(TenantStatus status);
}
