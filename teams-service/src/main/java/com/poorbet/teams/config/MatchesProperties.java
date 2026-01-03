package com.poorbet.teams.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
@ConfigurationProperties("matches")
@Getter
@Setter
public class MatchesProperties {
    private int inBatch;
}
