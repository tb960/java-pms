package com.opticalshop.erp.service.iam;

import com.opticalshop.erp.dto.TenantDTO;
import com.opticalshop.erp.dto.CreateTenantRequest;
import com.opticalshop.erp.exception.ResourceNotFoundException;
import com.opticalshop.erp.model.iam.Tenant;
import com.opticalshop.erp.model.iam.TenantStatus;
import com.opticalshop.erp.repository.iam.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantDTO createTenant(CreateTenantRequest request) {
        Tenant tenant = Tenant.builder()
            .tenantCode(request.getTenantCode())
            .tenantName(request.getTenantName())
            .status(TenantStatus.ACTIVE)
            .build();

        Tenant saved = tenantRepository.save(tenant);
        return mapToDTO(saved);
    }

    public TenantDTO getTenantById(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        return mapToDTO(tenant);
    }

    public TenantDTO getTenantByCode(String tenantCode) {
        Tenant tenant = tenantRepository.findByTenantCode(tenantCode)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with code: " + tenantCode));
        return mapToDTO(tenant);
    }

    public List<TenantDTO> getAllActiveTenants() {
        return tenantRepository.findByStatus(TenantStatus.ACTIVE)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public TenantDTO updateTenant(UUID id, CreateTenantRequest request) {
        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));

        tenant.setTenantName(request.getTenantName());
        Tenant updated = tenantRepository.save(tenant);
        return mapToDTO(updated);
    }

    public void deactivateTenant(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tenant not found with id: " + id));
        tenant.setStatus(TenantStatus.INACTIVE);
        tenantRepository.save(tenant);
    }

    private TenantDTO mapToDTO(Tenant tenant) {
        return TenantDTO.builder()
            .id(tenant.getId())
            .tenantCode(tenant.getTenantCode())
            .tenantName(tenant.getTenantName())
            .status(tenant.getStatus().toString())
            .createdAt(tenant.getCreatedAt())
            .updatedAt(tenant.getUpdatedAt())
            .build();
    }
}
