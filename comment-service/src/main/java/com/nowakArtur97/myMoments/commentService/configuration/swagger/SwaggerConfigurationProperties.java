package com.nowakArtur97.myMoments.commentService.configuration.swagger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "my-moments.swagger")
@ConstructorBinding
@AllArgsConstructor
@Getter
final class SwaggerConfigurationProperties {

    private final String version;

    private final String title;

    private final String description;

    private final String termsOfServiceUrl;

    private final String license;

    private final String licenseUrl;

    private final String contactName;

    private final String contactEmail;

    private final String contactUrl;

    private final String pathSelectors;

    private final String authorizationHeader;
}
