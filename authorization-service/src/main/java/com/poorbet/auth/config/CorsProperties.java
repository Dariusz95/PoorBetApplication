package com.poorbet.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "auth.cors")
public class CorsProperties {

    private List<String> allowedOrigins;
}