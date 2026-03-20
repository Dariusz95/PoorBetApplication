package com.poorbet.users.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    private final RsaKeyProvider rsaKeyProvider;

    public JwtUtil(RsaKeyProvider rsaKeyProvider) {
        this.rsaKeyProvider = rsaKeyProvider;
    }

    @Value("${jwt.issuer:poorbet-auth-service}")
    private String issuer;

    @Value("${jwt.access-token-expiration:900000}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}")
    private Long refreshTokenExpiration;

    public String generateAccessToken(String subject,
                                      List<String> roles,
                                      List<String> permissions,
                                      String tokenType,
                                      String clientId,
                                      List<String> audience) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        claims.put("token_type", tokenType);

        if (StringUtils.hasText(clientId)) {
            claims.put("client_id", clientId);
        }

        if (audience != null && !audience.isEmpty()) {
            claims.put("aud", audience);
        }

        return generateToken(subject, accessTokenExpiration, claims);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, refreshTokenExpiration, Map.of());
    }

    private String generateToken(String subject, Long expiration, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .setHeaderParam("kid", rsaKeyProvider.keyId())
                .issuedAt(new Date())
                .issuer(issuer)
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(rsaKeyProvider.privateKey(), SignatureAlgorithm.RS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(rsaKeyProvider.publicKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
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
        return claims.getSubject();
    }

    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }
}
