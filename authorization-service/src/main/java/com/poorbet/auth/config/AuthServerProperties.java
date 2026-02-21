package com.poorbet.auth.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "auth.server")
public class AuthServerProperties {

    @NotBlank
    private String issuer;

    @NotBlank
    private String audience;
}
