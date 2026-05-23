package com.opticalshop.erp.repository.iam;

import com.opticalshop.erp.model.iam.User;
import com.opticalshop.erp.model.iam.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByTenantIdAndUsername(UUID tenantId, String username);
    Optional<User> findByTenantIdAndEmail(UUID tenantId, String email);
    Optional<User> findByEmail(String email);
    List<User> findByTenantIdAndUserStatus(UUID tenantId, UserStatus status);
    
    @Query("SELECT u FROM User u JOIN u.roles ur WHERE u.tenantId = :tenantId AND ur.role.roleCode = :roleCode")
    List<User> findByTenantIdAndRoleCode(@Param("tenantId") UUID tenantId, @Param("roleCode") String roleCode);
}
