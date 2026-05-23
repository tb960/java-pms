package com.opticalshop.erp.model.iam;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_mfa_methods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMFAMethods {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "mfa_type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private MFAType mfaType;

    @Column(name = "secret_key")
    private String secretKey;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
