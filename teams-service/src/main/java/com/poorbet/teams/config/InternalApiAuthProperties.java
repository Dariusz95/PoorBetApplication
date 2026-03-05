package com.poorbet.teams.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "teams.internal-api")
public class InternalApiAuthProperties {

    private String token;
}
