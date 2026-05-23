package com.opticalshop.erp.service.iam;

import com.opticalshop.erp.dto.*;
import com.opticalshop.erp.exception.InvalidCredentialsException;
import com.opticalshop.erp.exception.ResourceNotFoundException;
import com.opticalshop.erp.exception.UserAlreadyExistsException;
import com.opticalshop.erp.exception.UserLockedException;
import com.opticalshop.erp.model.iam.*;
import com.opticalshop.erp.repository.iam.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityAuditLogRepository auditLogRepository;
    private final TenantRepository tenantRepository;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MINUTES = 30;

    public UserDTO registerUser(UUID tenantId, RegisterUserRequest request) {
        // Verify tenant exists
        tenantRepository.findById(tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + tenantId));

        // Check if user already exists
        if (userRepository.findByTenantIdAndUsername(tenantId, request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        if (userRepository.findByTenantIdAndEmail(tenantId, request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create user
        User user = User.builder()
            .tenantId(tenantId)
            .username(request.getUsername())
            .email(request.getEmail())
            .displayName(request.getDisplayName())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .userStatus(UserStatus.ACTIVE)
            .emailVerified(false)
            .phoneVerified(false)
            .build();

        User savedUser = userRepository.save(user);

        // Create credentials
        UserCredentials credentials = UserCredentials.builder()
            .userId(savedUser.getId())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .passwordAlgorithm("BCRYPT")
            .passwordChangedAt(LocalDateTime.now())
            .failedLoginAttempts(0)
            .build();

        userCredentialsRepository.save(credentials);

        // Audit log
        logSecurityEvent(tenantId, savedUser.getId(), SecurityEventType.USER_CREATED, "SUCCESS", null, null, null);

        return mapToDTO(savedUser);
    }

    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToDTO(user);
    }

    public UserDTO getUserByUsername(UUID tenantId, String username) {
        User user = userRepository.findByTenantIdAndUsername(tenantId, username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return mapToDTO(user);
    }

    public UserDTO authenticateUser(UUID tenantId, String username, String password) {
        User user = userRepository.findByTenantIdAndUsername(tenantId, username)
            .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        // Check if user is locked
        if (user.getUserStatus() == UserStatus.LOCKED) {
            UserCredentials creds = userCredentialsRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User credentials not found"));

            if (creds.getLockedUntil() != null && LocalDateTime.now().isBefore(creds.getLockedUntil())) {
                logSecurityEvent(tenantId, user.getId(), SecurityEventType.LOGIN_FAILED, "LOCKED", null, null, null);
                throw new UserLockedException("User account is locked. Try again later.");
            } else {
                // Unlock user
                user.setUserStatus(UserStatus.ACTIVE);
                creds.setFailedLoginAttempts(0);
                creds.setLockedUntil(null);
                userRepository.save(user);
                userCredentialsRepository.save(creds);
            }
        }

        UserCredentials credentials = userCredentialsRepository.findByUserId(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User credentials not found"));

        // Validate password
        if (!passwordEncoder.matches(password, credentials.getPasswordHash())) {
            credentials.setFailedLoginAttempts(credentials.getFailedLoginAttempts() + 1);

            // Lock account after max attempts
            if (credentials.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setUserStatus(UserStatus.LOCKED);
                credentials.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_DURATION_MINUTES));
                userRepository.save(user);
            }

            userCredentialsRepository.save(credentials);
            logSecurityEvent(tenantId, user.getId(), SecurityEventType.LOGIN_FAILED, "INVALID_PASSWORD", null, null, null);
            throw new InvalidCredentialsException("Invalid username or password");
        }

        // Reset failed attempts on successful login
        credentials.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        userCredentialsRepository.save(credentials);
        userRepository.save(user);

        logSecurityEvent(tenantId, user.getId(), SecurityEventType.LOGIN_SUCCESS, "SUCCESS", null, null, null);
        return mapToDTO(user);
    }

    public void assignRoleToUser(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        // Check if role assignment already exists
        if (userRoleRepository.findByIdUserId(userId).stream()
            .anyMatch(ur -> ur.getId().getRoleId().equals(roleId))) {
            log.warn("User {} already has role {}", userId, roleId);
            return;
        }

        UserRoleId userRoleId = new UserRoleId(userId, roleId);
        UserRole userRole = UserRole.builder()
            .id(userRoleId)
            .user(user)
            .role(role)
            .assignedAt(LocalDateTime.now())
            .build();

        userRoleRepository.save(userRole);
        logSecurityEvent(user.getTenantId(), userId, SecurityEventType.ROLE_ASSIGNED, "SUCCESS", null, null, role.getRoleCode());
    }

    public void removeRoleFromUser(UUID userId, UUID roleId) {
        UserRoleId userRoleId = new UserRoleId(userId, roleId);
        if (userRoleRepository.existsById(userRoleId)) {
            userRoleRepository.deleteById(userRoleId);

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

            logSecurityEvent(user.getTenantId(), userId, SecurityEventType.ROLE_REVOKED, "SUCCESS", null, null, role.getRoleCode());
        }
    }

    public Set<PermissionDTO> getUserPermissions(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return user.getRoles().stream()
            .flatMap(ur -> ur.getRole().getPermissions().stream())
            .map(this::mapPermissionToDTO)
            .collect(Collectors.toSet());
    }

    public boolean hasPermission(UUID userId, String permissionCode) {
        return getUserPermissions(userId).stream()
            .anyMatch(perm -> perm.getPermissionCode().equals(permissionCode));
    }

    public List<UserDTO> getUsersByTenantAndRole(UUID tenantId, String roleCode) {
        return userRepository.findByTenantIdAndRoleCode(tenantId, roleCode)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    private void logSecurityEvent(UUID tenantId, UUID userId, SecurityEventType eventType, String status, String ipAddress, String userAgent, String metadata) {
        SecurityAuditLog auditLog = SecurityAuditLog.builder()
            .tenantId(tenantId)
            .userId(userId)
            .eventType(eventType)
            .eventStatus(status)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .metadata(metadata)
            .build();

        auditLogRepository.save(auditLog);
    }

    private UserDTO mapToDTO(User user) {
        Set<RoleDTO> roles = user.getRoles().stream()
            .map(ur -> mapRoleToDTO(ur.getRole()))
            .collect(Collectors.toSet());

        return UserDTO.builder()
            .id(user.getId())
            .tenantId(user.getTenantId())
            .username(user.getUsername())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .displayName(user.getDisplayName())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .userStatus(user.getUserStatus().toString())
            .emailVerified(user.getEmailVerified())
            .phoneVerified(user.getPhoneVerified())
            .lastLoginAt(user.getLastLoginAt())
            .createdAt(user.getCreatedAt())
            .roles(roles)
            .build();
    }

    private RoleDTO mapRoleToDTO(Role role) {
        return RoleDTO.builder()
            .id(role.getId())
            .tenantId(role.getTenantId())
            .roleCode(role.getRoleCode())
            .roleName(role.getRoleName())
            .isSystemRole(role.getIsSystemRole())
            .createdAt(role.getCreatedAt())
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
