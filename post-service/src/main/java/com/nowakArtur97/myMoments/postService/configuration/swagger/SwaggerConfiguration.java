package com.nowakArtur97.myMoments.postService.configuration.swagger;

import com.nowakArtur97.myMoments.postService.feature.post.PostTag;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

@Configuration
@EnableConfigurationProperties(value = SwaggerConfigurationProperties.class)
class SwaggerConfiguration {

    @Bean
    Docket docket(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.ant(swaggerConfigurationProperties.getPathSelectors()))
                .build()
                .apiInfo(getApiDetails(swaggerConfigurationProperties))
                .tags(
                        new Tag(PostTag.RESOURCE, PostTag.DESCRIPTION)
                )
                .securityContexts(List.of(getSecurityContext(swaggerConfigurationProperties)))
                .securitySchemes(List.of(getApiKey(swaggerConfigurationProperties)));
    }

    private ApiInfo getApiDetails(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return new ApiInfoBuilder()
                .version(swaggerConfigurationProperties.getVersion())
                .title(swaggerConfigurationProperties.getTitle())
                .description(swaggerConfigurationProperties.getDescription())
                .termsOfServiceUrl(swaggerConfigurationProperties.getTermsOfServiceUrl())
                .license(swaggerConfigurationProperties.getLicense())
                .licenseUrl(swaggerConfigurationProperties.getLicenseUrl())
                .contact(getContact(swaggerConfigurationProperties))
                .build();
    }

    private Contact getContact(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return new Contact(swaggerConfigurationProperties.getContactName(),
                swaggerConfigurationProperties.getContactUrl(), swaggerConfigurationProperties.getContactEmail());
    }

    private ApiKey getApiKey(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return new ApiKey("JWT", swaggerConfigurationProperties.getAuthorizationHeader(), "header");
    }

    private SecurityContext getSecurityContext(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return SecurityContext.builder()
                .securityReferences(getDefaultAuth())
                .forPaths(PathSelectors.ant(swaggerConfigurationProperties.getPathSelectors()))
                .build();
    }

    private List<SecurityReference> getDefaultAuth() {

        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");

        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];

        authorizationScopes[0] = authorizationScope;

        return List.of(new SecurityReference("JWT", authorizationScopes));
    }
}
