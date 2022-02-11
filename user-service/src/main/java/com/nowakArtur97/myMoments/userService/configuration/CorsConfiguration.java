package com.nowakArtur97.myMoments.userService.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/api/v1/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .exposedHeaders("Authorization");
    }
}
