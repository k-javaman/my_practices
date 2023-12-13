package com.alibou.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Marks the class as a Rest Controller, handling HTTP requests
@RequestMapping("/api/v1/auth") // Sets the base URL for all methods in this controller
@RequiredArgsConstructor // Lombok annotation to generate a constructor for final fields (for dependency injection)
public class AuthenticationController {

    private final AuthenticationService service; // Injects AuthenticationService

    @PostMapping("/register") // HTTP POST endpoint for user registration
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request // Takes registration details from the request body
    ) {
        return ResponseEntity.ok(service.register(request)); // Calls the register method of the service and returns the response
    }

    @PostMapping("/authenticate") // HTTP POST endpoint for user authentication
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request // Takes authentication details from the request body
    ) {
        return ResponseEntity.ok(service.authenticate(request)); // Calls the authenticate method of the service and returns the response
    }
}

