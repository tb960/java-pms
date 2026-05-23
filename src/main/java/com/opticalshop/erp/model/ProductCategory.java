package com.opticalshop.erp.model;

public enum ProductCategory {
    FRAMES("Frames"),
    LENSES("Lenses"),
    CONTACT_LENSES("Contact Lenses"),
    ACCESSORIES("Accessories"),
    CLEANING_SOLUTION("Cleaning Solutions"),
    OTHER("Other");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
