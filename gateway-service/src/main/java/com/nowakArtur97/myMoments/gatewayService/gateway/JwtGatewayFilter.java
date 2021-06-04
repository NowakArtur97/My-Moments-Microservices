package com.nowakArtur97.myMoments.gatewayService.gateway;

import com.nowakArtur97.myMoments.gatewayService.exception.JwtTokenMissingException;
import com.nowakArtur97.myMoments.gatewayService.exception.UsernameNotFoundException;
import com.nowakArtur97.myMoments.gatewayService.user.UserService;
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

    private final UserService userService;

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

        return userService.findByUsername(username)
                .switchIfEmpty(Mono.error(() ->
                        new UsernameNotFoundException("User with name: '" + username + "' not found.")))
                .flatMap(userDocument -> {

                    if (jwtUtil.isTokenValid(jwt, userDocument.getUsername())) {

                        return filterChain.filter(serverWebExchange);

                    } else {
                        throw new JwtException("Invalid JWT token.");
                    }
                });
    }
}