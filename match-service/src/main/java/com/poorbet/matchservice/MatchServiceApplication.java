package com.poorbet.matchservice;

import com.poorbet.commons.auth.config.AuthWebClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
@Import(AuthWebClientConfiguration.class)
public class MatchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchServiceApplication.class, args);
	}

}
