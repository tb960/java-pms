package com.opticalshop.erp.repository.iam;

import com.opticalshop.erp.model.iam.UserSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionsRepository extends JpaRepository<UserSessions, UUID> {
    Optional<UserSessions> findByIdAndRevokedAtIsNull(UUID id);
    List<UserSessions> findByUserIdAndRevokedAtIsNull(UUID userId);
    List<UserSessions> findByUserId(UUID userId);
}
