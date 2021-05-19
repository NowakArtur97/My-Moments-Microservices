package com.nowakArtur97.myMoments.gatewayService.gateway;

import com.nowakArtur97.myMoments.gatewayService.exception.JwtTokenMissingException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class JwtGatewayFilter implements GatewayFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, GatewayFilterChain filterChain) {

        ServerHttpRequest request = serverWebExchange.getRequest();

        String path = request.getURI().getPath();

        if (jwtUtil.isNotSecured(path)) {

            return filterChain.filter(serverWebExchange);
        }

        String authorizationHeader = jwtUtil.getAuthorizationHeader(request);

        String username;
        String jwt;

        if (jwtUtil.isBearerTypeAuthorization(authorizationHeader)) {

            jwt = jwtUtil.getJwtFromHeader(authorizationHeader);
            username = jwtUtil.extractUsername(jwt);

        } else {
            throw new JwtTokenMissingException("JWT token is missing in request headers.");
        }

        if (jwtUtil.isTokenValid(jwt, username)) {

            return filterChain.filter(serverWebExchange);

        } else {
            throw new JwtException("Invalid JWT token.");
        }
    }
}
