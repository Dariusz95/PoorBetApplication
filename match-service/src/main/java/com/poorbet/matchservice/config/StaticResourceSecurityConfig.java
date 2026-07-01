package com.poorbet.matchservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class StaticResourceSecurityConfig {

    @Bean
    public WebSecurityCustomizer staticResourcesSecurityCustomizer() {
        return (WebSecurity web) -> web.ignoring().requestMatchers("/images/**");
    }
}
