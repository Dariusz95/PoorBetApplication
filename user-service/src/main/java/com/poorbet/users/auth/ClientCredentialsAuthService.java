package com.poorbet.users.auth;

import com.poorbet.commons.auth.dto.ClientCredentialsTokenRequest;
import com.poorbet.commons.auth.dto.TokenResponse;
import com.poorbet.commons.security.PoorbetTokenTypes;
import com.poorbet.users.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientCredentialsAuthService {

    private static final String CLIENT_CREDENTIALS = "client_credentials";

    private final AuthClientsProperties authClientsProperties;
    private final JwtUtil jwtUtil;

    public TokenResponse issueToken(ClientCredentialsTokenRequest request) {
        if (!CLIENT_CREDENTIALS.equals(request.grantType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported grant type");
        }

        AuthClientsProperties.ClientRegistration clientRegistration = authClientsProperties.getClients().get(request.clientId());
        if (clientRegistration == null || clientRegistration.getSecret() == null || !clientRegistration.getSecret().equals(request.clientSecret())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client credentials");
        }

        String token = jwtUtil.generateAccessToken(
                request.clientId(),
                List.of(),
                clientRegistration.getPermissions(),
                PoorbetTokenTypes.SERVICE,
                request.clientId(),
                clientRegistration.getAudiences()
        );
        long expiresIn = jwtUtil.getAccessTokenExpiration() / 1000;
        return new TokenResponse(token, "Bearer", expiresIn);
    }
}
