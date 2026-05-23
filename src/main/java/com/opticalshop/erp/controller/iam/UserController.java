package com.opticalshop.erp.controller.iam;

import com.opticalshop.erp.dto.*;
import com.opticalshop.erp.service.iam.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register/{tenantId}")
    public ResponseEntity<UserDTO> registerUser(@PathVariable UUID tenantId, @RequestBody RegisterUserRequest request) {
        UserDTO user = userService.registerUser(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable UUID userId) {
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{tenantId}/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable UUID tenantId, @PathVariable String username) {
        UserDTO user = userService.getUserByUsername(tenantId, username);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> assignRoleToUser(@PathVariable UUID userId, @PathVariable UUID roleId) {
        userService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable UUID userId, @PathVariable UUID roleId) {
        userService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/permissions")
    public ResponseEntity<Set<PermissionDTO>> getUserPermissions(@PathVariable UUID userId) {
        Set<PermissionDTO> permissions = userService.getUserPermissions(userId);
        return ResponseEntity.ok(permissions);
    }

    @GetMapping("/{userId}/has-permission/{permissionCode}")
    public ResponseEntity<Boolean> hasPermission(@PathVariable UUID userId, @PathVariable String permissionCode) {
        boolean hasPermission = userService.hasPermission(userId, permissionCode);
        return ResponseEntity.ok(hasPermission);
    }
}
