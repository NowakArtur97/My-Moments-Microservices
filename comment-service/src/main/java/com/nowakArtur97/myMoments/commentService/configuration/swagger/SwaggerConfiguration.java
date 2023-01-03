package com.nowakArtur97.myMoments.commentService.configuration.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = SwaggerConfigurationProperties.class)
@OpenAPIDefinition(info = @Info(title = "Post API", version = "v1"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
class SwaggerConfiguration {

    @Bean
    GroupedOpenApi groupedOpenApi(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return GroupedOpenApi
                .builder()
                .group("user")
                .pathsToMatch(swaggerConfigurationProperties.getPathSelectors())
                .build();
    }

    @Bean
    OpenAPI getApiDetails(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return new OpenAPI()
                .components(new Components())
                .info(new io.swagger.v3.oas.models.info.Info()
                        .version(swaggerConfigurationProperties.getVersion())
                        .title(swaggerConfigurationProperties.getTitle())
                        .description(swaggerConfigurationProperties.getDescription())
                        .termsOfService(swaggerConfigurationProperties.getTermsOfServiceUrl())
                        .license(getLicense(swaggerConfigurationProperties))
                        .contact(getContact(swaggerConfigurationProperties))
                );
    }

    private License getLicense(SwaggerConfigurationProperties swaggerConfigurationProperties) {
        License license = new License();
        license.setName(swaggerConfigurationProperties.getLicense());
        license.setUrl(swaggerConfigurationProperties.getLicenseUrl());
        return license;
    }

    private Contact getContact(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        Contact contact = new Contact();
        contact.setName(swaggerConfigurationProperties.getContactName());
        contact.setUrl(swaggerConfigurationProperties.getContactUrl());
        contact.setEmail(swaggerConfigurationProperties.getContactEmail());
        return contact;
    }
}
