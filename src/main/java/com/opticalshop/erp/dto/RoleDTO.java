package com.opticalshop.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private UUID id;
    private UUID tenantId;
    private String roleCode;
    private String roleName;
    private Boolean isSystemRole;
    private LocalDateTime createdAt;
    private Set<PermissionDTO> permissions;
}
