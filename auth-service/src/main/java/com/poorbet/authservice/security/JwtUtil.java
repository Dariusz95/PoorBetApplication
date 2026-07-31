package com.poorbet.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private static final String REFRESH_TOKEN_PURPOSE = "refresh";

    private final RsaKeyProvider rsaKeyProvider;

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
                                      String userId,
                                      String clientId,
                                      List<String> audience) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        claims.put("permissions", permissions);
        claims.put("token_type", tokenType);

        if (StringUtils.hasText(clientId)) {
            claims.put("client_id", clientId);
        }

        if (StringUtils.hasText(userId)) {
            claims.put("uid", userId);
        }

        if (audience != null && !audience.isEmpty()) {
            claims.put("aud", audience);
        }

        return generateToken(subject, accessTokenExpiration, claims);
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, refreshTokenExpiration, Map.of("token_purpose", REFRESH_TOKEN_PURPOSE));
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
        return parseClaims(token).getSubject();
    }

    public boolean isRefreshToken(String token) {
        return REFRESH_TOKEN_PURPOSE.equals(parseClaims(token).get("token_purpose", String.class));
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(rsaKeyProvider.publicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
