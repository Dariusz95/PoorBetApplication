package com.poorbet.walletservice.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty
    private Map<String, String> exchanges;

    @NotEmpty
    private Map<String, Consumer> consumers;


    @Getter
    @Setter
    public static class Consumer {

        @NotBlank
        private String exchange;

        @NotBlank
        private String queue;

        @NotBlank
        private String routingKey;
    }
}