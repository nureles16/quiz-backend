package com.example.quiz_app.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.util.Date;


@Component
public class JwtUtil {

    // Use a SecretKey instead of a String for the secret
    private final SecretKey secretKey = Keys.hmacShaKeyFor("1b0bdb62c3ce5a5639b33eb201a990f98478322d4367d781003b3111b6edface".getBytes());

    // Generate a JWT token for a given username
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours expiration
                .signWith(secretKey, SignatureAlgorithm.HS256) // Use SecretKey and algorithm
                .compact();
    }

    // Extract the username (subject) from a given token
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // Validate the token by checking its username and expiration status
    public boolean isTokenValid(String token, String username) {
        return username.equals(extractUsername(token)) && !isTokenExpired(token);
    }

    // Helper method to extract all claims from the token
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder() // Use parserBuilder()
                .setSigningKey(secretKey) // Set the SecretKey for validation
                .build() // Build the parser
                .parseClaimsJws(token) // Parse and validate the token
                .getBody(); // Extract the token body (claims)
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}
