package com.alibou.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

// @Configuration annotation indicates that the class can be used by the Spring IoC container as a source of bean definitions
@Configuration
// @EnableWebSecurity annotation enables Spring Security's web security support
@EnableWebSecurity
// @RequiredArgsConstructor generates a constructor with 1 parameter for each field that requires special handling
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final AuthenticationProvider authenticationProvider;

    private final LogoutHandler logoutHandler;

    // @Bean annotation indicates that a method produces a bean to be managed by the Spring container

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Start configuring HttpSecurity
        http
                // Disabling CSRF (Cross Site REquest Forgery)
                .csrf()
                .disable()
                // Allowing authorization for HTTP requests
                .authorizeHttpRequests()
                // Matching requests with the specified patterns
                .requestMatchers("/api/v1/auth/**")
                // Permitting all requests matching the specified patterns
                .permitAll()
                // Any request needs to be authenticated
                .anyRequest()
                .authenticated()
                .and()
                // Configuring session management
                .sessionManagement()
                // Defining session creation policy as stateless
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                // Adding JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(((request, response, authentication) ->
                        SecurityContextHolder.clearContext())
                );

        // Building the HttpSecurity and returning it
        return http.build();

    }
}

