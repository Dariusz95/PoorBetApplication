package com.poorbet.auth.config;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@Data
@ConfigurationProperties(prefix = "auth.token")
public class TokenProperties {

    @NotNull
    private Duration accessTokenTtl;

    @NotNull
    private Duration refreshTokenTtl;
    private boolean reuseRefreshTokens;
}