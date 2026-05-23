package com.opticalshop.erp.repository.iam;

import com.opticalshop.erp.model.iam.UserMFAMethods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserMFAMethodsRepository extends JpaRepository<UserMFAMethods, UUID> {
    List<UserMFAMethods> findByUserId(UUID userId);
    List<UserMFAMethods> findByUserIdAndIsActive(UUID userId, Boolean isActive);
    Optional<UserMFAMethods> findByUserIdAndIsPrimary(UUID userId, Boolean isPrimary);
}
