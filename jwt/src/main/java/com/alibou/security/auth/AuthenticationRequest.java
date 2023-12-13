package com.alibou.security.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok annotation to generate getters, setters, equals, hashCode, and toString methods
@Builder // Lombok annotation to provide a builder for the class
@AllArgsConstructor // Lombok annotation to generate a constructor with all arguments
@NoArgsConstructor // Lombok annotation to generate a default constructor
public class AuthenticationRequest {

    private String email; // User's email
    String password; // User's password
}
