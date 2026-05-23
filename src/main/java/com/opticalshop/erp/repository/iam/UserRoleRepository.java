package com.opticalshop.erp.repository.iam;

import com.opticalshop.erp.model.iam.UserRole;
import com.opticalshop.erp.model.iam.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findByIdUserId(UUID userId);
    List<UserRole> findByIdRoleId(UUID roleId);
}
