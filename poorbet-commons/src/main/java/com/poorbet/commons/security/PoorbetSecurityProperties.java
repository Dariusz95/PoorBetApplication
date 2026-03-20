package com.poorbet.commons.security;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "poorbet.security")
public class PoorbetSecurityProperties {

    private boolean enabled = true;
    private String issuer = "poorbet-auth-service";
    private String jwkSetUri;
    private List<String> unprotectedPaths = new ArrayList<>();
    private List<String> internalAudience = new ArrayList<>();
}
