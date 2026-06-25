package com.poorbet.matchservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.images")
public class ImagesProperties {
    private String path = "/app/data/images";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
