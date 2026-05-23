package com.opticalshop.erp.model.iam;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "permissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = "permission_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "permission_code", nullable = false, unique = true, length = 255)
    private String permissionCode;

    @Column(name = "resource_name", nullable = false, length = 100)
    private String resourceName;

    @Column(name = "action_name", nullable = false, length = 100)
    private String actionName;

    @Column(name = "description")
    private String description;
}
