package com.opticalshop.erp.repository.iam;

import com.opticalshop.erp.model.iam.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuthClientRepository extends JpaRepository<OAuthClient, UUID> {
    Optional<OAuthClient> findByClientId(String clientId);
    List<OAuthClient> findByTenantId(UUID tenantId);
}
