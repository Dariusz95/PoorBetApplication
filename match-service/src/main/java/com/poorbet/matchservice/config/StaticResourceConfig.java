package com.poorbet.matchservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(ImagesProperties.class)
public class StaticResourceConfig implements WebMvcConfigurer {

    private final ImagesProperties imagesProperties;

    public StaticResourceConfig(ImagesProperties imagesProperties) {
        this.imagesProperties = imagesProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations(
                        "file:" + imagesProperties.getPath() + "/",
                        "classpath:/static/images/"
                );
    }
}
