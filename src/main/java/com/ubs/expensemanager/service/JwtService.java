package com.ubs.expensemanager.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final Key key;
    private final long expirationMillis;

    /**
     * secretBase64: a base64-encoded secret string provided via configuration.
     * If you want a raw secret instead, adapt accordingly.
     */
    public JwtService(@Value("${app.jwt.secret}") String secretBase64,
                      @Value("${app.jwt.expiration-ms:3600000}") long expirationMillis) {
        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String email, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .setSubject(email)
            .claim("role", role) // consistent claim name
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + expirationMillis))
            .signWith(key)
            .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }
}
