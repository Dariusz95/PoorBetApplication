package com.poorbet.commons.security;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@NoArgsConstructor
@ConfigurationProperties(prefix = "poorbet.outbound-auth")
public class PoorbetOutboundAuthProperties {

    private boolean enabled = false;
    private boolean relayIncomingAuthorization = true;
    private String serviceToken;
}
