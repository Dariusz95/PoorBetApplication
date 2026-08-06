package com.poorbet.matchservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app.images")
public class ImagesProperties {
    private String path = "/app/data/images";

}
