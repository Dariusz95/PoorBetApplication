package com.poorbet.matchservice.match.config.rabbitmq;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "messaging")
@Validated
public class MessagingProperties {

    @NotBlank
    private String sourceService;

    @NotNull
    private Map<String, String> exchanges;
}
