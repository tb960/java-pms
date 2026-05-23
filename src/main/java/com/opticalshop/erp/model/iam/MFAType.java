package com.opticalshop.erp.model.iam;

public enum MFAType {
    TOTP("Time-based One-Time Password"),
    SMS_OTP("SMS One-Time Password"),
    EMAIL_OTP("Email One-Time Password"),
    WEBAUTHN("WebAuthn"),
    PASSKEY("Passkey");

    private final String displayName;

    MFAType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
