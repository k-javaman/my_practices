package com.alibou.security.config;

import com.alibou.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// @Configuration annotation indicates that the class can be used by the Spring IoC container as a source of bean definitions
@Configuration
// @RequiredArgsConstructor generates a constructor with 1 parameter for each field that requires special handling
@RequiredArgsConstructor
public class ApplicationConfig {

    // UserRepository bean injected by Spring
    private final UserRepository repository;

    // @Bean annotation indicates that a method produces a bean to be managed by the Spring container
    @Bean
    public UserDetailsService userDetailsService() {
        // Lambda function to find user by email in the repository
        return username -> repository.findByEmail(username)
                // If user not found, throw UsernameNotFoundException
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Create a DaoAuthenticationProvider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Set the UserDetailsService for the AuthenticationProvider
        authProvider.setUserDetailsService(userDetailsService());
        // Set the PasswordEncoder for the AuthenticationProvider
        authProvider.setPasswordEncoder(passwordEncoder());
        // Return the configured AuthenticationProvider
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        // Get the AuthenticationManager from the AuthenticationConfiguration
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Return a BCryptPasswordEncoder
        return new BCryptPasswordEncoder();
    }

}
