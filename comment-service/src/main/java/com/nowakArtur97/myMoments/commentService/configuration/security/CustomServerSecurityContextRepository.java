package com.nowakArtur97.myMoments.commentService.configuration.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
class CustomServerSecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtUtil jwtUtil;

    private final CustomReactiveAuthenticationManager customReactiveAuthenticationManager;

    private final JwtConfigurationProperties jwtConfigurationProperties;

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {

        ServerHttpRequest request = serverWebExchange.getRequest();

        String path = request.getURI().getPath();

        if (jwtConfigurationProperties.getIgnoredEndpoints().stream().anyMatch(path::contains)) {

            return Mono.empty();
        }

        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        String usernameOrEmail;
        String jwt;

        if (jwtUtil.isBearerTypeAuthorization(authorizationHeader)) {

            jwt = jwtUtil.getJwtFromHeader(authorizationHeader);
            usernameOrEmail = jwtUtil.extractUsername(jwt);

        } else {
            throw new JwtTokenMissingException("JWT token is missing in request headers.");
        }

        if (usernameOrEmail != null) {

            Authentication auth = new UsernamePasswordAuthenticationToken(jwt, usernameOrEmail);

            return customReactiveAuthenticationManager.authenticate(auth).map(SecurityContextImpl::new);
        }

        return Mono.empty();
    }
}
