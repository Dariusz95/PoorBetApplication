package com.poorbet.auth.config;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
@ConfigurationProperties(prefix = "auth.clients.frontend")
public class FrontendClientProperties {

    @NotBlank
    private String id;

    @NotBlank
    private String redirectUri;

    @NotEmpty
    private List<String> scopes;
}