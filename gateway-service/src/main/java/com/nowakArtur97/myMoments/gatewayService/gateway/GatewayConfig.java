package com.nowakArtur97.myMoments.gatewayService.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GatewayConfig {

    @Bean
    RouteLocator routes(RouteLocatorBuilder builder, JwtGatewayRequestFilter jwtGatewayRequestFilter) {

        return builder.routes()
                .route("registration",
                        r -> r.path("/api/v1/registration/**")
                                .uri("lb://user-service"))

                .route("authentication",
                        r -> r.path("/api/v1/authentication")
                                .uri("lb://user-service"))

                .route("users",
                        r -> r.path("/api/v1/users/**")
                                .filters(f -> f.filter(jwtGatewayRequestFilter))
                                .uri("lb://user-service"))

                .route("posts",
                        r -> r.path("/api/v1/posts/**")
                                .filters(f -> f.filter(jwtGatewayRequestFilter))
                                .uri("lb:post-service"))

                .route("comments",
                        r -> r.path("/api/v1/comments/**")
                                .filters(f -> f.filter(jwtGatewayRequestFilter))
                                .uri("lb:comment-service"))
                .build();
    }
}
