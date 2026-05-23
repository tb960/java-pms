package com.opticalshop.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTO {
    private UUID id;
    private String permissionCode;
    private String resourceName;
    private String actionName;
    private String description;
}
