package com.opticalshop.erp.controller.iam;

import com.opticalshop.erp.dto.CreateRoleRequest;
import com.opticalshop.erp.dto.RoleDTO;
import com.opticalshop.erp.service.iam.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/{tenantId}")
    public ResponseEntity<RoleDTO> createRole(@PathVariable UUID tenantId, @RequestBody CreateRoleRequest request) {
        RoleDTO role = roleService.createRole(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<RoleDTO> getRole(@PathVariable UUID roleId) {
        RoleDTO role = roleService.getRoleById(roleId);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/{tenantId}/code/{code}")
    public ResponseEntity<RoleDTO> getRoleByCode(@PathVariable UUID tenantId, @PathVariable String code) {
        RoleDTO role = roleService.getRoleByCode(tenantId, code);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/{tenantId}/all")
    public ResponseEntity<List<RoleDTO>> getAllRoles(@PathVariable UUID tenantId) {
        List<RoleDTO> roles = roleService.getAllRolesByTenant(tenantId);
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> assignPermissionToRole(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        roleService.assignPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermissionFromRole(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        roleService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tenantId}/create-default-roles")
    public ResponseEntity<Void> createDefaultRoles(@PathVariable UUID tenantId) {
        roleService.createDefaultRoles(tenantId);
        return ResponseEntity.ok().build();
    }
}
