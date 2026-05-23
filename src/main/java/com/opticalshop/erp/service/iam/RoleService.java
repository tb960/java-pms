package com.opticalshop.erp.service.iam;

import com.opticalshop.erp.dto.CreateRoleRequest;
import com.opticalshop.erp.dto.PermissionDTO;
import com.opticalshop.erp.dto.RoleDTO;
import com.opticalshop.erp.exception.ResourceNotFoundException;
import com.opticalshop.erp.model.iam.Permission;
import com.opticalshop.erp.model.iam.Role;
import com.opticalshop.erp.repository.iam.PermissionRepository;
import com.opticalshop.erp.repository.iam.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleDTO createRole(UUID tenantId, CreateRoleRequest request) {
        Role role = Role.builder()
            .tenantId(tenantId)
            .roleCode(request.getRoleCode())
            .roleName(request.getRoleName())
            .isSystemRole(false)
            .build();

        Role saved = roleRepository.save(role);
        return mapToDTO(saved);
    }

    public RoleDTO getRoleById(UUID roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        return mapToDTO(role);
    }

    public RoleDTO getRoleByCode(UUID tenantId, String roleCode) {
        Role role = roleRepository.findByTenantIdAndRoleCode(tenantId, roleCode)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + roleCode));
        return mapToDTO(role);
    }

    public List<RoleDTO> getAllRolesByTenant(UUID tenantId) {
        return roleRepository.findByTenantId(tenantId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public void assignPermissionToRole(UUID roleId, UUID permissionId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));

        role.getPermissions().add(permission);
        roleRepository.save(role);
    }

    public void removePermissionFromRole(UUID roleId, UUID permissionId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        role.getPermissions().removeIf(p -> p.getId().equals(permissionId));
        roleRepository.save(role);
    }

    public void createDefaultRoles(UUID tenantId) {
        // Admin role
        if (roleRepository.findByTenantIdAndRoleCode(tenantId, "ADMIN").isEmpty()) {
            Role adminRole = Role.builder()
                .tenantId(tenantId)
                .roleCode("ADMIN")
                .roleName("Administrator")
                .isSystemRole(true)
                .build();
            roleRepository.save(adminRole);
        }

        // User role
        if (roleRepository.findByTenantIdAndRoleCode(tenantId, "USER").isEmpty()) {
            Role userRole = Role.builder()
                .tenantId(tenantId)
                .roleCode("USER")
                .roleName("User")
                .isSystemRole(true)
                .build();
            roleRepository.save(userRole);
        }

        // Manager role
        if (roleRepository.findByTenantIdAndRoleCode(tenantId, "MANAGER").isEmpty()) {
            Role managerRole = Role.builder()
                .tenantId(tenantId)
                .roleCode("MANAGER")
                .roleName("Manager")
                .isSystemRole(true)
                .build();
            roleRepository.save(managerRole);
        }
    }

    private RoleDTO mapToDTO(Role role) {
        Set<PermissionDTO> permissions = role.getPermissions().stream()
            .map(this::mapPermissionToDTO)
            .collect(Collectors.toSet());

        return RoleDTO.builder()
            .id(role.getId())
            .tenantId(role.getTenantId())
            .roleCode(role.getRoleCode())
            .roleName(role.getRoleName())
            .isSystemRole(role.getIsSystemRole())
            .createdAt(role.getCreatedAt())
            .permissions(permissions)
            .build();
    }

    private PermissionDTO mapPermissionToDTO(Permission permission) {
        return PermissionDTO.builder()
            .id(permission.getId())
            .permissionCode(permission.getPermissionCode())
            .resourceName(permission.getResourceName())
            .actionName(permission.getActionName())
            .description(permission.getDescription())
            .build();
    }
}
