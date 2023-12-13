package com.alibou.security.auth;

import com.alibou.security.config.JwtService;
import com.alibou.security.token.Token;
import com.alibou.security.token.TokenRepository;
import com.alibou.security.token.TokenType;
import com.alibou.security.user.Role;
import com.alibou.security.user.User;
import com.alibou.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service // Marks this class as a Spring service
@RequiredArgsConstructor // Generates a constructor with required arguments (final fields)
public class AuthenticationService {

    private final UserRepository repository; // Repository for user data access

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService; //  Service for JWT Opertions

    public final AuthenticationManager authenticationManager; // Manages authentication processes

    public AuthenticationResponse register(RegisterRequest request) {
        // Register user and return JWT token
        var user = User.builder() // craete a new user object
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Encode and set password
                .role(Role.USER) // Set user role
                .build(); // Build the user object

        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user); // Generate JWT token for the user
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder() // Build the authentication response
                .token(jwtToken) // set the generated JWT token
                .build(); // Build the response object
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate user and return JWT token
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        ); // Authenticate the user

        var user = repository.findByEmail(request.getEmail()) // Retrieve the user from the database
                .orElseThrow(); // Throw exception if not found
        var jwtToken = jwtService.generateToken(user); // Generate JWT token for the user
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder() // Build the authentication response
                .token(jwtToken)
                .build(); // Build the response object
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
