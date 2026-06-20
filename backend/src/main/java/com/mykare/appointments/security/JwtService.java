package com.mykare.appointments.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
      
      private final SecretKey secretKey;
      private final long expirationMinutes;

      public JwtService( @Value("${jwt.secret}") String secretKey,
                         @Value("${jwt.expiration-minutes}") long expirationMinutes) {
            this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            this.expirationMinutes = expirationMinutes;

      }

    public String generateToken(UserPrincipal principal){
         Instant now = Instant.now();
         Instant expiresAt = now.plusSeconds(expirationMinutes * 60);

         return Jwts.builder()
                .subject(principal.getUsername())
                .claim("userId", principal.getId())
                .claim("fullName", principal.getFullName())
                .claim("role", principal.getAuthorities().iterator().next().getAuthority())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token){
         return extractAllClaims(token).getSubject();
    }

    public boolean isValid(String token, UserPrincipal principal){
         String username = extractUsername(token);
         return (username.equals(principal.getUsername()) && !isExpired(token));
    }

    private boolean isExpired(String token){
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
