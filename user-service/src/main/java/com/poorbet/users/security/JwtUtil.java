package com.poorbet.users.security;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    private final RsaKeyProvider rsaKeyProvider;

    @Value("${jwt.access-token-expiration:900000}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}")
    private Long refreshTokenExpiration;

    @Value("${jwt.issuer:poorbet-auth-service}")
    private String issuer;

    public JwtUtil(RsaKeyProvider rsaKeyProvider) {
        this.rsaKeyProvider = rsaKeyProvider;
    }

    public String generateAccessToken(String email, List<String> roles, List<String> permissions) {
        return generateToken(email, accessTokenExpiration, roles, permissions);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, refreshTokenExpiration, List.of(), List.of());
    }

    private String generateToken(String subject, Long expiration, List<String> roles, List<String> permissions) {
        return Jwts.builder()
                .header().add("kid", rsaKeyProvider.keyId()).and()
                .subject(subject)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(rsaKeyProvider.privateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(rsaKeyProvider.publicKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return issuer.equals(claims.getIssuer());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(rsaKeyProvider.publicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!issuer.equals(claims.getIssuer())) {
            throw new JwtException("Invalid issuer");
        }
        return claims.getSubject();
    }

    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}
