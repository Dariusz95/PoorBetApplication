package com.poorbet.users.auth;

import com.poorbet.commons.auth.dto.ClientCredentialsTokenRequest;
import com.poorbet.commons.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final ClientCredentialsAuthService clientCredentialsAuthService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(@Valid @RequestBody ClientCredentialsTokenRequest request) {
        return ResponseEntity.ok(clientCredentialsAuthService.issueToken(request));
    }
}
