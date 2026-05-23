package com.opticalshop.erp.controller.iam;

import com.opticalshop.erp.dto.CreateTenantRequest;
import com.opticalshop.erp.dto.TenantDTO;
import com.opticalshop.erp.service.iam.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantDTO> createTenant(@RequestBody CreateTenantRequest request) {
        TenantDTO tenant = tenantService.createTenant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tenant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantDTO> getTenant(@PathVariable UUID id) {
        TenantDTO tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(tenant);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<TenantDTO> getTenantByCode(@PathVariable String code) {
        TenantDTO tenant = tenantService.getTenantByCode(code);
        return ResponseEntity.ok(tenant);
    }

    @GetMapping
    public ResponseEntity<List<TenantDTO>> getAllActiveTenants() {
        List<TenantDTO> tenants = tenantService.getAllActiveTenants();
        return ResponseEntity.ok(tenants);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantDTO> updateTenant(@PathVariable UUID id, @RequestBody CreateTenantRequest request) {
        TenantDTO tenant = tenantService.updateTenant(id, request);
        return ResponseEntity.ok(tenant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateTenant(@PathVariable UUID id) {
        tenantService.deactivateTenant(id);
        return ResponseEntity.noContent().build();
    }
}
