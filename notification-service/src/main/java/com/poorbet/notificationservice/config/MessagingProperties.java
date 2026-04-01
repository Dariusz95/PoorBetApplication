package com.poorbet.notificationservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "messaging")
public class MessagingProperties {
    private String walletExchange;
    private String walletQueue;
    private String walletRoutingKey;
}
