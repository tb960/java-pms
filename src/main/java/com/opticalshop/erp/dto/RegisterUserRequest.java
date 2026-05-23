package com.opticalshop.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterUserRequest {
    private String username;
    private String email;
    private String password;
    private String displayName;
    private String firstName;
    private String lastName;
}
