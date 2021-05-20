package com.nowakArtur97.myMoments.gatewayService.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GatewayConfig {

    @Bean
    RouteLocator routes(RouteLocatorBuilder builder, JwtGatewayFilter jwtGatewayFilter) {

        return builder.routes()

                .route("registration",
                        r -> r.path("/api/v1/registration/**")
                                .uri("lb://user-service"))

                .route("authentication",
                        r -> r.path("/api/v1/authentication")
                                .uri("lb://user-service"))

                .route("users",
                        r -> r.path("/api/v1/users/**")
                                .filters(f -> f.filter(jwtGatewayFilter))
                                .uri("lb://user-service"))

                .route("posts",
                        r -> r.path("/api/v1/**")
                                .filters(f -> f.filter(jwtGatewayFilter))
                                .uri("lb://post-service"))

                .route("comments",
                        r -> r.path("/api/v1/posts/{id}/comments/**")
                                .filters(f -> f.filter(jwtGatewayFilter))
                                .uri("lb://comment-service"))
                .build();
    }
}
