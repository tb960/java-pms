package com.opticalshop.erp.model.iam;

public enum TenantStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended"),
    ARCHIVED("Archived");

    private final String displayName;

    TenantStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
