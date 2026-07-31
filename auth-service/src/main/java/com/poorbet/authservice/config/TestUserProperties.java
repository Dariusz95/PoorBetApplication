package com.poorbet.authservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = TestUserProperties.PREFIX)
@Data
public class TestUserProperties {
    public static final String PREFIX = "poorbet.test-user";

    private String email;
    private String password;
}
