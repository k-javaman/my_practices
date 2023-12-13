package com.alibou.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// @Service annotation is used with classes that provide some business functionalities
@Service
public class JwtService {

    // @Value annotation is used to inject values into fields
    @Value("${jwt.secret}")
    private String SECRET_KEY; // The secret key for JWT

    // Method to extract the username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Method to generate a token for a user

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder() // start building the JWT
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // set the subject to the username
                .setIssuedAt(new Date(System.currentTimeMillis())) // Set the issued at time to now
                // Set the expiration time to 24 hours from now
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Sign the JWT with the secret key
                .compact(); // Build the JWT and serialize it to a compact, URL-safe strin
    }

    // Method to check if a token is valid for a user
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Method to chekc if a token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Method to extract the expiration date from the token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // method to extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder() // Start building a JWT parser
                .setSigningKey(getSignInKey()) // Set the signing key
                .build() // Build the JWT parser
                .parseClaimsJws(token) // Parse the claims from the token
                .getBody(); // Get the Body of the JWT
    }

    // Method to get the signing key

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Decode the secret key
        return Keys.hmacShaKeyFor(keyBytes);
    }



}
