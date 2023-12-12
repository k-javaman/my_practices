package com.alibou.security.auth;

import com.alibou.security.config.JwtService;
import com.alibou.security.user.Role;
import com.alibou.security.user.User;
import com.alibou.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Constructor-based dependency injection is used here.

    public AuthenticationResponse register(RegisterRequest request) {
        // This method handles user registration and authentication.
        // It takes a RegisterRequest object as input, which contains user registration details.

        // Create a new User entity using the provided user registration details.
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hash and store the password.
                .role(Role.USER)
                .build();

        // Save the user entity to the repository (database).
        repository.save(user);

        // Generate a JWT (JSON Web Token) for the registered user.
        var jwtToken = jwtService.generateToken(user);

        // Build and return an AuthenticationResponse containing the JWT token.
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // This method handles user authentication.
        // It takes an AuthenticationRequest object as input, which contains user login details.

        // Use the AuthenticationManager to perform user authentication by creating an authentication token.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Retrieve the user entity from the repository based on the provided email.
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();

        // Generate a JWT token for the authenticated user.
        var jwtToken = jwtService.generateToken(user);

        // Build and return an AuthenticationResponse containing the JWT token.
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
